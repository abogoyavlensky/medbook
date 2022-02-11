(ns medbook.ui.views
  (:require [re-frame.core :as re-frame]
            [reitit.frontend.easy :as reitit-easy]
            [medbook.ui.subs :as subs]))


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


(defn router-component
  "Component for routing ui navigation."
  [{:keys [router]}]
  (let [current-route @(re-frame/subscribe [::subs/current-page])]
    [:div
     {:class ["container" "grid-lg"]}
     [header]
     (if current-route
       [(-> current-route :data :view) {:router router
                                        :current-route current-route}]
       [page-not-found])]))
