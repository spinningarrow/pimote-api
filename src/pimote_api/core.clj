(ns pimote-api.core
  (:require [ring.adapter.jetty :refer [run-jetty]]
            [ring.util.response :refer [content-type response]]
            [bidi.ring :refer [make-handler]]
            [clojure.java.io :as io]
            [pimote-api.remote :as remote])
  (:gen-class))

(def config (clojure.edn/read-string (slurp (io/resource "config.edn"))))

(defn index-handler
  [request]
  (response (pr-str {:message "hello!"})))

(defn tv-handler
  [request]
  (response (pr-str {:name "tv"})))

(defn receiver-handler
  [request]
  (response (pr-str {:name "receiver"})))

(defn executions-handler
  [{:keys [route-params]}]
  (let [device (get-in config [:devices (keyword (:device route-params))])
        action (get-in device [:actions (keyword (:action route-params))])
        remote (get-in config [:remotes (action :remote)])]
    (remote/tap (remote :name) (action :button))
    (response (str "This is device "
                 (:description device)
                 " and remote "
                 (remote :name)
                 " and action "
                 (:button action)))))

(defn wrap-access-control-allow-origin
  [handler]
  (fn [request]
    (let [response (handler request)]
      (assoc-in response [:headers "Access-Control-Allow-Origin"] "*"))))

(def handler
  (wrap-access-control-allow-origin
    (make-handler ["/" {"" index-handler
                        "tv" tv-handler
                        "receiver" receiver-handler
                        ["devices/" :device "/actions/" :action "/executions"] executions-handler}])))

(defn -main
  [& args]
  (run-jetty handler {:port (Integer/valueOf (or (System/getenv "port") "3000"))}))
