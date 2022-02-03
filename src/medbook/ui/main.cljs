(ns ^:figwheel-hooks medbook.ui.main
  (:require [reagent.dom :as reagent]
            [re-frame.core :as re-frame]
            [medbook.ui.views :as views]
            [medbook.ui.router :as router]
            [medbook.ui.events :as events]
            ; Import namespaces for compiler
            [medbook.ui.subs]))


(goog-define ^boolean DEBUG false)


(defn dev-setup
  []
  (when DEBUG
    (enable-console-print!)
    (println "dev mode")))


(defn mount-root
  []
  (re-frame/clear-subscription-cache!)
  (reagent/render [views/router-component {:router router/router}]
    (.getElementById js/document "app")))


(defn ^:after-load render
  []
  (dev-setup)
  (router/init-routes!)
  (re-frame/dispatch-sync [::events/initialize-db])
  (mount-root))


(render)
