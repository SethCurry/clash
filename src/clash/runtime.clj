(ns clash.runtime)

(def current-working-dir (atom (System/getProperty "user.dir")))