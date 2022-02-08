(ns medbook.ui.patients.views.form
  (:require [re-frame.core :as re-frame]
            [reagent.core :as reagent]
            [reitit.frontend.easy :as reitit-easy]
            [medbook.ui.patients.subs :as subs]
            [medbook.ui.patients.events :as events]))


(defn- error-hint
  [message]
  [:p
   {:class ["form-input-hint"]
    :key message}
   message])


(defn- input-field
  [{:keys [field label field-type submitting? errors pattern]}]
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
               :value @(re-frame/subscribe [::subs/patient-form-field field])
               :on-change #(re-frame/dispatch
                             [::events/update-patient-form-field field (-> % .-target .-value)])
               :disabled (true? submitting?)
               :class ["form-input"]}
        (some? pattern) (assoc :pattern pattern))]
     (map error-hint field-errors)]))


(defn- radio-on-change
  [field value data]
  (when (-> data .-target .-checked)
    (re-frame/dispatch
      [::events/update-patient-form-field field value])))


(def ^:private GENDER-VALUE-MALE 0)
(def ^:private GENDER-VALUE-FEMALE 1)


(defn- radio-input
  [label value field]
  [:label.form-radio
   [:input (cond-> {:type "radio"
                    :name (name field)
                    :value value
                    :checked (= value @(re-frame/subscribe [::subs/patient-form-field field]))
                    :on-change (partial radio-on-change field value)})]
   [:i.form-icon] label])


(defn- gender-input
  []
  [:div.form-group
   [:label.form-label "Gender"]
   [radio-input "Male" GENDER-VALUE-MALE :gender]
   [radio-input "Female" GENDER-VALUE-FEMALE :gender]])


(defn- form-error
  [error]
  [:div
   {:class ["toast" "toast-error" "col-12"]}
   error])


(defn patient-form
  "Render patient form for create or update view."
  [{:keys [patient-init-data submit-event]}]
  (let [];{:keys [full-name
        ;        gender
        ;        birthday
        ;        address
        ;        insurance-number]} @(re-frame/subscribe [::subs/patient-detail-current])
        ;data {:full-name full-name
        ;      :gender gender
        ;      :birthday birthday
        ;      :address address
        ;      :insurance-number insurance-number}
        ;params (reagent/atom {})]
        ;params patient-init-data]
    (fn []
      (let [errors @(re-frame/subscribe [::subs/patient-form-errors])
            patient-form-submitting? @(re-frame/subscribe [::subs/patient-form-submitting?])]
        [:div
         {:class ["column" "col-8"]}
         (when (seq (:form errors))
           (map form-error (:form errors)))
         [input-field {:field :full-name
                       :label "Full name"
                       :field-type "text"
                       :submitting? patient-form-submitting?
                       :errors errors}]
         [gender-input]
         [input-field {:field :birthday
                       :label "Birthday"
                       :field-type "date"
                       :submitting? patient-form-submitting?
                       :errors errors}]
         [input-field {:field :address
                       :label "Address"
                       :field-type "text"
                       :submitting? patient-form-submitting?
                       :errors errors}]
         [input-field {:field :insurance-number
                       :label "Insurance number"
                       :field-type "text"
                       :submitting? patient-form-submitting?
                       :pattern "^[\\d+]{16}$"
                       :errors errors}]
         [:button
          {:type :button
           :disabled (true? patient-form-submitting?)
           ;:on-click #(re-frame/dispatch [submit-event @params])
           ; TODO: patient id!
           :on-click #(re-frame/dispatch [submit-event])
           :class ["btn" "btn-primary" "btn-lg" "mt-2" "float-right"]}
          "Save"]
         [:a
          ;{:on-click #(re-frame/dispatch [::events/clear-patient-form])}
          {:href (reitit-easy/href :medbook.ui.router/home)
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
      (let [];patient-init-data (re-frame/subscribe [::subs/patient-detail-current])]
        [patient-form
         {;:patient-init-data patient-init-data
          :submit-event ::events/create-patient}])]]))


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
      (let [];patient-init-data (re-frame/subscribe [::subs/patient-detail-current])]
        ; TODO: need to pre-fill update form with patient by id!
        [patient-form
         {;:patient-init-data patient-init-data
          :submit-event ::events/update-patient}])]]))
