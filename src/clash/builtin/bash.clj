(ns clash.builtin.bash
  (:require [clash.state :refer [cwd]]
            [clojure.java.shell :refer [sh]]))

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
