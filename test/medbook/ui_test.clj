(ns medbook.ui-test
  (:require [clojure.test :refer :all]
            [etaoin.api :as etaoin]
            [digest :as digest]
            [medbook.util.system :as system-util]
            [medbook.testing-utils :as test-util]))


(use-fixtures :once
  (test-util/with-system
    ; TODO: uncomment!
    {:include {:medbook.figwheel/figwheel {:options {:mode :build-once :build-id system-util/BUILD-ID-TEST}}
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


(deftest test-patinents-list-ok
  (let [driver (get test-util/*test-system* ::test-util/chromedriver)
        db (get test-util/*test-system* :medbook.db/db)
        patients [(test-util/create-patient! db {:gender 0
                                                 :insurance-number "1111222233334444"})
                  (test-util/create-patient! db {:full-name "New patient 1"
                                                 :birthday #inst "1995-01-10"
                                                 :gender 1
                                                 :address "Some other address 10"
                                                 :insurance-number "5555666677778888"})]]
    (etaoin/go driver test-util/TEST-URL-BASE)
    (etaoin/wait-visible driver {:fn/has-text "Patients"} {:timeout 5})
    (etaoin/wait-visible driver [{:tag :tr}] {:timeout 5})
    (is (true? (etaoin/visible? driver {:tag :h2})))
    (is (etaoin/visible? driver {:tag :td :fn/text "John Doe"}))
    (is (etaoin/visible? driver {:tag :td :fn/text "1995-01-10"}))
    (is (etaoin/visible? driver {:tag :span :fn/text "Female"}))
    (is (etaoin/visible? driver {:tag :td :fn/text "Moscow, st. Tulskaya 1"}))
    (is (etaoin/visible? driver {:tag :td :fn/text (-> patients first :insurance-number)}))

    (is (etaoin/visible? driver {:tag :td :fn/text "New patient 1"}))
    (is (etaoin/visible? driver {:tag :td :fn/text "1990-02-02"}))
    (is (etaoin/visible? driver {:tag :span :fn/text "Male"}))
    (is (etaoin/visible? driver {:tag :td :fn/text "Some other address 10"}))
    (is (etaoin/visible? driver {:tag :td :fn/text (-> patients second :insurance-number)}))

    ; TODO: try to uncomment!
    (etaoin/screenshot driver "test/medbook/resources/result_page.png")
    (let [result-page-digest (digest/md5 (slurp "test/medbook/resources/result_page.png"))
          expected-page-digest (digest/md5 (slurp "test/medbook/resources/chrome/list_page_ok.png"))]
      (is (= expected-page-digest result-page-digest)))))
