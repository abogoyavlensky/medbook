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
  (let [created-ticket (sql/create-patient! db body)]
    (ring-response/response created-ticket)))
