(ns medbook.errors
  (:require [clojure.spec.alpha :as s]))


(def ^:private field-names
  {:full-name "Full name"
   :address "Address"
   :birthday "Birthday"
   :gender "Gender"
   :insurance-number "Insurance number"})


(defn- field-error
  [field message-tmpl]
  {:field (if (some? field) field :form)
   :message (if (some? field)
              (format message-tmpl (field field-names))
              message-tmpl)})


(def ^:private error-messages
  {:medbook.spec/full-name (field-error :full-name "%s value should be string.")
   :medbook.spec/address (field-error :address "%s value should be string.")
   :medbook.spec/gender (field-error :gender "%s value is invalid.")
   :medbook.spec/birthday (field-error :birthday "%s value has invalid format.")
   :medbook.spec/insurance-number (field-error :insurance-number "%s value should contain 16 digits without spaces.")
   :medbook.spec/not-empty-string
   (fn [problem]
     (let [field (peek (:in problem))]
       (field-error field "%s is required.")))})


(defn- problem->error-message
  [{:keys [via] :as problem}]
  (let [last-spec (peek via)
        error-message (get error-messages last-spec)]
    (cond
      (fn? error-message) (error-message problem)
      (map? error-message) error-message
      :else {:field :form
             :message "Form data is invalid."})))


(defn coercion-exception->error-messages
  "Convert spec problems to human-readable error messages."
  [explain-data]
  (->> (get-in explain-data [:problems ::s/problems])
    (map problem->error-message)
    (group-by :field)
    (reduce-kv
      (fn [m k v]
        (assoc m k (mapv :message v)))
      {})))
