(ns flyer.core.file
  (:require [clojure.java.io :as io]
            [clojure.string :as str])
  (:refer-clojure :exclude [read]))

(defn exists? [file-path]
  (.exists (io/file file-path)))

(defn read [file-path]
  (with-open [reader (io/reader file-path)]
    (slurp reader)))

(defn write! [file-path data]
  (with-open [writer (io/writer file-path :append true)]
    (.write writer (str data))))
