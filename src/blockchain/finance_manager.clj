(ns blockchain.finance-manager
  (:require [clojure.string :as str]
            [cheshire.core :as json]
            [clojure.tools.logging :as log]
            [blockchain.blockchain :refer :all]))

(def transactions (atom []))

(defn calculate-balance []
  (reduce (fn [balance {:keys [description amount]}]
            (let [amount-num (read-string amount)]
              (if (= description "Receita")
                (+ balance amount-num)
                (- balance amount-num))))
          0
          @transactions))

(defn add-transaction [transaction]
  (swap! transactions conj transaction)
)

(defn get-transactions []
  @transactions)

(defn parse-transaction [json-transaction]
  (json/parse-string json-transaction true))
