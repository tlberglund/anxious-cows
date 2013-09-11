(ns cow.ui
  (:require [crate.core :as crate])
  (:use [domina :only [by-id set-html!]]
        [cow.model :only [cows]]))

(def canvas (by-id "cow-pen"))


(defn cow-table [cows]
  (let [row-count (atom 0)
        format-num (fn [num] (format "%1.04f" num))
        format-pair (fn [vec] (str "(" (apply str (interpose ", " (map format-num vec))) ")"))
        render-pos (fn [cow] (format-pair (:pos cow)))
        td-id (fn [prefix] (str prefix @row-count))
       ]
    (crate/html 
      [:table#cows {:width "100%"}
        [:thead 
          [:tr [:th "Cow"] [:th "Position"]]
        ]
        [:tbody 
          (map
            #(crate/html [:tr {:id (str "cow-row-" (swap! row-count inc))}
              [:td (str (:id %))]
              [:td {:id (td-id "cow-position-")} (render-pos %)]
              ])
            (map deref cows))
        ]
      ])))

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
      (draw-circle ctx ctx-pos cow-size cow-color))))

(defn paint-sim [canvas cows]
  (do 
    (init-canvas canvas)
    (doseq [cow cows]
      (paint-cow canvas @cow))))

(paint-sim canvas cows)
(set-html! (by-id "cow-table") (cow-table cows))



