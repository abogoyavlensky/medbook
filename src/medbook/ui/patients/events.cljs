(ns medbook.ui.patients.events
  (:require [re-frame.core :as re-frame]
            [ajax.core :as ajax]
            ; import http-fx to register http-xhrio events
            [day8.re-frame.http-fx]))


(re-frame/reg-event-db
  ::get-patients-success
  (fn [db [_ patients]]
    (-> db
      (assoc :patients patients)
      (assoc :patients-loading? false))))


(re-frame/reg-event-db
  ::get-patients-error
  (fn [db [_ _]]
    (-> db
      (assoc :patients-error (str "Error happened while fetching patients. "
                               "Please try to reload the page."))
      (assoc :patients-loading? false))))


(re-frame/reg-event-fx
  ::get-patients
  (fn [{:keys [db]} _]
    {:db (-> db
           (assoc :patients-loading? true)
           (assoc :patients-error nil))
     :http-xhrio {:method :get
                  ; TODO: update to shared routes for api!
                  :uri "/api/v1/patients"
                  :format (ajax/json-request-format)
                  :response-format (ajax/json-response-format {:keywords? true})
                  :on-success [::get-patients-success]
                  :on-failure [::get-patients-error]}}))


(re-frame/reg-event-fx
  ::create-patient
  (fn [{:keys [db]} [_ params]]
    {:db (-> db
           (assoc :patient-form-submitting? true)
           (assoc :patient-form-errors nil))
     :http-xhrio {:method :post
                  ; TODO: update to shared routes for api!
                  :uri "/api/v1/patients"
                  :format (ajax/json-request-format)
                  :params params
                  :response-format (ajax/json-response-format {:keywords? true})
                  :on-success [::create-patient-success]
                  :on-failure [::create-patient-error]}}))


(re-frame/reg-event-fx
  ::create-patient-success
  (fn [{:keys [db]} [_ response]]
    {:db (-> db
           (assoc :patient-form-submitting? false)
           (assoc :patient-form-errors nil)
           (assoc :patient-new response))
     :fx/push-state {:route :medbook.ui.router/home}}))


(re-frame/reg-event-db
  ::create-patient-error
  (fn [db [_ {{:keys [messages]} :response}]]
    (-> db
      (assoc :patient-form-submitting? false)
      (assoc :patient-form-errors messages))))


(re-frame/reg-event-db
  ::clear-patient-new
  (fn [db [_ _]]
    (-> db
      (assoc :patient-new nil))))


(re-frame/reg-event-fx
  ::clear-patient-form
  (fn [{:keys [db]} [_ _]]
    {:db (-> db
           (assoc :patient-form-errors nil))
     :fx/push-state {:route :medbook.ui.router/home}}))
