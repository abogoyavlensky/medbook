(ns medbook.core
  (:gen-class)
  (:require [clojure.tools.logging :as log]
            [integrant.core :as ig]
            [medbook.util.system :as util]))


(defn -main
  "Run application system in production env."
  [& _args]
  (log/info "System is starting...")
  (let [config (util/config :prod)]
    (ig/load-namespaces config)
    (-> config
        (ig/init)
        (util/at-shutdown)))
  (log/info "System start completed."))
