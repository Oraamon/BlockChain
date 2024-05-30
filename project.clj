(defproject blockchain "0.1.0-SNAPSHOT"
  :description "API de Blockchain em Clojure"
  :dependencies [[org.clojure/clojure "1.10.1"]
                 [compojure "1.6.2"]
                 [ring/ring-defaults "0.3.2"]
                 [ring/ring-core "1.9.0"]
                 [cheshire "5.10.0"]
                 [clj-http "3.9.1"]
                 [org.clojure/tools.logging "1.1.0"]
                 [org.clojure/data.json "2.4.0"]] 
  :plugins [[lein-ring "0.12.5"]]
  :ring {:handler blockchain.core/app}
  :main ^:skip-aot blockchain.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}})
