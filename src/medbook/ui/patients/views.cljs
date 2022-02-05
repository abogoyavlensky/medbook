(ns medbook.ui.patients.views
  (:require [re-frame.core :as re-frame]
            [reitit.core :as reitit]
            [reitit.frontend.easy :as reitit-easy]
            [reagent.core :as reagent]))


(defn- create-patient-btn
  []
  [:a
   {:href (reitit-easy/href :medbook.ui.router/create-patient)
    :class ["btn" "btn-primary" "col-2"]}
   "Create patient"])


(defn patient-list
  "Render patient list page."
  [{:keys [current-route]}]
  (let [page-title (get-in current-route [:data :page-title])]
        ;tickets (re-frame/subscribe [:patients])
        ;loading? (re-frame/subscribe [:patients-loading?])]
    [:div
     {:class ["container"]}
     [:div
      {:class ["columns"]}
      [:h2
       {:class ["col-2" "col-mr-auto"]}
       page-title]
      [create-patient-btn]]]))
     ;[:div]]))
      ;(if (true? @loading?)
      ;  [:p "Loading..."]]]))
       ;(if (some? @error)
       ;  [:p @error]
       ;  (render-tickets-table @tickets)))]]))


(defn create-patient
  "Render creating patient page."
  [{:keys [current-route]}]
  (let [page-title (get-in current-route [:data :page-title])]
    [:div
     [:h2 page-title]]))

