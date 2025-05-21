(ns clash.core
  (:require [clojure.main :as main]
            [clash.startup :as startup])
  (:gen-class))

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (main/repl :init #(do
                      (require '[clash.startup :as startup])
                      (startup/init))))
