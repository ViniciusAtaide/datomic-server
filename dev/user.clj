(ns user
  (:require [reloaded.repl :refer [system init start stop go reset reset-all]]
            [server.core :refer [new-system]]))

(reloaded.repl/set-init! #(new-system {:port 3000 :db-uri "datomic:mem://hello"}))

