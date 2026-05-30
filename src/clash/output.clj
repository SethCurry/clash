(ns clash.output
  (:require [clojure.string :as string]
            [clojure.pprint :refer [print-table]]))

(defprotocol ShellData
  (to-printable [this]))

; redirect I/O to file
; - stderr and stdout to one
; - only stdout
; - only stderr
; - both but to separate files
(defn value-to-string [value]
  (cond
    (string? value) (string/join "\n" (string/split-lines value))
    (satisfies? ShellData value) (.to-printable value)
    :else (pr-str value)))

(defn print-value [value]
  (cond
    (string? value) (doseq [line (string/split-lines value)]
                      (println line))
    (sequential? value) (do (println "--------------------------------")
                            (doseq [item value]
                              (print-value item)
                              (println "--------------------------------")))
    (map? value) (print-table value)
    :else (println value)))
