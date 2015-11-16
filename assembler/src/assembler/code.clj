(ns assembler.code
  (:require [assembler.parser :as parser]
            [assembler.utils :as utils]))

(def jump-map
  {"JGT" "001"
   "JEQ" "010"
   "JGE" "011"
   "JLT" "100"
   "JNE" "101"
   "JLE" "110"
   "JMP" "111"})

(def dest-map
  {"M"   "001"
   "D"   "010"
   "MD"  "011"
   "A"   "100"
   "AM"  "101"
   "AD"  "110"
   "AMD" "111"})

(def comp-map
  {"0" {"0"   "101010"
        "1"   "111111"
        "-1"  "111010"
        "D"   "001100"
        "A"   "110000"
        "!D"  "001101"
        "!A"  "110001"
        "-D"  "001111"
        "-A"  "110011"
        "D+1" "011111"
        "A+1" "110111"
        "D-1" "001110"
        "A-1" "110010"
        "D+A" "000010"
        "D-A" "010011"
        "A-D" "000111"
        "D&A" "000000"
        "D|A" "010101"}
   "1" {"M"   "110000"
        "!M"  "110001"
        "-M"  "110011"
        "M+1" "110111"
        "M-1" "110010"
        "D+M" "000010"
        "D-M" "010011"
        "M-D" "000111"
        "D&M" "000000"
        "D|M" "010101"}})

(defmulti code first)

(defmethod code :jump
  [[_ s]]
  (get jump-map s "000"))

(defmethod code :dest
  [[_ s]]
  (get dest-map s "000"))

(defmethod code :comp
  [[_ s]]
  (let [cmp-map (->> comp-map
                     (map (fn [[a m]]
                            (map (fn [[mnem value]]
                                   [mnem (str a value)])
                                 m)))
                     flatten
                     (apply hash-map))]
    (get cmp-map s)))

(defmulti code-instruction (comp parser/instruction-type first))

(defmethod code-instruction :c
  [[instruction symbol-table]]
  {:post [(= 16 (count %))]}
  (let [[dest cmp jump] (parser/parse-instruction instruction)]
    (->> [cmp dest jump]
         (map #(code [%1 %2]) [:comp :dest :jump])
         (cons "111")
         (apply str))))

(defmethod code-instruction :a
  [[instruction symbol-table]]
  {:post [(= 16 (count %))]}
  (-> (parser/parse-instruction instruction)
      last
      (#(or (get symbol-table (keyword %)) %))
      Integer.
      (Integer/toString 2)
      (utils/padd-left-with "0" 16)))
