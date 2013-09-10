(ns cow.math)


(defn polar-to-rect 
  ([v] (apply polar-to-rect v))
  ([theta radius]
    [(* radius (Math/cos theta)) (* radius (Math/sin theta))]))

(defn square [x] (* x x))

(defn hypotenuse
  ([x y] (Math/sqrt (apply + (map square [x y]))))
  ([v] (apply hypotenuse v)))

(defn rect-to-polar
  ([v] (apply rect-to-polar v))
  ([x y]
    [(Math/atan (/ y x)) (hypotenuse x y)]))

(defn rand-normal 
  "This algorithm is not quite honest yet, but it gets the idea across."
  [mean stddev]
  (+ mean (* (- (/ (reduce + (repeatedly 10 rand)) 10) 0.5) stddev)))

(defn degrees-to-radians [deg]
  (* (/ deg 180) Math/PI))

(defn average [& v]
  (let [num (count v)]
    (/ (reduce + v) num)))
