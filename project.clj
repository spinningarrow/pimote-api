(defproject pimote-api "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "MIT"
            :url "https://opensource.org/licenses/MIT"}
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [ring "1.6.3"]
                 [ring.middleware.logger "0.5.0"]
                 [bidi "2.1.2"]]
  :main ^:skip-aot pimote-api.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}}
  :ring {:handler pimote-api.core/handler}
  :plugins [[lein-ring "0.12.2"]])
