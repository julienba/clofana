(ns clofana.core
  (:require [clofana.config :as config]
            [clofana.events]
            [clofana.router :as router]
            [clofana.subs]
            [clofana.views :as views]
            [reagent.dom :as rdom]
            [re-frame.core :as rf]))

(defn dev-setup []
  (when config/debug?
    (println "dev mode")))

(defn ^:dev/after-load mount-root []
  (rf/clear-subscription-cache!)
  (router/init-routes!)
  (let [root-el (.getElementById js/document "app")]
   (rdom/unmount-component-at-node root-el)
   (rdom/render [views/main-panel] root-el)))

(defn init []
  (rf/dispatch-sync [:init-db])
  (dev-setup)
  (mount-root))
