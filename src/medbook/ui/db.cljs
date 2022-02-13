(ns medbook.ui.db
  (:require [medbook.ui.patients.consts :as consts]))


(def default-db
  "Main data for the app."
  {:current-page nil
   :error-message nil
   :info-message nil
   ; patients
   :patients []
   :patients-loading? false
   :patient-form-submitting? false
   :patient-form {:full-name ""
                  :gender consts/GENDER-VALUE-MALE
                  :birthday ""
                  :address ""
                  :insurance-number ""}
   :patient-form-errors nil
   :patient-detail-loading? false})
