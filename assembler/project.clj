(defproject assembler "0.1.0-SNAPSHOT"
  :description "Assembler for the hack computer (Nand2Tetris)"
  :url "http://www.nand2tetris.org/"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.7.0"]]
  :profiles {:dev {:dependencies [[midje "1.8.2"]]}}
  :aot :all
  :main assembler.core)
