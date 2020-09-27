(ns clofana.header
  (:require [clofana.utils :as utils]
            [clofana.subs]))

(def routes
  {:home          "Home"
   :catalog       "Catalog"
   :empty-explore "Explore"})

(defn render [current-route]
  [:header {:class "masthead mb-auto"}
   [:div {:class "inner"}
    [:nav {:class "nav nav-masthead justify-content-center"}
     (for [[route-name txt] routes]

       [:a {:key route-name
            :class (str "nav-link" (when (= route-name current-route) " active"))
            :href (utils/href route-name)}
        (str txt)])]]])
