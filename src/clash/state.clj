(ns clash.state
  (:require [clojure.java.io :as io]))

(def cwd (atom (.getCanonicalPath
                (io/file (let [env-path (System/getenv "STARTING_DIR")]
                           (if (nil? env-path)
                             "."
                             env-path))))))
