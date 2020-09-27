(ns clofana.views.catalog.subs
  (:require [re-frame.core :as rf]))

(rf/reg-sub
 ::current
 (fn [db]
   (->> (get-in db [:catalog :current])
        (drop (* (get-in db [:catalog :current-page]) 20))
        (take 20))))

(rf/reg-sub
 ::total-count
 (fn [db]
   (get-in db [:catalog :count])))

(rf/reg-sub
 ::current-page
 (fn [db]
   (get-in db [:catalog :current-page])))

(rf/reg-sub
  ::active-targets
  (fn [db]
    (get-in db [:active-targets])))
