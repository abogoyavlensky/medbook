(ns medbook.server
  (:require [integrant.core :as ig]
            [ring.adapter.jetty :as ring]
            [clojure.tools.logging :as log]))


(defmethod ig/init-key ::server
  [_ {:keys [handler options]}]
  {}
  (ring/run-jetty handler options)
  (log/info (str "Server started on port: http://localhost:" (:port options))))


(defmethod ig/halt-key! ::server
  [_ server]
  (.stop server))
