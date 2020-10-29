(ns aoc.server
  (:require [compojure.route          :as route]
            [clojure.tools.logging    :as log]
            [aoc.views                :as v]
            [aoc.conf                 :as c]
            [aoc.handler              :as h]
            [compojure.core           :refer :all]
            [compojure.handler        :as handler]
            [org.httpkit.server       :refer [run-server]]
            [ring.middleware.json     :as middleware]))

(defonce server (atom nil))
(defroutes app-routes

  (GET "/"            [:as req] (v/index        (c/config) req :main))
  (GET "/ws"          [:as req] (h/ws           (c/config) req))
  (POST "/year"       [:as req] (h/year         (c/config) req))
  (POST "/standard"   [:as req] (h/standard     (c/config) req))
  (POST "/mode"       [:as req] (h/mode         (c/config) req))
  (POST "/gas"        [:as req] (h/gas          (c/config) req))
  (POST "/maintainer" [:as req] (h/maintainer   (c/config) req))
  (POST "/reset"      [:as req] (h/reset        (c/config) req)) 
  (POST "/id"         [:as req] (h/id           (c/config) req))
  (route/resources "/")
  (route/not-found (v/not-found)))

(def app
  (-> (handler/site app-routes)
      (middleware/wrap-json-body {:keywords? true})
      middleware/wrap-json-response))

(defn stop
  []
  (when-not (nil? @server)
    (@server :timeout 100)
    (reset! server nil)))

(defn start
  []
  (reset! server (run-server app {:port 8009})))
