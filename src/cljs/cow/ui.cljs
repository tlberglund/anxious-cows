(ns cow.ui
  (:use [domina :only [by-id]]
        [cow.model :only [cows]]))

(def canvas (by-id "cow-pen"))

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
        cow-color "#000000"
        cow-size 10]
    (do
      (.log js.console (str ctx-pos))
      (draw-circle ctx ctx-pos cow-size cow-color))))

(defn paint-sim [canvas cows]
  (do 
    (init-canvas canvas)
    (doseq [cow cows]
      (paint-cow canvas @cow))))

(.log js/console (str (map deref cows)))
(paint-sim canvas cows)




