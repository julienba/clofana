(ns clofana.core
  (:require [clojure.tools.logging :as log]
            [mount.core :as mount]
            clofana.config
            clofana.server) ; to load mount/defstate
  (:gen-class))

(defn -main [& _args]
  (log/info "Starting system...")
  (mount/start)
  (deref (promise)))
