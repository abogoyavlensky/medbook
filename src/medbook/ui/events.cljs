(ns medbook.ui.events
  (:require [re-frame.core :as re-frame]
            ;; import http-fx to register events
            [day8.re-frame.http-fx]
            [reitit.frontend.controllers :as reitit-controllers]
            [medbook.ui.db :as db]))


(re-frame/reg-event-db
  ::initialize-db
  (fn [_ _]
    db/default-db))


(re-frame/reg-event-db
  ::navigate
  (fn [db [_ new-match]]
    (let [old-match   (:current-page db)
          controllers (reitit-controllers/apply-controllers
                        (:controllers old-match)
                        new-match)]
      (assoc db :current-page (assoc new-match :controllers controllers)))))


;; Inspect app-db state
(comment
  (require '[re-frame.db :as rf-db])
  (swap! rf-db/app-db assoc :name "Some name")
  (deref rf-db/app-db))
