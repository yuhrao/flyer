(ns flyer.core.path-finder-test
  (:require [flyer.core.path-finder :as path-finder]
            [clojure.string :as str]
            [clojure.test :refer [deftest testing is]]
            [matcher-combinators.matchers :as m]
            [matcher-combinators.test :refer [match?]]))

;; See visual graph on doc/test-graphs/linear-graph.png
(deftest linear-graph
  (let [base-sample {:a {:b 10}
                     :b {:c 15}}
        origin :a
        destination :c
        best-path [:a :b :c]
        best-value 25
        result (path-finder/build-best-route base-sample origin destination)]

    (testing "Should build the best route"
      (is (match? best-path
             (first result))))

    (testing "Should contain the final cost"
      (is (match? best-value
             (second result))))))

(deftest non-linear-graph
;; See visual graph on doc/test-graphs/non-linear-graph-A.png
  (testing "Direct route to destination isn't the best"
    (let [base-sample {:a {:b 10 :c 30}
                       :b {:c 15}}
          origin :a
          destination :c
          best-path [:a :b :c]
          best-value 25
          result (path-finder/build-best-route base-sample origin destination)]
      (testing "Should build the best route"
        (is (match? best-path
             (first result))))

    (testing "Should contain the final cost"
      (is (match? best-value
             (second result))))))

;; See visual graph on doc/test-graphs/non-linear-graph-B.png
  (testing "Direct route to destination is the best"
    (let [base-sample {:a {:b 10 :c 15}
                       :b {:c 15}}
          origin :a
          destination :c
          best-path [:a :c]
          best-value 15
          result (path-finder/build-best-route base-sample origin destination)]
      (testing "Should build the best path"
        (is (match? best-path
             (first result))))

    (testing "Should contain the final cost"
      (is (match? best-value
             (second result)))))))

;; See visual graph on doc/test-graphs/unconnected-graph.png
(deftest unconnected-graph
  (let [base-sample {:a {:b 10}
                     :c {:d 15}}
        origin :a
        destination :d
        result (path-finder/build-best-route base-sample origin destination)]

    (testing "Best route shoudn't exist"
      (is (nil? result)))))

(deftest route-to-csv
  (let [input-route {:origin "GRU" :destination "SDU" :value 20.40}]
    (testing "should produce a valid csv line"
      (let [new-line (path-finder/to-csv-str {:new-line? false} input-route)]
        (is (match? "GRU,SDU,20.4"
                    new-line))))
    (testing "should produce a valid csv line whith a line break at the beginning"
      (let [new-line (path-finder/to-csv-str input-route)]
        (is (match? "\nGRU,SDU,20.4"
                    new-line))))))

(deftest trace-route
  (testing "Non linear graph"
    ;; See visual graph on doc/test-graphs/non-linear-graph-A.png
    (testing "Direct route to destination isn't the best"
      (let [graph {:a {:b 10 :c 30}
                   :b {:c 15}}
            origin :a
            destination :c
            best-path [:a :b :c]
            best-value 25
            result (path-finder/trace graph origin destination)]
        (testing "Should build the best route"
          (is (match? {:path best-path}
                      result)))

        (testing "Should contain the final cost"
          (is (match? {:cost best-value}
                      result)))))

    ;; See visual graph on doc/test-graphs/non-linear-graph-B.png
    (testing "Direct route to destination is the best"
      (let [graph {:a {:b 10 :c 15}
                   :b {:c 15}}
            origin :a
            destination :c
            best-path [:a :c]
            best-value 15
            result (path-finder/trace graph origin destination)]
        (testing "Should build the best route"
          (is (match? {:path best-path}
                      result)))

        (testing "Should contain the final cost"
          (is (match? {:cost best-value}
                      result))))))

  (testing "Linear graph"
    (let [graph {:a {:b 10}
                 :b {:c 15}}
          origin :a
          destination :c
          best-path [:a :b :c]
          best-value 25
          result (path-finder/trace graph origin destination)]

      (testing "Should build the best route"
          (is (match? {:path best-path}
                      result)))

        (testing "Should contain the final cost"
          (is (match? {:cost best-value}
                      result)))))


  (testing "Unconnected nodes"
    (let [graph {:a {:b 10}
                 :c {:d 15}}
          origin :a
          destination :d
          result (path-finder/trace graph origin destination)]

      (testing "Best route shoudn't exist"
        (is (nil? result))))))
