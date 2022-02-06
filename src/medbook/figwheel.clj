(ns medbook.figwheel
  (:require [integrant.core :as ig]
            [figwheel.main.api :as fig]
            [clojure.tools.logging :as log]
            [medbook.util.system :as system-util]))


(defmethod ig/init-key ::figwheel
  [_ _]
  (log/info (str "[Figwheel] Starting figwheel dev build..."))
  (fig/start
    {:mode :serve
     :rebel-readline false
     :open-url false}
    {:id system-util/BUILD-ID-DEV
     :config {:watch-dirs ["src"]
              :css-dirs   ["resources/public/css"]}
     :options {:main 'medbook.ui.main
               :output-to "target/resources/public/js/dev-main.js"
               :output-dir "target/resources/public/js/dev"
               :asset-path "/assets/js/dev"
               :closure-defines {'medbook.ui.main/DEBUG true
                                 "re_frame.trace.trace_enabled_QMARK_" true}
               :preloads ['day8.re-frame-10x.preload
                          'hashp.core]}}))


(defmethod ig/halt-key! ::figwheel
  [_ _]
  (log/info "[Figwheel] Stopping figwheel dev build...")
  (try
    (fig/stop system-util/BUILD-ID-DEV)
    (catch Exception _
      (log/info "[Figwheel] Warning: figwheel has been stopped."))))
