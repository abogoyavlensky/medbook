(ns medbook.ui-test
  (:require [clojure.test :refer :all]
            [etaoin.api :as etaoin]
            [medbook.util.system :as system-util]
            [medbook.testing-utils :as test-util]))


(use-fixtures :once
  (test-util/with-system
    {:include {:medbook.figwheel/figwheel {:options {:mode :build-once
                                                     :build-id system-util/BUILD-ID-TEST}}
               :medbook.testing-utils/chromedriver {:options
                                                    {:testing-env (or (System/getenv "TESTING_ENV")
                                                                    "local")}}}})
  (test-util/with-dropped-tables)
  (test-util/with-migrations))


(use-fixtures :each
  (test-util/with-truncated-tables))


(deftest test-front-page-not-found-ok
  ;#p (slurp "http://localhost:8001")
  (testing "check when page not found"
    ; TODO: move driver to ig component!
    (let [;driver (etaoin/chrome-headless {:host "127.0.0.1"
          ;                                :port 4444
          ;                                :args ["--no-sandbox"]})
          driver (get test-util/*test-system* ::test-util/chromedriver)]

      (etaoin/go driver (str test-util/TEST-URL-BASE "/wrong"))
      ;(etaoin/go driver (str "http://test:8001" "/wrong"))
      (etaoin/wait-visible driver {:fn/has-text "MedBook"} {:timeout 5})
      (is (etaoin/visible? driver {:tag :h2
                                   :fn/text "Page not found."})))))


(deftest test-wiki
  (let [;driver (etaoin/chrome-headless {:host "127.0.0.1" :port 4444 :args ["--no-sandbox"]})]
        driver (get test-util/*test-system* ::test-util/chromedriver)]
    ;(etaoin/go driver "https://en.wikipedia.org/")
    ;(etaoin/go driver "http://127.0.0.1:8001/")
    ;(etaoin/go driver "http://test:8001/")
    (etaoin/go driver test-util/TEST-URL-BASE)
    ;(etaoin/wait-visible driver [{:id :simpleSearch} {:tag :input :name :search}])
    ;(etaoin/wait-visible driver {:fn/has-text "Patients"} {:timeout 5})
    (etaoin/wait-visible driver {:tag :h2} {:timeout 3})
    (is (true? (etaoin/visible? driver {:tag :h2})))
    ;#p (etaoin/get-title driver)))
    (etaoin/screenshot driver "page.png")))
