(ns personal-finance.db)

(def registries
  (atom []))

(defn transactions []
  @registries)

(defn register [transaction]
  (let [updated-collection (swap! registries conj transaction)]
    (merge transaction {:id (count updated-collection)})))

(defn clean []
  (reset! registries []))

(defn- expense? [transaction]
  (= (:type transaction) "expense"))

(defn- calculate [acc transaction]
  (let [value (:value transaction)]
    (if (expense? transaction)
      (- acc value)
      (+ acc value))))

(defn balance []
  (reduce calculate 0 @registries))

(defn transactions-of-type [type]
  (filter #(= type (:type %)) (transactions)))


