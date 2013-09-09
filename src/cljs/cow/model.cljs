(ns cow.model
  (:require [clojure.browser.event :as event]
            [clojure.browser.dom :as dom]
            [goog.Timer]
            [crate.core :as crate]
            [cow.math :as math])
  (:use [domina :only [by-id set-html! set-text! set-value! set-style!]]))

(def cow-count 10)
(def max-starting-velocity 0.01)
(def max-turn-degrees 180)


(def cow-id (atom 0))
(defn random-cow []
  (let [random-velocity (fn [] (- max-starting-velocity (rand (* 2 max-starting-velocity))))
        random-position (fn [] (math/rand-normal 0 1))
        cow (atom {
                   :id (swap! cow-id inc)
                   :anxiety (rand)
                   :velocity [(random-velocity) (random-velocity)]
                   :pos [(random-position) (random-position)]
                   :self-differentiation (rand)
                   })]
    cow))

(def canvas (dom/get-element "model"))
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


(defn perturb-vector [velocity heading]
  (let [polar (math/rect-to-polar velocity)
        perturbed-polar (assoc polar 0 (+ (polar 0) heading))]
    ; (.log js/console (str (polar-to-rect polar) " | " velocity))
    ; Disable random walking for now
    ; (polar-to-rect perturbed-polar)))
    velocity))


(defn random-walk [cow]
  (let [velocity (:velocity cow)
        vx (velocity 0)
        vy (velocity 1)
        random-x (math/rand-normal vx (* 2 max-starting-velocity))
        random-y (math/rand-normal vy (* 2 max-starting-velocity))
        heading (Math/atan (/ vy vx))
        wander (* (math/rand-normal 0 (math/degrees-to-radians max-turn-degrees)))]
    (perturb-vector (:velocity cow) wander)))

(defn new-cow-velocity [cow]
  (let [velocity (:velocity cow)
        pos (:pos cow)
        negate (fn [vec n] (assoc vec n (- (vec n))))]
    (cond 
      (> (Math/abs (pos 0)) 0.97) (negate velocity 0)
      (> (Math/abs (pos 1)) 0.97) (negate velocity 1)
      :else (random-walk cow))))

(defn new-cow-anxiety [cows cow] 
  (* (:anxiety cow) 0.99))

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
  ([ctx position width height fill-style]
     (let [half-width (/ width 2)
           half-height (/ height 2)
           upper-left (- (position 0) half-width)
           upper-right (- (position 1) half-height)]
       (do 
         (.beginPath ctx)
         (set! (. ctx -fillStyle) fill-style)
         (.fillRect ctx upper-left upper-right width height)
         (.closePath ctx))))
  ([ctx position width fill-style]
     (draw-box ctx position width width fill-style))
  ([ctx position fill-style]
     (draw-box ctx position 5 fill-style)))

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
    (draw-circle ctx ctx-pos cow-size cow-color)))

(defn paint-sim [canvas cows]
  (do 
    (init-canvas canvas)
    (doseq [cow cows]
      (paint-cow canvas @cow))))

(defn cow-table [cows]
  (let [row-count (atom 0)
        format-num (fn [num] (format "%1.04f" num))
        format-pair (fn [vec] (str "(" (apply str (interpose ", " (map format-num vec))) ")"))
        render-pos (fn [cow] (format-pair (:pos cow)))
        render-velocity (fn [cow] (format-pair (:velocity cow)))
        render-anxiety (fn [cow] (format-num (:anxiety cow)))
        render-sd (fn [cow] (format-num (:self-differentiation cow)))
        td-id (fn [prefix] (str prefix @row-count))
    ]
    (crate/html 
      [:table#cows {:width "100%"}
        [:thead 
          [:tr [:th "Cow"] [:th "Position"] [:th "Velocity"] [:th "Anxiety"] [:th "SD"]]
        ]
        [:tbody 
          (map
            #(crate/html [:tr {:id (str "cow-row-" (swap! row-count inc))}
              [:td (str (:id %))]
              [:td {:id (td-id "cow-position-")} (render-pos %)]
              [:td {:id (td-id "cow-velocity-")} (render-velocity %)]
              [:td {:id (td-id "cow-anxiety-")} (render-anxiety %)]
              [:td {:id (td-id "cow-sd-")} (render-sd %)]])
            (map deref cows))
        ]
      ])))

(defn update-params [cows]
  (set-text! (by-id "elapsed-time") (js/Date.))
  (set-html! (by-id "cow-table") (cow-table cows)))


(defn cow-sim []
  (do
    (sim-cows cows)
    (update-params cows)
    (paint-sim canvas cows)))
              
(def timer (goog.Timer. (/ 1000 20)))
(event/listen (by-id "run-sim") goog.events.EventType.CLICK #(.start timer))
(event/listen (by-id "stop-sim") goog.events.EventType.CLICK #(.stop timer))
(event/listen (by-id "step-sim") goog.events.EventType.CLICK #(cow-sim))
(event/listen timer goog.Timer/TICK cow-sim)
(update-params cows)
(paint-sim canvas cows)
; (.start timer)

