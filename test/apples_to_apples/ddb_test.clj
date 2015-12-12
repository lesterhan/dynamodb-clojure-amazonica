(ns apples-to-apples.ddb-test
  (:require [apples-to-apples.ddb :refer :all]
            [amazonica.aws.dynamodbv2 :as dyn]
            [midje.sweet :refer :all]))

(facts "table creation"
       (delete-table :test-table)

       (fact "confirm created table exists"
             (ensure-table-exist :test-table)
             (table-exist? :test-table) => true))
