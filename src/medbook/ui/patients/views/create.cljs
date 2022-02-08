(ns medbook.ui.patients.views.create
  (:require [reitit.frontend.easy :as reitit-easy]
            [medbook.ui.patients.events :as events]
            [medbook.ui.patients.views.common :as common]))


(defn create-patient-view
  "Render creating patient page."
  [{:keys [current-route]}]
  (let [page-title (get-in current-route [:data :page-title])]
    [:div
     [:h2 page-title]
     [:a
      {:href (reitit-easy/href :medbook.ui.router/home)}
      "<- Back to list"]
     [:div
      {:class ["columns"]}
      (let [patient-init-data {:full-name ""
                               :gender "0"
                               :birthday ""
                               :address ""
                               :insurance-number ""}]
        [common/patient-form
         {:patient-init-data patient-init-data
          :submit-event ::events/create-patient}])]]))
