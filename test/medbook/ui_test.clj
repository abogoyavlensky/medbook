(ns medbook.ui-test
  (:require [clojure.test :refer :all]
            [etaoin.api :as etaoin]
            [medbook.testing-utils :as test-util]))


(use-fixtures :once
  (test-util/with-system))
;  (test-util/with-dropped-tables)
;  (test-util/with-migrations))
;
;
;(use-fixtures :each
;  (test-util/with-truncated-tables))


(deftest test-front-page-not-found-ok
  ;#p (slurp "http://localhost:8001")
  (testing "check when page not found"
    ; TODO: move driver to ig component!
    (let [driver (etaoin/chrome-headless {:host "127.0.0.1"
                                          :port 4444
                                          :args ["--no-sandbox"]})]
      (etaoin/go driver (str test-util/TEST-URL-BASE "/wrong"))
      (etaoin/wait-visible driver {:fn/has-text "MedBook"} {:timeout 5})
      (is (etaoin/visible? driver {:tag :h2
                                   :fn/text "Page not found."})))))

;(def driver
;  (etaoin/chrome {:host "127.0.0.1" :port 9515}))

;(def driver (etaoin/chrome-headless {:host "localhost" :port 4444 :args ["--no-sandbox"]}))


(deftest test-wiki
  (let [driver (etaoin/chrome-headless {:host "127.0.0.1" :port 4444 :args ["--no-sandbox"]})]
    ;(etaoin/go driver "https://en.wikipedia.org/")
    (etaoin/go driver "http://127.0.0.1:8001/")
    ;(etaoin/wait-visible driver [{:id :simpleSearch} {:tag :input :name :search}])
    ;(etaoin/wait-visible driver {:fn/has-text "Patients"} {:timeout 5})
    (etaoin/wait-visible driver {:tag :h2} {:timeout 5})
    (is (true? (etaoin/visible? driver {:tag :h2})))
    ;#p (etaoin/get-title driver)))
    (etaoin/screenshot driver "page.png")))
