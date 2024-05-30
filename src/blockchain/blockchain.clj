(ns blockchain.blockchain
  (:require [clojure.string :as str]
            [cheshire.core :as json]
            [clj-http.client :as http-client]
            [clojure.tools.logging :as log]))

(def genesis-block {:index 0
                    :timestamp (System/currentTimeMillis)
                    :transactions []
                    :previous-hash "0"
                    :nonce 0
                    :hash "0000000000000000000000000000000000000000000000000000000000000000"})

(defonce blockchain (atom [genesis-block]))

(defn sha256 [s]
  (let [digest (java.security.MessageDigest/getInstance "SHA-256")]
    (.update digest (.getBytes s))
    (str/join (map #(format "%02x" %) (.digest digest)))))

(defn hash-block [block]
  (sha256 (str (:index block) (:timestamp block) (:transactions block) (:previous-hash block) (:nonce block))))

(defn valid-hash? [hash]
  (str/starts-with? hash "0000"))

(defn proof-of-work [index timestamp transactions previous-hash]
  (loop [nonce 0]
    (let [hash (sha256 (str index timestamp transactions previous-hash nonce))]
      (if (valid-hash? hash)
        {:nonce nonce :hash hash}
        (recur (inc nonce))))))

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

(defn add-block [block]
  (swap! blockchain conj block))

(defn latest-block []
  (last @blockchain))

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
