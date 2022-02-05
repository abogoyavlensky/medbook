(ns medbook.routes
  (:require [clojure.spec.alpha :as s]
            [medbook.patients.handlers :as patients]))


(s/def ::id pos-int?)
(s/def ::full-name string?)
(s/def ::gender integer?)
(s/def ::birthday inst?)
(s/def ::address string?)
(s/def ::insurance-number string?)


(s/def ::patient
  (s/keys
    :req-un [::full-name
             ::gender
             ::birthday
             ::address
             ::insurance-number]
    :opt-un [::id]))


(s/def ::patient-with-id
  (s/merge
    ::patient
    (s/keys
      :opt-un [::id])))


(s/def ::patients
  (s/coll-of ::patient-with-id :kind vector?))


(s/def ::patient-update
  (s/and
    (s/keys
      :opt-un [::full-name
               ::gender
               ::birthday
               ::address
               ::insurance-number])
    (s/map-of keyword? any? :min-count 1)))


(def api-routes
  "All API routes with handlers and specs."
  ["/api"
   ["/v1"
    ["/patients"
     ["" {:name ::patient-list
          :get {:handler patients/patient-list
                :responses {200 {:body ::patients}}}
          :post {:handler patients/create-patient!
                 :parameters {:body ::patient}
                 :responses {200 {:body ::patient-with-id}}}}]
     ["/:patient-id" {:name ::patient-detail
                      :get {:handler patients/patient-detail
                            :parameters {:path {:patient-id ::id}}
                            :responses {200 {:body ::patient-with-id}}}
                      :put {:handler patients/update-patient!
                            :parameters {:path {:patient-id ::id}
                                         :body ::patient-update}
                            :responses {200 {:body ::patient-with-id}}}}]]]])
