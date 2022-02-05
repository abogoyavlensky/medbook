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
