(ns flyer.core.operations-test
  (:require [flyer.core.operations :as operations]
            [test-utils.file :as tu-file]
            [clojure.test :refer [deftest testing is]]
            [matcher-combinators.test]
            [matcher-combinators.matchers :as m]
            [clojure.string :as str]))

(deftest add-route
  (let [file-content "A,B,10.3\nB,C,12\nA,C,15"
        test-file-path (tu-file/create-new-test-file! "intput.txt" file-content)
        route-sample {:origin "A" :destination "B" :value 12.2}]
    (testing "should create a new route in a existent archive"
      (let [csv-line (operations/add-route! test-file-path route-sample)]
        (is (str/includes? (tu-file/read-file test-file-path) csv-line))))
    (tu-file/delete-test-file! test-file-path)))

(deftest get-best-route
  (let [sample-path "test/test-resources/input-sample.txt"
        expected-result [[:gru :brc :scl :orl :cdg] 40.0]
        origin :gru
        destination :cdg
        router-result (operations/get-best-route sample-path origin destination)]
    (testing "Should return a vector with a path sequence and a value"
      (is (match? (m/equals [vector? number?]) router-result)))
    (testing "Result path should be a sequence of keywords"
      (is (every? keyword? (first router-result))))
    (testing "Should return the best route"
      (is (match? (m/equals expected-result) router-result)))))
