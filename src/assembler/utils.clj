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

(defn remove-empty-lines
  "Removes empty lines from a string s"
  [s]
  (-> s
      s/split-lines
      (->> (remove empty)
           (s/join "\n"))))

(defn clean
  "Cleans up the string s. Removes comments and white spaces."
  [s]
  (-> s
      (s/replace #"//.*" "")
      (s/replace #" " "")
      (s/replace #"^(\r\n)+" "")
      (s/replace #"[\n]+" "\n")
      remove-empty-lines))

(defn clean-labels
  "Cleans up the string s. Removes labels definitions"
  [s]
  (-> s
      (s/replace #"\(.*\)" "")
      (s/replace #"[\n]+" "\n")
      clean))

(defn format-output-filename
  "format the output filename given an input filename
   Prog.asm => Prog.hack"
  [input-filename]
  (->> input-filename
       (re-find #"(.*)\.asm")
       last
       (format "%s.hack")))

(defn int?
  [s]
  (boolean (re-matches #"\d+" s)))
