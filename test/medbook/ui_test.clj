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


(deftest test-patients-list-ok
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


(deftest test-create-patient-and-get-list-ok
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


(deftest test-create-patient-and-get-validation-err
  (let [driver (get test-util/*test-system* ::test-util/chromedriver)
        invalid-insurance-number "1234567891"]
    (etaoin/go driver test-util/TEST-URL-BASE)
    (etaoin/wait-visible driver {:tag :h2
                                 :fn/has-text "Patients"})
    (etaoin/click driver {:tag :a
                          :fn/text "Create patient"})
    (etaoin/wait-visible driver {:tag :h2
                                 :fn/has-text "Create new patient"})
    (etaoin/fill driver {:tag :input
                         :name :full-name} "")
    (etaoin/fill driver :insurance-number invalid-insurance-number)
    (etaoin/click driver {:tag :button
                          :fn/text "Save"})
    (etaoin/wait-enabled driver {:tag :button
                                 :fn/text "Save"})
    (is (etaoin/visible? driver {:tag :p
                                 :class :form-input-hint
                                 :fn/text "Full name is required."}))
    (is (etaoin/visible? driver {:tag :p
                                 :class :form-input-hint
                                 :fn/text "Address is required."}))
    (is (etaoin/visible? driver {:tag :p
                                 :class :form-input-hint
                                 :fn/text "Birthday value has invalid format."}))
    (is (etaoin/visible? driver {:tag :p
                                 :class :form-input-hint
                                 :fn/text "Insurance number value should contain 16 digits without spaces."}))))


(deftest test-create-patient-clean-from-after-validation-err
  (let [driver (get test-util/*test-system* ::test-util/chromedriver)]
    ; try to create patient with empty form
    (etaoin/go driver test-util/TEST-URL-BASE)
    (etaoin/wait-visible driver {:tag :h2 :fn/has-text "Patients"})
    (etaoin/click driver {:tag :a :fn/text "Create patient"})
    (etaoin/wait-visible driver {:tag :h2
                                 :fn/has-text "Create new patient"})
    (etaoin/click driver {:tag :button
                          :fn/text "Save"})
    (etaoin/wait-enabled driver {:tag :button
                                 :fn/text "Save"})
    (is (etaoin/visible? driver {:tag :p
                                 :class :form-input-hint
                                 :fn/text "Full name is required."}))
    ; return to list page
    (etaoin/go driver test-util/TEST-URL-BASE)
    (etaoin/wait-visible driver {:tag :h2 :fn/has-text "Patients"})
    ; check that form is clean on the creating page
    (etaoin/click driver {:tag :a :fn/text "Create patient"})
    (etaoin/wait-visible driver {:tag :h2
                                 :fn/has-text "Create new patient"})
    (is (false? (etaoin/visible? driver {:tag :p
                                         :class :form-input-hint
                                         :fn/text "Full name is required."})))))


(deftest test-update-patient-and-get-list-ok
  (let [db (get test-util/*test-system* :medbook.db/db)
        patient (-> (test-util/create-patient! db {:full-name "John Doe"})
                  (test-util/patient->output))]
    (testing "check updating patient and showing it on the list page"
      (let [driver (get test-util/*test-system* ::test-util/chromedriver)]
        (etaoin/go driver test-util/TEST-URL-BASE)
        (etaoin/wait-visible driver {:tag :h2
                                     :fn/has-text "Patients"})
        (etaoin/click driver {:tag :a :fn/text "Edit"})
        (etaoin/wait-visible driver {:tag :h2 :fn/has-text "Edit patient"})
        ; clear full-name input
        (test-util/clear-input driver :full-name (:full-name patient))
        (etaoin/fill driver :full-name "New name")
        (etaoin/click driver {:tag :button
                              :fn/text "Save"})
        (etaoin/wait-visible driver [{:tag :table}
                                     {:tag :th
                                      :fn/has-text "Full name"}])
        (is (false? (etaoin/visible? driver {:tag :td :fn/text "John Doe"})))
        (is (etaoin/visible? driver {:tag :td :fn/text "New name"}))
        ; check info panel
        (etaoin/wait-visible driver {:tag :div :fn/has-class "toast"})
        (is (etaoin/visible? driver
              {:tag :p
               :fn/has-text (str "New patient New name has been updated successfully!")}))))

    (testing "check patient has been updated in db"
      (let [patient-from-db (-> (test-util/get-patient-by-insurance db
                                  (:insurance-number patient))
                              (test-util/patient->output))]
        (is (= (assoc patient :full-name "New name")
              patient-from-db))))))


(deftest test-update-patient-with-validation-err
  (let [db (get test-util/*test-system* :medbook.db/db)
        patient (-> (test-util/create-patient! db {:full-name "John Doe"})
                  (test-util/patient->output))]
    (testing "check updating patient and showing it on the list page"
      (let [driver (get test-util/*test-system* ::test-util/chromedriver)]
        (etaoin/go driver test-util/TEST-URL-BASE)
        (etaoin/wait-visible driver {:tag :h2
                                     :fn/has-text "Patients"})
        (etaoin/click driver {:tag :a :fn/text "Edit"})
        (etaoin/wait-visible driver {:tag :h2 :fn/has-text "Edit patient"})
        ; clear full-name input
        (test-util/clear-input driver :full-name (:full-name patient))
        (etaoin/fill driver :full-name "")
        (etaoin/click driver {:tag :button
                              :fn/text "Save"})
        (etaoin/wait-enabled driver {:tag :button
                                     :fn/text "Save"})
        (is (etaoin/visible? driver {:tag :p
                                     :class :form-input-hint
                                     :fn/text "Full name is required."}))))))


(deftest test-update-patient-with-patient-does-not-exist-err
  (testing "check updating patient and showing it on the list page"
    (let [driver (get test-util/*test-system* ::test-util/chromedriver)]
      (etaoin/go driver (str test-util/TEST-URL-BASE "/patient/update/100"))
      (etaoin/wait-visible driver {:tag :h2 :fn/has-text "Edit patient"})
      (etaoin/wait-visible driver {:tag :div :fn/has-class "toast-error"})
      (etaoin/screenshot driver "page.png")
      (is (etaoin/visible? driver
            {:tag :p
             :fn/has-text "Patient does not exist."})))))


(deftest test-delete-patient-ok
  (let [db (get test-util/*test-system* :medbook.db/db)
        patient (test-util/create-patient! db)]
    (testing "check updating patient and showing it on the list page"
      (let [driver (get test-util/*test-system* ::test-util/chromedriver)]
        (etaoin/go driver test-util/TEST-URL-BASE)
        (etaoin/wait-visible driver {:tag :h2
                                     :fn/has-text "Patients"})
        (etaoin/visible? driver {:tag :td :fn/text "John Doe"})
        ;; go to form
        (etaoin/click driver {:tag :a :fn/text "Edit"})
        (etaoin/wait-visible driver {:tag :h2 :fn/has-text "Edit patient"})
        ;; delete patient
        (etaoin/click driver {:tag :button
                              :fn/text "Delete"})
        (etaoin/wait-visible driver {:tag :p
                                     :fn/has-text "There are no patients yet."})
        (is (false? (etaoin/visible? driver {:tag :td :fn/text "John Doe"})))
        (etaoin/wait-visible driver {:tag :div :fn/has-class "toast"})
        (is (etaoin/visible? driver
              {:tag :p
               :fn/has-text (str "Patient has been deleted successfully.")})))

      (testing "check patient has been deleted from db"
        (is (nil? (test-util/get-patient-by-insurance db (:insurance-number patient))))))))
