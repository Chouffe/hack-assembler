(ns assembler.utils-test
  (:require [assembler.utils :refer :all]
            [midje.sweet :refer :all]))

(tabular "About padd-left-with"
  (fact (padd-left-with ?s ?x ?limit) => ?expected)
  ?s          ?x      ?limit      ?expected
  "101"       "0"     4           "0101"
  "101"       "0"     8           "00000101"
  "101"       "1"     4           "1101")

(tabular "About clean"
  (fact (clean ?s) => ?expected)
  ?s                          ?expected
  "\n\n\n"                    ""
  "\r\n"                      ""
  "\r\n"                      ""
  "// comment"                ""
  "M=M+1     "                "M=M+1"
  "M=M+1 \n@12 //comment"     "M=M+1\n@12"
  "M=M+1 \r@12 //comment"     "M=M+1\r@12"
  "M=M+1;JMP   \n@12\nD=1"     "M=M+1;JMP\n@12\nD=1")

(tabular "About clean-labels"
  (fact (clean-labels ?s) => ?expected)
  ?s                          ?expected
  "(LOOP)"                    ""
  "(EQ_END)"                  ""
  "(OUTPUT_FIRST)"            ""
  "\r\n(LOOP)"                ""
  "\r\n(LOOP)\n(END)"         "")

(let [base-path "test/data/"]
  (tabular "About clean"
    (fact (-> (str base-path ?filename) slurp ?clean-fn) => (slurp (str base-path ?expected-filename)))
    ?filename          ?clean-fn        ?expected-filename
    "Max.asm"          clean            "Max.clean.asm"
    "Max.clean.asm"    clean-labels     "Max.clean.labels.asm"))
