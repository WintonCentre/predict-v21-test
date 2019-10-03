(defproject predict-v21-test "2.1.0"
  :description "verify cljs predictv2.1 model against R model"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [
                 [org.clojure/clojure "1.9.0"]
                 [org.clojure/clojurescript "1.10.238"]     ; This breaks. Maybe node issue?
                 ;[org.clojure/clojure "1.10.1"]
                 ;[org.clojure/clojurescript "1.10.520"]
                 [org.clojure/core.async "0.4.474"]
                 [org.clojure/test.check "0.10.0-alpha2"]
                 [com.rpl/specter "1.1.0"]
                 [cljs-ajax "0.7.3"]
                 [predict-model "2.1.3"]
                 [predict-r-model "0.1.0"]
                 [funcool/promesa "1.9.0"]
                 [figwheel-sidecar "0.5.18"]]
  :jvm-opts ["-Xmx1g"]

  :plugins [[lein-cljsbuild "1.1.7" :exclusions [[org.clojure/clojure]]]
            [lein-ancient "0.6.15"]
            [lein-doo "0.1.8"]
            [lein-figwheel "0.5.13"]
            ]

  :source-paths ["src"]

  :cljsbuild {:builds
              [

               {:id           "runner"
                :source-paths ["src" "test"]
                ;; The presence of a :figwheel configuration here
                ;; will cause figwheel to inject the figwheel client
                ;; into your build. Use in the repl.
                :figwheel     true

                :compiler     {:asset-path           "target/js/runner/dev"
                               :output-to            "target/js/runner/runner.js"
                               :output-dir           "target/js/runner/dev"
                               :main                 predict-v21-test.runner
                               :target               :nodejs
                               :optimizations        :none
                               :source-map-timestamp true
                               :libs                 ["src/js/predict2model.js"]
                               }}

               ]}

  :figwheel
  {  ;; :http-server-root "public" ;; default and assumes "resources"
   ;; :server-port 3449 ;; default
   ;; :server-ip "127.0.0.1"

   :css-dirs ["resources/public/css"]             ;; watch and update CSS

   ;; Start an nREPL server into the running figwheel process
   ;; :nrepl-port 7888

   ;; Server Ring Handler (optional)
   ;; if you want to embed a ring handler into the figwheel http-kit
   ;; server, this is for simple ring servers, if this

   ;; doesn't work for you just run your own server :) (see lein-ring)

   ;; :ring-handler hello_world.server/handler

   ;; To be able to open files in your editor from the heads up display
   ;; you will need to put a script on your path.
   ;; that script will have to take a file path and a line number
   ;; ie. in  ~/bin/myfile-opener
   ;; #! /bin/sh
   ;; emacsclient -n +$2 $1
   ;;
   ;; :open-file-command "myfile-opener"

   ;; if you are using emacsclient you can just use
   ;; :open-file-command "emacsclient"

   ;; if you want to disable the REPL
   ;; :repl false

   ;; to configure a different figwheel logfile path
   ;; :server-logfile "tmp/logs/figwheel-logfile.log"

   ;; to pipe all the output to the repl
   ;; :server-logfile false

   }

  :profiles {:dev {:dependencies [[figwheel-sidecar "0.5.19-SNAPSHOT"]
                                  [cider/piggieback "0.4.1"]]
                   :source-paths ["src" "dev"]
                   :repl-options {:nrepl-middleware [cider.piggieback/wrap-cljs-repl]}}})

