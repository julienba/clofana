(ns clofana.config
  (:require [aero.core :as aero]
            [mount.core :refer [defstate]]))

(defn read-config []
  (aero/read-config "resources/config.edn"))

(defstate config :start (read-config))
