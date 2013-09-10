(ns cow.ui
  (:require [clojure.browser.event :as event]
            [clojure.browser.dom :as dom]
            [goog.Timer]
            [crate.core :as crate])
  (:use [domina :only [by-id set-html! set-text! set-value! set-style!]]
        [cow.model :only [cows sim-cows hit-fence? anxiety-near-me]]))

(def canvas (by-id "model"))


(defn cow-table [cows]
  (let [row-count (atom 0)
        format-num (fn [num] (format "%1.04f" num))
        format-pair (fn [vec] (str "(" (apply str (interpose ", " (map format-num vec))) ")"))
        render-pos (fn [cow] (format-pair (:pos cow)))
        render-velocity (fn [cow] (format-pair (:velocity cow)))
        render-anxiety (fn [cow] (format-num (:anxiety cow)))
        render-sd (fn [cow] (format "%3d" (* 100 (:self-differentiation cow))))
        td-id (fn [prefix] (str prefix @row-count))
    ]
    (crate/html 
      [:table#cows {:width "100%"}
        [:thead 
          [:tr [:th "Cow"] [:th "Position"] [:th "Velocity"] [:th "Anxiety"] [:th "SD"] [:th "Debug"]]
        ]
        [:tbody 
          (map
            #(crate/html [:tr {:id (str "cow-row-" (swap! row-count inc))}
              [:td (str (:id %))]
              [:td {:id (td-id "cow-position-")} (render-pos %)]
              [:td {:id (td-id "cow-velocity-")} (render-velocity %)]
              [:td {:id (td-id "cow-anxiety-")} (render-anxiety %)]
              [:td {:id (td-id "cow-sd-")} (render-sd %)]
              [:td]
              ])
            (map deref cows))
        ]
      ])))

(defn update-params [cows]
  (set-text! (by-id "elapsed-time") (js/Date.))
  ; (set-html! (by-id "cow-table") (cow-table cows))
  )


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
        cow-color (str "rgb(" (format "%d" (int (* 256 (:anxiety cow)))) ",0,0)")
        cow-size (int (+ 5 (* 15 (:anxiety cow))))]
    ; (draw-box ctx ctx-pos cow-size cow-color)))
    (do
      (draw-circle ctx ctx-pos cow-size cow-color)
      ; (set! (. ctx -fillStyle) "#303030")
      ; (set! (. ctx -font) "8pt Arial")
      ; (.fillText ctx (str (:id cow)) (+ (ctx-pos 0) 10) (+ (ctx-pos 1) 4))
      )))

(defn paint-sim [canvas cows]
  (do 
    (init-canvas canvas)
    (doseq [cow cows]
      (paint-cow canvas @cow))))


(def frame-count (atom 0))

(defn cow-sim []
  (do
    (swap! frame-count inc)
    (sim-cows cows)
    (update-params cows)
    (paint-sim canvas cows)))

(defn update-fps []
  (set-text! (by-id "fps") (str @frame-count))
  (swap! frame-count (fn [] 0)))

(def sim-timer (goog.Timer. (/ 1000 20)))
(def fps-timer (goog.Timer. 1000))
(event/listen (by-id "run-sim") goog.events.EventType.CLICK #(.start sim-timer))
(event/listen (by-id "stop-sim") goog.events.EventType.CLICK #(.stop sim-timer))
(event/listen (by-id "step-sim") goog.events.EventType.CLICK #(cow-sim))
(event/listen sim-timer goog.Timer/TICK cow-sim)
(event/listen fps-timer goog.Timer/TICK update-fps)
(update-params cows)
(paint-sim canvas cows)
(.start fps-timer)
; (.start sim-timer)



