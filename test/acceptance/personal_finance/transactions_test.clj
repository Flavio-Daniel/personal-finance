(ns personal-finance.transactions-test
  (:require [midje.sweet :refer :all]
            [personal-finance.transactions :refer :all]))

(fact "A transaction without value is not valid"
      (valida? {:type "revenue"}) => false)

(fact "A transaction with negative value is not valid"
      (valida? {:value -10 :type "revenue"}) => false)

(fact "A transaction with non numeric value is not valid"
      (valida? {:value "mil" :type "revenue"}) => false)

(fact "A transaction without type is not valid"
      (valida? {:value 100}) => false)

(fact "A transaction with unknown type is not valid"
      (valida? {:value 1000 :type "investment"}) => false)

(fact "A transaction with known type and positive numeric value is valid"
      (valida? {:value 230 :type "revenue"}) => true)