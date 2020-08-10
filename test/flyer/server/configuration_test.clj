(ns flyer.server.configuration-test
  (:require [flyer.server.configuration :as server-config]
            [io.pedestal.http :as server]
            [reitit.http.interceptors.muuntaja :as muuntaja-interceptor]
            [reitit.core :as reitit]
            [matcher-combinators.test]
            [clojure.test :refer [deftest testing is]]
            [io.pedestal.interceptor :as interceptor]))


(deftest create-router
  (testing "Router without routes"
    (let [router (server-config/create-router [])]
      (testing "Should return a valid router"
        (is (match? :reitit.core/router
                    (type router))))
      (testing "Shoould contain swagger route"
        (is (match? reitit.core.Match
                    (-> router
                        (reitit/match-by-name :swagger/json)
                        type))))))
  (testing "Router with routes"
    (let [router (server-config/create-router [["/route" ::route]])]
      (testing "Should return a valid router"
        (is (match? :reitit.core/router
                    (type router))))
      (testing "Shoould contain swagger route"
        (is (match? reitit.core.Match
                    (-> router
                        (reitit/match-by-name :swagger/json)
                        type))))
      (testing "Shoould contain swagger route"
        (is (match? reitit.core.Match
                    (-> router
                        (reitit/match-by-name ::route)
                        type)))))))

(deftest router-interceptor
  (testing "Router interceptor without routes"
    (let [router-interceptor (server-config/router-interceptor {:routes []})]
      (testing "Should return a interceptor"
        (is (match? interceptor/interceptor?
                    router-interceptor)))))
  (testing "Router interceptor without routes"
    (let [router-interceptor (server-config/router-interceptor {:routes [["/route" ::route]]})]
      (testing "Should return a interceptor"
        (is (match? interceptor/interceptor?
                   router-interceptor))))))

(deftest create-server-map
  (let [server-routes [["/hello" {:get {:handler (constantly true)}}]]
        server-port 1010
        server-map (server-config/create-server-map {:port server-port :routes server-routes})]
    (testing "Should create a valid service map"
      (is (match? map? server-map)))
    (testing "Server map should have the configured port"
      (is (match? server-port
                  (::server/port server-map))))
    (testing "Server map should contain the router interceptor as last interceptor"
      (is (match? :reitit.http/router
                  (->> server-map
                       ::server/interceptors
                       last
                       :name))))))
