(ns cow.model)

(def cow-count 25)
(def max-starting-velocity 0.01)
(def cow-id (atom 0))

(defn random-cow []
  (let [random-velocity (fn [] (- max-starting-velocity (rand (* 2 max-starting-velocity))))
        random-position (fn [] (- 0.95 (rand 1.9)))
        cow (atom {
                   :id (swap! cow-id inc)
                   :pos [(random-position) (random-position)]
                   })]
    cow))

(def cows (repeatedly cow-count random-cow))

