(ns flyer.file.csv
  (:require [flyer.file.core :as file]
            [clojure.string :as str]))

;; TODO: tests
(def ^:private normalize-key (comp keyword str/lower-case))
(def ^:private normalize-cost (comp float read-string))

;; TODO: tests
(defn- assoc-route [m origin destination value]
  (let [v (float value)]
    (if (contains? m origin)
      (assoc-in m [origin destination] v)
      (-> (assoc m origin {})
          (assoc-in [origin destination] v)))))

;; TODO: tests
(defn to-graph [file-path]
  (->> (file/read file-path)
       str/split-lines
       (map #(str/split % #","))
       (reduce (fn [route-map [origin destination cost]]
                 (-> route-map
                     (assoc-route (normalize-key  origin)
                                  (normalize-key  destination)
                                  (normalize-cost cost)))) {})))

;; TODO: tests
(defn- intersection->csv-newline
  [{:keys [origin destination cost] :as route}]
  (let [new-line-str (->> [origin destination cost]
                          (str/join ","))]
    (str "\n" new-line-str)))

;; TODO: tests
(defn add-intersection! [file-path {:keys [origin destination cost] :as intersection}]
  (->> intersection
       intersection->csv-newline
       (file/write! file-path)))
