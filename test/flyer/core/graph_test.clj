(ns flyer.core.graph-test
  (:require [flyer.core.graph :as graph]
            [clojure.test :refer [deftest testing is]]
            [matcher-combinators.test]))

(defn- extract-neighbor-values [data]
  (->> data
      vals
      (mapcat vals)))

(deftest from-valid-csv-string
  (let [sample " A,B,10\nB,C,12\nA,C,15"
        result (graph/from-csv-string sample)]
    (testing "Should return a map"
      (is (match? map? result)))
    (testing "Should be a map of maps"
      (is (every? map? (vals result))))
    (testing "Each neighbor should have a float value"
      (is (every? float? (extract-neighbor-values result))))
    (testing "Every node should have at least one neighbor"
      (is (every? not-empty (vals result))))))
