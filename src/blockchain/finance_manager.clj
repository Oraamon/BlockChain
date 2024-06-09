(ns blockchain.finance-manager
  (:require [clojure.string :as str]
            [cheshire.core :as json]
            [clojure.tools.logging :as log]
            [blockchain.blockchain :refer :all]))

(def transactions (atom [])) ; Define um átomo chamado transactions para armazenar as transações

;; Função para calcular o saldo
(defn calculate-balance []
  (reduce (fn [balance {:keys [description amount]}]
            (let [amount-num (read-string amount)]
              (if (= description "Receita")
                (+ balance amount-num)
                (- balance amount-num))))
          0
          @transactions))

;; Função para adicionar uma transação
(defn add-transaction [transaction]
  (swap! transactions conj transaction)

;; Função para obter todas as transações
(defn get-transactions []
  @transactions)

;; Função para analisar uma transação em formato JSON
(defn parse-transaction [json-transaction]
  (json/parse-string json-transaction true))
