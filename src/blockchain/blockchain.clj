(ns blockchain.blockchain
  (:require [clojure.string :as str]
            [cheshire.core :as json]
            [clj-http.client :as http-client]
            [clojure.tools.logging :as log]))

;; Função para calcular o hash SHA-256 de uma string
(defn sha256 [s]
  (let [digest (java.security.MessageDigest/getInstance "SHA-256")]
    (.update digest (.getBytes s))
    (str/join (map #(format "%02x" %) (.digest digest)))))

;; Função para verificar se um hash é válido
(defn valid-hash? [hash]
  (str/starts-with? hash "0000"))

;; Função para realizar o Proof of Work
(defn proof-of-work [index timestamp transactions previous-hash]
  (loop [nonce 0]
    (let [hash (sha256 (str index timestamp transactions previous-hash nonce))]
      (if (valid-hash? hash)
        {:nonce nonce :hash hash}
        (recur (inc nonce))))))

;; Definição do bloco Genesis
(def genesis-block
  (let [genesis-template
        {:index 0
         :timestamp (System/currentTimeMillis)
         :transactions [{:description "Bloco Genesis"}]
         :previous-hash "0"}
        genesis-pow-result (proof-of-work (:index genesis-template)
                                           (:timestamp genesis-template)
                                           (:transactions genesis-template)
                                           (:previous-hash genesis-template))]
    (assoc genesis-template
           :nonce (:nonce genesis-pow-result)
           :hash (:hash genesis-pow-result))))

;; Atom para armazenar a blockchain
(defonce blockchain (atom [genesis-block]))


;; Função para criar um novo bloco
(defn create-block [transactions previous-hash]
  (let [index (count @blockchain)
        timestamp (System/currentTimeMillis)
        pow-result (proof-of-work index timestamp transactions previous-hash)]
    {:index index
     :timestamp timestamp
     :transactions transactions
     :previous-hash previous-hash
     :nonce (:nonce pow-result)
     :hash (:hash pow-result)}))

;; Função para adicionar um bloco à blockchain
(defn add-block [block]
  (swap! blockchain conj block))

;; Função para obter o bloco mais recente da blockchain
(defn latest-block []
  (last @blockchain))

;; Função para minerar um novo bloco
(defn mine-block [transactions]
  (let [previous-block (latest-block)
        new-block (create-block transactions (:hash previous-block))]
    (add-block new-block)
    new-block))

(def chave "c4f153ae54785f058345")
(def api-url "https://free.currencyconverterapi.com/api/v6/convert")

(defn obter-cotacao [moeda-de moeda-para]
  (log/info "Parâmetros para obter cotação" {:moeda-de moeda-de :moeda-para moeda-para})
  (let [query (str moeda-de "_" moeda-para)]
    (log/info "Requisição para API de cotação" {:query query :api-url api-url})
    (try
      (let [response (http-client/get api-url {:query-params {"q" query "compact" "ultra" "apiKey" chave}})
            body (json/parse-string (:body response) true)]
        (log/info "Resposta da API" {:body body})
        (let [cotacao (get body query)]
          (log/info "Valor da cotação" {:valor cotacao})
          cotacao))
      (catch Exception e
        (log/error e "Erro ao obter cotação")
        nil))))
