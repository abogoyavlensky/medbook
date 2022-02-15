(ns medbook.spec
  (:require [clojure.spec.alpha :as s]
            [clojure.string :as str])
  #?(:clj (:import [java.time.format DateTimeFormatter DateTimeParseException]
                   [java.time LocalDate])))


(def ^:private MALE-GENDER 0)
(def ^:private FEMALE-GENDER 1)


(s/def ::not-empty-string
  (fn [value]
    (and
      (string? value)
      (boolean (seq (str/trim value))))))


(def ^:private INSURANCE-NUMBER-LENGTH 16)

(s/def ::id pos-int?)
(s/def ::full-name ::not-empty-string)


(s/def ::gender #{MALE-GENDER FEMALE-GENDER})


#?(:clj
   (def ^:private date-format
     (DateTimeFormatter/ofPattern "yyyy-MM-dd")))


#?(:clj
   (defn- valid-date?
     [date-str]
     (try
       (LocalDate/parse date-str date-format)
       (catch DateTimeParseException _
         false))))


(s/def ::birthday
  (s/and
    string?
    #?(:clj valid-date?)))


(s/def ::address ::not-empty-string)


(defn- insurance-number-length-valid?
  [value]
  (= INSURANCE-NUMBER-LENGTH (count value)))


(defn- insurance-number-length-digits?
  [value]
  (some? (re-matches #"^[\d+]{16}$" value)))


(s/def ::insurance-number
  (s/and
    ::not-empty-string
    insurance-number-length-valid?
    insurance-number-length-digits?))


(s/def ::patient
  (s/keys
    :req-un [::full-name
             ::gender
             ::birthday
             ::address
             ::insurance-number]
    :opt-un [::id]))


(s/def ::patient-with-id
  (s/merge
    ::patient
    (s/keys
      :opt-un [::id])))


(s/def ::patients
  (s/coll-of ::patient-with-id :kind vector?))


(s/def ::patient-update
  (s/and
    (s/keys
      :opt-un [::full-name
               ::gender
               ::birthday
               ::address
               ::insurance-number])
    (s/map-of keyword? any? :min-count 1)))
