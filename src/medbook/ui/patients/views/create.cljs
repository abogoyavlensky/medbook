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
  [{:keys [params field label field-type submitting? errors]}]
  (let [field-name-str (name field)
        form-classes ["form-group"]
        form-classes* (if (seq errors)
                        (conj form-classes "has-error")
                        form-classes)]
    [:div
     {:class form-classes*}
     [:label
      {:for field-name-str
       :class ["form-label"]}
      label]
     [:input
      {:id field-name-str
       :name field-name-str
       :type field-type
       :value (get @params field)
       :on-change #(swap! params assoc field (-> % .-target .-value))
       :disabled (true? submitting?)
       :class ["form-input"]}]
     (map error-hint errors)]))


(defn- patient-form
  []
  (let [patient-form-submitting? @(re-frame/subscribe [::subs/patient-form-submitting?])
        params (reagent/atom {})]
        ;errors (re-frame/subscribe [::subs/patient-form-errors])]
    (fn []
      [:div
       {:class ["column" "col-8"]}
       ;(when (seq (:form @errors))
       ;  (map form-error (:form @errors))}
       [input-field {:params params
                     :field :full-name
                     :label "Full name"
                     :field-type "text"
                     :submitting? patient-form-submitting?}]
                     ;:errors (:title @errors)}]
       [input-field {:params params
                     :field :gender
                     :label "Gender"
                     :field-type "number"
                     :submitting? patient-form-submitting?}]
                     ;:errors (:description @errors)}]
       [input-field {:params params
                     :field :birthday
                     :label "Birthday"
                     :field-type "date"
                     :submitting? patient-form-submitting?}]
                     ;:errors (:applicant @errors)}]
       [input-field {:params params
                     :field :address
                     :label "Address"
                     :field-type "text"
                     :submitting? patient-form-submitting?}]
                     ;:errors (:executor @errors)}]
       [input-field {:params params
                     :field :insurance-number
                     :label "Insurance number"
                     :field-type "text"
                     :submitting? patient-form-submitting?}]
                     ;:errors (:completed-at @errors)}]
       [:button
        {:type :button
         :disabled (true? patient-form-submitting?)
         :on-click #(re-frame/dispatch [::events/create-patient @params])
         :class ["btn" "btn-primary" "btn-lg" "mt-2" "float-right"]}
        "Save"]
       [:a
        {:href (reitit-easy/href :medbook.ui.router/home)
         :class ["btn" "btn-lg" "mt-2" "float-right" "mr-2"]}
        "Cancel"]])))


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
