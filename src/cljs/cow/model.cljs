(ns cow
  (:require [clojure.browser.event :as event]
            [clojure.browser.dom :as dom]
            [goog.Timer]))

(def cow-count 20)
(def pi 3.1415926535)


(defn x-from-polar [theta radius]
  (* radius (Math/cos theta)))

(defn y-from-polar [theta radius]
  (* radius (Math/sin theta)))

(defn random-cow []
  (let [theta (- (* 2 pi) (rand (* 4 pi)))
        radius (rand)
        cow (atom {
        :anxiety 0
        :angle (- (* 2 pi) (rand (* 4 pi)))
        :velocity (rand 0.01)
        :x (x-from-polar theta radius)
        :y (y-from-polar theta radius)
        :self-differentiation (rand)
        })]
    cow)
  )

(def canvas (dom/get-element "model"))
(def timer (goog.Timer. (/ 1000 20)))

(def cows (doall (take cow-count (repeatedly random-cow))))



(defn sim-cows [cows]
  (doseq [cow cows]
    (let [new-x (+ (:x @cow) (x-from-polar (:angle @cow) (:velocity @cow)))
          new-y (+ (:y @cow) (y-from-polar (:angle @cow) (:velocity @cow)))
          ]
      (swap! cow assoc :x new-x :y new-y))))

(defn init-canvas [canvas]
  (let [ctx (.getContext canvas "2d")
        width (.getAttribute canvas "width")
        height (.getAttribute canvas "height")]
    (do
      (.clearRect ctx 0 0 width height)
      (.beginPath ctx)
      (.arc ctx (/ width 2) (/ height 2) (/ width 2) 0 (* 2 pi) false)
      (.stroke ctx))))

(defn paint-cow [canvas cow]
  (let [ctx (.getContext canvas "2d")
        width (.getAttribute canvas "width")
        height (.getAttribute canvas "height")
        ctx-x (+ (/ width 2) (* (/ width 2) (:x cow)))
        ctx-y (+ (/ height 2) (* (/ height 2) (:y cow)))]
    (do 
      (.beginPath ctx)
      (.fillRect ctx ctx-x ctx-y 5 5))
      (.closePath ctx)))

(defn paint-sim [canvas cows]
  (do 
    (init-canvas canvas)
    (doseq [cow cows]
      (paint-cow canvas @cow))))

(defn cow-sim []
  (do
    (sim-cows cows)
    (paint-sim canvas cows)))

(event/listen timer goog.Timer/TICK cow-sim)
(.start timer)
