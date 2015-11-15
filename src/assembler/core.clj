(ns assembler.core
  (:require [assembler.utils :refer :all]
            [assembler.code :as code]
            [assembler.symbol-table :as st]
            [clojure.string :as s])
  (:gen-class))

(defn assemble
  "Assembles asm instructions. It cleans up the instructions and codes each
   mnemonic to its Machine Language value."
  [instructions]
  (let [symbol-table (st/first-pass instructions)]
    (-> instructions
        clean
        clean-labels
        s/split-lines
        (->> (map #(code/code-instruction [% symbol-table]))
             (s/join "\n" )))))

(defn -main
  [& args]
  (let [filename (first *command-line-args*)]
    (->> filename
         slurp
         assemble
         (spit (->> filename
                    (re-find #"(.*)\.asm")
                    last
                    (format "%s.hack"))))))
