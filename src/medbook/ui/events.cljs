(ns medbook.ui.events
  (:require [re-frame.core :as re-frame]
            ;; import http-fx to register events
            [day8.re-frame.http-fx]
            [medbook.ui.db :as db]))


(re-frame/reg-event-db
  :event/initialize-db
  (fn [_ _]
    db/default-db))


;; Inspect app-db state
(comment
  (require '[re-frame.db :as rf-db])
  (swap! rf-db/app-db assoc :name "Some name")
  (deref rf-db/app-db))
