(ns personal-finance.balance-acceptance-test
  (:require
    [midje.sweet :refer :all]
    [personal-finance.handler :refer [app]]
    [personal-finance.auxiliar :refer :all]))

(against-background
  [(before :facts (start-server port-default))
   (after :facts (stop-server))]
  (fact "The starting balance is 0" :acceptance
        (content "/balance") => "0"))