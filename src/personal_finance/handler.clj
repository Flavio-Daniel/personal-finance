(ns personal-finance.handler
  (:require [personal-finance.db :as db]
            [compojure.core :refer :all]
            [compojure.route :as route]
            [ring.middleware.defaults :refer [wrap-defaults api-defaults]]
            [ring.middleware.json :refer [wrap-json-body]]
            [cheshire.core :as json]
            [personal-finance.transactions :as transactions]))

(defn as-json [content & [status]]
      {:status (or status 200)
       :headers {"Content-Type" "application/json; charset=utf-8"}
       :body    (json/generate-string content)})

(defroutes app-routes
           (GET "/" [] "Hello World")
           (GET "/balance" [] (as-json {:balance (db/balance)}))
           (GET "/expenses" [] (as-json {:transactions (db/transactions-of-type "expense")}))
           (GET "/revenues" [] (as-json {:transactions (db/transactions-of-type "revenue")}))
           (GET "/transactions" {filters :params}
             (as-json {:transactions
                       (if (empty? filters)
                         (db/transactions)
                         (db/transactions-with-filter filters))}))
           (POST "/transactions" request
             (if (transactions/valida? (:body request))
               (-> (db/register (:body request))
                   (as-json 201))
               (as-json {:message "Invalid Request"} 422)))
           (route/not-found "Not Found"))

(def app
  (-> (wrap-defaults app-routes api-defaults)
      (wrap-json-body {:keywords? true :bigdecimals? true})))
