(ns medbook.api-test
  (:require [clojure.test :refer :all]
            [ring.mock.request :as mock]
            [muuntaja.core :as m]
            [medbook.testing-utils :as test-util]))

(use-fixtures :once
  (test-util/with-system)
  (test-util/with-dropped-tables)
  (test-util/with-migrations))

(use-fixtures :each
  (test-util/with-truncated-tables))



(deftest a-test
  #p (keys test-util/*test-system*)

  (let [db (get test-util/*test-system* :medbook.db/db)]
    (test-util/create-patient! db)
    (test-util/create-patient! db {:full-name "New patient 1"}))

  (let [app (get test-util/*test-system* :medbook.handler/handler)
        method :get
        route-name :medbook.routes/patient-list
        uri "/api/v1/patients"
        body nil
        api-request! (fn []
                       (let [request (app (cond-> (mock/request method uri)
                                           (some? body) (mock/json-body body)))]
                         (update request :body (partial m/decode "application/json"))))]
    #p (api-request!))
    ;#p (get test-util/*test-system* :medbook.handler/handler))


  (testing "FIXME, I fail."
    (is (= 0 0))))
