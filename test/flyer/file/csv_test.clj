(ns flyer.file.csv-test
  (:require [flyer.file.csv :as csv]
            [test-utils.file :as tu-file]
            [matcher-combinators.test :refer [match?]]
            [clojure.test :refer [deftest testing is]]
            [clojure.string :as str]))

(deftest parse-to-graph
  (testing "Should produce a valid graph"
    (let [file-content "A,B,12\nB,C,10\n"
          file-path (tu-file/create-new-test-file! file-content)
          expected-result {:a {:b 12.0}
                           :b {:c 10.0}}]
      (is (match? expected-result
                  (csv/to-graph file-path)))
      (tu-file/delete-test-file! file-path))))

(deftest add-intersection-to-csv
  (testing "Should write route to csv"
    (let [file-content "A,B,12\nB,C,10\n"
          file-path (tu-file/create-new-test-file! file-content)
          new-intersection {:origin "A",
                            :destination "B"
                            :value 12}
          intersection-csv "A,B,12"]
      (csv/add-intersection! file-path new-intersection)
      (is (str/includes?
           (tu-file/read-file file-path)
           intersection-csv))
      (tu-file/delete-test-file! file-path))))
