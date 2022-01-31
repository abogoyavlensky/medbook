(ns medbook.handler
  (:require [integrant.core :as ig]
            [reitit.dev.pretty :as pretty]
            [reitit.ring :as ring]
            [ring.middleware.cookies :as cookies]
            [reitit.ring.middleware.exception :as exception]
            [reitit.ring.middleware.parameters :as params]
            [reitit.ring.middleware.muuntaja :as muuntaja]
            [reitit.ring.middleware.multipart :as multipart]
            [reitit.ring.coercion :as ring-coercion]
            [reitit.coercion.spec :as coercion-spec]
            [muuntaja.core :as muuntaja-core]
            [reitit.ring.spec :as ring-spec]
            [medbook.patients :as patients]))


(defn- handler
  "Main application handler."
  [_context]
  (ring/ring-handler
    (ring/router
      [["/api" {}
        ["/patient" {:name ::patient-list
                     :get {:handler patients/patient-list
                           :parameters {}}}]]
       ["/" {:name ::index}]]
      {:validate ring-spec/validate
       :exception pretty/exception
       :data {:muuntaja muuntaja-core/instance
              :coercion coercion-spec/coercion
              :middleware [; parse any request params
                           params/parameters-middleware
                           ; negotiate request and response
                           muuntaja/format-middleware
                           ; coerce request and response to spec
                           ring-coercion/coerce-exceptions-middleware
                           ring-coercion/coerce-request-middleware
                           ring-coercion/coerce-response-middleware
                           ; handle any exceptions
                           exception/exception-middleware
                           ; handle multipart data
                           multipart/multipart-middleware
                           cookies/wrap-cookies]
              ; all handlers should return rendered html string
              :responses {200 {:body string?}}}})
    (ring/routes
      (ring/create-resource-handler {:path "/assets/"})
      (ring/redirect-trailing-slash-handler)
      (ring/create-default-handler))))


(defmethod ig/init-key ::handler
  [_ context]
  (handler context))
