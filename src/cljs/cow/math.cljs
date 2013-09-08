(ns cow)  

(defn polar-to-rect 
  ([v] (polar-to-rect (v 0) (v 1)))
  ([theta radius]
    [(* radius (Math/cos theta)) (* radius (Math/sin theta))]))

(defn square [x] (* x x))

(defn hypotenuse
  ([x y] (Math/sqrt (+ (square x) (square y))))
  ([v] (apply hypotenuse v)))

(defn rect-to-polar
  ([v]
   [(Math/asin (/ (v 1) (v 0))) (hypotenuse pos)])
  ([x y]
    (rect-to-polar [x y])))

(defn rand-normal [mean stddev]
  (/ (reduce + (repeatedly 10 rand) 10))
