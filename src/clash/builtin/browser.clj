(ns clash.builtin.browser
  (:require [clojure.builtin.bash :refer [cmd]]
            [clojure.string :as string]))

(defn browser
  "Opens the given URL in the default browser."
  [url]
  (cmd "open" (if (string/starts-with? url "http") url (str "http://" url))))

(defn open-link
  "Opens a Link or list of Links in the default browser."
  [link]
  (if (sequential? link)
    (doseq [l link]
      (open-link l))
    (browser (:url link))))
