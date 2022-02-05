(ns medbook.ui.patients.views
  (:require [re-frame.core :as re-frame]
            [reitit.core :as reitit]
            [reitit.frontend.easy :as reitit-easy]
            [reagent.core :as reagent]
            [medbook.ui.patients.subs :as subs]))


(defn- create-patient-btn
  []
  [:a
   {:href (reitit-easy/href :medbook.ui.router/create-patient)
    :class ["btn" "btn-primary" "col-2"]}
   "Create patient"])


(defn- render-patient-item
  [active-patient-id patient]
  (let [tr-class (cond-> []
                   (and
                     (some? active-patient-id)
                     (= active-patient-id (:id patient))) (conj "active"))]
    [:tr
     {:key (:id patient)
      :class tr-class}
     [:td (:full-name patient)]
     [:td (:gender patient)]
     [:td (:birthday patient)]
     [:td (:address patient)]
     [:td (:insurance-number patient)]]))


(defn- empty-patients
  []
  [:div.empty
   [:p.empty-title.h5 "There are no patients yet."]
   [:p.empty-subtitle
    "Please create a new patient by clicking \"Create patient\" button above."]])


(defn- render-patients-table
  [patients]
  (let [];ticket-new-id (re-frame/subscribe [:ticket-new-id])]
    (if (seq patients)
      [:div
       ;(when (some? @ticket-new-id)
       ;  [:div.toast.toast-success
       ;   [:button.btn.btn-clear.float-right
       ;    {:on-click #(re-frame/dispatch [:event/clear-ticket-new-id])}]
       ;   [:p "New ticket has been created successfully!"]])
       [:table
        {:class ["table"]}
        [:thead
         [:tr
          [:th "Title"]
          [:th "Description"]
          [:th "Applicant"]
          [:th "Executor"]
          [:th "Completion date"]]]
        [:tbody
         (map (partial render-patient-item nil #_@ticket-new-id) patients)]]]
      [empty-patients])))


(defn patient-list-view
  "Render patient list page."
  [{:keys [current-route]}]
  (let [page-title (get-in current-route [:data :page-title])
        patients @(re-frame/subscribe [::subs/patients])
        loading? @(re-frame/subscribe [::subs/patients-loading?])]
    [:div
     {:class ["container"]}
     [:div
      {:class ["columns"]}
      [:h2
       {:class ["col-2" "col-mr-auto"]}
       page-title]
      [create-patient-btn]]
     ;[:div
     ; (if (true? @loading?)
     ;   [:p "Loading..."]]]
       ;(if (some? @error)
       ;  [:p @error]
     [render-patients-table patients]]))


(defn create-patient-view
  "Render creating patient page."
  [{:keys [current-route]}]
  (let [page-title (get-in current-route [:data :page-title])]
    [:div
     [:h2 page-title]]))

