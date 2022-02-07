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
   :patient-detail-current nil
   :patient-detail-loading? false})
