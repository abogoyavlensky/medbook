(ns medbook.util.middlewares
  (:require [ring.middleware.reload :as reload]))


(defn wrap-handler-context
  "Add system component dependencies and options for handler to request as a context."
  [handler context]
  (fn [request]
    (-> request
      (assoc request handler :context context)
      (handler))))


(defn wrap-reload
  "Wrap handler var to auto reload namespaces before each request."
  [handler context]
  (let [reload! (#'reload/reloader ["src"] true)]
    (fn
      ([request]
       (reload!)
       ((handler context) request))
      ([request respond raise]
       (reload!)
       ((handler context) request respond raise)))))
