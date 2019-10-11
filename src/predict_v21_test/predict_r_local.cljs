(ns predict-v21-test.predict-r-local
  "Shells out to an R model of predict"
  (:require [cljs.nodejs :as nodejs]))

(def shell (nodejs/require "shelljs"))

(def version "0.1.1")
;(def maven-directory (str "${HOME}/.m2/repository/predict-r-model/predict-r-model/" version "/"))
;(def extract-directory maven-directory)
;(def library-jar (str "predict-r-model-" version ".jar"))
(def predict-shell-file "predict_r.sh")
(def predict-r-file "Predict-v2.1-2018-04-07.R")

;(def predict-sh (str extract-directory predict-shell-file))
(def predict-sh (str "/Users/jin/Workspace/PredictBreast_Testing/predict-v21-test/tempdebug/" predict-shell-file))

(defn exec
  "shell out a command, and return the output.
  exec is synchronous - which helps hugely with check.test testing"
  [command]
  (.exec shell command #js {:silent false})
  )

;(exec (str "tar -zxvf " maven-directory library-jar " -C " extract-directory " " predict-shell-file))
;(exec (str "tar -zxvf " maven-directory library-jar " -C " extract-directory " " predict-r-file))
;
;; sh needs to become executable. Before was not having this issue but now need this fix.
;(print (str extract-directory))
;(print (str predict-shell-file))
;(print (str predict-sh))
;(exec (str predict-sh))
;;(print (str "chmod +x " extract-directory " " predict-shell-file))
;(print (str "chmod +x " extract-directory" "predict-shell-file))
;(exec (str "chmod +x " extract-directory" "predict-shell-file))


(defn errmsg [cmd]
  (println "Check " cmd " works in your copy of R. Check libraries are available. (Sanity check edit 2)"))

(defn r-predict
  "Map js/cljs input keys to R input variables and post them to R"
  [{:keys [age size nodes grade erstat detection her2 ki67 rtime radio? bis? chemoGen horm radio bis tra]
    :as   inputs}]
  ;(println "===> start of r-predict clojure")
  ;(enable-console-print!)
  (let [command (str predict-sh
                     " '"
                     (.stringify js/JSON
                                 (clj->js (->> inputs
                                               (map (fn [[k v]]
                                                      [(cond
                                                         (= k :age) :age.start
                                                         (= k :erstat) :er
                                                         (= k :detection) :screen
                                                         (= k :chemoGen) :generation
                                                         (= k :tra) :traz
                                                         (= k :radio?) :r.enabled
                                                         :else k) v]))
                                               (into {}))))
                     "'")
        ]
    (try
      (do
        (prn command)
        (let [response (exec command)]
          (prn response)
          (print (:benefits2.1.10 (js->clj (.parse js/JSON response) :keywordize-keys true)))
          (merge {:pi10 (into [] (concat [0 0 0 0 0] (into [] (map :pi10 (map (fn [row] (select-keys row [:pi10])) (:benefits2.1.10 (js->clj (.parse js/JSON response) :keywordize-keys true)))))))}
                 (apply merge-with
                        #(if (vector? %1) (conj %1 %2) [%2])
                        {:r [] :h [] :c [] :t [] :b [] :rh [] :rhc [] :hc [] :hct [] :hctb [] :rhct [] :rhctb []}
                        (:benefits2.1 (js->clj (.parse js/JSON response) :keywordize-keys true))
                        )))
        )
      (catch :default e (errmsg command)))
    ))