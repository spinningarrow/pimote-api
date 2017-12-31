(ns pimote-api.core
  (:require [ring.adapter.jetty :refer [run-jetty]]
            [ring.util.response :refer [content-type response]]
            [ring.middleware.logger :refer [wrap-with-logger]]
            [bidi.ring :refer [make-handler]]
            [cheshire.core :refer [generate-string]]
            [clojure.java.io :as io]
            [pimote-api.remote :as remote])
  (:gen-class))

(def config (clojure.edn/read-string (slurp (io/resource "config.edn"))))

(defn index-handler
  [request]
  {:message "hello!"})

(defn actions-handler
  [{:keys [route-params]}]
  (let [device (get-in config [:devices (keyword (:device route-params))])]
    (sort (keys (device :actions)))))

(defn executions-handler
  [{:keys [route-params]}]
  (let [device (get-in config [:devices (keyword (:device route-params))])
        action (get-in device [:actions (keyword (:action route-params))])
        remote (get-in config [:remotes (action :remote)])]
    (remote/tap (remote :name) (action :button))
    {:device (device :description)
     :remote (remote :name)
     :button (action :button)}))

(defn wrap-response-edn
  [handler]
  (fn [request]
    (let [body (handler request)]
      (response (pr-str body)))))

(defn wrap-response-json
  [handler]
  (fn [request]
    (let [body (handler request)
          json-response (response (generate-string body))]
      (assoc-in json-response [:headers "Content-Type"] "application/json"))))

(defn wrap-access-control-allow-origin
  [handler]
  (fn [request]
    (let [response (handler request)]
      (assoc-in response [:headers "Access-Control-Allow-Origin"] "*"))))

(def routes
  ["/" {"" index-handler
        ["devices/" :device "/actions"] actions-handler
        ["devices/" :device "/actions/" :action "/executions"] executions-handler}])

(def handler
  (->
    (make-handler routes)
    wrap-response-json
    wrap-access-control-allow-origin
    wrap-with-logger))

(defn -main
  [& args]
  (run-jetty handler {:port (Integer/valueOf (or (System/getenv "port") "3000"))}))
