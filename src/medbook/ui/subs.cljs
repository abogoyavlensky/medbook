(ns medbook.ui.subs
  (:require [re-frame.core :as re-frame]))


(re-frame/reg-sub
  ::current-page
  (fn [db]
    (:current-page db)))


(re-frame/reg-sub
  ::info-message
  (fn [db]
    (:info-message db)))


(re-frame/reg-sub
  ::error-message
  (fn [db]
    (:error-message db)))
