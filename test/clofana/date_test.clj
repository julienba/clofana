(ns clofana.date-test
  (:require [clojure.test :refer :all]
            [clofana.date :as sup]
            [java-time :as t]))

(deftest interval-test
  (testing "Days and step 2"
    (let [result (sup/all-dates-in-interval
                   (t/local-date-time 2015 10)
                   (t/plus (t/local-date-time 2015 10) (t/days 4))
                   :days
                   2)]
      (is (= 2 (count result)))
      (is (= [2015 10 1] (t/as (first result) :year :month-of-year :day-of-month)))
      (is (= [2015 10 3] (t/as (last result) :year :month-of-year :day-of-month)))))

  (testing "All Minutes"
    (let [result (sup/all-dates-in-interval
                   (t/local-date-time 2020 07 24 19 36)
                   (t/local-date-time 2020 07 24 19 46)
                   :minutes
                   1)]
      (is (= 10 (count result)))
      (is (= [2020 7 24 36] (t/as (first result) :year :month-of-year :day-of-month :minute-of-hour)))
      (is (= [2020 7 24 45] (t/as (last result) :year :month-of-year :day-of-month :minute-of-hour))))))
