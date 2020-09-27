(ns clofana.views.catalog.events
  (:require [re-frame.core :as rf]))

(defn- match? [txt search-term]
  (> (.indexOf txt search-term) -1))

(rf/reg-event-db
 ::search
 (fn [db [_ {:keys [q] :as _params}]]
   (let [new-catalog (->> (get-in db [:catalog :all])
                          (filter (fn [{:keys [metric type help]}]
                                    (or (match? metric q)
                                        (match? type q)
                                        (match? help q)))))]
     (-> db
         (assoc-in [:catalog :current] new-catalog)
         (assoc-in [:catalog :count] (count new-catalog))
         (assoc-in [:catalog :current-page] 0)))))

(rf/reg-event-db
 ::reset-table
 (fn [db _]
   (-> db
       (assoc-in [:catalog :current] (take 20 (get-in db [:catalog :all])))
       (assoc-in [:catalog :count]   (count (get-in db [:catalog :all])))
       (assoc-in [:catalog :current-page] 0))))

(rf/reg-event-db
 ::goto
 (fn [db [_ page]]
   (-> db
       (assoc-in [:catalog :current-page] page))))
