(ns cow.math)

(defn square [x] (* x x))

(defn hypotenuse
  ([x y] (Math/sqrt (apply + (map square [x y]))))
  ([v] (apply hypotenuse v)))
