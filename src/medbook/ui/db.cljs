(ns medbook.ui.db)


(def default-db
  "Main data for the app."
  {:current-page nil
   :patients []
   :patients-loading? false
   :patient-form-submitting? false

   ; TODO: use it!
   :patients-error nil

   :patient-form-errors nil
   :patient-new nil
   :patient-detail-current {:full-name ""
                            :gender "0"
                            :birthday ""
                            :address ""
                            :insurance-number ""}
   :patient-detail-loading? false

   :patient-form {:full-name ""
                  ; TODO: import const var!
                  :gender 0
                  :birthday ""
                  :address ""
                  :insurance-number ""}})
