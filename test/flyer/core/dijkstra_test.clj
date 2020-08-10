(ns flyer.core.dijkstra-test
  (:require [flyer.core.dijkstra :as dj]
            [clojure.test :refer [deftest testing is]]
            [matcher-combinators.test]))

(def route-map {:gru {:brc 10, :cdg 75, :scl 20, :orl 56},
                :brc {:scl 5},
                :orl {:cdg 5},
                :scl {:orl 20}})

;; See visual graph on doc/test-graphs/linear-graph.png
(deftest linear-graph
  (let [base-sample {:a {:b 10}
                     :b {:c 15}}
        origin :a
        destination :c
        intermediate :b
        result (dj/dijkstra base-sample origin destination)]

    (testing "Should contain all nodes"
      (is (match? #{:a :b :c}
                  (-> result
                      keys
                      set))))

    (testing "Result should contain intermediate node"
      (is (contains? result intermediate)))

    (testing "Intermediate should contain it's cost"
      (is (match? 10
             (-> result
                 intermediate
                 second))))

    (testing "Destination key should contain the final cost"
      (is (match? 25
             (-> result
                 destination
                 second))))))

(deftest non-linear-graph
;; See visual graph on doc/test-graphs/non-linear-graph-A.png
  (testing "Direct route to destination isn't the best"
    (let [base-sample {:a {:b 10 :c 30}
                       :b {:c 15}}
          origin :a
          destination :c
          intermediate :b
          result (dj/dijkstra base-sample origin destination)]
      (testing "Should contain all nodes"
        (is (match? #{:a :b :c}
               (-> result
                   keys
                   set))))

      (testing "Result should contain intermediate node"
        (is (match? origin
               (-> result
                   intermediate
                   first)))
        (is (match? intermediate
               (-> result
                   destination
                   first))))

      (testing "Destination key should contain the final cost"
        (is (match? 25
               (-> result
                   destination
                   second))))))

;; See visual graph on doc/test-graphs/non-linear-graph-B.png
  (testing "Direct route to destination is the best"
    (let [base-sample {:a {:b 10 :c 15}
                       :b {:c 15}}
          origin :a
          destination :c
          result (dj/dijkstra base-sample origin destination)]

      (testing "Should contain all nodes"
        (is (match? #{:a :b :c}
               (-> result
                   keys
                   set))))

      (testing "Route should have no intermediates"
        (is (match? origin
               (-> result
                   destination
                   first))))

      (testing "Destination key should contain the final cost"
        (is (match? 15
               (-> result
                   destination
                   second)))))))

;; See visual graph on doc/test-graphs/unconnected-graph.png
(deftest unconnected-graph
  (let [base-sample {:a {:b 10}
                     :c {:d 15}}
        origin :a
        destination :d
        result (dj/dijkstra base-sample origin destination)]

    (testing "Shouldn't have destination node"
      (is (not (contains? result destination))))))
