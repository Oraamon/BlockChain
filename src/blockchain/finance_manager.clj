(ns blockchain.finance-manager
  (:require [clojure.string :as str]
            [cheshire.core :as json]
            [clojure.tools.logging :as log]
            [blockchain.blockchain :refer :all]))

(def transactions (atom []))

(defn add-transaction [transaction]
  (swap! transactions conj transaction)
)

(defn get-transactions []
  @transactions)

(defn parse-transaction [json-transaction]
  (json/parse-string json-transaction true))
