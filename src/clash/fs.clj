(ns clash.fs
  (:require [clojure.java.io :as io]
            [clojure.string :as string]))


(def cwd (atom (.getCanonicalPath
                (io/file (let [env-path (System/getenv "STARTING_DIR")]
                           (if (nil? env-path)
                             "."
                             env-path))))))

(defn absolute-path
  "Converts a path to an absolute path.
   It is assumed that the path is relative to the cwd variable.
   
   Args:
   - path (string): The path to convert.

   Returns:
   - The absolute path.
  "
  [path]
  (try
    (.getCanonicalPath (io/file (io/file @cwd) path))
    (catch Exception e
      (if (string/ends-with? (.getMessage e) "is not a relative path")
        path
        (throw (Exception. (str "Error getting absolute path for " path ": " (.getMessage e))))))))

(defmacro cd
  "Changes the current working directory to the given directory."
  [dir]
  `(reset! cwd (.getCanonicalPath (io/file (io/file @cwd) ~(str dir)))))


(defn find-files
  "Finds all files in a directory and its subdirectories.
   
   Args:
   - dir (string): The directory to find files in.

   Returns:
   - (java.io.File[]) A sequence of file objects.
  "
  [dir]
  (let [all-files (.listFiles (io/file dir))]
    (flatten (map (fn [file] (if (.isDirectory file)
                               (find-files file)
                               file))
                  all-files))))

(defn file-has-extension?
  "Checks if a file has a given extension.
   
   Args:
   - file (java.io.File): The file to check.
   - extension (string): The extension to check for.

   Returns:
   - true if the file has the extension, false otherwise.
  "
  [file extension]
  (let [file-name (.getName file)]
    (string/ends-with? file-name (str "." extension))))
