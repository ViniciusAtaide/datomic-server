(ns server.db
  (:require [datomic.api :as d]
            [com.stuartsierra.component :as component]
            [clojure.pprint :as pp]))

(defn create-database [uri]
  (d/create-database uri)
  (let [conn (d/connect uri)
        schema (load-file "resources/schema.edn")
        seed (load-file "resources/seed.edn")]
    @(d/transact conn schema)
    @(d/transact conn seed)))

(defn get-fields [conn attr value fields]
  (d/q '[:find (pull ?e fields)
         :in $ ?attr ?value fields
         :where
         [?e ?attr ?field]]
       (d/db conn) attr value fields))

(defrecord Database [uri conn]

  component/Lifecycle

  (start [component]
    (let [_ (create-database uri)
          conn (d/connect uri)]
      (assoc component :conn conn)))
  (stop [component]
    (when conn (d/release conn))
    (assoc component :conn nil)))

(defn new-db [uri]
  (map->Database {:uri uri}))



