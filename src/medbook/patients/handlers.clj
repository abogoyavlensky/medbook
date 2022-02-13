(ns medbook.patients.handlers
  (:require [clojure.spec.alpha :as s]
            [ring.util.response :as ring-response]
            [slingshot.slingshot :refer [throw+]]
            [medbook.patients.sql :as sql]
            [medbook.patients.serializers :as serializers]))


(defn patient-list
  "Return patients data from db."
  [{{:keys [db]} :context}]
  (let [patients (->> (sql/get-patient-list! db)
                   (s/unform ::patient-list->response))]
    (ring-response/response patients)))


(defn create-patient!
  "Create patient to db by given params."
  [{{:keys [db]} :context
    {:keys [body]} :parameters}]
  (let [created-patient (->> body
                          (s/conform ::serializers/patient->response)
                          (sql/create-patient! db)
                          (s/unform ::serializers/patient->response))]
    (ring-response/response created-patient)))


(defn patient-detail
  "Return data for particular patient by id from db."
  [{{:keys [db]} :context
    {{:keys [patient-id]} :path} :parameters}]
  (let [patient (->> (sql/get-patient-detail! db patient-id)
                  (s/unform ::serializers/patient->response))]
    (if (some? patient)
      (ring-response/response patient)
      (throw+ {:type :medbook.handler/error
               :messages {:common ["Patient does not exist."]}}))))


(defn update-patient!
  "Create patient to db by given params."
  [{{:keys [db]} :context
    {:keys [body]
     {:keys [patient-id]} :path} :parameters}]
  (let [patient (->> body
                  (s/conform ::serializers/patient->response)
                  (sql/update-patient! db patient-id)
                  (s/unform ::serializers/patient->response))]
    (ring-response/response patient)))


(defn- deletion-success-response
  []
  {:status  204
   :headers {}
   :body    nil})


(defn delete-patient!
  "Delete patient from db by given id."
  [{{:keys [db]} :context
    {{:keys [patient-id]} :path} :parameters}]
  (if (some? (sql/get-patient-detail! db patient-id))
    (do
      (sql/delete-patient! db patient-id)
      (deletion-success-response))
    (throw+ {:type :medbook.handler/error
             :messages {:common ["Patient does not exist."]}})))
