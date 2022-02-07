(ns medbook.ui.patients.views.update
  (:require [reitit.frontend.easy :as reitit-easy]
            [re-frame.core :as re-frame]
            [medbook.ui.patients.subs :as subs]
            [medbook.ui.patients.events :as events]
            [medbook.ui.patients.views.common :as common]))


(defn update-patient-view
  "Render updating patient page."
  [{:keys [current-route]}]
  (let [page-title (get-in current-route [:data :page-title])]
    [:div
     [:h2 page-title]
     [:a
      {:href (reitit-easy/href :medbook.ui.router/home)}
      "<- Back to list"]
     [:div
      {:class ["columns"]}
      (let [patient-init-data @(re-frame/subscribe [::subs/patient-detail-current])]
        ; TODO: need to pre-fill update form with patient by id!
        [common/patient-form
         {:patient-init-data patient-init-data
          :submit-event ::events/update-patient}])]]))
