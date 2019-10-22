(ns predict-v21-test.point-test-run
  (:require [predict.models.predict :refer [her2-rh ki67-rh
                                            cljs-predict
                                            ]]
            [predict-v21-test.cljs-r-compare :refer [model1 model2
                                                     select-comparison-keys
                                                     ]])
  )


(def input-point-01 (into {}
                          [
                           [:bis :yes]
                           [:age 25]
                           [:radio nil]
                           [:bis? true]
                           [:tra :yes]
                           [:ki67 1]
                           [:chemoGen 3]
                           [:size 2]
                           [:radio? false]
                           [:nodes 2]
                           [:grade 1]
                           [:erstat 1]
                           [:rtime 15]
                           [:her2 1]
                           [:detection 1]
                           [:horm :h5]
                           [:her2-rh (her2-rh 1)]
                           [:ki67-rh (ki67-rh 1 1)]
                           [:delay 0]
                           ]))



; Model 1 is R
(model1 input-point-01)

; Model 2 is CLJS
(model2 input-point-01)

;select-comparison-keys select only needed ones
(select-comparison-keys (:benefits2-1 (cljs-predict input-point-01)))