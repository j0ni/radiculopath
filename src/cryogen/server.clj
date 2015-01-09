(ns cryogen.server
  (:require [compojure.core :refer [GET defroutes]]
            [compojure.route :as route]
            [ring.util.response :refer [redirect]]
            [cryogen-core.watcher :refer [start-watcher!]]
            [cryogen-core.compiler :refer [compile-assets-timed read-config]]
            [cider.nrepl :as cider]
            [clojure.tools.nrepl.server :as nrepl]))

(defn start-repl [port]
  (let [cider-middleware (map resolve cider/cider-middleware)]
    (try
      (nrepl/start-server
       :port port
       :handler (apply nrepl/default-handler cider-middleware))
      (catch Throwable t
        (let [message (.getMessage t)]
          (println message)
          (.printStackTrace t))))))

(defn init []
  (let [config (read-config)
        ignored-files (:ignored-files config)]
    (when-let [repl-port (:repl-port config)]
      (start-repl repl-port))
    (compile-assets-timed)
    (start-watcher! "resources/templates" ignored-files compile-assets-timed)))

(defroutes handler
  (GET "/" [] (redirect (str (:blog-prefix (read-config)) "/index.html")))
  (route/resources "/")
  (route/not-found "Page not found"))
