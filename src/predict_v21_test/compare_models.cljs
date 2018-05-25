(ns predict-v21-test.compare-models
  (:require
    [cljs.test :refer-macros [deftest is testing run-tests]]
    [clojure.test.check.generators :as gen]
    [clojure.spec.alpha :as s]
    [clojure.spec.test.alpha :as st]
    [com.rpl.specter :as sp]
    ))

(s/def ::pair (s/and vector? #(= (count %) 2)))

(s/def ::true true?)

(defn conform-inputs [inputs]
  (s/conform ::inputs inputs))

;; tolerance for float compares
(def epsilon 1e-12)

(defn compare-floats
  "Compare two floats up to a tolerance of epsilon"
  [epsilon f1 f2]
  (< (Math.abs (- f1 f2)) epsilon))

(def epsilon-compare (partial compare-floats epsilon))

(defn extract-numbers
  "Extract all the numbers from a form"
  [form]
  (sp/select (sp/walker number?) form)
  )

(defn deep-epsilon-compare
  "Compare numbers within two nested forms up to epsilon"
  [[form1 form2]]
  (map epsilon-compare (extract-numbers form1) (extract-numbers form2)))


(defn compare-models
  "Compare the results of running two models that should agree"
  [inputs model1 model2]
  ((juxt model1 model2) inputs))


(defn find-difference
  "Compare year by year - i.e. transpose the result of compare-models"
  [inputs model1 model2]
  (apply map  vector (compare-models inputs model1 model2)))                                 ;transpose