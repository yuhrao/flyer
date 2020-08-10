(ns flyer.server.routes.route-test
  (:require [flyer.server.routes.route :as route-routes]
            [flyer.server.main :as flyer-server]
            [reitit.core :as reitit]
            [matcher-combinators.test]
            [clojure.test :refer [deftest testing is]]
            [matcher-combinators.matchers :as m]
            [test-utils.file :as tu-file]))

(def router (reitit/router route-routes/routes))

(flyer-server/stop! server-atom)

(deftest containing-routes
    (testing "Should contain methods for path /route"
      (is (match? #{:post}
                  (-> (reitit/match-by-path router "/route")
                      :data
                      keys
                      set)))))

(deftest add-routes
  (let [server-atom (atom nil)
        file-path (tu-file/create-new-test-file! "add-routes" "")
        req-path (-> router
                     (reitit/match-by-name ::route-routes/add)
                     reitit/match->path)
        server-port 3000]
    (flyer-server/start! {:port server-port :routes [route-routes/routes]
                          :file-path file-path})

    (tu-file/delete-test-file! file-path)
    (flyer-server/stop! server-atom)))

(comment
  (let [router (reitit/router route-routes/routes)]
    (testing "Should contain post method for path /route"
      (reitit/match->path (reitit/match-by-path router "/route") {:origin "GRU" :destination "CDG"})))
  )
