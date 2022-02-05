(ns medbook.patients.handlers
  (:require [ring.util.response :as ring-response]
            [medbook.patients.sql :as sql]))


(defn patient-list
  "Return patients data from db."
  [{:keys [context _parameters] :as _request}]
  (let [patients (sql/get-patient-list! (:db context))]
    (ring-response/response patients)))
