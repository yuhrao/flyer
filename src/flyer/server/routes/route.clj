(ns flyer.server.routes.route
  (:require [flyer.core.operations :as operations]))

(def add-route-interceptor
  {:name ::add
   :enter (fn [{:keys [file-path request] :as context}]
            (let [{body :body-params} request]
              (operations/add-route! file-path body)
              (assoc context :response {:status 201})))})

(def routes
  [["/route"
    {:post {:summary "Add a new route"
            :name ::add
            :responses {201 {:body nil}}
            :parameters {:body {:origin string?
                                :destination string?
                                :value number?}}
            :interceptors [add-route-interceptor]}}]])

(comment
  (let [m {:a 1 :b 2}
        {c :a} m]
    c))
