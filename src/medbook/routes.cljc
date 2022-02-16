(ns medbook.routes
  (:require #?(:cljs [reitit.core :as reitit])
            #?(:cljs [reitit.coercion.spec :as reitit-spec])
            #?(:cljs [reitit.frontend :as reitit-front])
            #?(:clj [medbook.patients.handlers :as patients])
            [medbook.spec :as spec]))


(def api-routes
  "All API routes with handlers and specs."
  ["/api"
   ["/v1"
    ["/patients"
     ["" {:name ::patient-list
          :get {#?@(:clj [:responses {200 {:body ::spec/patients}}])
                #?@(:clj [:handler patients/patient-list])}
          :post {:parameters {:body ::spec/patient}
                 #?@(:clj [:responses {200 {:body ::spec/patient-with-id}}])
                 #?@(:clj [:handler patients/create-patient!])}}]
     ["/:patient-id" {:name ::patient-detail
                      :get {:parameters {:path {:patient-id ::spec/id}}
                            #?@(:clj [:responses {200 {:body ::spec/patient-with-id}}])
                            #?@(:clj [:handler patients/patient-detail])}
                      :put {:parameters {:path {:patient-id ::spec/id}
                                         :body ::spec/patient-update}
                            #?@(:clj [:responses {200 {:body ::spec/patient-with-id}}])
                            #?@(:clj [:handler patients/update-patient!])}
                      :delete {:parameters {:path {:patient-id ::spec/id}}
                               #?@(:clj [:responses {204 {:body nil?}}])
                               #?@(:clj [:handler patients/delete-patient!])}}]]]])


#?(:cljs
   (def api-router
     "Router for backend api."
     (reitit-front/router
       [api-routes]
       {:data {:coercion reitit-spec/coercion}})))


#?(:cljs
   (defn api-route-path
     "Return api route path by its name."
     ([route-name]
      (api-route-path route-name {}))
     ([route-name {:keys [path query]}]
      (-> api-router
        (reitit/match-by-name route-name path)
        (reitit/match->path query)))))
