(ns medbook.patients.handlers
  (:require [ring.util.response :as ring-response]
            [medbook.patients.sql :as sql]))


(defn patient-list
  "Return patients data from db."
  [{{:keys [db]} :context}]
  (let [patients (sql/get-patient-list! db)]
    (ring-response/response patients)))


(defn create-patient!
  "Create patient to db by given params."
  [{{:keys [db]} :context
    {:keys [body]} :parameters}]
  (let [created-patient (sql/create-patient! db body)]
    (ring-response/response created-patient)))


(defn patient-detail
  "Return data for particular patient by id from db."
  [{{:keys [db]} :context
    {{:keys [patient-id]} :path} :parameters}]
  (let [patient (sql/get-patient-detail! db patient-id)]
    (ring-response/response patient)))


(defn update-patient!
  "Create patient to db by given params."
  [{{:keys [db]} :context
    {:keys [body]
     {:keys [patient-id]} :path} :parameters}]
  (let [patient (sql/update-patient! db patient-id body)]
    (ring-response/response patient)))
