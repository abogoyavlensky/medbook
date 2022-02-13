(ns user
  (:require [integrant.repl :as ig-repl]
            [integrant.repl.state :as ig-state]
            [clojure.tools.namespace.repl :refer [set-refresh-dirs]]
            [medbook.util.system :as system-util]
            [figwheel.main.api :as fig]
            [clojure.test :as test]
            [hashp.core]))


(set-refresh-dirs "dev" "src" "test")


(integrant.repl/set-prep!
  (constantly
    ; Add figwheel component to the dev system.
    (-> (system-util/config :dev)
      (assoc :medbook.figwheel/figwheel {}))))
      ; Uncomment for running system without components.
      ;(select-keys []))))


(defn reset
  "Restart system."
  []
  (ig-repl/prep)
  (ig-repl/reset))


(defn stop
  "Stop system."
  []
  (ig-repl/halt))


(defn cljs-repl
  "Run clojurescript repl."
  []
  (fig/cljs-repl system-util/BUILD-ID-DEV))


(defn run-all-tests
  "Run all tests for the project."
  []
  (reset)
  (test/run-all-tests #"medbook.*-test"))


(comment
  (keys ig-state/system)
  (system-util/config :dev)
  (cljs-repl))
