(ns ^:figwheel-hooks medbook.ui.main
  (:require [reagent.dom :as reagent]
            [re-frame.core :as re-frame]))


(goog-define ^boolean DEBUG false)


(defn dev-setup
  []
  (when DEBUG
    (enable-console-print!)
    (println "dev mode")))


(defn main-panel
  []
  [:div
   {:class ["container" "grid-lg"]}
   [:h1 "Hello re-frame!?"]])


(defn mount-root
  []
  (re-frame/clear-subscription-cache!)
  (reagent/render
    [main-panel]
    (.getElementById js/document "app")))


(defn ^:after-load render
  []
  (dev-setup)
  (mount-root))


(render)
