(ns medbook.figwheel
  (:require [integrant.core :as ig]
            [figwheel.main.api :as fig]
            [clojure.tools.logging :as log]
            [medbook.util.system :as system-util]))


(defn- start-figwheel
  [{:keys [mode build-id]
    :or {mode :serve
         build-id system-util/BUILD-ID-DEV}}]
  (fig/start
    {:mode mode
     :rebel-readline false
     :cljs-devtools false
     :open-url false}
    {:id build-id
     :config (cond-> {:css-dirs   ["resources/public/css"]}
               (= :serve mode) (assoc :watch-dirs ["src"]))
     :options (cond-> {:main 'medbook.ui.main
                       :output-to (format "target/resources/public/js/%s-main.js" build-id)
                       :output-dir (format "target/resources/public/js/%s" build-id)
                       :asset-path (format "/assets/js/%s" build-id)}
                (= :build-once mode) (assoc
                                       :clean-outputs true
                                       :optimizations :whitespace)
                (= :serve mode) (assoc
                                  :closure-defines {'medbook.ui.main/DEBUG true
                                                    "re_frame.trace.trace_enabled_QMARK_" true}
                                  :preloads ['day8.re-frame-10x.preload
                                             'hashp.core]))})
  {:build-id build-id})


(defmethod ig/init-key ::figwheel
  [_ {:keys [options]}]
  (log/info (str "[Figwheel] Starting figwheel dev build..."))
  (start-figwheel options))


(defmethod ig/halt-key! ::figwheel
  [_ {:keys [build-id]}]
  (log/info "[Figwheel] Stopping figwheel dev build...")
  (try
    (fig/stop build-id)
    (catch Exception _
      (log/info "[Figwheel] Warning: figwheel has been stopped."))))


; Run build-once
(comment
  (fig/stop system-util/BUILD-ID-TEST)
  (let [options {:mode :build-once
                 :build-id system-util/BUILD-ID-TEST}]
    (start-figwheel options)))
