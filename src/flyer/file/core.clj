(ns flyer.file.core
  (:refer-clojure :exclude [read])
  (:require [clojure.java.io :as io]
            [clojure.string :as str])
  (:import java.io.FileNotFoundException))

(defn exists? [file-path]
  (.exists (io/file file-path)))

(defn read [file-path]
  (with-open [reader (io/reader file-path)]
    (slurp reader)))

(defn write! [file-path data]
  (if (exists? file-path)
    (with-open [writer (io/writer file-path :append true)]
      (.write writer (str data)))
    (throw (FileNotFoundException.))))
