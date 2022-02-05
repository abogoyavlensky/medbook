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


(def api-routes
  "All API routes with handlers and specs."
  ["/patient" {:name ::patient-list
               :get {:handler patients/patient-list
                     :responses {200 {:body ::patients}}}
               :post {:handler patients/create-patient!
                      :parameters {:body ::patient}
                      :responses {200 {:body ::patient-with-id}}}}])
