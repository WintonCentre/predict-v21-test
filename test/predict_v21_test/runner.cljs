(ns predict-v21-test.runner
  (:require [cljs.test :refer-macros [deftest is testing run-tests]]

            [predict-v21-test.compare-models :refer [conform-inputs]]
            [predict-v21-test.cljs-r-compare :as cljs-r]
            [com.rpl.specter :as sp]))

(enable-console-print!)

(deftest cljs-r-test
  (let [result (cljs-r/check-models)
        fail (sp/select [0 :clojure.test.check/ret :fail sp/FIRST sp/FIRST] (:details result))
        smallest (sp/select [0 :clojure.test.check/ret :shrunk :smallest sp/FIRST sp/FIRST] (:details result))]
    (when-not (:success result)
      (do
        (println (:details result))
        (println "fails on " fail)
        (println "smallest " smallest)
        (println "failure on " (conform-inputs (first fail)))
        (println "smallest " (conform-inputs (first smallest)))))
    (is (true? (:success result)) "quickcheck style comparison test js/cljs")))

(run-tests 'predict-v21-test.runner)
