(ns medbook.util.system
  (:require [aero.core :as aero]
            [clojure.java.io :as io]
            [clojure.tools.logging :as log]
            [integrant.core :as ig])
  (:import (clojure.lang IFn)))


(def PROFILES
  "Available profiles for app."
  #{:dev :prod})


(def BUILD-ID-DEV "dev")

; Add #ig/ref tag for reading integrant config from aero.
(defmethod aero/reader 'ig/ref
  [_ _ value]
  (ig/ref value))


(defn config
  "Return edn config with all variables set."
  [profile]
  {:pre [(contains? PROFILES profile)]}
  (-> "config.edn"
    (io/resource)
    (aero/read-config {:profile profile})))


(defn at-shutdown
  "Add hook for shutdown system on sigterm."
  [system]
  (-> (Runtime/getRuntime)
    (.addShutdownHook
      (Thread. ^IFn (bound-fn []
                      (log/info "System shutdown...")
                      (ig/halt! system)
                      (shutdown-agents)
                      (log/info "System is stopped."))))))
