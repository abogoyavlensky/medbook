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
