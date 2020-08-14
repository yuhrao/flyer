(ns flyer.server.common-interceptors)

(defn route-csv-interceptor [file-path]
  {::name ::add-file-path
   :enter (fn [context]
            (assoc context :file-path file-path))})
