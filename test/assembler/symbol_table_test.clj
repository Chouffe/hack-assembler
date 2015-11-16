(ns assembler.symbol-table-test
  (:require [assembler.symbol-table :refer :all]
            [midje.sweet :refer :all]))

(tabular "About handle-label-definition"
  (fact (handle-label-definition ?state ?instruction) => ?expected)
  ?state         ?instruction  ?expected
  [{} 0]         "(LOOP)"      [{:LOOP 0} 0]
  [{} 10]        "(LOOP)"      [{:LOOP 10} 10]
  [{} 10]        "(LOOP)"      [{:LOOP 10} 10]
  [{} 10]        "(sys.init)"  [{:sys.init 10} 10]
  [{} 10]        "@1"          [{} 11]
  [{:LOOP 1} 10] "@1"          [{:LOOP 1} 11])

(tabular "About handle-symbols"
  (fact (handle-symbols ?state ?instruction) => ?expected)
  ?state                     ?instruction         ?expected
  [{} 16]                    "@label"             [{:label 16} 17]
  [{} 16]                    "@symbol.init"       [{:symbol.init 16} 17]
  [{} 16]                    "@ponggame.0"        [{:ponggame.0 16} 17]
  [{:label 3} 16]            "@label"             [{:label 3} 16]
  [{:label 3} 16]            "@12"                [{:label 3} 16]
  [{} 16]                    "@INFINITE_LOOP"     [{} 16]
  [{:label 3} 16]            "@M=1"               [{:label 3} 16])

(tabular "About first-pass"
  (fact (first-pass ?symbol-table ?instructions ?start) => ?expected)
  ?symbol-table          ?instructions                ?start  ?expected
  {:a 0 :b 1}            "@1\n@a\n@c"                 16      {:a 0 :b 1 :c 16}
  {:a 0 :b 1}            "@1\n@b\n@c"                 256     {:a 0 :b 1 :c 256}
  {:a 0 :b 1}            "@1\n(LOOP)\n@foo"           16      {:a 0 :b 1 :foo 16 :LOOP 1}
  {:a 0 :b 1}            "@0\nD;JLE\n@END\n@counter"  16      {:a 0 :b 1 :counter 16}
  pre-defined-symbols    "@R5\n@R0\n@R1"              16      (contains {:R5 5 :R0 0 :R1 1}))

(let [base-path "test/data/"]
  (tabular "About first-pass"
    (fact (->> (str base-path ?filename) slurp (first-pass ?symbol-table)) => ?expected)
    ?filename          ?symbol-table          ?expected
    "Max.asm"          pre-defined-symbols    (contains (merge pre-defined-symbols
                                                               {:OUTPUT_FIRST 10
                                                                :OUTPUT_D 12
                                                                :INFINITE_LOOP 14}))
    "Rect.asm"         pre-defined-symbols    (contains (merge pre-defined-symbols
                                                               {:counter 16
                                                                :address 17
                                                                :LOOP 10
                                                                :INFINITE_LOOP 23}))))
