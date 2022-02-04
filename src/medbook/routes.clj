(ns medbook.routes
  (:require [medbook.patients :as patients]))


(def api-routes
  ["/patient" {:name ::patient-list
               :get {:handler patients/patient-list
                     :parameters {}}}])
