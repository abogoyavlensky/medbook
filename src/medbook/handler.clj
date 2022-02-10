(ns medbook.handler
  (:require [clojure.tools.logging :as log]
            [integrant.core :as ig]
            [ring.util.response :as response]
            [reitit.ring :as ring]
            [reitit.ring.middleware.exception :as exception]
            [reitit.ring.middleware.parameters :as params]
            [reitit.ring.middleware.muuntaja :as muuntaja]
            [reitit.ring.coercion :as ring-coercion]
            [reitit.coercion.spec :as coercion-spec]
            [reitit.ring.spec :as ring-spec]
            [muuntaja.core :as muuntaja-core]
            [medbook.routes :as routes]
            [medbook.errors :as errors]
            [medbook.util.middlewares :as middlewares-util])
  (:import (java.sql SQLException)))


(defn- create-index-handler
  [{:keys [index-file root]}]
  (letfn [(index-handler-fn
            [_request]
            (-> index-file
              (response/resource-response {:root root})
              (response/content-type "text/html")))]
    (fn
      ([request]
       (index-handler-fn request))
      ([request respond _]
       (respond (index-handler-fn request))))))


(defn- default-error-handler
  [message exception _request]
  {:status 500
   :body {:type message
          :exception (.getClass exception)
          :error (ex-message exception)
          :data (ex-data exception)}})


(defn- application-error-handler
  [exception _request]
  (let [error-data (ex-data exception)]
    {:status (or (:http-code error-data) 400)
     :body {:type "Application error"
            :exception (.getClass exception)
            :error (:type error-data)
            :messages (:messages error-data)}}))


(defn- create-coercion-handler
  [exception _]
  (let [error-data (ex-data exception)
        messages (errors/coercion-exception->error-messages error-data)]
    {:status 400
     :body {:type "Request error"
            :exception (.getClass exception)
            :error (:type error-data)
            :messages messages}}))


(defn- wrap-exception
  [handler e request]
  (log/error (pr-str (:request-method request) (:uri request)) e)
  (handler e request))


(def exception-middleware
  "Common exception middleware to handle all errors."
  (exception/create-exception-middleware
    (merge
      exception/default-handlers
      {; exception from slingshot with :type as :medbook.handler/error
       ::error application-error-handler

       ; Database exception
       SQLException (partial default-error-handler "Database error")

       ; Request spec validation exception
       :reitit.coercion/request-coercion create-coercion-handler

       ; override the default handler
       ::exception/default (partial default-error-handler "Unexpected error")

       ; print stack-traces for all exceptions
       ::exception/wrap wrap-exception})))


(defn router
  "Return application router."
  [context]
  (ring/router
    [routes/api-routes
     ["/assets/*" (ring/create-resource-handler)]]
    {:validate ring-spec/validate
     :data {:muuntaja muuntaja-core/instance
            :coercion coercion-spec/coercion
            :middleware [; add handler options to request
                         [middlewares-util/wrap-handler-context context]
                         ; parse any request params
                         params/parameters-middleware
                         ; negotiate request and response
                         muuntaja/format-middleware
                         ; handle any exceptions
                         exception-middleware
                         ; coerce request and response to spec
                         ring-coercion/coerce-request-middleware
                         ring-coercion/coerce-response-middleware]}}))

(def ^:private handler
  "Main application handler."
  (fn [context]
    (ring/ring-handler
      (router context)
      (ring/routes
        (create-index-handler {:index-file "index.html"
                               :root "public"})
        (ring/redirect-trailing-slash-handler)
        (ring/create-default-handler)))))


(defmethod ig/init-key ::handler
  [_ {:keys [options] :as context}]
  (if (true? (:wrap-reload? options))
    (middlewares-util/wrap-reload #'handler context)
    (handler context)))
