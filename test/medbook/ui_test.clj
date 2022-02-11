(ns medbook.ui-test
  (:require [clojure.test :refer :all]
            [etaoin.api :as etaoin]
            [medbook.util.system :as system-util]
            [medbook.testing-utils :as test-util]))


(use-fixtures :once
  (test-util/with-system
    {:include {:medbook.figwheel/figwheel {:options
                                           {:mode :build-once
                                            :build-id system-util/BUILD-ID-TEST}}
               :medbook.testing-utils/chromedriver {}}})
  (test-util/with-dropped-tables)
  (test-util/with-migrations))


(use-fixtures :each
  (test-util/with-truncated-tables))


(deftest test-front-page-not-found-ok
  (testing "check when page not found"
    (let [driver (get test-util/*test-system* ::test-util/chromedriver)]
      (etaoin/go driver (str test-util/TEST-URL-BASE "/wrong"))
      (etaoin/wait-visible driver {:fn/has-text "MedBook"} {:timeout 5})
      (is (etaoin/visible? driver {:tag :h2
                                   :fn/text "Page not found."})))))


(deftest test-wiki
  (let [driver (get test-util/*test-system* ::test-util/chromedriver)]
    (etaoin/go driver test-util/TEST-URL-BASE)
    (etaoin/wait-visible driver {:fn/has-text "Patients"} {:timeout 5})
    (is (true? (etaoin/visible? driver {:tag :h2})))
    (etaoin/screenshot driver "page.png")))
