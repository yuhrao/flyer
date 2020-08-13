(ns flyer.console.main
  (:require [flyer.core.operations :as operations]
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
        result (operations/get-best-route file-path origin destination)]
    (if-not (nil? result)
      (console/print-result result)
      (console/print-not-found origin destination))))

(defn loop-requests [file-path]
  (while true
    (request-route file-path)))
