(ns medbook.ui.router
  (:require [re-frame.core :as re-frame]
            [reitit.coercion.spec :as reitit-spec]
            [reitit.frontend :as reitit-front]
            [reitit.frontend.easy :as reitit-easy]
            [medbook.ui.events :as events]
            [medbook.ui.views :as views]))


(defn- on-navigate
  [new-match]
  (when new-match
    (re-frame/dispatch [::events/navigate new-match])))


(def ^:private routes
  ["/"
   [""
    {:name ::home
     :view views/home-page
     :link-text "Home"
     :controllers
     [{:start (fn [& _params] (js/console.log "Entering home page"))
       :stop  (fn [& _params] (js/console.log "Leaving home page"))}]}]])


(def router
  "Router for frontend pages."
  (reitit-front/router
    routes
    {:data {:coercion reitit-spec/coercion}}))


(defn init-routes!
  "Initial setup router."
  []
  (reitit-easy/start! router on-navigate {:use-fragment false}))
