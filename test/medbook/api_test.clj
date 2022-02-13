(ns medbook.api-test
  (:require [clojure.test :refer :all]
            [clojure.tools.logging :as log]
            [clojure.instant :as instant]
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


(deftest test-patient-detail-invalid-id-err
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


(deftest test-patient-detail-does-not-exist-err
  (bond/with-stub! [[log/log* (constantly nil)]]
    (let [response (test-util/api-request! :get
                     :medbook.routes/patient-detail
                     {:path {:patient-id 1111}})]
      (is (= 400 (:status response)))
      (is (= {:error "medbook.handler/error"
              :exception "clojure.lang.ExceptionInfo"
              :messages {:common ["Patient does not exit."]}
              :type "Application error"}
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


(deftest test-patient-create-already-exists-err
  (bond/with-stub! [[log/log* (constantly nil)]]
    (let [db (get test-util/*test-system* :medbook.db/db)
          params {:full-name "John Doe"
                  :gender 0
                  :birthday "1990-02-02"
                  :address "Moscow, st. Tulskaya 1"
                  :insurance-number (test-util/rand-insurance-number)}
          _ (test-util/create-patient! db (update params :birthday instant/read-instant-date))
          response (test-util/api-request! :post
                     :medbook.routes/patient-list
                     {:body params})
          patients-from-db (test-util/get-patient-list db)]
      (is (= 400 (:status response)))
      (is (= 1 (count patients-from-db)))
      (is (= {:error "medbook.handler/error"
              :exception "clojure.lang.ExceptionInfo"
              :messages {:insurance-number ["Insurance number already exists."]}
              :type "Application error"}
            (:body response))))))


(deftest test-patient-create-invalid-params-err
  (bond/with-stub! [[log/log* (constantly nil)]]
    (let [db (get test-util/*test-system* :medbook.db/db)
          params {:full-name ""
                  :gender "wrong"
                  :birthday "199002test"
                  :address 111
                  :insurance-number "2222"}
          response (test-util/api-request! :post
                     :medbook.routes/patient-list
                     {:body params})
          patients-from-db (test-util/get-patient-list db)]
      (is (= 400 (:status response)))
      (is (= 0 (count patients-from-db)))
      (is (= {:error "reitit.coercion/request-coercion"
              :exception "clojure.lang.ExceptionInfo"
              :messages {:address ["Address is required."]
                         :birthday ["Birthday value has invalid format."]
                         :gender ["Gender value is invalid."]
                         :full-name ["Full name is required."]
                         :insurance-number ["Insurance number value should contain 16 digits without spaces."]}
              :type "Request error"}
            (:body response))))))


(deftest test-patient-create-invalid-insurance-number-err
  (bond/with-stub! [[log/log* (constantly nil)]]
    (let [params {:full-name "John Doe"
                  :gender 1
                  :birthday "1990-02-02"
                  :address "Some street"
                  :insurance-number "1111222233334444"}]
      (testing "check long number"
        (let [response (test-util/api-request! :post
                         :medbook.routes/patient-list
                         {:body (assoc params :insurance-number "111122223333444499999")})]
          (is (= 400 (:status response)))
          (is (= {:error "reitit.coercion/request-coercion"
                  :exception "clojure.lang.ExceptionInfo"
                  :messages {:insurance-number ["Insurance number value should contain 16 digits without spaces."]}
                  :type "Request error"}
                (:body response)))))

      (testing "check short number"
        (let [response (test-util/api-request! :post
                         :medbook.routes/patient-list
                         {:body (assoc params :insurance-number "11112222")})]
          (is (= 400 (:status response)))
          (is (= {:error "reitit.coercion/request-coercion"
                  :exception "clojure.lang.ExceptionInfo"
                  :messages {:insurance-number ["Insurance number value should contain 16 digits without spaces."]}
                  :type "Request error"}
                (:body response)))))

      (testing "check number contains letters"
        (let [response (test-util/api-request! :post
                         :medbook.routes/patient-list
                         {:body (assoc params :insurance-number "111122223333aaaa")})]
          (is (= 400 (:status response)))
          (is (= {:error "reitit.coercion/request-coercion"
                  :exception "clojure.lang.ExceptionInfo"
                  :messages {:insurance-number ["Insurance number value should contain 16 digits without spaces."]}
                  :type "Request error"}
                (:body response)))))

      (testing "check number is empty"
        (let [response (test-util/api-request! :post
                         :medbook.routes/patient-list
                         {:body (assoc params :insurance-number "")})]
          (is (= 400 (:status response)))
          (is (= {:error "reitit.coercion/request-coercion"
                  :exception "clojure.lang.ExceptionInfo"
                  :messages {:insurance-number ["Insurance number is required."]}
                  :type "Request error"}
                (:body response))))))))


(deftest test-patient-update-ok
  (let [db (get test-util/*test-system* :medbook.db/db)
        params {:full-name "New patient"
                :gender 1
                :birthday "1990-02-02"
                :address "New address"
                :insurance-number (test-util/rand-insurance-number)}
        patient (test-util/create-patient! db)
        response (test-util/api-request! :put
                   :medbook.routes/patient-detail
                   {:path {:patient-id (:id patient)}
                    :body params})
        patients-from-db (test-util/get-patient-list db)]
    (is (= 200 (:status response)))
    (is (= 1 (count patients-from-db)))
    (is (= (assoc params :id (:id patient))
          (:body response)))))


(deftest test-patient-update-already-exists-err
  (bond/with-stub! [[log/log* (constantly nil)]]
    (let [db (get test-util/*test-system* :medbook.db/db)
          patient (test-util/create-patient! db)
          another-patient (test-util/create-patient! db)
          params {:full-name "New patient"
                  :gender 1
                  :birthday "1990-02-02"
                  :address "New address"
                  :insurance-number (:insurance-number another-patient)}
          response (test-util/api-request! :put
                     :medbook.routes/patient-detail
                     {:path {:patient-id (:id patient)}
                      :body params})]
      (is (= 400 (:status response)))
      (is (= {:error "medbook.handler/error"
              :exception "clojure.lang.ExceptionInfo"
              :messages {:insurance-number ["Insurance number already exists."]}
              :type "Application error"}
            (:body response))))))


(deftest test-patient-update-invalid-params-err
  (bond/with-stub! [[log/log* (constantly nil)]]
    (let [db (get test-util/*test-system* :medbook.db/db)
          params {:full-name ""
                  :gender "wrong"
                  :birthday "199002test"
                  :address 111
                  :insurance-number "2222"}
          patient (test-util/create-patient! db)
          response (test-util/api-request! :put
                     :medbook.routes/patient-detail
                     {:path {:patient-id (:id patient)}
                      :body params})]
      (is (= 400 (:status response)))
      (is (= {:error "reitit.coercion/request-coercion"
              :exception "clojure.lang.ExceptionInfo"
              :messages {:address ["Address is required."]
                         :birthday ["Birthday value has invalid format."]
                         :gender ["Gender value is invalid."]
                         :full-name ["Full name is required."]
                         :insurance-number ["Insurance number value should contain 16 digits without spaces."]}
              :type "Request error"}
            (:body response))))))


(deftest test-patient-delete-ok
  (let [db (get test-util/*test-system* :medbook.db/db)
        patient (test-util/create-patient! db)
        response (test-util/api-request! :delete
                   :medbook.routes/patient-detail
                   {:path {:patient-id (:id patient)}})]
    (is (= 204 (:status response)))
    (is (nil? (test-util/get-patient-by-insurance db (:insurance-number patient))))))


(deftest test-patient-delete-does-not-exist-err
  (bond/with-stub! [[log/log* (constantly nil)]]
    (let [response (test-util/api-request! :delete
                     :medbook.routes/patient-detail
                     {:path {:patient-id 1111}})]
      (is (= 400 (:status response)))
      (is (= {:error "medbook.handler/error"
              :exception "clojure.lang.ExceptionInfo"
              :messages {:common ["Patient does not exit."]}
              :type "Application error"}
            (:body response))))))
