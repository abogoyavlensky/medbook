(ns medbook.ui.patients.events
  (:require [re-frame.core :as re-frame]
            [ajax.core :as ajax]
            ; import http-fx to register http-xhrio events
            [day8.re-frame.http-fx]))


(re-frame/reg-event-db
  ::get-patients-success
  (fn [db [_ patients]]
    (assoc db
      :patients patients
      :patients-loading? false)))


(re-frame/reg-event-db
  ::get-patients-error
  (fn [db [_ _]]
    (assoc db
      :patients-error (str "Error happened while fetching patients. "
                        "Please try to reload the page.")
      :patients-loading? false)))


(re-frame/reg-event-fx
  ::get-patients
  (fn [{:keys [db]} _]
    {:db (assoc db
           :patients-loading? true
           :patients-error nil)
     :http-xhrio {:method :get
                  ; TODO: update to shared routes for api!
                  :uri "/api/v1/patients"
                  :format (ajax/json-request-format)
                  :response-format (ajax/json-response-format {:keywords? true})
                  :on-success [::get-patients-success]
                  :on-failure [::get-patients-error]}}))


(re-frame/reg-event-fx
  ::get-patient-detail
  (fn [{:keys [db]} [_ patient-id]]
    {:db (assoc db :patient-detail-loading? true)
     ; TODO: uncomment!
     ;(assoc :patients-error nil))
     :http-xhrio {:method :get
                  ; TODO: update to shared routes for api!
                  :uri (str "/api/v1/patients/" patient-id)
                  :format (ajax/json-request-format)
                  :response-format (ajax/json-response-format {:keywords? true})
                  :on-success [::get-patient-detail-success]
                  :on-failure [::get-patient-detail-error]}}))


(re-frame/reg-event-db
  ::get-patient-detail-success
  (fn [db [_ patient]]
    (assoc db
      :patient-form patient
      :patient-detail-loading? false)))


(re-frame/reg-event-db
  ::get-patient-detail-error
  (fn [db [_ _]]
    (assoc db
      :patients-error (str "Error happened while fetching a patient. "
                        "Please try to reload the page.")
      :patient-detail-loading? false)))


(re-frame/reg-event-fx
  ::create-patient
  (fn [{:keys [db]} _]
    {:db (assoc db
           :patient-form-submitting? true
           :patient-form-errors nil)
     :http-xhrio {:method :post
                  ; TODO: update to shared routes for api!
                  :uri "/api/v1/patients"
                  :format (ajax/json-request-format)
                  :params (:patient-form db)
                  :response-format (ajax/json-response-format {:keywords? true})
                  :on-success [::create-patient-success]
                  :on-failure [::create-patient-error]}}))


(re-frame/reg-event-fx
  ::create-patient-success
  (fn [{:keys [db]} [_ response]]
    {:db (assoc db
           :patient-form-submitting? false
           :patient-form-errors nil
           :info-message (str "New patient " (:full-name response) " has been created successfully!")
           :patient-form nil)
     :fx/push-state {:route :medbook.ui.router/home}}))


(re-frame/reg-event-db
  ::create-patient-error
  (fn [db [_ {{:keys [messages]} :response}]]
    (assoc db
      :patient-form-submitting? false
      :patient-form-errors messages)))


(re-frame/reg-event-db
  ::clear-patient-form
  (fn [db [_ _]]
    (assoc db
      :patient-form {:full-name ""
                     ; TODO: import const var!
                     :gender 0
                     :birthday ""
                     :address ""
                     :insurance-number ""}
      :patient-form-errors nil)))


(re-frame/reg-event-db
  ::update-patient-form-field
  (fn [db [_ field value]]
    (assoc-in db [:patient-form field] value)))


(re-frame/reg-event-fx
  ::update-patient
  (fn [{:keys [db]} _]
    {:db (assoc db
           :patient-form-submitting? true
           :patient-form-errors nil)
     :http-xhrio {:method :put
                  ; TODO: update to shared routes for api!
                  :uri (str "/api/v1/patients/" (get-in db [:patient-form :id]))
                  :format (ajax/json-request-format)
                  ;:params params
                  :params (dissoc (:patient-form db) :id)
                  :response-format (ajax/json-response-format {:keywords? true})
                  :on-success [::update-patient-success]
                  :on-failure [::update-patient-error]}}))


(re-frame/reg-event-fx
  ::update-patient-success
  (fn [{:keys [db]} [_ response]]
    {:db (assoc db
           :patient-form-submitting? false
           :patient-form-errors nil
           :info-message (str "New patient " (:full-name response) " has been updated successfully!")
           :patient-form {})
     :fx/push-state {:route :medbook.ui.router/home}}))


(re-frame/reg-event-db
  ::update-patient-error
  (fn [db [_ {{:keys [messages]} :response}]]
    (assoc db
      :patient-form-submitting? false
      :patient-form-errors messages)))


(re-frame/reg-event-fx
  ::delete-patient
  (fn [{:keys [db]} [_ patient-id]]
    {:db (assoc db
           :patient-delete-submitting? true
           :patient-delete-errors nil)
     :http-xhrio {:method :delete
                  ; TODO: update to shared routes for api!
                  :uri (str "/api/v1/patients/" patient-id)
                  :format (ajax/json-request-format)
                  :response-format (ajax/json-response-format {:keywords? true})
                  :on-success [::delete-patient-success]
                  :on-failure [::delete-patient-error]}}))


(re-frame/reg-event-fx
  ::delete-patient-success
  (fn [{:keys [db]} [_ _response]]
    {:db (assoc db :patient-delete-submitting? false
           :patient-delete-errors nil
           :info-message "Patient has been deleted successfully.")
     :fx/push-state {:route :medbook.ui.router/home}}))


(re-frame/reg-event-db
  ::delete-patient-error
  (fn [db [_ {{:keys [messages]} :response}]]
    (assoc db :patient-delete-submitting? false
      :patient-delete-errors messages)))
