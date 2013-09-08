(ns cow
  (:require [clojure.browser.event :as event]
            [clojure.browser.dom :as dom]
            [goog.Timer]))

(def cow-count 10)


(defn random-cow []
  (let [max-velocity 0.001
        random-velocity (fn [] (- max-velocity (rand (* 2 max-velocity))))
        random-position (fn [] (- 1 (rand 2)))
        cow (atom {
                   :anxiety 0
                   :velocity (vec (repeatedly 2 random-velocity))
                   :pos (vec (repeatedly 2 random-position))
                   :self-differentiation (rand)
                   })]
    cow)
  )

(def canvas (dom/get-element "model"))
(def timer (goog.Timer. (/ 1000 20)))
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

(defn new-cow-velocity [cow]
  (let [velocity (:velocity cow)
        pos (:pos cow)
        negate (fn [vec n] (assoc vec n (- (vec n))))]
    (cond 
      (> (Math/abs (pos 0)) 0.99) (negate velocity 0)
      (> (Math/abs (pos 1)) 0.99) (negate velocity 1)
      :else velocity)))

(defn new-cow-anxiety [cows cow]
  
  )

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
  ([ctx position width height]
     (let [half-width (/ width 2)
           half-height (/ height 2)
           upper-left (- (position 0) half-width)
           upper-right (- (position 1) half-height)]
       (do 
         (.beginPath ctx)
         (.fillRect ctx upper-left upper-right width height)
         (.closePath ctx))))
  ([ctx position width]
     (draw-box ctx position width width))
  ([ctx position]
     (draw-box ctx position 5)))

(defn cow-to-canvas-coord [canvas-dim cow-coord]
  (let [dimension (/ canvas-dim 2)]
    (+ dimension (* dimension cow-coord))))

(defn paint-cow [canvas cow]
  (let [ctx (.getContext canvas "2d")
        ctx-size (vec (map #(.getAttribute canvas %1) ["width" "height"]))
        ctx-pos (vec (map cow-to-canvas-coord ctx-size (:pos cow)))]
    (draw-box ctx ctx-pos)))

(defn paint-sim [canvas cows]
  (do 
    (init-canvas canvas)
    (doseq [cow cows]
      (paint-cow canvas @cow))))

(defn update-params [cows]
  (let [elapsed-time (dom/get-element "elapsed-time")]
    (.innerHTML elapsed-time (.getTime (java.util.Date.)))))

(defn cow-sim []
  (do
    (sim-cows cows)
    (paint-sim canvas cows)))

(event/listen timer goog.Timer/TICK cow-sim)
(.start timer)

