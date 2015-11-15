(ns assembler.core-test
  (:require [assembler.core :refer :all]
            [midje.sweet :refer :all]))

(tabular "About assemble"
  (fact (assemble ?instructions) => ?expected)
  ?instructions                    ?expected
  "@1"                             "0000000000000001"
  "@1\n@2"                         "0000000000000001\n0000000000000010"
  "@1     \n  @2"                  "0000000000000001\n0000000000000010"
  "@1 //co\n  @2"                  "0000000000000001\n0000000000000010"

  "D=A"                            "1110110000010000"
  "@0"                             "0000000000000000"
  "D=D+A"                          "1110000010010000"
  "M=D"                            "1110001100001000"

  "AM=M-1"                         "1111110010101000")


(let [base-path "test/data/"]
  (tabular "About assemble"
    (fact (-> (str base-path ?filename) slurp assemble) => (slurp (str base-path ?expected-filename)))
    ?filename                   ?expected-filename
    "Add.asm"                   "Add.hack"
    "MaxL.asm"                  "MaxL.hack"
    "Max.asm"                   "Max.hack"
    "RectL.asm"                 "RectL.hack"
    "Rect.asm"                  "Rect.hack"
    "PongL.asm"                 "PongL.hack"))
