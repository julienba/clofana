(ns clofana.views.catalog.core
  (:require [re-frame.core :as rf]
            [clofana.utils :as utils]
            [clofana.views.catalog.events :as events]
            [clofana.views.catalog.subs :as subs]))

(defn render []
  (let [active-targets @(rf/subscribe [::subs/active-targets])
        current-page   @(rf/subscribe [::subs/current-page])
        data           @(rf/subscribe [::subs/current])
        total-count    @(rf/subscribe [::subs/total-count])
        nb-page        (int (/ total-count 20))]
    [:div.container
     [:h1 "Catalog"]

     [:div
      [:form
       [:div {:class "form-group"}
        [:label {:for "activeTarget"} (str "Active Targets:" (count active-targets))]
        [:select {:class "form-control", :id "activeTarget"
                  :onChange (fn [e] (rf/dispatch [:metrics (str (utils/js-event->value e))]))}
         (for [[idx {:keys [labels]}] (map-indexed (fn [i e] [i e]) active-targets)]
           [:option {:key idx} (str labels)])]]]]

     (when current-page
       [:div.data-table
        [:div {:class "float-right search btn-group" :style {:minWidth "60%"
                                                             :paddingBottom "20px"}}
         [:input {:class "form-control"
                  :type "text"
                  :placeholder "Search (min 3 chars)"
                  :autoComplete "off"
                  :onChange (fn [e]
                              (let [current-value (utils/js-event->value e)]
                                (cond
                                  (= (count current-value) 0)
                                  (rf/dispatch [::events/reset-table {}])

                                  (> (count current-value) 2)
                                  (rf/dispatch [::events/search {:q current-value}]))))}]]
        [:p (str "Total metrics count " total-count)]

        (when-not false (empty? data)
          [:<>
           [:table {:class "table table-striped table-bordered"}
            [:thead
             [:tr
              [:th "Metric"]
              [:th "Type"]
              [:th "Help"]
              [:th "Action"]]]
            [:tbody
             (for [[idx {:keys [metric type help]}] (map-indexed (fn [i e] [i e]) data)]
               [:tr {:key idx}
                [:td metric]
                [:td type]
                [:td help]
                [:td [:a {:href (utils/href :explore {:metric (str metric "{}")
                                                      :type type})}
                      "Explore"]]])]]


           [:div {:class "fixed-table-pagination"}
            [:div {:class "pagination"}
             [:ul {:class "pagination"}
              ; Previous
              [:li {:class "page-item page-pre"}
               [:a {:class "page-link"
                    :aria-label "previous page"
                    :onClick #(when (pos? current-page)
                                (rf/dispatch [::events/goto (dec current-page)]))}
                "‹"]]
              ; Page 1 to X
              (for [page (range (inc nb-page))]
                [:li {:key page :class (if (= page current-page)
                                         "page-item active"
                                         "page-item")}
                 [:a {:class "page-link"
                      :aria-label (str "to page " page)
                      :onClick #(rf/dispatch [::events/goto (int page)])}
                  (str page)]])

              ; Next
              [:li {:class "page-item page-next"}
               [:a {:class "page-link"
                    :aria-label "next page"
                    :onClick #(when (< current-page nb-page)
                                (rf/dispatch [::events/goto (inc current-page)]))}
                "›"]]]]]])])]))
