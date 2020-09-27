(ns clofana.views.explore.subs
  (:require [re-frame.core :as rf]))

(rf/reg-sub
 ::stats
 (fn [db]
   (get-in db [:explore/query-result])))

(rf/reg-sub
 ::graph
 (fn [db]
   (get-in db [:explore/query-result :ui/graph])))

(rf/reg-sub
 ::current-tab
 (fn [db]
   (get-in db [:explore/tab])))

(rf/reg-sub
 ::query-failure?
 (fn [db]
   (get-in db [:explore/query-failure?])))
