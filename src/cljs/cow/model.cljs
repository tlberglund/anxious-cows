(ns cow.model)

(def cow-count 25)
(def max-starting-velocity 0.01)
(def cow-id (atom 0))

(defn random-cow []
  (let [random-velocity (fn [] (- max-starting-velocity (rand (* 2 max-starting-velocity))))
        random-position (fn [] (- 0.95 (rand 1.9)))
        cow (atom {
                   :id (swap! cow-id inc)
                   :anxiety (rand 0.1)
                   :velocity [(random-velocity) (random-velocity)]
                   :pos [(random-position) (random-position)]
                   })]
    cow))

(def cows (repeatedly cow-count random-cow))

(defn hit-fence?
  "Or, you know, come within 96% of the fence."
  [cow] (not (not-any? #(>= (Math/abs %) 0.96) (:pos cow))))

(defn new-cow-velocity [cow]
  (let [velocity (:velocity cow)
        pos (:pos cow)
        negate (fn [vec n] (assoc vec n (- (vec n))))]
    (cond
      (> (Math/abs (pos 0)) 0.97) (negate velocity 0)
      (> (Math/abs (pos 1)) 0.97) (negate velocity 1)
      :else velocity)))

(defn new-cow-anxiety [cow]
  (cond
    ; spaz out
    (hit-fence? cow) 1

    ; slowly chill out
    :else (* (:anxiety cow) 0.95)))

(defn sim-cows [cows]
  (doseq [cow-atom cows]
    (let [cow @cow-atom
          new-velocity (new-cow-velocity cow)
          new-pos (vec (map + new-velocity (:pos cow)))
          new-anxiety (new-cow-anxiety cow)]
      (swap! cow-atom assoc
             :anxiety new-anxiety
             :pos new-pos
             :velocity new-velocity))))
