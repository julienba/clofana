(ns clofana.handler.datasource
  (:require [clofana.prom                    :as prom]
            [clofana.date                    :as date]))

(defn- average [xs]
  (if (empty? xs)
    0
    (/ (apply + xs)
       (count xs))))

(defn aggregate-by-date
  [values merge-fn target-dates]
  (loop [data values
         [start end & other] target-dates
         results []]
    (if (nil? end)
      (conj results [start (merge-fn (map second data))])
      (let [part (take-while #(< (first %) end) data)]
        (recur
          (drop (count part) data)
          (cons end other)
          (conj results [start (merge-fn (map second part))]))))))

(def hour-in-ms 3600)
(def day-in-ms (* hour-in-ms 24))
(def week-in-ms (* day-in-ms 7))

(defn- prom-data->chartjs-data [prom-data start-dt end-dt]
  (let [target-dates (->> (date/all-dates-in-interval start-dt end-dt :minutes 1)
                          (map date/local-date-time->epoch))]
    {:labels target-dates
     :datasets (map (fn [{:keys [metric values]}]
                      {:label (str metric)
                       :data (map second (aggregate-by-date values
                                                            (fn [xs] (average (map read-string xs)))
                                                            target-dates))})

                    prom-data)}))

(defn- compute-stats [ts-seq]
  (for [{:keys [metric values]} ts-seq
        :let [only-values (map read-string (map second values))]]
       {:metric metric
        ;:xs only-values
        :stats {:min (apply min only-values)
                :max (apply max only-values)
                :avg (average only-values)}}))

(defn- prom-query [{:keys [start end queries]}]
  (let [results (into {} (map (fn [query]
                                {query (prom/query nil {:end   end
                                                        :start start
                                                        :query query})})
                              queries))
        all-ts (->> results
                    vals
                    (mapcat #(-> % :data :result)))]
      {:all-ts all-ts
       :errors []}))

(defn query-handlers [{:keys [start end queries]}]
  (let [start       (int (/ start 1000))
        end         (int (/ end 1000))
        ts-now      (:all-ts (prom-query {:queries queries :start start :end end}))
        ts-minus-1h (:all-ts (prom-query {:queries queries :start (- start hour-in-ms) :end (- end hour-in-ms)}))
        ts-minus-1d (:all-ts (prom-query {:queries queries :start (- start day-in-ms) :end (- end day-in-ms)}))
        ts-minus-1w (:all-ts (prom-query {:queries queries :start (- start week-in-ms) :end (- end week-in-ms)}))]

    {:errors {}
     ;:all-ts all-ts ; DEBUG
     :ui/stats        (compute-stats ts-now)
     :ui/stats-h-t-h  (compute-stats ts-minus-1h)
     :ui/stats-d-t-d  (compute-stats ts-minus-1d)
     :ui/stats-w-t-w  (compute-stats ts-minus-1w)
     :ui/graph        (prom-data->chartjs-data
                       ts-now
                       (date/epoch->local-date-time start)
                       (date/epoch->local-date-time end))}))
