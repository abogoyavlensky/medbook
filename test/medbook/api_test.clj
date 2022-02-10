(ns medbook.api-test
  (:require [clojure.test :refer :all]
            [clojure.tools.logging :as log]
            [bond.james :as bond]
            [medbook.testing-utils :as test-util]))


(use-fixtures :once
  (test-util/with-system
    {:exclude [:medbook.server/server]})
  (test-util/with-dropped-tables)
  (test-util/with-migrations))


(use-fixtures :each
  (test-util/with-truncated-tables))


(deftest test-patient-list-ok
  (let [db (get test-util/*test-system* :medbook.db/db)
        patients [(test-util/create-patient! db)
                  (test-util/create-patient! db {:full-name "New patient 1"})]
        expected (->> patients
                   (map test-util/patient->output)
                   (reverse))
        response (test-util/api-request! :get :medbook.routes/patient-list)]
    (is (= 200 (:status response)))
    (is (= expected (:body response)))))


(deftest test-patient-list-empty
  (let [expected []
        response (test-util/api-request! :get :medbook.routes/patient-list)]
    (is (= 200 (:status response)))
    (is (= expected (:body response)))))


(deftest test-patient-detail-ok
  (let [db (get test-util/*test-system* :medbook.db/db)
        expected (-> (test-util/create-patient! db)
                   (test-util/patient->output))
        response (test-util/api-request! :get
                   :medbook.routes/patient-detail
                   {:path {:patient-id (:id expected)}})]
    (is (= 200 (:status response)))
    (is (= expected (:body response)))))


(deftest test-patient-detail-wrong-id-err
  (bond/with-stub! [[log/log* (constantly nil)]]
    (let [response (test-util/api-request! :get
                     :medbook.routes/patient-detail
                     {:path {:patient-id "lkjsf"}})]
      (is (= 400 (:status response)))
      (is (= {:error "reitit.coercion/request-coercion"
              :exception "clojure.lang.ExceptionInfo"
              :messages {:form ["Form data is invalid."]}
              :type "Request error"}
            (:body response))))))


(deftest test-patient-create-ok
  (let [db (get test-util/*test-system* :medbook.db/db)]
    (is (= 0 (count (test-util/get-patient-list db))))
    (let [params {:full-name "John Doe"
                  :gender 0
                  :birthday "1990-02-02"
                  :address "Moscow, st. Tulskaya 1"
                  :insurance-number (test-util/rand-insurance-number)}
          response (test-util/api-request! :post
                     :medbook.routes/patient-list
                     {:body params})
          patients-from-db (test-util/get-patient-list db)]
      (is (= 200 (:status response)))
      (is (= 1 (count patients-from-db)))
      (is (= params
             (->  (first patients-from-db)
                  (test-util/patient->output)
                  (dissoc :id))))
      (is (= (assoc params :id (:id (first patients-from-db)))
            (:body response))))))
