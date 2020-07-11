(ns personal-finance.balance-acceptance-test
  (:require
    [midje.sweet :refer :all]
    [personal-finance.handler :refer [app]]
    [personal-finance.auxiliar :refer :all]
    [cheshire.core :as json]
    [clj-http.client :as http]))

(against-background
  [(before :facts (start-server port-default))
   (after :facts (stop-server))]

  (fact "The starting balance is 0" :acceptance
        ;;; should return a json format
        (json/parse-string (content "/balance") true) => {:balance 0})

  (fact "The balance is 10 after a revenue transaction of 10" :acceptance

        (http/post (route-to "/transactions")
                   {:body (json/generate-string {:valor 10 :tipo "receita"})})

        (json/parse-string (content "/balance") true) => {:balance 10}))