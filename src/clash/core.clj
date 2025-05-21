(ns clash.core
  (:require [clojure.main :as main]
            [clash.startup :as startup])
  (:gen-class))

(in-ns 'user)
(defn test-func []
  (println "Hello, World!"))

(in-ns 'clash.core)
(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (main/repl :init #(do
                      (require '[clash.startup :as startup])
                      (startup/init))))
