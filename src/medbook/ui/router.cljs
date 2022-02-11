(ns medbook.ui.router
  (:require [re-frame.core :as re-frame]
            [reitit.coercion.spec :as reitit-spec]
            [reitit.frontend :as reitit-front]
            [reitit.frontend.easy :as reitit-easy]
            [medbook.ui.events :as events]
            [medbook.ui.patients.events :as patients-events]
            [medbook.ui.patients.views.list :as patients-views-list]
            [medbook.ui.patients.views.form :as patients-views-form]))


(def ^:private routes
  ["/"
   [""
    {:name ::home
     :page-title "Patients"
     :view patients-views-list/patient-list-view
     :controllers
     [{:start (fn [& _] (re-frame/dispatch [::patients-events/get-patients]))
       ; TODO: clear info panel!
       :stop  (fn [& _params] (js/console.log "Leaving home page"))}]}]
   ["patient"
    {:controllers
     [{:stop (fn [& _] (re-frame/dispatch [::patients-events/clear-patient-form]))}]}
    ["/create"
     {:name ::create-patient
      :page-title "Create new patient"
      :view patients-views-form/create-patient-view}]
    ["/update/:patient-id"
     {:name ::update-patient
      :page-title "Edit patient"
      :view patients-views-form/update-patient-view
      :parameters {:path {:patient-id integer?}}
      :controllers [{:identity identity  ; pass the whole match to controller
                     :start (fn [{{{:keys [patient-id]} :path} :parameters}]
                              (re-frame/dispatch
                                [::patients-events/get-patient-detail patient-id]))}]}]]])


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
