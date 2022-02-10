(ns medbook.testing-utils
  (:require [clojure.test :refer :all]
            [clojure.string :as str]
            [integrant.core :as ig]
            [reitit.core :as reitit]
            [integrant.repl.state :as state]
            [automigrate.core :as automigrate]
            [ring.mock.request :as mock]
            [muuntaja.core :as m]
            [medbook.util.system :as system-util]
            [medbook.util.db :as db-util]
            [medbook.handler :as handler]))


(def ^:dynamic *test-system* nil)


(defn with-system
  ([]
   (with-system {}))
  ([{:keys [exclude] :or {exclude []}}]
   (fn [test-fn]
     (let [test-config (apply dissoc (system-util/config :test) exclude)]
       ; TODO: try to remove!
       (ig/load-namespaces test-config)
       (binding [*test-system* (ig/init test-config)]
         (try
           (test-fn)
           (finally
             (ig/halt! *test-system*))))))))


(defn- all-tables
  [db]
  (->> {:select [:table_name]
        :from [:information_schema.tables]
        :where [:and
                [:= :table_schema "public"]
                [:= :table_type "BASE TABLE"]]}
    (db-util/exec! db)
    (map (comp keyword :table-name))))


(defn- truncate-all-tables
  "Remove all data from database tables for public schema."
  [db]
  (doseq [table (all-tables db)
          :when (not= :automigrate_migrations table)]
    (db-util/exec! db {:truncate table})))


(defn with-truncated-tables
  "Remove all data from all tables except migration's table."
  []
  (fn [f]
    (let [db (get *test-system* :medbook.db/db)]
      (truncate-all-tables db)
      (f))))


(defn- drop-all-tables
  "Remove all tables from database for public schema."
  [db]
  ;(doseq [table (all-tables)])
  (db-util/exec! db {:drop-table (all-tables db)}))


(defn with-dropped-tables
  "Remove all data from all tables except migration's table."
  []
  (fn [f]
    (let [db (get *test-system* :medbook.db/db)]
      (drop-all-tables db)
      (f))))


(defn with-migrations
  "Run migrations on a test db."
  []
  (fn [f]
    (let [config (system-util/config :test)]
      (automigrate/migrate {:models-file "resources/db/models.edn"
                            :migrations-dir "resources/db/migrations"
                            :jdbc-url (get-in config [:medbook.db/db :options :jdbc-url])})
      (f))))


(defn- rand-insurance-number
  []
  (->> (map (fn [_] (rand-int 10)) (range 16))
    (map str)
    (str/join)))


(defn create-patient!
  "Create and return testing patient."
  ([db]
   (create-patient! db {}))
  ([db params]
   (let [values (merge
                  {:full-name "John Doe"
                   :gender (rand-int 2)
                   :birthday #inst "1990-02-02"
                   :address "Moscow, st. Tulskaya 1"
                   :insurance-number (rand-insurance-number)}
                  params)]

     (db-util/exec-one! db
       {:insert-into :patient
        :values [values]
        :on-conflict [:id]
        :do-nothing true
        :returning [:id :full-name :gender :birthday :address :insurance-number]}))))


(defn route-path
  "Return route path by its name."
  ([route route-name]
   (route-path route route-name {}))
  ([route route-name {:keys [path query]}]
   (-> route
     (reitit/match-by-name route-name path)
     (reitit/match->path query))))


(defn api-request!
  ([http-method route-name]
   (api-request! http-method route-name {}))
  ([http-method route-name {:keys [path query body]}]
   (let [app (get *test-system* :medbook.handler/handler)
         uri (route-path (handler/router {}) route-name {:path path
                                                         :query query})
         request (app (cond-> (mock/request http-method uri)
                        (some? body) (mock/json-body body)))]
     (update request :body (partial m/decode "application/json")))))


(comment
  (require '[clojure.pprint :as pprint])
  (require '[clojure.reflect :as r])
  (require '[integrant.repl.state :as state])

  (let [db (get state/system :medbook.db/db)]
    (pprint/print-table (:members (r/reflect db)))
    (pool. db)))
