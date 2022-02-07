(ns medbook.routes
  (:require [clojure.spec.alpha :as s]
            [clojure.string :as str]
            [spec-tools.core :as st]
            [medbook.patients.handlers :as patients]))


(def ^:private MALE-GENDER 0)
(def ^:private FEMALE-GENDER 1)


(defn- gender->db
  [_ value]
  (try
    (Integer. value)
    (catch NumberFormatException _
      ; will be caught by spec
      nil)))


(s/def ::not-empty-string
  (fn [value]
    (and
      (string? value)
      (boolean (seq (str/trim value))))))


(def ^:private INSURANCE-NUMBER-LENGTH 16)

(s/def ::id pos-int?)
(s/def ::full-name ::not-empty-string)


(s/def ::gender
  (st/spec
    {:spec #{MALE-GENDER FEMALE-GENDER}
     :decode/json gender->db}))


(s/def ::birthday inst?)
(s/def ::address ::not-empty-string)


(defn- insurance-number-length-valid?
  [value]
  (= INSURANCE-NUMBER-LENGTH (count value)))


(defn- insurance-number-length-digits?
  [value]
  (some? (re-matches #"^[\d+]{16}$" value)))


(s/def ::insurance-number
  (s/and
    ::not-empty-string
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
