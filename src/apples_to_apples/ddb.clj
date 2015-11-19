(ns apples-to-apples.ddb
  (:require [amazonica.aws.dynamodbv2 :as dyn]))

(def local-connection-opts
  {:access-key "local-access"
   :secret-key "local-secret"
   :endpoint   "http://localhost:8000"})                    ;*1*

(def aws-connection-opts
  {:access-key "aCceSsKeYfOrYoUrUsEr"
   :secret-key "sEcReTKeYfOrYoUrUsEr"
   :endpoint   "https://dynamodb.us-east-1.amazonaws.com"}) ;*2*

(defn list-tables []
  (->> local-connection-opts
       (dyn/list-tables)
       :table-names
       (map keyword)))                                      ;*3*

(defn table-exist? [table-name]
  (-> (list-tables) set (contains? table-name)))            ;*4*


(defn delete-table [table-name]
  (if (table-exist? table-name)
    (dyn/delete-table
      local-connection-opts
      {:table-name (name table-name)})))

(defn ensure-table [table-name]
  (when-not (table-exist? table-name)
    (dyn/create-table
      local-connection-opts
      :table-name (name table-name)
      :key-schema [{:attribute-name "id" :key-type "HASH"}]
      :attribute-definitions [{:attribute-name "id" :attribute-type "N"}]
      :provisioned-throughput {:read-capacity-units  50
                               :write-capacity-units 50})
    ))