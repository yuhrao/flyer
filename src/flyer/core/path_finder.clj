(ns flyer.core.path-finder
  (:require [clojure.string :as str]
            [flyer.core.dijkstra :as dj]))

(defn- ^:deprecated dijkstra-out->route [preds origin destination]
  "Reverse walk on preds to reconstruct the shortest path"
  (loop [[pred] (preds destination) path (list destination)]
    (if (nil? pred)
      nil
      (if (= pred origin)
        (cons origin path)
        (recur (preds pred) (cons pred path))))))

(defn ^:deprecated build-best-route
  ([route-map origin destination]
   (let [preds (dj/dijkstra route-map origin destination)
         path (dijkstra-out->route preds origin destination)]
     (if (nil? path)
       nil
       [(into [] path) (second (preds destination))]))))

(defn ^:deprecated to-csv-str
  ([route]
   (to-csv-str {:new-line? true} route))
  ([{:keys [new-line?] :as opts} {:keys [origin destination value] :as route}]
   (let [new-line-vec [origin destination value]
         new-line-str (str/join "," new-line-vec)]
     (if new-line?
       (str "\n" new-line-str)
       new-line-str))))

(defn- build-path [preds origin destination]
  "Reverse walk on preds to reconstruct the shortest path"
  (loop [[pred dist] (preds destination) path (list destination)]
    (if (nil? pred)
      nil
      (if (= pred origin)
        (cons origin path)
        (recur (preds pred) (cons pred path))))))

(defn trace [graph origin destination]
  (let [preds (dj/dijkstra graph origin destination)
        path (-> preds
                 (build-path origin destination))
        cost (-> (preds destination)
                  second)]
    (when path
      {:path (into [] path)
       :cost cost})))
