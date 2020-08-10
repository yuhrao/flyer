(ns flyer.server.common-interceptors
  (:require [io.pedestal.interceptor :as interceptor]))

(defn route-csv-interceptor [file-path]
  (interceptor/map->Interceptor
   {::name ::add-file-path
    :enter (fn [context]
             (assoc context :file-path file-path))}))
