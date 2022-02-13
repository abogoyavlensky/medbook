(ns medbook.routes
  (:require [medbook.spec :as spec]
            [medbook.patients.handlers :as patients]))


(def api-routes
  "All API routes with handlers and specs."
  ["/api"
   ["/v1"
    ["/patients"
     ["" {:name ::patient-list
          :get {:handler patients/patient-list
                :responses {200 {:body ::spec/patients}}}
          :post {:handler patients/create-patient!
                 :parameters {:body ::spec/patient}
                 :responses {200 {:body ::spec/patient-with-id}}}}]
     ["/:patient-id" {:name ::patient-detail
                      :get {:handler patients/patient-detail
                            :parameters {:path {:patient-id ::spec/id}}
                            :responses {200 {:body ::spec/patient-with-id}}}
                      :put {:handler patients/update-patient!
                            :parameters {:path {:patient-id ::spec/id}
                                         :body ::spec/patient-update}
                            :responses {200 {:body ::spec/patient-with-id}}}
                      :delete {:handler patients/delete-patient!
                               :parameters {:path {:patient-id ::spec/id}}
                               :responses {204 {:body nil?}}}}]]]])
