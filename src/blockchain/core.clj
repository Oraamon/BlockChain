(ns blockchain.core
  (:require [compojure.core :refer :all]
            [compojure.route :as route]
            [ring.middleware.defaults :refer [wrap-defaults site-defaults]]
            [ring.middleware.resource :refer [wrap-resource]]
            [ring.middleware.file-info :refer [wrap-file-info]]
            [ring.util.response :refer [resource-response]]
            [blockchain.blockchain :refer :all]
            [blockchain.finance-manager :refer :all]
            [cheshire.core :as json]
            [clojure.tools.logging :as log]))

(defroutes app-routes
  (POST "/transacao" {body :body}
    (let [transacao (parse-transaction (slurp body))]
      (add-transaction transacao)
      {:status 201 :body (json/generate-string {:status "Transação adicionada"})}))
  (POST "/registrar-transacoes" []
    (let [transacoes @transactions]
      (if (empty? transacoes)
        {:status 400 :body (json/generate-string {:status "No transactions to register"})}
        (do
          (mine-block transacoes)
          {:status 201 :body (json/generate-string {:status "Transactions registered in blockchain"})}))))
  (GET "/transacoes" []
    {:status 200 :body (json/generate-string (get-transactions))})
  (GET "/saldo" []
    (let [balance (calculate-balance)]
      {:status 200 :body (json/generate-string {:saldo balance})}))
  (GET "/cadeia" []
    (let [cadeia (rest @blockchain)]
      {:status 200 :body (json/generate-string cadeia)}))
  (GET "/cotacao" request
    (let [params (:query-params request)
          moeda-de (get params "de")
          moeda-para (get params "para")]
      (log/info "Parâmetros recebidos" {:params params :de moeda-de :para moeda-para})
      (if (and moeda-de moeda-para)
        (let [cotacao (obter-cotacao moeda-de moeda-para)
              valor (get cotacao (str moeda-de "_" moeda-para))]
          (log/info "Cotação obtida" {:valor valor})
          {:status 200 :body (json/generate-string {:de moeda-de :para moeda-para :cotacao valor})})
        {:status 400 :body (json/generate-string {:error "Parâmetros 'de' e 'para' são necessários"})})))
  (GET "/" []
    (resource-response "index.html" {:root "public"}))
  (route/not-found "Not Found"))

(def custom-defaults
  (assoc-in site-defaults [:security :anti-forgery] false))

(def app
  (-> app-routes
      (wrap-defaults custom-defaults)
      (wrap-resource "public")
      (wrap-file-info)))
