(ns flyer.core.path-finder-test
  (:require [flyer.core.path-finder :as path-finder]
            [clojure.string :as str]
            [clojure.test :refer [deftest testing is]]
            [matcher-combinators.matchers :as m]
            [matcher-combinators.test]))

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

(deftest produce-result-route-string
  (let [routing-path [:gru :brc :scl :orl :cdg]
        routing-value 30.0
        routing-result [routing-path routing-value]
        result (path-finder/to-result-route-str routing-result)]
    (testing "Should produce a normalized result string"
      (let [[path-str value-str] (str/split result #" > ")
            value (-> value-str
                      (str/replace #"\$" "")
                      read-string
                      float)
            path (->> (str/split path-str #" - ")
                      (map str/lower-case)
                      (map keyword)
                      (into []))]
        

        (is (match? (m/equals [path value])
                    routing-result))))))
