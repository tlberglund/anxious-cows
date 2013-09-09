(ns cow.ui
  (:require [clojure.browser.event :as event]
            [clojure.browser.dom :as dom]
            [goog.Timer]
            [crate.core :as crate])
  (:use [domina :only [by-id set-html! set-text! set-value! set-style!]]
        [cow.model :only [cows paint-sim sim-cows]]))

(def canvas (by-id "model"))


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



