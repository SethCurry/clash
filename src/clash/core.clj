(ns clash.core
  (:require
   [clojure.repl :refer [doc]]
   [clash.output :refer [value-to-string print-value]]
   [reply.main :as reply]
   [clojure.java.io :as io]
   [taoensso.telemere :as t]
   [clash.builtin.fs :refer [expand-home]])
  (:gen-class))

(defmacro help [symbol]
  (doc symbol))

(defn config-path []
  (expand-home "~/.config/clash"))

(defn init-path []
  (str (config-path) "/init.clj"))

(defn has-init-file? []
  (.exists (io/file (init-path))))

(defn start-shell []
  (let [base-reply-options {:standalone true
                       ;:custom-init (read-string (str "(do " (slurp "/Users/scurry/.config/clash/init.clj") ")"))
                            :value-to-string value-to-string
                            :print-value print-value}
        reply-options (if (has-init-file?)
                        (assoc base-reply-options :custom-init (read-string (str "(do " (slurp (init-path)) ")")))
                        base-reply-options)]
    (reply/launch-standalone reply-options)))

(defn -main [& all-args]
  (t/set-min-level! :debug)
  (start-shell))
