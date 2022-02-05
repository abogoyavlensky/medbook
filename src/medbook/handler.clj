(ns medbook.handler
  (:require [integrant.core :as ig]
            [ring.middleware.cookies :as cookies]
            [ring.util.response :as response]
            [reitit.dev.pretty :as pretty]
            [reitit.ring :as ring]
            [reitit.ring.middleware.exception :as exception]
            [reitit.ring.middleware.parameters :as params]
            [reitit.ring.middleware.muuntaja :as muuntaja]
            [reitit.ring.middleware.multipart :as multipart]
            [reitit.ring.coercion :as ring-coercion]
            [reitit.coercion.spec :as coercion-spec]
            [reitit.ring.spec :as ring-spec]
            [muuntaja.core :as muuntaja-core]
            [medbook.routes :as routes]
            [medbook.util.middlewares :as middlewares-util]))


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


(def ^:private handler
  "Main application handler."
  (fn [context]
    (ring/ring-handler
      (ring/router
        [["/api" {}
          routes/api-routes]
         ["/assets/*" (ring/create-resource-handler)]]
        {:validate ring-spec/validate
         :exception pretty/exception
         :data {:muuntaja muuntaja-core/instance
                :coercion coercion-spec/coercion
                :middleware [; add handler options to request
                             [middlewares-util/wrap-handler-context context]
                             ; parse any request params
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
                             cookies/wrap-cookies]}})
                ; TODO: fix spec for response!
                ;:responses {200 {:body string?}}}})
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
