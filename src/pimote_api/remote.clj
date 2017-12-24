(ns pimote-api.remote
  "A thin wrapper around irsend"
  (:require [clojure.java.shell :refer [sh]]))

(defn tap
  [remote button]
  (sh "irsend" "SEND_ONCE" remote button))

(defn hold
  [remote button]
  (sh "irsend" "SEND_START" remote button))

(defn release
  [remote button]
  (sh "irsend" "SEND_STOP" remote button))
