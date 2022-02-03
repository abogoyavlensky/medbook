(ns medbook.db
  (:require [integrant.core :as ig]
            [hikari-cp.core :as cp]))


(defmethod ig/init-key ::db
  [_ {:keys [options]}]
  (cp/make-datasource options))


(defmethod ig/halt-key! ::db
  [_ datasource]
  (cp/close-datasource datasource))
