(ns user
  (:require [clash.builtin.browser :refer [browser]]
            [clash.builtin.bash :refer [cmd $]]
            [clash.state :refer [cwd]]
            [clash.builtin.fs :refer [cd ls]]
            [clash.builtin.editor :refer [edit edit-temporary]]))
