(ns clofana.server
  (:require [clojure.tools.logging           :as log]
            clojure.walk
            [clofana.handler.datasource :as datasource]
            [clofana.prom                    :as prom]
            [mount.core                      :refer [defstate]]
            [muuntaja.core                   :as m]
            [reitit.ring                     :as ring]
            [reitit.ring.middleware.muuntaja :as muuntaja]
            [reitit.ring.coercion            :as coercion]
            [ring.adapter.jetty              :refer [run-jetty]]
            [ring.middleware.params          :refer [wrap-params]]
            reitit.coercion.spec
            reitit.coercion))

(def internal-routes
  ["/internal"
   ["/status" {:get (fn [_]
                      {:status 200
                       :body "OK"})}]])

(def datasources-routes
  ["/datasource"
   ["/active-target"
    {:get (fn [_req]
            {:status 200
             :body (:activeTargets (prom/active-targets))})}]

   ["/query"
    {:post (fn [{:keys [body-params]}]
             {:status 200
              :body (datasource/query-handlers body-params)})}]

   ; deprecated
   ["/metric/all"
    {:get (fn [_req]
            {:status 200
             :body (prom/build-catalog)})}]

   ["/metric/get"
    {:get (fn [{{:strs [labels]} :query-params}]
            {:status 200
             :body (prom/metadata (read-string labels))})}]

   ["/metric/series"
    {:get (fn [{{:strs [query]} :query-params}]
            (let [{:keys [result] :as series-result} (prom/series query)]
              (if ( = result "success")
                {:status 200
                 :body (prom/build-series-dimension series-result)}
                {:status 404
                 :body (format "No series for request %s" query)})))}]])

(def app
  (ring/ring-handler
    (ring/router
     [internal-routes
      datasources-routes]
     {:conflicts (constantly nil)
      :data {:muuntaja m/instance
             :middleware [wrap-params
                          muuntaja/format-negotiate-middleware
                          muuntaja/format-response-middleware
                          muuntaja/format-request-middleware
                          coercion/coerce-exceptions-middleware
                          coercion/coerce-request-middleware
                          coercion/coerce-response-middleware]}})
    (ring/routes
     (ring/create-resource-handler {:path "/" :root "/public"})
     (ring/create-default-handler))))

(defstate server
  :start (do
           (log/infof "Start server on port %d" 4000)
           (run-jetty #'app {:port 4000, :join? false}))
  :stop (.stop server))
