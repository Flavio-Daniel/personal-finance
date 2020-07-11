(ns personal-finance.auxiliar
  (:require
    [personal-finance.handler :refer [app]]
    [ring.adapter.jetty :refer [run-jetty]]
    [clj-http.client :as http]))

(def server (atom nil))

(defn start-server [port]
  (swap! server
         (fn [_] (run-jetty app {:port port :join? false}))))

(defn stop-server []
  (.stop @server))

(def port-default 3001)

(defn route-to [route]
  (str "http://localhost:" port-default route))

(def request-to (comp http/get route-to))

(defn content [route] (:body (request-to route)))



