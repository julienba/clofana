(ns clofana.views.graph
  "Chart create using example from https://github.com/reagent-project/reagent-cookbook/tree/master/recipes/highcharts
   and https://github.com/yogthos/prag-prog-re-frame-article"
  (:require ["chart.js"       :as chartjs]
            [reagent.core     :as r]
            [reagent.dom      :as rdom]
            [re-frame.core    :as rf]))

(def background-colors
  ["#FF0000" "#00FFFF"  "#C0C0C0" "#0000FF"  "#808080" "#0000A0"  "#000000"
   "#ADD8E6" "#FFA500" "#800080"  "#A52A2A" "#FFFF00"  "#800000" "#00FF00" "#008000"
   "#FF00FF" "#808000"])

(defn- get-color [idx]
  (nth background-colors (mod idx (count background-colors))))


(defn- render-data [node {:keys [labels datasets] :as _charts-data}]
  (let [context    (.getContext node "2d")
        chart-data {:type "line"
                    :options {:scales {:xAxes [{:type "time"}]}}
                              ;:legend {:position "bottom"}}

                    :data {:labels (map #(* % 1000) labels)
                           :datasets (into [] (map-indexed (fn [idx {:keys [label data]}]
                                                             {:label (str label)
                                                              :data  data
                                                              :fill false
                                                              :pointRadius 0.5
                                                              :lineTension 0
                                                              :borderWidth 2
                                                              :backgroundColor (get-color idx)
                                                              :borderColor (get-color idx)})
                                                          datasets))}}]
    (chartjs. context (clj->js chart-data))))

(defn- destroy-chart [chart]
  (when @chart
    (.destroy @chart)
    (reset! chart nil)))

(defn- render-chart [chart subscribe-vec]
  (fn [component]
    (when-let [data @(rf/subscribe subscribe-vec)]
      (destroy-chart chart)
      (reset! chart (render-data (rdom/dom-node component) data)))))

(defn- render-canvas [subscribe-vec]
  (when @(rf/subscribe subscribe-vec)
    [:canvas]))
     ;{:width "75%"}]))
     ;  :height "290"}]))

(defn component [subscribe-vec]
  (let [chart (atom nil)]
    (r/create-class
      {:component-did-mount    (render-chart chart subscribe-vec)
       :component-did-update   (render-chart chart subscribe-vec)
       :component-will-unmount (fn [_] (destroy-chart chart))
       :render                 (fn []
                                 ;[:div {:style {:width "75%"}}
                                  (render-canvas subscribe-vec))})))
