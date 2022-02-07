(ns medbook.ui.subs
  (:require [re-frame.core :as re-frame]))


(re-frame/reg-sub
  ::current-page
  (fn [db]
    (:current-page db)))


(re-frame/reg-sub
  ::patient-form-errors
  (fn [db]
    (:patient-form-errors db)))
