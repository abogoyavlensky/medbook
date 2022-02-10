(ns medbook.patients.handlers
  (:require [clojure.spec.alpha :as s]
            [clojure.instant :as instant]
            [ring.util.response :as ring-response]
            [slingshot.slingshot :refer [throw+]]
            [medbook.patients.sql :as sql]))


(defn- date->string
  [date-inst]
  (format "%1$tY-%1$tm-%1$td" date-inst))


(defn- string->date
  [date-str]
  (instant/read-instant-date date-str))


(s/def ::birthday
  (s/conformer
    string->date
    date->string))


(s/def ::patient->response
  (s/keys
    :req-un [::birthday]))


(s/def ::patient-list->response
  (s/coll-of ::patient->response :kind vector?))


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
                          (s/conform ::patient->response)
                          (sql/create-patient! db)
                          (s/unform ::patient->response))]
    (ring-response/response created-patient)))


(defn patient-detail
  "Return data for particular patient by id from db."
  [{{:keys [db]} :context
    {{:keys [patient-id]} :path} :parameters}]
  (let [patient (->> (sql/get-patient-detail! db patient-id)
                  (s/unform ::patient->response))]
    (if (some? patient)
      (ring-response/response patient)
      (throw+ {:type :medbook.handler/error
               :messages {:common ["Patient does not exit."]}}))))


(defn update-patient!
  "Create patient to db by given params."
  [{{:keys [db]} :context
    {:keys [body]
     {:keys [patient-id]} :path} :parameters}]
  (let [patient (->> body
                  (s/conform ::patient->response)
                  (sql/update-patient! db patient-id)
                  (s/unform ::patient->response))]
    (ring-response/response patient)))


(defn deletion-success-response
  "Return 204 response status without body"
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
             :messages {:common ["Patient does not exit."]}})))


(comment
  (require '[integrant.repl.state :as ig-state])

  (let [db (get ig-state/system :medbook.db/db)
        id 40]
    (->> 40
      (sql/get-patient-detail! db)
      (s/conform ::patient->response))))
