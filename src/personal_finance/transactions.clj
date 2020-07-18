(ns personal-finance.transactions)

(defn valida?
  [transaction]
  (and (contains? transaction :value)
       (number? (:value transaction))
       (pos? (:value transaction))
       (contains? transaction :type)
       (or (= "expense" (:type transaction))
           (= "revenue" (:type transaction)))))


