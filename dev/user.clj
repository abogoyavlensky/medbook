(ns user
  (:require [integrant.repl :as ig-repl]
            [integrant.repl.state :as ig-state]
            [clojure.tools.namespace.repl :refer [set-refresh-dirs]]
            [hashp.core]
            [medbook.util.system :as system-util]))


(set-refresh-dirs "dev" "src" "test")


(integrant.repl/set-prep!
  (constantly (system-util/config :dev)))


(defn reset
  "Restart system."
  []
  (ig-repl/prep)
  (ig-repl/reset))


(defn stop
  "Stop system."
  []
  (ig-repl/halt))


(comment
  (keys ig-state/system))
