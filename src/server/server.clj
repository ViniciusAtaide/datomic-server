(ns server.server
  (:require [aleph.http :refer [start-server]]
            [clojure.data.json :as json]
            [clojure.pprint :as pp]
            [com.stuartsierra.component :as component]
            [reitit.http :as http]
            reitit.interceptor.sieppari
            [reitit.ring :as ring]
            [server.db :refer [get-fields]]))

(defn handler [request]
  {:status 200
   :body (json/write-str
          (get-fields
           (:database (:services request))
           :user/name
           "Vinny"
           '[*]))})

(defn router [services]
  (ring/router
   ["/api" {:get {:interceptors [{:enter (fn [ctx] (update ctx :request assoc :services services))}]
                  :handler handler}}]))

(defn app [services]
  (http/ring-handler
   (router services)
   (ring/create-default-handler)
   {:executor reitit.interceptor.sieppari/executor}))

(defrecord Server [port database connection]

  component/Lifecycle

  (start [component]
    (let [server (start-server (app {:database (:conn database)}) {:port port :join? false})]
      (assoc component :connection server)))

  (stop [component]
    (when connection
      (.close connection)
      component)))

(defn new-server [port]
  (map->Server {:port port :join? false :async? true}))
