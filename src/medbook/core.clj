(ns medbook.core
  (:gen-class)
  (:require [clojure.tools.logging :as log]
            [integrant.core :as ig]
            [medbook.util.system :as system-util]))


(defn -main
  "Run application system in production env."
  [& _args]
  (log/info "System is starting...")
  (let [config (system-util/config :prod)]
    (ig/load-namespaces config)
    (-> config
      (ig/init)
      (system-util/at-shutdown)))
  (log/info "System start completed."))
