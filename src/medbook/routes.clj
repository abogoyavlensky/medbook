(ns medbook.routes
  (:require [clojure.spec.alpha :as s]
            [spec-tools.core :as st]
            [medbook.patients.handlers :as patients]))


(def ^:private INSURANCE-NUMBER-LENGTH 16)

(s/def ::id pos-int?)
(s/def ::full-name string?)


(s/def ::gender
  (st/spec
    {:spec #{0 1}
     :decode/json #(if (string? %2) (Integer. %2) %2)}))


(s/def ::birthday inst?)
(s/def ::address string?)


(defn- insurance-number-length-valid?
  [value]
  (= INSURANCE-NUMBER-LENGTH (count value)))


(defn- insurance-number-length-digits?
  [value]
  (some? (re-matches #"^[\d+]{16}$" value)))


(s/def ::insurance-number
  (s/and
    string?
    insurance-number-length-valid?
    insurance-number-length-digits?))


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
