(ns medbook.ui.router
  (:require [re-frame.core :as re-frame]
            [reitit.coercion.spec :as reitit-spec]
            [reitit.frontend :as reitit-front]
            [reitit.frontend.easy :as reitit-easy]
            [medbook.ui.events :as events]
            [medbook.ui.patients.events :as patients-events]
            [medbook.ui.patients.views.list :as patients-views-list]
            [medbook.ui.patients.views.create :as patients-views-create]))


(def ^:private routes
  ["/"
   [""
    {:name ::home
     :view patients-views-list/patient-list-view
     :page-title "Patients"
     :controllers
     [{:start (fn [& _] (re-frame/dispatch [::patients-events/get-patients]))
       :stop  (fn [& _params] (js/console.log "Leaving home page"))}]}]
   ["create-patient"
    {:name ::create-patient
     :view patients-views-create/create-patient-view
     :page-title "Create new patient"
     :controllers
     [{:start (fn [& _params] (js/console.log "Entering create page"))
       :stop  (fn [& _params] (js/console.log "Leaving create page"))}]}]])


(def router
  "Router for frontend pages."
  (reitit-front/router
    routes
    {:data {:coercion reitit-spec/coercion}}))


(defn- on-navigate
  [new-match]
  (when new-match
    (re-frame/dispatch [::events/navigate new-match])))


(defn init-routes!
  "Initial setup router."
  []
  (reitit-easy/start! router on-navigate {:use-fragment false}))
