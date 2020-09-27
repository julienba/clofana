(ns clofana.prom
  (:require [cheshire.core   :as json]
            [clojure.string :as string]
            [clojure.tools.logging :as log]
            [clj-http.client :as http]
            [clofana.config :as config]))

(defn- get-prom-url []
  (get-in config/config [:prometheus :url]))

(defn get-prom-basic-auth []
  [(get-in config/config [:prometheus :user])
   (get-in config/config [:prometheus :password])])

(defn- extract-data
  "Utitlity to get the data from the data field in case of success and log the error otherwise"
  [{:keys [body status]} url-msg]
  (if (= 200 status)
    (let [resp (json/parse-string body true)]
      (if (= "success" (:status resp))
        (:data resp)
        (do
          (log/errorf "Fail to run %s: %s" url-msg body)
          [])))
    (do
      (log/errorf "Fail to run %s %d : %s" url-msg status body)
      [])))

(defn query [_ {:keys [query start end] :as params}]
  (let [{:keys [body status]} (http/get (str (get-prom-url) "/api/v1/query_range")
                                        {:basic-auth (get-prom-basic-auth)
                                         :query-params {"query" query
                                                        "start" start
                                                        "end"   end
                                                        "step"  "60s"}})]
    (if (= 200 status)
      (json/parse-string body true)
      (log/errorf "Fail to run query %s" params))))

(defn- map->label [m]
  (str "{"
       (->> m
            (map (fn [[k v]] (str (name k) " =\"" v "\"")))
            (string/join ","))
       "}"))

(defn metadata [target]
  (try
    (let [{:keys [body status]} (http/get (str (get-prom-url) "/api/v1/targets/metadata")
                                          {:basic-auth (get-prom-basic-auth)
                                           :query-params {:match_target (map->label target)}})]


      (if (= 200 status)
        (let [resp (json/parse-string body true)]
          (if (= "success" (:status resp))
            (:data resp)
            (do (log/errorf "Fail to run metadata: %s" body)
              [])))
        (do (log/errorf "Fail to run metadata %d : %s" status body)
          [])))
    (catch Exception _
      (log/errorf "Unexpected error while calling %s" target)
      [])))

(defn active-targets []
  (let [resp (http/get (str (get-prom-url) "/api/v1/targets?state=active")
                       {:basic-auth (get-prom-basic-auth)
                        :query-params {}})]
    (extract-data resp "active-target")))

(defn build-catalog
  "Fetch all active targets and fetch all metrics for each"
  []
  (let [targets-response (active-targets)
        targets (when (= "success" (:status targets-response))
                    (map :labels (get-in targets-response [:data :activeTargets])))]
    (mapcat metadata targets)))

(defn series
  "Finding metadata
  curl -g 'http://localhost:9090/api/v1/series?' --data-urlencode 'match[]=up' --data-urlencode 'match[]=process_start_time_seconds{job='\"prometheus\"}'
  https://prometheus.io/docs/prometheus/latest/querying/api/#querying-metadata"
  [match]
  (let [{:keys [body status]} (http/get (str (get-prom-url) "/api/v1/series?match[]=" match)
                                        {:basic-auth (get-prom-basic-auth)})]
                                         ;:query-params {:match match}})]
    (if (= 200 status)
      (json/parse-string body true)
      (log/errorf "series %s" match))))

(defn build-series-dimension
  "Return a map of set containaing all the possible value for each dimension. Can be use for an auto complete"
  [{:keys [data] :as _series-result}]
  (reduce
   (fn [acc e]
     (reduce-kv
      (fn [acc1 k v]
        (update acc1 k (fnil #(conj % v) #{v})))
      acc
      e))
   {}
   data))
