(ns medbook.ui.events
  (:require [re-frame.core :as re-frame]
            [reitit.frontend.controllers :as reitit-controllers]
            [reitit.frontend.easy :as reitit-easy]
            [medbook.ui.db :as db]))


(re-frame/reg-event-db
  ::initialize-db
  (fn [_ _]
    db/default-db))


(re-frame/reg-event-db
  ::navigate
  (fn [db [_ new-match]]
    (let [old-match (:current-page db)
          controllers (reitit-controllers/apply-controllers
                        (:controllers old-match)
                        new-match)]
      (assoc db :current-page (assoc new-match :controllers controllers)))))


(re-frame/reg-fx :fx/push-state
  (fn [{:keys [route]}]
    (reitit-easy/push-state route)))


(re-frame/reg-fx :fx/ajax
  (fn [{:keys [uri on-success]}]
    (-> (js/fetch uri)
      (.then (fn [response]
               (-> (.json response)
                 (.then (fn [resp]
                          (re-frame/dispatch (conj on-success (js->clj resp :keywordize-keys true)))))))))))


(re-frame/reg-event-db
  ::clear-info-message
  (fn [db [_ _]]
    (assoc db :info-message nil)))


(re-frame/reg-event-db
  ::clear-error-message
  (fn [db [_ _]]
    (assoc db :error-message nil)))


;; Inspect app-db state
(comment
  (require '[re-frame.db :as rf-db])
  (swap! rf-db/app-db assoc :name "Some name")
  (deref rf-db/app-db))
