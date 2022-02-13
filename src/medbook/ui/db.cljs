(ns medbook.ui.db
  (:require [medbook.ui.patients.consts :as consts]))


(def default-db
  "Main data for the app."
  {:current-page nil
   :patients []
   :patients-loading? false
   :patient-form-submitting? false
   :patient-form {:full-name ""
                  :gender consts/GENDER-VALUE-MALE
                  :birthday ""
                  :address ""
                  :insurance-number ""}
   :patient-form-errors nil
   :error-message nil

   ; TODO: use it!
   :patient-detail-loading? false

   ; TODO: use it!
   :patient-delete-submitting? false

   ; TODO: delete!
   :patient-delete-errors nil

   :info-message nil})
