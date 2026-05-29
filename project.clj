(defproject io.scurry/clash "0.0.1-SNAPSHOT"
  :description "A Clojure library for building shells"
  :url "https://github.com/SethCurry/clash"
  :license {:name "MIT"
            :url "https://opensource.org/licenses/MIT"}
  :dependencies [[org.clojure/clojure "1.12.5"]
                 [reply/reply "0.5.1"]]
  :repl-options {:init-ns clash.core})
