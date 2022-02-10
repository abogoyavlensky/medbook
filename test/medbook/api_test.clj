(ns medbook.api-test
  (:require [clojure.test :refer :all]
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
        route-name :medbook.routes/patient-list]
    #p (test-util/api-request! method route-name))


  (testing "FIXME, I fail."
    (is (= 0 0))))
