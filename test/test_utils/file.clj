(ns test-utils.file
  (:require [clojure.string :as str]
            [clojure.java.io :as io])
  (:import java.util.UUID))

(def test-resources-path "test/test-resources/")

(defn- gen-test-file-path [file-name]
  (str test-resources-path (UUID/randomUUID) "-" file-name))

(defn create-new-test-file!
  [file-name data]
  (let [file-path (gen-test-file-path file-name)]
    (with-open [writer (io/writer file-path :append true)]
      (.write writer (str data))
      file-path)))

(defn read-file [file-path]
  (with-open [reader (io/reader file-path)]
    (slurp reader)))

(defn delete-test-file!
  [test-file-path]
  (-> test-file-path 
      io/file
      io/delete-file))
