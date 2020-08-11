(ns flyer.main
  (:gen-class)
  (:require [flyer.console.main :as console-app]
            [flyer.server.main :as flyer-server]
            [flyer.server.routes.route :as route-routes]))

(defonce server (atom nil))

(defn -main [file-path]
  (flyer-server/start! {:port 3000
                        :routes [route-routes/routes]
                        :file-path file-path}
                       server)
  (console-app/loop-requests file-path))
