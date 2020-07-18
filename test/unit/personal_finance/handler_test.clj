(ns personal-finance.handler-test
  (:require [personal-finance.handler :refer :all]
            [personal-finance.db :as db]
            [midje.sweet :refer :all]
            [ring.mock.request :as mock]
            [cheshire.core :as json]))

(facts "Output 'Hello World' at root route" :unit
       (let [response (app (mock/request :get "/"))]
         (fact "the response status is 200"
               (:status response) => 200)
         (fact "the text body is 'Hello World'"
               (:body response) => "Hello World")))

(facts "Starting balance is zero" :unit
       (against-background [(json/generate-string {:balance 0})
                            => "{\"balance\":0}"
                            ;;mock para chamada db/balance
                            (db/balance) => 0])

       (let [response (app (mock/request :get "/balance"))]
         (fact "the response status is 200"
               (:status response) => 200)
         (fact "the format of response is 'application/json'"
               (get-in response [:headers "Content-Type"]) => "application/json; charset=utf-8")
         (fact "the body response is 0"
               (:body response) => "{\"balance\":0}")))

(facts "Invalid rout does not exists" :unit
       (let [response (app (mock/request :get "/invalid"))]
         (fact "the response error status is 404"
               (:status response) => 404)
         (fact "the text body is 'Not Found'"
               (:body response) => "Not Found")))

(facts "Register a revenue transaction of value 10"
       ;; creates a mock for the function db/register
       (against-background (db/register {:value 10
                                         :type  "revenue"})
                           => {:id 1 :value 10 :type "revenue"})
       (let [response (app (-> (mock/request :post "/transactions")
                               ;; creates POST content
                               (mock/json-body {:value 10 :type "revenue"})))]
         (fact "the status response is 201"
               (:status response) => 201)
         (fact "the body text is a json with the content and an id"
               (:body response) => "{\"id\":1,\"value\":10,\"type\":\"revenue\"}")))

(facts "There is a route to deal with the filter os transaction by type"
       (against-background
         [(db/transactions-of-type "revenue") => '({:id 1 :value 2000 :type "revenue"})
          (db/transactions-of-type "expense") => '({:id 2 :value 89 :type "expense"})
          (db/transactions) => '({:id 1 :value 2000 :type "revenue"}
                                 {:id 2 :value 89 :type "expense"})]
         (fact "Filter by revenue"
               (let [response (app (mock/request :get "/revenues"))]
                 (:status response) => 200
                 (:body response) => (json/generate-string {:transactions '({:id 1 :value 2000 :type "revenue"})})))

         (fact "Filter by expense"
               (let [response (app (mock/request :get "/expenses"))]
                 (:status response) => 200
                 (:body response) => (json/generate-string {:transactions '({:id 2 :value 89 :type "expense"})})))

         (fact "Without transaction filter"
               (let [response (app (mock/request :get "/transactions"))]
                 (:status response) => 200
                 (:body response) => (json/generate-string {:transactions '({:id 1 :value 2000 :type "revenue"}
                                                                            {:id 2 :value 89 :type "expense"})})))))



