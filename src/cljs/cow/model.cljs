(ns cow
  (:require [clojure.browser.event :as event]
            [clojure.browser.dom :as dom]
            [goog.Timer]))

(def cow-count 20)

(defn polar-to-rect [theta radius]
  [(* radius (Math/cos theta)) (* radius (Math/sin theta))])

(defn square [x] (* x x))

(defn hypotenuse 
  ([x y] (Math/sqrt (+ (square x) (square y))))
  ([v] (apply hypotenuse v)))

(defn random-cow []
  (let [theta (- (* 2 Math/PI) (rand (* 4 Math/PI)))
        radius (rand)
        cow (atom {
        :anxiety 0
        :angle (- (* 2 Math/PI) (rand (* 4 Math/PI)))
        :velocity (rand 0.01)
        :pos (polar-to-rect theta radius)
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

(defn hit-fence? [cow]
  (let [cow-radius (hypotenuse (:pos cow))]
    (>= cow-radius 1)))

(comment ball.angle = 2 * math.atan2(dy, dx) - ball.angle)
(defn incident-angle [cow]
  (- (* 2 (Math/atan2 (:y cow) (:x cow))) (:angle cow)))

(defn new-cow-angle [cow cows]
  (if (hit-fence? cow)
    (incident-angle cow)
    (:angle cow)))

(defn new-cow-velocity [cow cows]
  (:velocity cow))

(defn sim-cows [cows]
  (doseq [cow-atom cows]
    (let [cow @cow-atom
          new-angle (new-cow-angle cow cows)
          new-velocity (new-cow-velocity cow cows)
          delta-pos (polar-to-rect new-angle new-velocity)
          new-pos (vec (map + delta-pos (:pos cow)))]
      (swap! cow-atom assoc :pos new-pos 
                            :angle new-angle 
                            :velocity new-velocity))))

(defn cow-to-canvas-coord [canvas-dim cow-coord]
  (let [dimension (/ canvas-dim 2)]
    (+ dimension (* dimension cow-coord))))

(defn draw-box [ctx position]
  (do 
    (.beginPath ctx)
    (.fillRect ctx (- (position 0) 2) (- (position 1) 2) 5 5)
    (.closePath ctx)))

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

(defn cow-sim []
  (do
    (sim-cows cows)
    (paint-sim canvas cows)))

(event/listen timer goog.Timer/TICK cow-sim)
(.start timer)

