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

(defn ensure-table-exist [table-name]
  (when-not (table-exist? table-name)
    (dyn/create-table
      local-connection-opts
      :table-name (name table-name)
      :key-schema [{:attribute-name "id" :key-type "HASH"}]
      :attribute-definitions [{:attribute-name "id" :attribute-type "N"}]
      :provisioned-throughput {:read-capacity-units  50
                               :write-capacity-units 50})
    ))

(defn get-item-from-table [table-name id]
  (dyn/get-item
    local-connection-opts
    :table-name (name table-name)
    :key {:id {:n id}}))

(defn put-item-into-table [table-name item]
  (dyn/put-item
    local-connection-opts
    :table-name (name table-name)
    :item item))

(defn- seq-to-put-requests [data-collection]
  (->> data-collection
       (mapv (fn [data] {:put-request {:item data}}))
       doall))

(defn populate-table [table-name data]
  (loop [collection data]
    (when (> (count collection) 0)
      (dyn/batch-write-item
        (connections-opts)
        :request-items
        {(name table-name)
         (seq-to-put-requests (take 25 collection))})
      (recur (drop 25 collection)))))

(defn parallel-get-all-from-table [table-name]
  (let [total-segments 2000
        base-opts {:table-name (name table-name)}
        result-set (doall (pmap #(dyn/scan (connections-opts)
                                           (assoc base-opts
                                             :total-segments total-segments
                                             :segment %))
                                (range 0 total-segments)))

        aggregated-results (mapcat :items result-set)]
    aggregated-results))

(defn parallel-get-all-from-table-with [table-name key value type]
  (let [total-segments 2000
        base-opts {:table-name                  (name table-name)
                   :filter-expression           "#key_placeholder = :value_placeholder"
                   :expression-attribute-names  {"#key_placeholder" (name key)}
                   :expression-attribute-values {":value_placeholder" {type value}}}
        result-set (doall (pmap #(dyn/scan (connections-opts)
                                           (assoc base-opts
                                             :total-segments total-segments
                                             :segment %))
                                (range 0 total-segments)))
        aggregated-results (mapcat :items result-set)]
    aggregated-results))
