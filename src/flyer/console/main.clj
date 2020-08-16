(ns flyer.console.main
  (:require [flyer.core.path-finder :as path-finder]
            [flyer.file.csv :as csv]
            [flyer.console.io :as console]
            [clojure.string :as str]))

(defn- process-answer [answer]
  (->> (str/split answer #"-")
       (map str/lower-case)
       (map keyword)))

(defn ask-for-route []
  (process-answer (console/ask "Please enter the route: ")))

(defn request-route [file-path]
  (let [[origin destination] (ask-for-route)
        graph (csv/to-graph file-path)
        result (path-finder/trace graph origin destination)]
    (if-not (nil? result)
      (console/print-result result)
      (console/print-not-found origin destination))))

(defn loop-requests [file-path]
  (while true
    (request-route file-path)))
