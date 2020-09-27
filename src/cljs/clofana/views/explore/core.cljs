(ns clofana.views.explore.core
  (:require [re-frame.core :as rf]
            [clofana.views.explore.events :as events]
            [clofana.views.explore.subs :as subs]
            [clofana.views.explore.form :as form]
            [clofana.views.explore.table :as table]
            [clofana.views.graph :as graph]))

(defn- documentation []
  [:div.documentation
   [:h3 "Documentation"]
   [:span {:class "float-right"}
    [:a {:href "https://prometheus.io/docs/prometheus/latest/querying/basics/"}
     "Query documentation"]]

   [:p
    [:span {:class "bold" :style {:font-weight "bold"}} "Example: "]
    [:span "rate(node_cpu_seconds_total{mode=\"user\"}[5m])"]]])

(def menus
  {:graph "Graph"
   :stats "Stats"})

(defn render [{:keys [metric type]}]
  (let [current-tab (or @(rf/subscribe [::subs/current-tab])
                        :graph)]
    [:div.container
     [:h2 "Exploration"]

     (documentation)

     (form/metric-form metric type)

     [:ul {:class "nav nav-tabs"}
      (for [[nav-key txt] menus]
        [:li {:key nav-key :class "nav-item"}
          [:a {:class (str "nav-link" (when (= nav-key current-tab) " active"))
               :onClick #(rf/dispatch [::events/change-tab nav-key])}
           txt]])]

     (condp = current-tab
       :stats [table/render]
       :graph [:div
               [:h3 "Graph"]
               [:div {:style {:position "absolute"
                              :width "98%"
                              :height "20px"
                              :left "5px"}}
                [graph/component [::subs/graph]]]])]))
