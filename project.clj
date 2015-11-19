(defproject dynamodb-clojure-amazonica "0.1.0-SNAPSHOT"
  :description "sample code for working with dynamodb in clojure"
  :url "lesterhan.com/blog"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.7.0"]
                 [cheshire "5.5.0"]
                 [amazonica "0.3.36"]
                 [org.clojure/core.async "0.2.374"]]
  :plugins [[lein-midje "3.1.3"]]
  :profiles {:dev {:dependencies [[midje "1.7.0"]]}}
  )
