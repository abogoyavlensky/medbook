(ns medbook.ui.patients.views.create
  (:require [reitit.frontend.easy :as reitit-easy]
            [re-frame.core :as re-frame]
            [reagent.core :as reagent]
            [medbook.ui.patients.subs :as subs]
            [medbook.ui.patients.events :as events]))


(defn- error-hint
  [message]
  [:p
   {:class ["form-input-hint"]
    :key message}
   message])


(defn- input-field
  [{:keys [params field label field-type submitting? errors pattern]}]
  (let [field-name-str (name field)
        form-classes ["form-group"]
        field-errors (get errors field)
        form-classes* (if (seq field-errors)
                        (conj form-classes "has-error")
                        form-classes)]
    [:div
     {:class form-classes*}
     [:label
      {:for field-name-str
       :class ["form-label"]}
      label]
     [:input
      (cond-> {:id field-name-str
               :name field-name-str
               :type field-type
               :value (get @params field)
               :on-change #(swap! params assoc field (-> % .-target .-value))
               :disabled (true? submitting?)
               :class ["form-input"]}
        (some? pattern) (assoc :pattern pattern))]
     (map error-hint field-errors)]))


(defn- form-error
  [error]
  [:div
   {:class ["toast" "toast-error" "col-12"]}
   error])


(defn- patient-form
  []
  (let [params (reagent/atom {:full-name ""
                              :gender ""
                              :birthday ""
                              :address ""
                              :insurance-number ""})]
    (fn []
      (let [errors @(re-frame/subscribe [::subs/patient-form-errors])
            patient-form-submitting? @(re-frame/subscribe [::subs/patient-form-submitting?])]
        [:div
         {:class ["column" "col-8"]}
         (when (seq (:form errors))
           (map form-error (:form errors)))
         [input-field {:params params
                       :field :full-name
                       :label "Full name"
                       :field-type "text"
                       :submitting? patient-form-submitting?
                       :errors errors}]
         [input-field {:params params
                       :field :gender
                       :label "Gender"
                       :field-type "number"
                       :submitting? patient-form-submitting?
                       :errors errors}]
         [input-field {:params params
                       :field :birthday
                       :label "Birthday"
                       :field-type "date"
                       :submitting? patient-form-submitting?
                       :errors errors}]
         [input-field {:params params
                       :field :address
                       :label "Address"
                       :field-type "text"
                       :submitting? patient-form-submitting?
                       :errors errors}]
         [input-field {:params params
                       :field :insurance-number
                       :label "Insurance number"
                       :field-type "text"
                       :submitting? patient-form-submitting?
                       :pattern "^[\\d+]{16}$"
                       :errors errors}]
         [:button
          {:type :button
           :disabled (true? patient-form-submitting?)
           :on-click #(re-frame/dispatch [::events/create-patient @params])
           :class ["btn" "btn-primary" "btn-lg" "mt-2" "float-right"]}
          "Save"]
         [:a
          {:on-click #(re-frame/dispatch [::events/clear-patient-form])
           :class ["btn" "btn-lg" "mt-2" "float-right" "mr-2"]}
          "Cancel"]]))))


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
      [patient-form]]]))
