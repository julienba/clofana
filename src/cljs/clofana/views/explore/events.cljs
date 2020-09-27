(ns clofana.views.explore.events
  (:require [cljs-time.core :as t]
            [cljs-time.coerce :as tc]
            [clofana.events :as events]
            [re-frame.core :as rf]))

(rf/reg-event-db
 ::on-success-query
 (fn [db [_ result]]
   (-> db
     (assoc :explore/query-result result)
     (assoc :explore/query-failure? false))))

(rf/reg-event-db
 ::on-failure-query
 (fn [db [_ _result]]
   (assoc db :explore/query-failure? true)))

(rf/reg-event-fx
 ::query
 (fn [{:keys [db]} [_ {:keys [start queries] :as _params}]]
   (when-not (empty? queries)
     {:db db
      :http-xhrio (merge events/default-ajax-request
                         {:method     :post
                          :uri        "/datasource/query"
                          :params     {:start   (tc/to-long (t/minus (t/now) (t/hours start)))
                                       :end     (tc/to-long (t/now))
                                       :queries queries}
                          :on-success [::on-success-query]
                          :on-failure [::on-failure-query]})})))

; NOTE it won't work for multiple query.
; I need to update-in with the query as parameter, maybe I can pass it as arg with the :on-success or the server need to return it
(rf/reg-event-db
 ::on-success-series
 (fn [db [_ result]]
   (assoc db :explore/series-result result)))

(rf/reg-event-fx
 ::series
 (fn [{:keys [db]} [_ {:keys [query] :as _params}]]
   {:db db
    :http-xhrio (merge events/default-ajax-request
                       {:method     :get
                        :uri        "/metric/series"
                        :params     {:query query}
                        :on-success [::on-success-series]
                        :on-failure [:events/on-failure]})}))

(rf/reg-event-db
  ::change-tab
  (fn [db [_ new-tab]]
    (assoc db :explore/tab new-tab)))
