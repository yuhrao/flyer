(ns flyer.console.io
  (:require [clojure.string :as str]
            [flyer.core.operations :as operations]))

(defn ask [question]
  (print question) 
  (flush) 
  (read-line))

(defn to-result-route-str
  "Generate the result route in format '<path> R$<value>'"
  [[path value]]
  (let [path-str (->> path
                      (map (comp str/upper-case name))
                      (str/join  " - "))]
    (format "%s > $%.2f" path-str value))) 

(defn print-result [routing-result]
  (println "Best route:" (to-result-route-str routing-result)))

(defn print-not-found [& targets]
  (->> targets
       (map name)
       (map str/upper-case)
       (str/join "-")
       (println "No routes found for")))
