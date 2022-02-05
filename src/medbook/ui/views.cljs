(ns medbook.ui.views
  (:require [re-frame.core :as re-frame]
            [medbook.ui.subs :as subs]))


(defn home-page
  "View for home page."
  []
  [:div
   {:class ["container" "grid-lg"]}
   [:h1 "Home page!"]])


(defn router-component
  "Component for routing ui navigation."
  [{:keys [_router]}]
  (let [current-page @(re-frame/subscribe [::subs/current-page])]
    [:div
     (when current-page
       [(-> current-page :data :view)])]))
