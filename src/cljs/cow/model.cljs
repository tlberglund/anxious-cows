(ns cow
  (:require [clojure.browser.event :as event]
            [clojure.browser.dom :as dom]
            [goog.Timer]))

(def cow-count 5)
(def max-starting-velocity 0.01)
(def max-turn-degrees 180)


(defn polar-to-rect 
  ([v] (apply polar-to-rect v))
  ([theta radius]
    [(* radius (Math/cos theta)) (* radius (Math/sin theta))]))

(defn square [x] (* x x))

(defn hypotenuse
  ([x y] (Math/sqrt (+ (square x) (square y))))
  ([v] (apply hypotenuse v)))

(defn rect-to-polar
  ([v] (apply rect-to-polar v))
  ([x y]
    [(Math/atan (/ y x)) (hypotenuse [x y])]))

(defn rand-normal [mean stddev]
  (+ mean (* (- (/ (reduce + (repeatedly 10 rand)) 10) 0.5) stddev)))

(defn degrees-to-radians [deg]
  (* (/ deg 180) Math/PI))

(def cow-id (atom 0))
(defn random-cow []
  (let [random-velocity (fn [] (- max-starting-velocity (rand (* 2 max-starting-velocity))))
        ; random-position (fn [] (- 1 (rand 2)))
        random-position (fn [] (rand-normal 0 1))
        cow (atom {
                   :id (swap! cow-id inc)
                   :anxiety 0
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


; (defn perturb-vector [velocity heading]
;   (let [polar (rect-to-polar velocity)
;         perturbed-polar (assoc polar 0 (+ (polar 0) heading))]
;     (polar-to-rect polar)))
(defn perturb-vector [velocity heading]
  (let [polar (rect-to-polar velocity)
        perturbed-polar (assoc polar 0 (+ (polar 0) heading))]
    (polar-to-rect polar)))


(defn random-walk [cow]
  (let [velocity (:velocity cow)
        vx (velocity 0)
        vy (velocity 1)
        random-x (rand-normal vx (* 2 max-starting-velocity))
        random-y (rand-normal vy (* 2 max-starting-velocity))
        heading (Math/atan (/ vy vx))
        wander (* (rand-normal 0 (degrees-to-radians max-turn-degrees)))]
    ; (perturb-vector (:velocity cow) wander)))
    ; (:velocity cow)))
    [random-x random-y]))

(defn new-cow-velocity [cow]
  (let [velocity (:velocity cow)
        pos (:pos cow)
        negate (fn [vec n] (assoc vec n (- (vec n))))]
    (cond 
      (> (Math/abs (pos 0)) 0.99) (negate velocity 0)
      (> (Math/abs (pos 1)) 0.99) (negate velocity 1)
      :else (random-walk cow))))
      ; :else (:velocity cow))))

(defn new-cow-anxiety [cows cow] ())

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

(defn update-cow-debug [element cows]
  (letfn [(render-pos [cow] (:pos cow))
          (render-velocity [cow] (:velocity cow))
          (render-anxiety [cow] (:anxiety cow))
          (render-sd [cow] (:self-differentiation cow))
          (cow-parms [cow] (map #(cow %) [:pos :velocity :anxiety :self-differentiation]))
          (cow-cells [cow] (apply str (map #(dom/element "td" (str %)) (cow-parms cow))))
          (cow-row [cow] (dom/element "tr" (cow-cells cow)))]
      (let [rows (map #(cow-row @%) cows)]
        (dom/append element rows))))

(defn update-params [cows]
  (let [elapsed-time (dom/get-element "elapsed-time")]
    (dom/set-text elapsed-time (js/Date.))))
    ; (update-cow-debug "cow-table-body" cows)))


(defn cow-sim []
  (do
    (sim-cows cows)
    (update-params cows)
    (paint-sim canvas cows)))

(defn build-cow-table []
  (dom/append (dom/get-element "parameters")
    (dom/element "table" {:id "cows"} (dom/element "thead")  )))


 ; (dom/append
 ;      (dom/element "thead"
 ;        (dom/element "td" "Position")
 ;        (dom/element "td" "Velocity")
 ;        (dom/element "td" "Anxiety")
 ;        (dom/element "td" "S-D"))
 ;      (dom/element "tbody"
 ;        (repeatedly cow-count 
 ;          (dom/element "row"))))))

(def timer (goog.Timer. (/ 1000 20)))
(event/listen timer goog.Timer/TICK cow-sim)
(.start timer)
(build-cow-table)
