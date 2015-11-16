(ns assembler.code_test
  (:require [assembler.code :refer :all]
            [midje.sweet :refer :all]))

(tabular "About code"
  (fact (code [?type ?field]) => ?expected)
  ?type      ?field     ?expected
  :jump      "JMP"      "111"
  :jump      "JGT"      "001"
  :jump      "JEQ"      "010"
  :jump      "JLT"      "100"
  :jump      "JNE"      "101"
  :jump      "JLE"      "110"
  :jump      nil        "000"

  :dest      nil        "000"
  :dest      "M"        "001"
  :dest      "D"        "010"
  :dest      "MD"       "011"
  :dest      "A"        "100"
  :dest      "AM"       "101"
  :dest      "AD"       "110"
  :dest      "AMD"      "111"

  :comp      "0"        "0101010"
  :comp      "1"        "0111111"
  :comp      "-1"       "0111010"
  :comp      "D"        "0001100"
  :comp      "A"        "0110000"
  :comp      "!D"       "0001101"
  :comp      "!A"       "0110001"
  :comp      "-D"       "0001111"
  :comp      "-A"       "0110011"

  :comp      "M"        "1110000"
  :comp      "!M"       "1110001"
  :comp      "-M"       "1110011"
  :comp      "M+1"      "1110111"
  :comp      "M-1"      "1110010"
  :comp      "D+M"      "1000010"
  :comp      "D-M"      "1010011"
  :comp      "M-D"      "1000111"
  :comp      "D&M"      "1000000"
  :comp      "D|M"      "1010101"
  :comp      nil        nil)

(tabular "About code-instruction"
  (fact (code-instruction [?instruction ?symbol-table]) => ?expected)
  ?instruction      ?symbol-table  ?expected
  "@THAT"           {:THAT 3}      "0000000000000011"
  "@label"          {:label 16}    "0000000000010000"
  "@1"              {}             "0000000000000001"
  "@3"              {}             "0000000000000011"
  "@9"              {}             "0000000000001001"

  "M;JMP"           {}             "1111110000000111")
