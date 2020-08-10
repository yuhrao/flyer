(ns flyer.console.io
  (:require [clojure.string :as str]
            [flyer.core.operations :as operations]))

(defn ask [question]
  (print question) 
  (flush) 
  (read-line))

(defn valid-answer? [answer]
  (->> (str/upper-case answer)
       (re-matches #"(\w{3})[-](\w{3})")
       boolean))

(defn to-result-route-str
  "Generate the result route in format '<path> R$<value>'"
  [[path value]]
  (let [path-str (->> path
                      (map (comp str/upper-case name))
                      (str/join  " - "))]
    (format "%s > $%.2f" path-str value))) 

(defn print-result [routing-result]
  (print "Best route: " (to-result-route-str routing-result)))
