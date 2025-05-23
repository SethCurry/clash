(ns clash.lib.db
    (:require [next.jdbc :as jdbc]))

(def dsn (jdbc/get-datasource {
    :dbtype "postgresql"
    :dbname "clash"
    :host "localhost"
    :port 5432
    :user "clash"
    :password "clash"
}))

