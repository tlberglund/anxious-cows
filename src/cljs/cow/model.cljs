(ns cow.model
  (:require [cow.math :as math]))

(def cow-count 10)
(def max-starting-velocity 0.01)
(def max-turn-degrees 180)


(def cow-id (atom 0))
(defn random-cow []
  (let [random-velocity (fn [] (- max-starting-velocity (rand (* 2 max-starting-velocity))))
        random-position (fn [] (math/rand-normal 0 1))
        cow (atom {
                   :id (swap! cow-id inc)
                   :anxiety (rand)
                   :velocity [(random-velocity) (random-velocity)]
                   :pos [(random-position) (random-position)]
                   :self-differentiation (rand)
                   })]
    cow))

(def cows (repeatedly cow-count random-cow))

(defn init-canvas [canvas]
  (let [ctx (.getContext canvas "2d")
        width (.getAttribute canvas "width")
        height (.getAttribute canvas "height")]
    (do
      (.clearRect ctx 0 0 width height)
      (.beginPath ctx)
      (.moveTo ctx 0 0)
      (set! (. ctx -lineWidth) 5)
      (.lineTo ctx 0 width)
      (.lineTo ctx width height)
      (.lineTo ctx height 0)
      (.lineTo ctx 0 0)
      (.stroke ctx))))


(defn hit-fence? 
  ([cow] (hit-fence? ((:pos cow) 0) ((:pos cow) 1)))
  ([x y] (every? #(> (Math/abs %) 1) (vector x y))))


(defn perturb-vector [velocity heading]
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

(defn new-cow-anxiety [cows cow] 
  (* (:anxiety cow) 0.99))

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

(defn draw-box 
  ([ctx position width height fill-style]
     (let [half-width (/ width 2)
           half-height (/ height 2)
           upper-left (- (position 0) half-width)
           upper-right (- (position 1) half-height)]
       (do 
         (.beginPath ctx)
         (set! (. ctx -fillStyle) fill-style)
         (.fillRect ctx upper-left upper-right width height)
         (.closePath ctx))))
  ([ctx position width fill-style]
     (draw-box ctx position width width fill-style))
  ([ctx position fill-style]
     (draw-box ctx position 5 fill-style)))

(defn draw-circle [ctx position radius fill-style]
  (do
    (set! (. ctx -fillStyle) fill-style)
    (.beginPath ctx)
    (.arc ctx (position 0) (position 1) radius 0 (* 2 Math/PI) true)
    (.closePath ctx)
    (.fill ctx)))

(defn cow-to-canvas-coord [canvas-dim cow-coord]
  (let [dimension (/ canvas-dim 2)]
    (+ dimension (* dimension cow-coord))))

(defn paint-cow [canvas cow]
  (let [ctx (.getContext canvas "2d")
        ctx-size (vec (map #(.getAttribute canvas %1) ["width" "height"]))
        ctx-pos (vec (map cow-to-canvas-coord ctx-size (:pos cow)))
        cow-color (str "rgb(" (format "%d" (int (* 256 (:anxiety cow)))) ",0,0)")
        cow-size (int (+ 5 (* 15 (:anxiety cow))))]
    ; (draw-box ctx ctx-pos cow-size cow-color)))
    (draw-circle ctx ctx-pos cow-size cow-color)))

(defn paint-sim [canvas cows]
  (do 
    (init-canvas canvas)
    (doseq [cow cows]
      (paint-cow canvas @cow))))



              
