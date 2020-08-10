(ns flyer.server.main
  (:require [io.pedestal.http :as server]
            [reitit.pedestal :as pedestal]
            [reitit.swagger :as swagger]
            [flyer.server.common-interceptors :as common-interceptors]
            [flyer.server.configuration :as server-config]))

(defn stop! [server-atom]
  (when-not (nil? @server-atom)
    (swap! server-atom server/stop)))

(defn start! [{:keys [port routes file-path] :as opts} server-atom]
  (let [default-interceptors [(common-interceptors/route-csv-interceptor file-path)]]
    (stop! server-atom)

  (reset! server-atom
          (-> (server-config/create-server-map {:routes routes :port port :default-interceptors default-interceptors})
              (server/start)))))

(comment
  (def hello-route
    ["/hello"
     {:get {:handler (fn [{:keys [testing]}] {:status 200 :body {:hello (str testing "World!")}})}}])

  (defonce srv (atom nil))

  (require '[flyer.server.routes.route :as route-routes])
  
  (start! {:port 3000
           :routes [route-routes/routes]
           :file-path "resources/input-file.txt"} srv)

  (stop! srv)
  )
