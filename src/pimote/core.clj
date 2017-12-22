(ns pimote.core
  (:require [ring.adapter.jetty :refer [run-jetty]]
            [ring.util.response :refer [content-type response]]
            [bidi.ring :refer [make-handler]])
  (:gen-class))

(defn index-handler
  [request]
  (response (pr-str {:message "hello!"})))

(defn tv-handler
  [request]
  (response (pr-str {:name "tv"})))

(defn receiver-handler
  [request]
  (response (pr-str {:name "receiver"})))

(def handler
  (make-handler ["/" {"" index-handler
                      "tv" tv-handler
                      "receiver" receiver-handler}]))

(defn -main
  [& args]
  (run-jetty handler {:port (Integer/valueOf (or (System/getenv "port") "3000"))}))
