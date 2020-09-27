(ns clofana.router
  "Route and the boilerplate around"
  (:require [clofana.views.explore.form :as explore.form]
            [clofana.views.explore.events :as explore.events]
            [clojure.string :as string]
            [day8.re-frame.tracing :refer-macros [fn-traced]]
            [re-frame.core :as rf]
            [reitit.coercion.spec :as rss]
            [reitit.frontend :as rfe]
            [reitit.frontend.controllers :as rfc]
            [reitit.frontend.easy :as rfee]))

;; subs
(rf/reg-sub
  :current-route
  (fn [db]
    (:current-route db)))

;; events
(rf/reg-event-fx
  :navigate
  (fn [_ [_ & route]]
    {:navigate! route}))

(rf/reg-event-fx
  :navigated
  (fn [{:keys [db]} [_ new-match]]
    (let [old-match   (:current-route db)
          controllers (rfc/apply-controllers (:controllers old-match) new-match)
          page-title  (str "Clofana ~ " (string/capitalize (name (get-in new-match [:data :name]))))]
      (set! (.-title js/document) page-title)
      {:db (-> db
               (assoc :current-route (assoc new-match :controllers controllers)))})))

;; effects

(rf/reg-fx
  :navigate!
  (fn-traced [route]
             (apply rfee/push-state route)))

;; router definition

(def routes
  ["/"
   [""
    {:name :home}]

   ["catalog"
    {:name :catalog
     :controllers
     [{:start (fn [& _params]
                (rf/dispatch [:active-target]))}]}]

   ["explore"
    {:name :empty-explore}]
   ["explore/:type/:metric"
    {:name :explore
     :parameters {:path {:metric string? :type string?}}
     :controllers
     [{:parameters {:path [:metric :type]}
       :start (fn [{{:keys [metric type] :as _params} :path}]
                (rf/dispatch [::explore.events/query {:start 1
                                                      :queries [(explore.form/clean-metric metric type)]}]))}]}]])

(def router
  (rfe/router
   routes
   {:data {:coercion rss/coercion}}))

(defn on-navigate [new-match]
  (when new-match
    (rf/dispatch [:navigated new-match])))

(defn navigate [page]
  (rf/dispatch [:navigate page]))

(defn init-routes! []
  (rfee/start!
   router
   on-navigate
   {:use-fragment true}))
