(ns medbook.patients.sql
  (:require [clojure.string :as str]
            [medbook.util.db :as db-util]
            [slingshot.slingshot :refer [throw+]])
  (:import [org.postgresql.util PSQLException]))


(def ^:private patient-fields
  [:id :full-name :gender :birthday :address :insurance-number])


(defn create-patient!
  "Create a patient in db with given params."
  [db params]
  (try
    (db-util/exec-one! db
      {:insert-into :patient
       :values [params]
       :on-conflict [:id]
       :do-nothing true
       :returning patient-fields})
    (catch PSQLException e
      (let [msg (ex-message e)
            unique-err-pattern "duplicate key value violates unique constraint"]
        (when (str/includes? msg unique-err-pattern)
          (let [unique-err-msg "Insurance number already exists."]
            (throw+ {:type :medbook.handler/error
                     :messages {:insurance-number [unique-err-msg]}})))
        (throw e)))))


(defn get-patient-list
  "Return patient data vector from db."
  [db]
  (db-util/exec! db
    {:select patient-fields
     :from [:patient]
     :order-by [[:created-at :desc]]}))


(defn get-patient-detail
  "Return single patient data from db."
  [db patient-id]
  (db-util/exec-one! db
    {:select patient-fields
     :from [:patient]
     :where [:= :id patient-id]}))


(defn update-patient!
  "Update an existing patient in db with given params."
  [db patient-id params]
  (try
    (db-util/exec-one! db
      {:update :patient
       :set params
       :where [:= :id patient-id]
       :returning patient-fields})
    (catch PSQLException e
      (let [msg (ex-message e)
            unique-err-pattern "duplicate key value violates unique constraint"]
        (when (str/includes? msg unique-err-pattern)
          (let [unique-err-msg "Insurance number already exists."]
            (throw+ {:type :medbook.handler/error
                     :messages {:insurance-number [unique-err-msg]}})))
        (throw e)))))


(defn delete-patient!
  "Delete patient from db."
  [db patient-id]
  (db-util/exec-one! db
    {:delete-from :patient
     :where [:= :id patient-id]}))


; Create patient
(comment
  (require '[integrant.repl.state :as ig-state])

  (let [db (get ig-state/system :medbook.db/db)
        patient-data {:full-name "Some Full Name 3"
                      :gender 1
                      :birthday #inst "1990-02-02"
                      :address "Moscow, st ..."
                      :insurance-number "0000111122223333"}]
    (create-patient! db patient-data)))
