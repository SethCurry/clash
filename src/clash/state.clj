(ns clash.state
  (:require [clojure.java.io :as io]))

(def cwd (atom (.getCanonicalPath
                (io/file (let [env-path (System/getenv "STARTING_DIR")]
                           (if (nil? env-path)
                             "."
                             env-path))))))

(def env (atom {}))

(defn set-env [env-key env-value]
  (swap! env #(assoc % env-key env-value)))
