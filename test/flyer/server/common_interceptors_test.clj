(ns flyer.server.common-interceptors-test
  (:require [flyer.server.common-interceptors :as common-interceptors]
            [io.pedestal.interceptor :as interceptor]
            [matcher-combinators.test]
            [clojure.test :refer [deftest is testing]]))

(deftest csv-file-interceptor
  (let [csv-interceptor (common-interceptors/route-csv-interceptor "some-path")]
    (testing "should return a valid interceptor"
      (is (match? interceptor/interceptor?
                  (interceptor/map->Interceptor csv-interceptor))))
    (testing "interceptor enter function should add file path to context map"
      (is (match? {:file-path string?}
                  ((:enter csv-interceptor) {}))))))
