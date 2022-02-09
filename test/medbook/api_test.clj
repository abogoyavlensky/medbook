(ns medbook.api-test
  (:require [clojure.test :refer :all]
            [ring.mock.request :as mock]
            [medbook.testing-utils :as test-util]))

(use-fixtures :once
  (test-util/with-system)
  (test-util/with-migrations)
  (test-util/with-dropped-tables))

(use-fixtures :each
  (test-util/with-truncated-tables))



(deftest a-test
  #p (keys test-util/*test-system*)
  (testing "FIXME, I fail."
    (is (= 0 0))))
