(defproject cryogen "0.1.0"
  :description "Simple static site generator"
  :url "https://github.com/lacarmen/cryogen"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.6.0"]
                 [ring/ring-devel "1.3.2"]
                 [compojure "1.3.1"]
                 [ring-server "0.3.1"]
                 [cryogen-core "0.1.13"]
                 [org.clojure/tools.nrepl "0.2.5"]
                 [cider/cider-nrepl "0.9.0-SNAPSHOT"]]
  :plugins [[lein-ring "0.8.13"]]
  :main cryogen.core
  :ring {:init cryogen.server/init
         :handler cryogen.server/handler}
  :repl-options {:init-ns user}
  :profiles {:dev {:source-paths ["dev"]}})
