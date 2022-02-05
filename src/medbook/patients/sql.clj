(ns medbook.patients.sql
  (:require [medbook.util.db :as db-util]))


(defn create-patient!
  "Create a patient in db with given params."
  [db params]
  (db-util/exec-one! db
    {:insert-into :patient
     :values [params]
     :on-conflict [:id]
     :do-nothing true
     :returning [:id :full-name :gender :birthday :address :insurance-number]}))


(defn get-patient-list!
  "Return patient data vector from db."
  [db]
  (db-util/exec! db
    {:select [:id :full-name :gender :birthday :address :insurance-number]
     :from [:patient]
     :order-by [[:created-at :desc]]}))


(defn get-patient-detail!
  "Return single patient data from db."
  [db patient-id]
  (db-util/exec-one! db
    {:select [:id :full-name :gender :birthday :address :insurance-number]
     :from [:patient]
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
