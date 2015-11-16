(ns assembler.parser_test
  (:require [assembler.parser :refer :all]
            [midje.sweet :refer :all]))

(tabular "About instruction-type"
  (fact (instruction-type ?instruction) => ?expected)
  ?instruction  ?expected
  "@16"         :a
  "@0"          :a
  "@sys.init"   :a
  "@label"      :a
  "@LOOP"       :a
  "M=M+1"       :c
  "M=M+1;JMP"   :c
  "M=A|D;JLT"   :c
  "(sys.init)"  :label
  "(sys.halt)"  :label
  "(LOOP)"      :label
  "(END)"       :label)

(tabular "About parse-instruction"
  (fact (parse-instruction ?instruction) => ?expected)
  ?instruction     ?expected
  "(LOOP)"          "LOOP"
  "(END)"           "END"
  "(END_EQ)"        "END_EQ"
  "@16"             ["@" "16"]
  "@0"              ["@" "0"]
  "@sys.init"       ["@" "sys.init"]
  "@R15"            ["@" "R15"]
  "M=M+1"           ["M" "M+1" nil]
  "M=!A"            ["M" "!A" nil]
  "M=M+1;JMP"       ["M" "M+1" "JMP"]
  "A+M;JMP"         [nil "A+M" "JMP"]
  "M=A|D;JLT"       ["M" "A|D" "JLT"]
  "0;JMP"           [nil "0" "JMP"]
  "AM=M-1"          ["AM" "M-1" nil])
