(ns flyer.server.configuration
  (:require [io.pedestal.interceptor :as interceptor]
            [io.pedestal.http :as server]
            [muuntaja.core :as m]
            [reitit.coercion.spec]
            [reitit.http :as http]
            [reitit.http.coercion :as coercion]
            [reitit.http.interceptors.exception :as exception]
            [reitit.http.interceptors.multipart :as multipart]
            [reitit.http.interceptors.muuntaja :as muuntaja-interceptor]
            [reitit.http.interceptors.muuntaja :as muuntaja]
            [reitit.http.interceptors.parameters :as parameters]
            [reitit.pedestal :as pedestal]
            [reitit.ring :as ring]
            [reitit.swagger :as swagger]
            [reitit.swagger-ui :as swagger-ui]))

(defn- add-default-interceptors
  ([] (add-default-interceptors nil))
  ([interceptors]
   {:interceptors (->> interceptors
                       (concat [(muuntaja-interceptor/format-interceptor)])
                       (map interceptor/map->Interceptor)
                       (into []))}))

(def ^:private ring-handlers
  (ring/routes
   (swagger-ui/create-swagger-ui-handler
    {:path "/"
     :config {:validatorUrl nil
              :operationsSorter "alpha"}})
   (ring/create-resource-handler)
   (ring/create-default-handler)))

(defn- merge-routes [routes]
  (conj routes
        ["/swagger.json" 
         {:name :swagger/json
          :get {:no-doc true
                :swagger {:info {:title "Flyer API"
                                 :description "An application to get the best routes"}}
                :handler (swagger/create-swagger-handler)}}]
          ))

(defn create-router [routes]
  (http/router
   (merge-routes routes)
   {:data {:coercion reitit.coercion.spec/coercion
          :muuntaja m/instance
          :interceptors [;; swagger feature
                         swagger/swagger-feature
                         ;; query-params & form-params
                         (parameters/parameters-interceptor)
                         ;; content-negotiation
                         (muuntaja/format-negotiate-interceptor)
                         ;; encoding response body
                         (muuntaja/format-response-interceptor)
                         ;; exception handling
                         (exception/exception-interceptor)
                         ;; decoding request body
                         (muuntaja/format-request-interceptor)
                         ;; coercing response bodys
                         (coercion/coerce-response-interceptor)
                         ;; coercing request parameters
                         (coercion/coerce-request-interceptor)
                         ;; multipart
                         (multipart/multipart-interceptor)]}}))


(defn router-interceptor [{:keys [routes default-interceptors]}]
  (pedestal/routing-interceptor
   (create-router routes) 
   ;; optional default ring handlers (if no routes have matched)
   ring-handlers 
   (add-default-interceptors default-interceptors)))

(defn create-server-map [{:keys [port routes default-interceptors] :as opts}]
  (-> {::server/type :jetty
       ::server/port port
       ::server/join? false
       ::server/routes []
       ::server/secure-headers {:content-security-policy-settings
                                {:default-src "'self'"
                                 :style-src "'self' 'unsafe-inline'"
                                 :script-src "'self' 'unsafe-inline'"}}}
      
      (server/default-interceptors)
      (pedestal/replace-last-interceptor (router-interceptor opts))
      (server/create-server)))
