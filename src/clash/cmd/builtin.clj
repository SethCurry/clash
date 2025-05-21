(ns clash.cmd.builtin
  (:require [clojure.java.shell :as shell]
            [clash.runtime :as runtime]))

(defn cd [dir]
  (reset! runtime/current-working-dir dir))

(defn pwd []
  @runtime/current-working-dir)

(defn sh [& rest]
  (apply shell/sh rest))