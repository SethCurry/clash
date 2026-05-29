(ns clash.builtin.editor
  (:require [clash.builtin.fs :refer [absolute-path]]
            [clash.builtin.bash :refer [cmd]]))

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
