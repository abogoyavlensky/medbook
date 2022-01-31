(ns medbook.patients
  (:require [ring.util.response :as ring-response]))


(defn patient-list
  "Patient list handler."
  [{:keys [_context _parameters] :as _request}]
  ; TODO: add json middleware to api router config!
  (ring-response/response "{:status :ok}"))
