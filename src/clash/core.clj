(ns clash.core
  (:require [clojure.main :as main])
  (:gen-class))

(in-ns 'user)
(defn test-func []
  (println "Hello, World!"))

(in-ns 'clash.core)
(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (do 
  (main/main)))
