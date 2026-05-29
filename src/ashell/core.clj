(ns ashell.core
  (:require [clash.core :as clash]))

(defn -main [& all-args]
  (clash/start-shell))
