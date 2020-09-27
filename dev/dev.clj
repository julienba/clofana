(ns dev
  (:require [clojure.pprint :refer [pprint]]
            [clojure.tools.logging :as log]
            [clojure.tools.namespace.repl :as tn]
            [mount.core :as mount :refer [defstate]]
            [mount.tools.graph :refer [states-with-deps]]
            [mount-up.core :as mu]
            clofana.config
            clofana.server))

(mu/on-up :info mu/log :before)

(defn start []
  (log/info "Start!")
  (mount/start))

(defn stop []
  (mount/stop))

(defn refresh []
  (stop)
  (tn/refresh))

(defn refresh-all []
  (stop)
  (tn/refresh-all))

(defn go
  []
  (start)
  :ready)

(defn reset
  []
  (stop)
  (tn/refresh :after 'dev/go))
