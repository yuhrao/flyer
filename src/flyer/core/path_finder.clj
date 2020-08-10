(ns flyer.core.path-finder
  (:require [clojure.string :as str]
            [flyer.core.dijkstra :as dj]))

(defn- dijkstra-out->route [preds origin destination]
  "Reverse walk on preds to reconstruct the shortest path"
  (loop [[pred dist] (preds destination) path (list destination)]
    (if (nil? pred)
      nil
      (if (= pred origin)
        (cons origin path)
        (recur (preds pred) (cons pred path))))))

(defn build-best-route
  ([route-map origin destination]
   (let [preds (dj/dijkstra route-map origin destination)
         path (dijkstra-out->route preds origin destination)]
     (if (nil? path)
       nil
       [(into [] path) (second (preds destination))]))))

(defn to-csv-str
  ([route]
   (to-csv-str {:new-line? true} route))
  ([{:keys [new-line?] :as opts} {:keys [origin destination value] :as route}]
   (let [new-line-vec [origin destination value]
         new-line-str (str/join "," new-line-vec)]
     (if new-line?
       (str "\n" new-line-str)
       new-line-str))))

(defn to-result-route-str
  "Generate the result route in format '<path> R$<value>'"
  [[path value]]
  (let [path-str (->> path
                      (map (comp str/upper-case name))
                      (str/join  " - "))]
    (format "%s > $%.2f" path-str value))) 
