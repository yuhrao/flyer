(ns flyer.server.main
  (:require [io.pedestal.http :as server]
            [reitit.pedestal :as pedestal]
            [reitit.swagger :as swagger]
            [flyer.server.configuration :as server-config]))

(defn stop! [server-atom]
  (when-not (nil? @server-atom)
    (swap! server-atom server/stop)))

(defn start! [{:keys [port] :as opts} server-atom]
  (stop! server-atom)

  (reset! server-atom
          (-> (server-config/server-map {:routes routes :port port})
              (server/start))))

(comment
  (def routes
    [["/hello"
      {:get {:swagger {:info{:title "Hello route"
                             :description "Just for test"}}
             :handler (fn [{:keys [testing]}] {:status 200 :body {:hello (str testing "World!")}})}}]])


  (defonce srv (atom nil))
  
  (start! {:port 3000 } srv)
  (stop! srv)
  )
