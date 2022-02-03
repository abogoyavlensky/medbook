(ns medbook.ui.views
  (:require [re-frame.core :as re-frame]
            [medbook.ui.subs :as subs]))


(defn home-page
  []
  [:div
   {:class ["container" "grid-lg"]}
   [:h1 "Home page!"]])


(defn router-component
  [{:keys [_router]}]
  (let [current-page @(re-frame/subscribe [::subs/current-page])]
    [:div
     (when current-page
       [(-> current-page :data :view)])]))
