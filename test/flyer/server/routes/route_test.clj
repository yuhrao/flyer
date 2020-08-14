(ns flyer.server.routes.route-test
  (:require [flyer.server.routes.route :as route-routes]
            [cheshire.core :as json]
            [flyer.server.main :as flyer-server]
            [reitit.core :as reitit]
            [matcher-combinators.test :refer [match? thrown-match?]]
            [clojure.test :refer [deftest testing is]]
            [clj-http.client :as client-http]
            [test-utils.file :as tu-file]))

(def router (reitit/router route-routes/routes))

(deftest containing-routes
    (testing "Should contain methods for path /route"
      (is (match? #{:post :get}
                  (-> (reitit/match-by-path router "/route")
                      :data
                      keys
                      set)))))

(deftest add-routes
  (let [server-atom (atom nil)
        server-port 3000
        file-path   (tu-file/create-new-test-file! "add-routes" "")
        req-url     (str "http://localhost:" server-port "/route")]
    (try 
      (flyer-server/start! {:port      server-port :routes [route-routes/routes]
                            :file-path file-path} server-atom)

      (let [response (client-http/post req-url
                                       {:body    (json/encode {:origin      "A"
                                                               :destination "B"
                                                               :cost        12})
                                        :headers {"Content-Type" "application/json"}
                                        :accept  :json})]
        (testing "Response should return status 'created'"
          (is (match?  201
                      (:status response))))
        (testing "Response shouldn't return a body"
          (is (match? empty?
                      (:body response))))

        (testing "Should persist route in the file"
          (is (match? "\nA,B,12"
                      (tu-file/read-file file-path)))))
      (finally 
        (tu-file/delete-test-file! file-path)
        (flyer-server/stop! server-atom)))))

(deftest get-best-routes
  (let [server-atom (atom nil)
        server-port 3000
        file-path (tu-file/create-new-test-file! "add-routes" "A,B,12")
        req-path (-> router
                     (reitit/match-by-name ::route-routes/add)
                     reitit/match->path)
        req-url (str "http://localhost:" server-port "/route")]
    (try 
      (flyer-server/start! {:port server-port :routes [route-routes/routes]
                            :file-path file-path} server-atom)

      (testing "Valid route request"
        (let [response (client-http/get req-url
                                        {:query-params {"origin" "A"
                                                        "destination" "B"}
                                         :accept :json})
              response-body (json/decode (:body response) true)]
          (testing "Response should return status 'success'"
            (is (match?  200
                         (:status response))))
          (testing "Response should return path and value"
            (is (match? {:path vector? :value number?}
                        response-body)))

          (testing "Responde should return right values"
            (is (match? ["A" "B"]
                        (:path response-body)))
            (is (match? 12.0
                        (:value response-body))))))

      (testing "Valid route request"
        (testing "Response should return status 'not found'"
          (is (thrown-match? clojure.lang.ExceptionInfo
                             {:status 404}
                             (client-http/get req-url
                                              {:query-params {"origin" "A"
                                                              "destination" "C"}
                                               :accept :json})))))
      (finally 
        (tu-file/delete-test-file! file-path)
        (flyer-server/stop! server-atom)))))

(comment
  (let [router (reitit/router route-routes/routes)]
    (testing "Should contain post method for path /route"
      (reitit/match->path (reitit/match-by-path router "/route") {:origin "GRU" :destination "CDG"})))

  (let [server-port 3000
        req-url "http://localhost:3000/route"
        response (client-http/get req-url
                                  {:query-params {"origin" "GRU"
                                                  "destination" "CDG"}
                                   :accept :json})
        response-body (json/decode (:body response) true)]

    response-body
    )

  (deftest tst-tst
    (is (thrown-match? clojure.lang.ExceptionInfo
                       {:status 404}
                       )))
)
