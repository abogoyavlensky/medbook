(ns medbook.ui-test
  (:require [clojure.test :refer :all]
            [etaoin.api :as etaoin]
            [medbook.util.system :as system-util]
            [medbook.testing-utils :as test-util]))


(use-fixtures :once
  (test-util/with-system
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
    (etaoin/wait-visible driver {:tag :h2
                                 :fn/has-text "Patients"} {:timeout 5})
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
    (is (etaoin/visible? driver {:tag :td :fn/text (-> patients second :insurance-number)}))))


(deftest test-front-create-patient-and-get-list-ok
  (let [db (get test-util/*test-system* :medbook.db/db)
        insurance-number "1234567891234567"]
    (testing "check creating patient and showing it on the list page"
      (let [driver (get test-util/*test-system* ::test-util/chromedriver)]
        ; check that patient table is empty
        (is (= [] (test-util/get-patient-list db)))
        (etaoin/go driver test-util/TEST-URL-BASE)
        (etaoin/wait-visible driver {:tag :h2
                                     :fn/has-text "Patients"})
        (etaoin/wait-visible driver {:tag :p
                                     :fn/has-text "There are no patients yet."})
        (etaoin/click driver {:tag :a
                              :fn/text "Create patient"})
        (etaoin/wait-visible driver {:tag :h2
                                     :fn/has-text "Create new patient"})
        (etaoin/fill driver {:tag :input
                             :name :full-name} "John Doe")
        (etaoin/fill driver {:tag :input
                             :name :birthday} "02-02-1998")
        (etaoin/fill driver {:tag :input
                             :name :address} "City name, st. Street 10")
        (etaoin/fill driver :insurance-number insurance-number)
        (etaoin/click driver {:tag :button
                              :fn/text "Save"})
        (etaoin/screenshot driver "page.png")
        (etaoin/wait-visible driver [{:tag :table}
                                     {:tag :th
                                      :fn/has-text "Full name"}])
        (is (etaoin/visible? driver {:tag :td
                                     :fn/text "John Doe"}))
        (etaoin/wait-visible driver {:tag :div
                                     :fn/has-class "toast"})
        (is (etaoin/visible? driver {:tag :p
                                     :fn/has-text (str "New patient John Doe has been created successfully!")}))))

    (testing "check patient has been created in db"
      (let [patient (-> (test-util/get-patient-by-insurance db insurance-number)
                      (test-util/patient->output))]
        (is (= {:full-name "John Doe"
                :gender 0
                :birthday "1998-02-02"
                :address "City name, st. Street 10"
                :insurance-number insurance-number}
              (dissoc patient :id)))))))
