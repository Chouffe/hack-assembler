(ns assembler.symbol-table
  (:require [clojure.string :as s]
            [assembler.utils :refer :all]
            [assembler.parser :as parser]))

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
    (let [value (-> instruction parser/parse-instruction second)]
      (if (or (int? value) (get symbol-table (keyword value)))
        [symbol-table current-memory-address]
        [(assoc symbol-table (keyword value) current-memory-address)
         (inc current-memory-address)]))))

(defn- handle-with
  "Handler function that takes in instructions a clean function a reduce
  function and an initial value"
  [instructions clean-f reduce-f x0]
  (->> instructions
       clean-f
       s/split-lines
       (reduce reduce-f x0)
       first))

(defn first-pass
  "Takes in a symbol-table and instructions and returns a symbol table"
  ([instructions]
   (first-pass pre-defined-symbols instructions))
  ([symbol-table instructions]
   (first-pass symbol-table instructions start-symbol-address))
  ([symbol-table instructions start-symbol-address]
   (let [label-symbol-table
         (handle-with instructions
                      clean
                      handle-label-definition
                      [symbol-table 0])]
     (handle-with instructions
                  (comp clean-labels clean)
                  handle-symbols
                  [label-symbol-table start-symbol-address]))))
