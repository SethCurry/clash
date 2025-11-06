(defproject io.scurry/clash "0.0.1a"
  :description "A Clojure library for building shells"
  :url "https://github.com/SethCurry/clash"
  :license {:name "MIT"
            :url "https://opensource.org/licenses/MIT"}
  :dependencies [[org.clojure/clojure "1.11.1"]
                 [reply/reply "0.5.0"]]
  :repl-options {:init-ns clash.core})
