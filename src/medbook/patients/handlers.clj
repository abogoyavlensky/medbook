(ns medbook.patients.handlers
  (:require [ring.util.response :as ring-response]
            [medbook.patients.sql :as sql]))


(defn patient-list
  "Return patients data from db."
  [{:keys [context _parameters] :as _request}]
  ; TODO: add validation output with spec!
  (let [patients (sql/get-patient-list! (:db context))]
    (->> patients
         ;(map #(s/unform ::patient-with-id %))
      (ring-response/response))))
