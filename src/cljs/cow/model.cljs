(ns cow
  (:require [clojure.browser.event :as event]
            [clojure.browser.dom :as dom]
            [goog.Timer]))

(def cow-count 20)

(defn polar-to-rect [theta radius]
  [(* radius (Math/cos theta)) (* radius (Math/sin theta))])

(defn square [x] (* x x))

(defn hypotenuse [x y] 
  (sqrt (+ (square x) (square y))))

(defn random-cow []
  (let [theta (- (* 2 Math/PI) (rand (* 4 Math/PI)))
        radius (rand)
        cow (atom {
        :anxiety 0
        :angle (- (* 2 Math/PI) (rand (* 4 Math/PI)))
        :velocity (rand 0.01)
        :x ((polar-to-rect theta radius) 0)
        :y ((polar-to-rect theta radius) 1)
        :self-differentiation (rand)
        })]
    cow)
  )

(def canvas (dom/get-element "model"))
(def timer (goog.Timer. (/ 1000 20)))
(def cows (doall (take cow-count (repeatedly random-cow))))

(defn init-canvas [canvas]
  (let [ctx (.getContext canvas "2d")
        width (.getAttribute canvas "width")
        height (.getAttribute canvas "height")]
    (do
      (.clearRect ctx 0 0 width height)
      (.beginPath ctx)
      (.arc ctx (/ width 2) (/ height 2) (/ width 2) 0 (* 2 Math/PI) false)
      (.stroke ctx))))

(defn hit-fence? [canvas cow]
  (let [width (.getAttribute canvas "width")
        fence-radius (/ width 2)
        cow-radius (hypotenuse (:x cow) (:y cow))]
    (> cow-radius fence-radius)))

(def incident-angle [canvas cow]

  )

(defn sim-cows [cows]
  (doseq [cow cows]
    (let [new-x (+ (:x @cow) ((polar-to-rect (:angle @cow) (:velocity @cow)) 0))
          new-y (+ (:y @cow) ((polar-to-rect (:angle @cow) (:velocity @cow)) 1))]
      (swap! cow assoc :x new-x :y new-y))))

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
(.log js/console cows)