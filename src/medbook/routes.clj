(ns medbook.routes
  (:require [medbook.patients.handlers :as patients]))


(def api-routes
  "All API routes with handlers and specs."
  ["/patient" {:name ::patient-list
               :get {:handler patients/patient-list
                     :parameters {}}}])
