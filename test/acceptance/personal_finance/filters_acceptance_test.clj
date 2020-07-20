(ns personal-finance.filters-acceptance-test
  (:require [midje.sweet :refer :all]
            [cheshire.core :as json]
            [personal-finance.auxiliar :refer :all]
            [clj-http.client :as http]
            [personal-finance.db :as db]))

(def random-transactions
  '({:value 7.0M :type "expense" :tags ["ice cream" "entertainment"]}
    {:value 88.0M :type "expense" :tags ["book" "education"]}
    {:value 106.0M :type "expense" :tags ["course" "education"]}
    {:value 8000.0M :type "revenue" :tags ["wage"]}))

(against-background
  [(before :facts [(start-server port-default)
                   (db/clean)])
   (after :facts (stop-server))]

  (fact "There is not any revenue" :acceptance
        (json/parse-string (content "/revenues") true)
        => {:transactions '()})

  (fact "There is not any expense" :acceptance
        (json/parse-string (content "/expenses") true)
        => {:transactions '()})

  (fact "There is not any transaction" :acceptance
        (json/parse-string (content "/transactions") true)
        => {:transactions '()})

  (against-background
    [(before :facts [(doseq [transaction random-transactions]
                       (db/register transaction))])
     (after :facts (db/clean))]

    (fact "There are 3 expenses" :acceptance
          (count (:transactions (json/parse-string (content "/expenses") true)))
          => 3)

    (fact "There is 1 revenue" :acceptance
          (count (:transactions (json/parse-string (content "/revenues") true)))
          => 1)

    (fact "There are 4 transactions" :acceptance
          (count (:transactions (json/parse-string (content "/transactions") true)))
          => 4)

    (fact "There is 1 revenue with tag 'wage'"
          (count (:transactions (json/parse-string (content "/transactions?tags=wage") true))) => 1)

    (fact "There are 2 expenses with with tag 'book' or 'course'"
          (count (:transactions (json/parse-string (content "/transactions?tags=book&tags=course") true))) => 2)

    (fact "There are 2 expenses with with tag 'education'"
          (count (:transactions (json/parse-string (content "/transactions?tags=education") true))) => 2)))

