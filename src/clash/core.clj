(ns clash.core
  (:require [clojure.pprint :refer [print-table]]
            [clojure.java.shell :refer [sh]]
            [clojure.string :as string]
            [clash.fs :refer [cwd absolute-path]]
            [clojure.repl :refer [doc]]
            [reply.main :as reply]))

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

(defn cmd
  "Executes the given command in the current working directory.

   Requires arguments to be passed as strings."
  [& args]
  (apply sh (flatten [args :dir @cwd])))

(defmacro $
  "Executes the provided command as bash would.

   Does not require arguments to be passed as strings."
  [& args]
  `(apply sh (flatten [~@(map str args) :dir @cwd])))

(defn browser
  "Opens the given URL in the default browser."
  [url]
  (cmd "open" (if (string/starts-with? url "http") url (str "http://" url))))

(defn read-file
  "Reads the contents of the given file and returns them as a string."
  [file]
  (slurp (absolute-path file)))

(defn edit
  "Opens the target file in Cursor."
  [target & {:keys [blocking] :or {blocking false}}]
  (let [edit-args ["cursor" (absolute-path target)]
        with-blocking (if blocking
                        (concat edit-args ["-n" "-w"])
                        edit-args)]
    (apply cmd with-blocking)))

(defn edit-temporary
  "Creates a temporary file and opens it in Cursor."
  [& {:keys [name content ext] :or {name "sclj-temp" content "" ext ".txt"}}]
  (let [temp-file (java.io.File/createTempFile name ext)]
    (spit temp-file content)
    (edit (.getAbsolutePath temp-file) :blocking true)
    (let [content (slurp temp-file)]
      (.delete temp-file)
      content)))

(defn- ls-impl [path]
  (string/split-lines (:out  (cmd "ls" path))))

(defn ls
  "Lists the contents of the given path.

   Returns a list of file names as strings."
  [& [path]]
  (if path
    (ls-impl (absolute-path path))
    (ls-impl ".")))

(defn open-link
  "Opens a Link or list of Links in the default browser."
  [link]
  (if (sequential? link)
    (doseq [l link]
      (open-link l))
    (browser (:url link))))
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
