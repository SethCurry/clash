(ns clash.builtin.browser
  (:require [clash.builtin.bash :refer [cmd]]
            [clojure.string :as string]))

(defn browser
  "Opens the given URL in the default browser."
  [url]
  (cmd "open" (if (string/starts-with? url "http") url (str "http://" url))))
