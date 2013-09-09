(ns cow.math)


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
    [(Math/atan (/ y x)) (hypotenuse x y)]))

(defn rand-normal [mean stddev]
  (+ mean (* (- (/ (reduce + (repeatedly 10 rand)) 10) 0.5) stddev)))

(defn degrees-to-radians [deg]
  (* (/ deg 180) Math/PI))
