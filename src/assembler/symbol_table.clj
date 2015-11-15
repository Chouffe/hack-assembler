(ns assembler.symbol-table
  (:require [assembler.parser :as parser]
            [assembler.utils :refer :all]
            [clojure.string :as s]))

(def pre-defined-symbols
  (merge
    {:SCREEN 16384
     :KBD    24576
     :SP     0
     :LCL    1
     :ARG    2
     :THIS   3
     :THAT   4}
    (into {} (for [i (range 16)] [(keyword (str "R" i)) i]))))

(def start-symbol-address 16)

(defn handle-label-definition
  "Reducer function for handling label definition"
  [[symbol-table line-number] instruction]
  (if-not (= :label (parser/instruction-type instruction))
    [symbol-table (inc line-number)]
    [(-> instruction
         parser/parse-instruction
         keyword
         (#(assoc symbol-table % line-number)))
     line-number]))

(defn handle-symbols
  "Reducer function for handling symbols"
  [[symbol-table current-memory-address] instruction]
  (if-not (= :a (parser/instruction-type instruction))
    [symbol-table current-memory-address]
    (let [value (-> instruction
                    parser/parse-instruction
                    second
                    keyword)]
      (try
        (Integer. (name value))
        [symbol-table current-memory-address]
        (catch java.lang.NumberFormatException e
          (if (or (get symbol-table value) (re-matches #"[A-Z|_]*" (name value)))
            [symbol-table current-memory-address]
            [(assoc symbol-table value current-memory-address)
             (inc current-memory-address)]))))))

(defn first-pass
  "Takes in a symbol-table and instructions and returns a symbol table"
  ([instructions]
   (first-pass pre-defined-symbols instructions))
  ([symbol-table instructions]
   (first-pass symbol-table instructions start-symbol-address))
  ([symbol-table instructions start-symbol-address]
   (->> instructions
        clean
        ((juxt
           (comp first
                 (partial reduce handle-symbols [symbol-table start-symbol-address])
                 s/split-lines
                 clean-labels)
           (comp first
                 (partial reduce handle-label-definition [symbol-table 0])
                 s/split-lines)))
        (apply merge))))