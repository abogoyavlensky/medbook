(ns medbook.patients.sql
  (:require [medbook.util.db :as db-util]))


(defn get-patient-list!
  [db]
  (db-util/exec! db
    {:select [:id :full-name :gender :birthday :address :insurance-number]
     :from [:patient]
     :order-by [[:created-at :desc]]}))


(defn create-patient!
  [db params]
  ; TODO: add validation with spec!
  (db-util/exec-one! db
    {:insert-into :patient
     :values [params]
     :on-conflict [:id]
     :do-nothing true}))

; Create patient
(comment
  (require '[integrant.repl.state :as ig-state])
  (let [db (get ig-state/system :medbook.db/db)
        patient-data {:full-name "Some Full Name"
                      :gender 1
                      :birthday #inst "1990-02-02"
                      :address "Moscow, st ..."
                      :insurance-number "0000111122223333"}]
    (create-patient! db patient-data)))
