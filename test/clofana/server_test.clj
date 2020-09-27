(ns clofana.server-test
  (:require [cheshire.core   :as json]
            [clojure.test :refer :all]
            [clofana.server :as sup]))

(deftest internal-test
  (testing "Status is OK"
    (is (= {:status 200, :body "OK"}
           (sup/app {:request-method :get, :uri "/internal/status"})))))


(deftest datasource-test
  (testing "Query is answering an expected structure"
    (let [results (-> (sup/app {:request-method :post
                                :uri "/datasource/query"
                                :body-params {:start 1596886710698,
                                              :end 1596890310700,
                                              :query "rate(node_cpu_seconds_total{mode=\"user\"}[5m])"}})
                      :body
                      slurp
                      (json/parse-string true))
          labels (get-in results [:ui/graph :labels])]
      (is (= {:errors {}, :ui/stats [], :ui/stats-h-t-h [], :ui/stats-d-t-d [], :ui/stats-w-t-w [], :ui/graph {:labels nil, :datasets []}}
             (assoc-in results [:ui/graph :labels] nil)))
      (is (= 1596886680 (first labels)))
      (is (= 1596890220 (last labels))))))
