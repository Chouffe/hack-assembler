(ns assembler.utils
  (:require [clojure.string :as s]))

(defn padd-left-with
  "Padds s with the character x up to the limit of characters allowed."
  [s x limit]
  (-> s
      reverse
      (concat (repeat x))
      (->> (take limit)
           reverse
           (apply str))))

;; TODO: fixme for max.asm
(defn clean
  "Cleans up the string s. Removes comments and white spaces."
  [s]
  (-> s
      (s/replace #"//.*" "")
      (s/replace #" " "")
      (s/replace #"^(\r\n)+" "")
      (s/replace #"[\n]+" "\n")
      s/split-lines
      (->> (remove empty)
           (s/join "\n"))))

;; TODO: fixme for max.asm
(defn clean-labels
  "Cleans up the string s. Removes labels definitions"
  [s]
  (-> s
      (s/replace #"\(.*\)" "")
      (s/replace #"[\n]+" "\n")
      clean
      s/split-lines
      (->> (remove empty)
           (s/join "\n"))))
