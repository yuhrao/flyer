(ns flyer.core.graph
  (:require [clojure.string :as str]))

(defn- assoc-route [origin destination value data]
  (let [v (float value)]
    (if (contains? data origin)
      (assoc-in data [origin destination] v)
      (-> (assoc data origin {})
          (assoc-in [origin destination] v)))))

(defn from-csv-string [csv-str]
  (->> (str/split-lines csv-str)
       (map #(str/split % #","))
       (reduce (fn [route-map [origin destination value]]
                 (->> route-map
                      (assoc-route (keyword (str/lower-case origin))
                                   (keyword (str/lower-case destination))
                                   (read-string value)))) {})))
