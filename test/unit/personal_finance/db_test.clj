(ns personal-finance.db-test
  (:require [personal-finance.db :refer :all]
            [midje.sweet :refer :all]
            [personal-finance.db :as db]))

(def random-transactions '({:value 2 :type "expense"}
                           {:value 10 :type "revenue"}
                           {:value 200 :type "expense"}
                           {:value 1000 :type "revenue"}))

(facts "Keep a transaction in an atom"
       (against-background [(before :facts (clean))]
                           (fact "the collection starts empty"
                                 (count (transactions)) => 0)
                           (fact "the transaction is the first register"
                                 (register {:value 7 :type "revenue"}) => {:id 1 :value 7 :type "revenue"}
                                 (count (transactions)) => 1)))

(facts "Calculates a balance given a transaction collection"
       (against-background [(before :facts (clean))]
                           (fact "balance is positive when there are only revenues"
                                 (register {:value 1 :type "revenue"})
                                 (register {:value 10 :type "revenue"})
                                 (register {:value 100 :type "revenue"})
                                 (register {:value 1000 :type "revenue"})
                                 (balance) => 1111)
                           (fact "balance is negative when there are only expenses"
                                 (register {:value 2 :type "expense"})
                                 (register {:value 20 :type "expense"})
                                 (register {:value 200 :type "expense"})
                                 (register {:value 2000 :type "expense"})
                                 (balance) => -2222)
                           (fact "balance is the sum of revenues minus expenses"
                                 (register {:value 2 :type "expense"})
                                 (register {:value 10 :type "revenue"})
                                 (register {:value 200 :type "expense"})
                                 (register {:value 1000 :type "revenue"})
                                 (balance) => 808)))

(facts "filter transactions by type"

       (against-background
         [(before :facts
                  [(db/clean)
                   (doseq [transaction random-transactions]
                     (db/register transaction))])]

         (fact "find only the revenues"
               (transactions-of-type "revenue") => '({:value 10 :type "revenue"}
                                                     {:value 1000 :type "revenue"}))

         (fact "find only the expenses"
               (transactions-of-type "expense") => '({:value 2 :type "expense"}
                                                     {:value 200 :type "expense"}))))