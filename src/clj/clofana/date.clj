(ns clofana.date
  (:require [java-time :as t]))

(defn round-date
  "By can by days, hours, minutes"
  [date by]
  (assert (#{:days :hours :minutes :seconds} by) "Cannot round date")
  (t/truncate-to date by))

(defn all-dates-in-interval [start-date end-date by step]
  (assert (t/before? start-date end-date) "Start date should be before end")
  (let [start-date (round-date start-date by)
        end-date   (round-date end-date by)
        by-fn (condp = by
                :days     t/days
                :hours    t/hours
                :minutes  t/minutes
                :seconds  t/seconds)]
    (loop [result [start-date]]
      (let [l (last result)
            d (t/plus l (by-fn step))]
        (if (t/before? d end-date)
          (recur
            (conj result d))
          result)))))

; there must be a simple way....
(defn epoch->local-date-time [epoch]
  (java.time.LocalDateTime/ofInstant
   (t/instant (t/java-date (* epoch 1000)))
   (t/zone-id)))

(defn local-date-time->epoch [dt]
  (/ (.toEpochMilli (.toInstant (.atZone dt (t/zone-id))))
     1000))
