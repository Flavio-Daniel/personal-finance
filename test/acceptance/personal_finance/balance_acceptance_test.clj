(ns personal-finance.balance-acceptance-test
  (:require
    [midje.sweet :refer :all]
    [personal-finance.handler :refer [app]]
    [personal-finance.auxiliar :refer :all]
    [cheshire.core :as json]
    [clj-http.client :as http]
    [personal-finance.db :as db]))

(against-background
  [(before :facts [(start-server port-default) (db/clean)])
   (after :facts (stop-server))]

  (fact "The starting balance is 0" :acceptance
        (json/parse-string (content "/balance") true) => {:balance 0})

  (fact "The balance is 10 after a revenue transaction of 10" :acceptance

        (http/post (route-to "/transactions") (revenue 10))

        (json/parse-string (content "/balance") true) => {:balance 10})

  (fact "The balance is 1000 when we create 2 revenues of 2000 and 1 expense of 3000" :acceptance

        (http/post (route-to "/transactions") (revenue 2000))
        (http/post (route-to "/transactions") (revenue 2000))
        (http/post (route-to "/transactions") (expense 3000))

        (json/parse-string (content "/balance") true) => {:balance 1000})

  (fact "Reject a transaction without a value" :acceptance
        (let [response (http/post (route-to "/transactions") (content-as-json {:type "revenue"}))]
          (:status response) => 422))

  (fact "reject a transaction with negative value" :acceptance
        (let [response (http/post (route-to "/transactions") (revenue -100))]
          (:status response) => 422))

  (fact "reject a transaction with a value that is not a number" :acceptance
        (let [response (http/post (route-to "/transactions") (revenue "mil"))]
          (:status response) => 422))

  (fact "Reject a transaction without a type" :acceptance
        (let [response (http/post (route-to "/transactions") (content-as-json {:value 100}))]
          (:status response) => 422))

  (fact "Reject a transaction with an unknown type" :acceptance
        (let [response (http/post (route-to "/transactions") (content-as-json {:value 100
                                                                               :type "investment"}))]
          (:status response) => 422))

  )