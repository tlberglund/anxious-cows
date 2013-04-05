(ns cow
  (:require [clojure.browser.event :as event]
            [clojure.browser.dom :as dom]
            [goog.Timer]))

(def cow-count 50)
(def pi 3.1415926535)

(defn random-cow []
  (let [cow (atom {
    :anxiety 0
    :angle (- (* 2 pi) (rand (* 4 pi)))
    :velocity (rand)
    :x (- 1 (rand 2))
    :y (- 1 (rand 2))
    :self-differentiation (rand)
    })]
    cow)
  )

(def canvas (dom/get-element "model"))
(def timer (goog.Timer. (/ 1000 60)))

(defn init-simulator [count]
  (let [cows (doall (take cow-count (repeatedly random-cow)))]
    
    cows))

(def cows (init-simulator cow-count))

(defn paint-cow [canvas]
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
  (doseq [cow cows]
    (paint-cow canvas @cow)))

(defn cow-sim []
  (paint-sim canvas cows))

(event/listen timer goog.Timer/TICK cow-sim)
(.start timer)
