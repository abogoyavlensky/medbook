(ns medbook.build
  "Tools for building and deploying lib artefacts to Clojars."
  (:require [clojure.java.io :as io]
            [clojure.string :as str]
            [clojure.tools.build.api :as tools-build]
            [org.corfield.build :as build-clj]
            [cljs.build.api :as cljs]
            [digest :as digest]))


(def ^:private JS-PATH "resources/public/js/")
(def ^:private JS-PROD-ORIGIN "prod-main.js")
(def ^:private JS-PROD-HASH "%s.js")
(def ^:private INDEX-HTML-SOURCE "resources/public/index.html")
(def ^:private INDEX-HTML-PROD "resources/public/prod.html")
(def ^:private UBER-FILE "medbook.standalone.jar")


(defn- clean-prod-assets
  [opts]
  (tools-build/delete {:path JS-PATH})
  (tools-build/delete {:path INDEX-HTML-PROD})
  (tools-build/delete {:path UBER-FILE})
  opts)


(defn- build-cljs
  [opts]
  (println "\nBuilding cljs...")
  (cljs/build "src"
    {:main 'medbook.ui.main
     :output-to "resources/public/js/prod-main.js"
     :output-dir "resources/public/js/prod"
     :asset-path "/assets/js/prod"
     :optimizations :advanced
     :closure-defines {'medbook.ui.main/DEBUG false}
     :parallel-build true})
  opts)


(defn hash-prod-assets
  "Hash production static files and update its names."
  [opts]
  (println "\nHashing static assets...")
  (let [js-prod-origin (str JS-PATH JS-PROD-ORIGIN)
        js-hash (digest/md5 (io/as-file js-prod-origin))
        js-hashed-name (format JS-PROD-HASH js-hash)
        js-hashed-path (str JS-PATH js-hashed-name)]
    (.renameTo (io/file js-prod-origin) (io/file js-hashed-path))
    (as-> (slurp INDEX-HTML-SOURCE) $
          (str/replace $ #"dev-main.js" js-hashed-name)
          (spit INDEX-HTML-PROD $)))
  opts)


(defn build
  "Build an uberjar for the app."
  [opts]
  (-> opts
    (assoc
      :uber-file UBER-FILE
      :main 'medbook.core)
    (build-clj/clean)
    (clean-prod-assets)
    (build-cljs)
    (hash-prod-assets)
    (build-clj/uber)))
