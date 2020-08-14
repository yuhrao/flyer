(ns flyer.server.routes.route
  (:require [flyer.file.csv :as csv]
            [flyer.core.path-finder :as path-finder]
            [medley.core :as medley]
            [clojure.string :as str]))

(def add-route-interceptor
  {:name ::add
   :enter (fn [{:keys [file-path request] :as context}]
            (let [{body :body-params} request]
              (csv/add-intersection! file-path body))
            (assoc context :response {:status 201}))})

(defn normalize-parameters [{:keys [query-params]}]
  (medley/map-vals (comp keyword str/lower-case) query-params))

(def get-route-interceptor
  {:name  ::best-route
   :enter (fn [{:keys [file-path request] :as context}]
            (let [{:keys [origin destination]} (normalize-parameters request)
                  graph                        (csv/to-graph file-path)
                  result                       (path-finder/trace graph origin destination)]
              (if (nil? result)
                (assoc context :response {:status 404})
                (let [response (-> result
                                   (update :path (partial map (comp str/upper-case name)))
                                   (update :path (partial into [])))]
                  (assoc context :response {:status 200 :body response})))))})

(def routes
  [["/route"
    {:post {:summary "Add a new route"
            :name ::add
            :responses {201 {:body nil}}
            :parameters {:body {:origin string?
                                :destination string?
                                :cost number?}}
            :interceptors [add-route-interceptor]}
     :get {:swagger {:responses {200 {:description "Returned best path"
                                      :schema {:type "object"
                                               :required ["path" "cost"]
                                               :example {:path [:cdg :vcp :gru]
                                                         :cost 14.4}
                                               :properties [{:path {:type "array"
                                                                    :collectionFormat "string"}
                                                             :cost {:type "float"}}]}}}
                     :parameters [{:name "origin"
                                   :in "query"
                                   :description "Route origin"
                                   :required true
                                   :type "string"}
                                  {:name "destination"
                                   :in "query"
                                   :description "Route destination"
                                   :required true
                                   :type "string"}]}
           :summary "Get best route given a origin and a destination"
           :parameters {:query {:origin string? :destination string?}}
           :interceptors [get-route-interceptor]}}]])
