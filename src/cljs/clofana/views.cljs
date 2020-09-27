(ns clofana.views
  (:require [clofana.subs]
            [re-frame.core :as rf]
            [clofana.header :as header]
            [clofana.views.home.core    :as views.home]
            [clofana.views.catalog.core :as views.catalog]
            [clofana.views.explore.core :as views.explore]))

(defn main-panel []
  (let [current-route (rf/subscribe [:current-route])]
    (fn []
      (let [route-name   (get-in @current-route [:data :name])
            route-params (get-in @current-route [:path-params])]
        [:div
         (header/render route-name)

         [:main {:role "main"}
          (condp = route-name
            :home           (views.home/render)
            :catalog        (views.catalog/render)
            :explore        (views.explore/render route-params)
            :empty-explore  (views.explore/render {})
            [:div (str "Unknown view: " route-name)])]]))))
