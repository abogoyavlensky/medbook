(ns ^:figwheel-hooks medbook.ui.main
  (:require [reagent.dom :as reagent]
            [re-frame.core :as re-frame]
            [medbook.ui.views :as views]
            ; Import namespaces for compiler
            [medbook.ui.events]))


(goog-define ^boolean DEBUG false)


(defn dev-setup
  []
  (when DEBUG
    (enable-console-print!)
    (println "dev mode")))


(defn mount-root
  []
  (re-frame/clear-subscription-cache!)
  (reagent/render [views/main-panel] (.getElementById js/document "app")))


(defn ^:after-load render
  []
  (dev-setup)
  (re-frame/dispatch-sync [:event/initialize-db])
  (mount-root))


(render)
