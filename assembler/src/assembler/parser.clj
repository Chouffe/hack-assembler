(ns assembler.parser)

(def ^:private label-definition-regex #"^\((.+)\)")
(def ^:private a-instruction-regex #"@([\d|\w|\.|\$]+)")
(def ^:private a-instruction-label-regex #"@([a-zA-Z|_|\.|\$]+[0-9]*)")
(def ^:private c-instruction-regex
  #"^(?:(\w*)=)?([\w|\+|\-|\!|\||&|\d]*)(?:;?(\w{3}))?$")

(defn instruction-type
  "Given an instruction as a string s, it returns its type :a, :a-symbol label
    or :c
   (instruction-type '@label') => :a
   (instruction-type 'M=M+1') => :c"
  [s]
  (cond
    (re-matches label-definition-regex s) :label
    (re-matches a-instruction-label-regex s) :a
    (re-matches a-instruction-regex s) :a
    :else :c))

(defmulti parse-instruction instruction-type)

(defmethod parse-instruction :a
  [s]
  (->> s
       (re-matches a-instruction-regex)
       last
       (conj ["@"])))

(defmethod parse-instruction :label
  [s]
  (->> s
       (re-matches label-definition-regex)
       last))

(defmethod parse-instruction :c
  [s]
  (->> s
       (re-matches c-instruction-regex)
       rest))
