(ns personal-finance.handler-test
  (:require [midje.sweet :refer :all]
            [ring.mock.request :as mock]
            [personal-finance.handler :refer :all]))

(facts "Output 'Hello World' at root route" :unit
       (let [response (app (mock/request :get "/"))]
         (fact "the response status is 200"
               (:status response) => 200)
         (fact "the text body is 'Hello World'"
               (:body response) => "Hello World")))

(facts "Output saldo at saldo route" :unit
       (let [response (app (mock/request :get "/balance"))]
         (fact "the response status is 200"
               (:status response) => 200)
         (fact "the body response is 0"
               (:body response) => "0")))

(facts "Invalid rout does not exists" :unit
       (let [response (app (mock/request :get "/invalid"))]
         (fact "the response error status is 404"
               (:status response) => 404)
         (fact "the text body is 'Not Found'"
               (:body response) => "Not Found")))



