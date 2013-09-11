(ns cow.model
  (:require [cow.math :as math]))

(def cow-count 10)
(def max-starting-velocity 0.01)
(def max-turn-degrees 180)
(def cow-id (atom 0))

(defn random-cow []
  (let [random-velocity (fn [] (- max-starting-velocity (rand (* 2 max-starting-velocity))))
        random-position (fn [] (- 0.95 (rand 1.9)))
        cow (atom {
                   :id (swap! cow-id inc)
                   :anxiety (rand 0.1)
                   :velocity [(random-velocity) (random-velocity)]
                   :pos [(random-position) (random-position)]
                   :self-differentiation (rand)
                   })]
    cow))

(def cows (repeatedly cow-count random-cow))

(defn hit-fence?
  "Or, you know, come within 96% of the fence."
  [cow] (not (not-any? #(>= (Math/abs %) 0.96) (:pos cow))))


(defn perturb-vector 
  "This is currently disabled because I apparently don't understand
that the range of arctan is ±π."
  [velocity heading]
  (let [polar (math/rect-to-polar velocity)
        perturbed-polar (assoc polar 0 (+ (polar 0) heading))]
    ; (.log js/console (str (polar-to-rect polar) " | " velocity))
    ; Disable random walking for now
    ; (polar-to-rect perturbed-polar)))
    velocity))


(defn random-walk [cow]
  (let [velocity (:velocity cow)
        vx (velocity 0)
        vy (velocity 1)
        random-x (math/rand-normal vx (* 2 max-starting-velocity))
        random-y (math/rand-normal vy (* 2 max-starting-velocity))
        heading (Math/atan (/ vy vx))
        wander (* (math/rand-normal 0 (math/degrees-to-radians max-turn-degrees)))]
    (perturb-vector (:velocity cow) wander)))

(defn new-cow-velocity [cow]
  (let [velocity (:velocity cow)
        pos (:pos cow)
        negate (fn [vec n] (assoc vec n (- (vec n))))]
    (cond 
      (> (Math/abs (pos 0)) 0.97) (negate velocity 0)
      (> (Math/abs (pos 1)) 0.97) (negate velocity 1)
      :else (random-walk cow))))

(defn cow-distance [self-cow other-cow]
  (math/hypotenuse (map - (:pos self-cow) (:pos other-cow))))

(defn distance-discounted-anxiety 
  "Nearby anxiety between individuals fades with the inverse 
square of distance. If individuals are closer than 0.05 units,
they are considered to be touching. (Otherwise there is a
painful asymptote to deal with, and we don't likes those.)"
  [self-cow other-cow]
  (let [distance (cow-distance self-cow other-cow)
        capped-distance (max distance 0.05)
        inverse-square (/ 1 (math/square capped-distance))]
    (* (:anxiety other-cow) inverse-square)))

(defn anxiety-near-me [cows cow]
  (let [anxiety-horizon 0.8
        nearby-cows (filter #(> anxiety-horizon (cow-distance cow @%)) cows)]
  ; (math/average (map #(distance-discounted-anxiety cow @%) nearby-cows))))
    (apply max (map #(:anxiety @%) nearby-cows))))

(defn new-cow-anxiety [cows cow]
  (let [anxiety-threshold 0.9
        ; local-maximum (apply max (map #(distance-discounted-anxiety cow @%) cows))
        nearby-anxiety (anxiety-near-me cows cow)
        assumed-anxiety (* (- 1 (:self-differentiation cow)) nearby-anxiety)
        ]
    ; (.log js/console nearby-anxiety " " assumed-anxiety)
    (cond
      ; spaz out
      (hit-fence? cow) 1

      ; take on anxiety
      (>= nearby-anxiety anxiety-threshold) assumed-anxiety

      ; slowly chill out
      :else (* (:anxiety cow) 0.95))))

(defn sim-cows [cows]
  (doseq [cow-atom cows]
    (let [cow @cow-atom
          new-velocity (new-cow-velocity cow)
          new-pos (vec (map + new-velocity (:pos cow)))
          new-anxiety (new-cow-anxiety cows cow)]
      (swap! cow-atom assoc 
             :anxiety new-anxiety
             :pos new-pos 
             :velocity new-velocity))))



              
