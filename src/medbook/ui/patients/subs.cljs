(ns medbook.ui.patients.subs
  (:require [re-frame.core :as re-frame]))


(re-frame/reg-sub
  ::patients
  (fn [db]
    (:patients db)))


(re-frame/reg-sub
  ::patients-loading?
  (fn [db]
    (:patients-loading? db)))


(re-frame/reg-sub
  ::patient-form-submitting?
  (fn [db]
    (:patient-form-submitting? db)))


(re-frame/reg-sub
  ::patient-form-errors
  (fn [db]
    (:patient-form-errors db)))


(re-frame/reg-sub
  ::patient-new
  (fn [db]
    (:patient-new db)))


(re-frame/reg-sub
  ::patient-detail-current
  (fn [db]
    (:patient-detail-current db)))


(re-frame/reg-sub
  ::patient-form
  (fn [db]
    (:patient-form db)))


(re-frame/reg-sub
  ::patient-form-field
  (fn [db [_ field]]
    (get-in db [:patient-form field])))
