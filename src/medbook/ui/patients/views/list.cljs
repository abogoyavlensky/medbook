(ns medbook.ui.patients.views.list
  (:require [re-frame.core :as re-frame]
            [reitit.frontend.easy :as reitit-easy]
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
  (if (seq patients)
    [:div
     [:table
      {:class ["table"]}
      [:thead
       [:tr
        [:th "Full name"]
        [:th "Gender"]
        [:th "Birthday"]
        [:th "Address"]
        [:th "Insurance number"]]]
      [:tbody
       (map (partial render-patient-item nil) patients)]]]
    [empty-patients]))


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
     [:div
      (if (true? loading?)
        [:p "Loading..."]
        [render-patients-table patients])]]))
