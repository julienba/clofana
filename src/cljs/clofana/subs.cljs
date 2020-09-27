(ns clofana.subs
  (:require [re-frame.core :as rf]))

(rf/reg-sub
 :exploration/graph
 (fn [db]
   (get-in db [:query-result :ui/graph])))

(rf/reg-sub
 :exploration/stats
 (fn [db]
   (select-keys (get-in db [:query-result])
                [:ui/stats :ui/stats-h-t-h :ui/stats-d-t-d :ui/stats-w-t-w])))

(rf/reg-sub
 :current-catalog
 (fn [db]
   (get-in db [:catalog :current])))
