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
       (against-background (json/generate-string {:balance 0}) => "{\"balance\":0}")

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


