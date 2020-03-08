(ns server.core
  (:require [server.server :refer [new-server]]
            [server.db :refer [new-db]]
            [com.stuartsierra.component :refer [system-map using]]))

(defn new-system [{:keys [port db-uri]}]
  (system-map
   :database (new-db db-uri)
   :server (using (new-server port)
                  {:database :database})))



