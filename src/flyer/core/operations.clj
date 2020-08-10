(ns flyer.core.operations
  (:require [flyer.core.graph :as graph]
            [flyer.core.operations]
            [flyer.core.path-finder :as path-finder]
            [flyer.core.file :as file]))

(defn add-route! [file-path {:keys [origin destination value] :as route}]
  (let [csv-str (path-finder/to-csv-str route)]
    (file/write! file-path csv-str)
    csv-str))

(defn get-best-route [file-path origin destination]
  (-> (file/read file-path)
      (graph/from-csv-string)
      (path-finder/build-best-route origin destination)))
