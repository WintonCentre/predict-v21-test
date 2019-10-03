(ns predict-v21-test.cljs-r-compare
  (:require
    [cljs.test :refer-macros [deftest is testing run-tests]]
    [clojure.test.check]
    [clojure.test.check.generators :as gen]
    [clojure.spec.alpha :as s]
    [clojure.spec.test.alpha :as st]
    [clojure.test.check.properties]
    [predict-v21-test.compare-models :refer [compare-models deep-epsilon-compare]]
    [predict.models.r.predict  :refer [r-predict]]
    [predict.models.predict :refer [cljs-predict]]
    )
  )

(s/def ::age (s/int-in 25 86))
(s/def ::size (s/int-in 0 501))                             ; mm
(s/def ::nodes (s/or :no-met1 (s/int-in 1 101)))
(s/def ::grade #{1 2 3 9})                                ;(s/or :just (s/int-in 1 4) :unknown #{9})
(s/def ::erstat #{0 1})                                     ;ER+ == 1 ER- == 0
(s/def ::detection #{0 1 2})                              ;=2 it fails ;(s/or :just (s/int-in 0 2) :unknown #{2}) (screen elsewhere)
(s/def ::her2 #{0 1 9})                                     ;(s/or :just (s/int-in 0 2) :unknown #{9})
(s/def ::ki67 #{0 1 9})                                   ;Ki67 = 0 it fails ;(s/or :just (s/int-in 0 2) :unknown #{9})
(s/def ::chemoGen #{2 3 0})                                 ;(s/or :just (s/int-in 2 4) :none #{0}) (generation elsewhere)
(s/def ::rtime #{15})                                       ;; specify 10 year prediction range
(s/def ::radio? #{0})
(s/def ::radio #{0})
(s/def ::tra #{0 1})
(s/def ::horm #{0 1})
(s/def ::bis #{0 1})
(s/def ::bis? #{1})

(def gen-size 1000)

(defn generate-inputs
  "Generate n randomised inputs"
  [n]
  (gen/sample (s/gen ::inputs) n))


(s/def ::inputs* (s/keys
                   :req-un [::age
                            ::size
                            ::nodes
                            ::grade
                            ::erstat
                            ::detection
                            ::her2
                            ::ki67
                            ::rtime
                            ::radio?
                            ::bis?
                            ::chemoGen
                            ::horm
                            ::radio
                            ::bis
                            ::tra
                            ]
                   ))

(s/def ::inputs (s/keys
                  :req-un [::age
                           ::size
                           ::nodes
                           ::grade
                           ::erstat
                           ::detection
                           ::her2
                           ::ki67
                           ::rtime
                           ::radio?
                           ::bis?
                           ::chemoGen
                           ::horm
                           ::radio
                           ::bis
                           ::tra]
                  ))

(defn convert-inputs [inputs]
  (->> inputs
       (map (fn [[k v]]
              [k
               (cond
                 (= k :bis) (if (= v 1) :yes nil)
                 (= k :radio) (if (= v 1) :yes nil)
                 (= k :bis?) (if (= v 1) true false)
                 (= k :tra) (if (= v 1) :yes nil)
                 (= k :radio?) (if (= v 1) true false)
                 (= k :horm) (if (= v 1) :yes nil)
                 :else v)
               ]))
       (into {})))


(defn fix-r-model-keys [results, inputs]
  (->> (if (= (:radio? inputs) 0) (assoc results :hr (:h results)) results)
       (map (fn [[k v]]
              [(cond
                 (= k :hc) (if (= (:radio? inputs) 0) :hrc k)
                 (= k :hct) (if (= (:radio? inputs) 0) :hrct k)
                 (= k :hctb) (if (= (:radio? inputs) 0) :hrctb k)
                 :else k) v]))
       (into {}))
  )

(defn select-comparison-keys [results]
  (select-keys results [:h :hr :hrc :hrct :hrctb]))

(defn model1 [inputs] (select-comparison-keys (fix-r-model-keys
                                          (js->clj (r-predict inputs) :keywordize-keys true) inputs)))

(comment
  (def inputs (first (generate-inputs 1)))
  (print inputs)
  (r-predict inputs)

  (compare-model1-model2 inputs)

  (map model1 (generate-inputs 100))                          ; r-model, eval timed out
  (map model2 (generate-inputs 5))
  (compare-model1-model2 (first (generate-inputs 5)))
  (map compare-model1-model2 (generate-inputs 5))

  (st/instrument `compare-model1-model2)
  (st/check `compare-model1-model2 {:clojure.test.check/opts {:num-tests 1}})

  (dotimes [i 10]
    (st/check `compare-model1-model2 {:clojure.test.check/opts {:num-tests 1}})
    )

  (dotimes [i 10]
    (def inputs (first (generate-inputs 1)))
    (print inputs)
    (r-predict inputs)
    (print "")
    (print "")
    )
  )

(defn model2 [inputs] (select-comparison-keys
                        (into {}
                              (map (fn [[key value]] {key (into [] (map #(* 100 %) (drop 1 value)))})
                                   (:benefits2-1 (cljs-predict (convert-inputs inputs)))))))

(defn compare-model1-model2
  [inputs]
  (compare-models inputs model1 model2))

(s/fdef compare-model1-model2
        :args (s/cat :inputs (s/spec ::inputs))
        :ret :predict-v21-test.compare-models/pair
        :fn #(every? true? (deep-epsilon-compare (:ret %))))

(defn check-models
  "Use test/check to compare models and find minimal failure case. "
  []
  (st/instrument `compare-model1-model2)
  (let [check-result (st/check `compare-model1-model2 {:clojure.test.check/opts {:num-tests gen-size}})]
    {:success (->> check-result first :clojure.test.check/ret :result)
     :details check-result}))
