(ns medbook.ui.db)


(def default-db
  "Main data for the app."
  {:current-page nil
   :patients []
   :patients-loading? false
   :patient-form-submitting? false
   :patients-error nil
   :patient-form-errors nil
   :patient-new nil})
