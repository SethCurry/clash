(ns clash.core
  (:require [clojure.pprint :refer [print-table]]
            [clojure.string :as string]
            [clojure.repl :refer [doc]]
            [reply.main :as reply]
            [taoensso.telemere :as t])
  (:gen-class))

(defprotocol ShellData
  (to-printable [this])
  (to-row [this]))

(defprotocol Tableable
  (table-to-console [this]))

(defrecord Table [rows]
  Tableable
  (table-to-console [this]
    (print-table (doall (map #(if (satisfies? ShellData %) (to-row %) %) rows)))))

(defrecord ShellState [cwd])

(defmacro help [symbol]
  (doc symbol))

; redirect I/O to file
; - stderr and stdout to one
; - only stdout
; - only stderr
; - both but to separate files
(defn value-to-string [value]
  (cond
    (string? value) (string/join "\n" (string/split-lines value))
    (sequential? value) (if (map? (first value))
                          (->Table value)
                          (map value-to-string value))
    (satisfies? ShellData value) (.to-printable value)
    :else (pr-str value)))

(defn print-value [value]
  (cond
    (satisfies? Tableable value) (.table-to-console value)
    (string? value) (doseq [line (string/split-lines value)]
                      (println line))
    (sequential? value) (do (println "--------------------------------")
                            (doseq [item value]
                              (print-value item)
                              (println "--------------------------------")))
    (map? value) (print-table value)
    :else (println value)))

(defn start-shell []
  (reply/launch-standalone {:standalone true
                            :custom-eval "" ;(slurp "src/user.clj")
                            :value-to-string value-to-string
                            :print-value print-value
                            :skip-default-init true}))

(defn -main [& all-args]
  (t/set-min-level! :debug)
  (start-shell))
