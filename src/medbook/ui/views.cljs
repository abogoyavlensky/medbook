(ns medbook.ui.views
  (:require [re-frame.core :as re-frame]
            [reitit.frontend.easy :as reitit-easy]
            [medbook.ui.subs :as subs]
            [medbook.ui.events :as events]))


(defn- header
  []
  [:h1
   [:a
    {:href (reitit-easy/href :medbook.ui.router/home)}
    "MedBook"]])


(defn- page-not-found
  []
  [:div
   [:h2 "Page not found."]
   [:a
    {:href (reitit-easy/href :medbook.ui.router/home)
     :class ["btn"]}
    "-> Home page"]])


(defn info-panel
  "Show info message on the page if it exists."
  []
  (let [info-message @(re-frame/subscribe [::subs/info-message])]
    (when (some? info-message)
      [:div.toast.toast-success
       {:class ["mb-2"]}
       [:button.btn.btn-clear.float-right
        {:on-click #(re-frame/dispatch [::events/clear-info-message])}]
       [:p info-message]])))


(defn router-component
  "Component for routing ui navigation."
  [{:keys [router]}]
  (let [current-route @(re-frame/subscribe [::subs/current-page])]
    [:div
     {:class ["container" "grid-lg"]}
     [header]
     [info-panel]
     (if current-route
       [(-> current-route :data :view) {:router router
                                        :current-route current-route}]
       [page-not-found])]))
