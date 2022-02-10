(ns medbook.api-test
  (:require [clojure.test :refer :all]
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
                   (map test-util/patient->response)
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
                   (test-util/patient->response))
        response (test-util/api-request! :get
                   :medbook.routes/patient-detail
                   {:path {:patient-id (:id expected)}})]
    (is (= 200 (:status response)))
    (is (= expected (:body response)))))
