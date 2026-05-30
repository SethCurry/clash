(ns clash.core
  (:require [clojure.pprint :refer [print-table]]
            [clojure.string :as string]
            [clojure.repl :refer [doc]]
            [reply.main :as reply]
            [clojure.java.io :as io]
            [taoensso.telemere :as t])
  (:import [java.io File])
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

(let [homedir (io/file (System/getProperty "user.home"))
      usersdir (.getParent homedir)]
  (defn home
    "With no arguments, returns the current value of the `user.home` system
     property. If a `user` is passed, returns that user's home directory. It
     is naively assumed to be a directory with the same name as the `user`
     located relative to the parent of the current value of `user.home`."
    ([] homedir)
    ([user] (if (empty? user) homedir (io/file usersdir user)))))

(defn expand-home
  "If `path` begins with a tilde (`~`), expand the tilde to the value
  of the `user.home` system property. If the `path` begins with a
  tilde immediately followed by some characters, they are assumed to
  be a username. This is expanded to the path to that user's home
  directory. This is (naively) assumed to be a directory with the same
  name as the user relative to the parent of the current value of
  `user.home`."
  [path]
  (let [path (str path)]
    (if (.startsWith path "~")
      (let [sep (.indexOf path File/separator)]
        (if (neg? sep)
          (home (subs path 1))
          (io/file (home (subs path 1 sep)) (subs path (inc sep)))))
      path)))

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
