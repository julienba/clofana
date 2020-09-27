(defproject clofana "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
            :url "https://www.eclipse.org/legal/epl-2.0/"}
  :dependencies [[org.clojure/clojure "1.10.1"]
                 [org.clojure/clojurescript "1.10.773"
                  :exclusions [com.google.javascript/closure-compiler-unshaded
                               org.clojure/google-closure-library
                               org.clojure/google-closure-library-third-party]]
                 [org.clojure/core.async "1.3.610"]
                 [com.google.javascript/closure-compiler-unshaded "v20200719"]

                 [thheller/shadow-cljs "2.11.0" :exclusions [ring/ring-core nrepl]]
                 [nrepl "0.7.0"]
                 [org.clojure/data.json "1.0.0"]
                 [org.clojure/tools.reader "1.3.3"]

                 [reagent "0.10.0"]
                 [re-frame "1.1.1"]
                 [cljs-http "0.1.46"]
                 [day8.re-frame/async-flow-fx "0.1.0"]

                 [metosin/reitit "0.4.2" :exclusions [com.cognitect/transit-clj borkdude/edamame]]

                 [aero "1.1.3"] ; config
                 ; replace by reitit
                 [ring/ring-core "1.8.1"]
                 [ring/ring-devel "1.8.1"]
                 [ring/ring-jetty-adapter "1.8.1"]
                 [ring/ring-json "0.5.0"]
                 [ring/ring-defaults "0.3.2"]
                 [mount "0.1.16"]
                 [tolitius/mount-up "0.1.2"]
                 [com.fzakaria/slf4j-timbre "0.3.17"] ; logs crap]
                 [clojure.java-time "0.3.2"]
                 [org.clojure/tools.logging "0.5.0"]
                 [metosin/ring-http-response "0.9.0"]

                 [cheshire "5.10.0"] ; json
                 [clj-http "3.10.0"]

                 ; Routing
                 [com.fasterxml.jackson.core/jackson-core "2.10.0"]
                 ;[com.fasterxml.jackson.datatype/jackson-datatype-joda "2.10.0"]
                 [com.fasterxml.jackson.core/jackson-databind "2.10.0"]
                 [com.fasterxml.jackson.datatype/jackson-datatype-jsr310 "2.10.0"]

                 [fork "1.2.5"] ; form

                 [kibu/pushy "0.3.8"] ; routing
                 [vlad "3.3.2"] ; form validation

                 [day8.re-frame/tracing "0.6.0"]
                 [day8.re-frame/http-fx "0.2.1"]]

  :plugins [[lein-shell "0.5.0"]
            [lein-shadow "0.2.2"]]

  :min-lein-version "2.9.0"

  :source-paths ["src/clj" "src/cljs" "src/cljc"]

  :clean-targets ^{:protect false} [;"resources/public/js/compiled" ; comment until I figure out where the problem is with shadow-cljs
                                    "target"]

  :shadow-cljs {:nrepl {:port 8777}
                :builds {:app {:target :browser
                               :output-dir "resources/public/js/compiled"
                               :asset-path "/js/compiled"
                               :modules {:app {:init-fn clofana.core/init
                                               :preloads [devtools.preload]}}

                               :devtools {:http-root "resources/public"
                                          :http-port 8280}}}}



  :aliases {
            "watch"        ["with-profile" "dev" "do"
                            ["shadow" "watch" "app"]]
            "release"      ["with-profile" "prod" "do"
                            ["shadow" "release" "app"]]}

  :profiles {:dev {:dependencies [[binaryage/devtools "1.0.2"]
                                  ;[day8.re-frame/re-frame-10x "0.5.1"]
                                  [day8.re-frame/tracing "0.6.0"]
                                  [javax.servlet/servlet-api "2.5"]]
                   :jvm-opts ["-Xmx1g" "-server"]
                   :source-paths ["src/clj" "src/cljs" "src/cljc" "dev"]
                   :repl-options {:init-ns dev
                                  :init (start)}}
             :uberjar {;:aot :all
                       :aot         [clofana.core]
                       :main        clofana.core
                       :omit-source true}
                       ;:prep-tasks  ["compile" ["release"]]} ; comment until I figure out where the problem is with shadow-cljs
             :prod {}})

  ;:main ^:skip-aot clofana.core)
