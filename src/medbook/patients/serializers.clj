(ns medbook.patients.serializers
  (:require [clojure.spec.alpha :as s]
            [clojure.instant :as instant]))


(defn- date->string
  [date-inst]
  (format "%1$tY-%1$tm-%1$td" date-inst))


(defn- string->date
  [date-str]
  (instant/read-instant-date date-str))


(s/def ::birthday
  (s/conformer
    string->date
    date->string))


(s/def ::patient->response
  (s/keys
    :req-un [::birthday]))


(s/def ::patient-list->response
  (s/coll-of ::patient->response :kind vector?))
