(ns medbook.figwheel
  (:require [integrant.core :as ig]
            [figwheel.main.api :as fig]
            [clojure.tools.logging :as log]))


(def ^:private BUILD-ID-DEV "dev")


(defmethod ig/init-key ::figwheel
  [_ _]
  (log/info (str "[Figwheel] Starting figwheel dev build..."))
  (fig/start
    {:mode :serve
     :rebel-readline false
     :cljs-devtools false
     :open-url false}
    {:id BUILD-ID-DEV
     :config {:watch-dirs ["src"]
              :css-dirs   ["resources/public/css"]}
     :options {:main 'medbook.ui.main
               :output-to "target/public/assets/dev-main.js"
               :output-dir "target/public/assets/dev"
               :asset-path "assets/dev"}}))


(defmethod ig/halt-key! ::figwheel
  [_ _]
  (log/info "[Figwheel] Stopping figwheel dev build...")
  (fig/stop BUILD-ID-DEV))
