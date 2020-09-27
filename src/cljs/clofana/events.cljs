(ns clofana.events
  (:require [ajax.core :as ajax]    ;; so you can use this in the response-format below
            [clofana.db :as db]
            [day8.re-frame.http-fx]
            [day8.re-frame.tracing :refer-macros [fn-traced]]
            [re-frame.core :as rf]
            [cljs-time.core :as t]
            [cljs-time.coerce :as tc]))

;; -- re-frame app-db events ---------------------------------------------

(rf/reg-event-db
 :init-db
 (fn-traced [_ _]
            db/default-db))

;; -- Shared  ---------------------------------------------
(def default-ajax-request
  {:timeout         30000
   :format          (ajax/json-request-format)
   :response-format (ajax/json-response-format {:keywords? true})})

(rf/reg-event-db
  ::on-failure
  (fn [db [_ result]]
    (assoc db :failure result)))

;; -- Active targets  ---------------------------------------------

(rf/reg-event-fx
 ::on-success-active-target
 (fn [{:keys [db]} [_ results]]
   (let [label (:labels (first results))]
     {:db (assoc db :active-targets results)
      :dispatch-n (list [:metrics label])})))

(rf/reg-event-fx
 :active-target
 (fn [{:keys [db]}]
   {:db db
    :http-xhrio (merge default-ajax-request
                       {:method     :get
                        :uri        "/datasource/active-target"
                        :on-success [::on-success-active-target]
                        :on-failure [::on-failure]})}))

;; -- Metrics  ---------------------------------------------
(rf/reg-event-db
 ::on-success-metrics
 (fn [db [_ result]]
   (assoc db :catalog {:all result
                       :count (count result)
                       :current result
                       :current-page 0})))

(rf/reg-event-fx
 :metrics
 (fn [{:keys [db]} [_ params]]
   {:db db
    :http-xhrio (merge default-ajax-request
                       {:method     :get
                        :uri        "/datasource/metric/get"
                        :params     {:labels (str params)}
                        :on-success [::on-success-metrics]
                        :on-failure [::on-failure]})}))

;; -- Query  ---------------------------------------------
(rf/reg-event-db
 ::on-success-query
 (fn [db [_ result]]
   (assoc db :query-result result)))

(rf/reg-event-fx
 :query
 (fn [{:keys [db]} [_ {:keys [start queries] :as _params}]]
   {:db db
    :http-xhrio (merge default-ajax-request
                       {:method     :post
                        :uri        "/datasource/query"
                        :params     {:start   (tc/to-long (t/minus (t/now) (t/hours start)))
                                     :end     (tc/to-long (t/now))
                                     :queries queries}
                        :on-success [::on-success-query]
                        :on-failure [::on-failure]})}))

;; -- Auto complete  ---------------------------------------------
(rf/reg-event-db
 ::on-success-auto-complete
 (fn [db [_ result]]
   (assoc db :exploration/autocomplete result)))

(rf/reg-event-fx
 :auto-complete
 (fn [{:keys [db]} [_ {:keys [q] :as _params}]]
   {:db db
    :http-xhrio (merge default-ajax-request
                       {:method     :get
                        :uri        "/datasource/metric/search"
                        :params     {:q q}
                        :on-success [::on-success-auto-complete]
                        :on-failure [::on-failure]})}))
