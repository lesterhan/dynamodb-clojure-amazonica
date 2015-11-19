(ns apples-to-apples.ddb-test
  (:require [apples-to-apples.ddb :refer :all]
            [midje.sweet :refer :all]))

(facts "table creation"
       (delete-table :test-table)

       (fact "confirm created table exists"
             (ensure-table :test-table)
             (table-exist? :test-table) => true))
