(ns clofana.views.explore.table
  (:require [re-frame.core :as rf]
            [goog.string :as gstring]
            [clofana.views.explore.subs :as subs]))

(defn- pad-str
  ([number]      (pad-str number 1))
  ([number size] (gstring/format (str "%." size "f") number)))

(defn- to-percent [original new-value]
  (cond
    (and (zero? original) (zero? new-value))
    0

    (nil? new-value)
    0

    (zero? original)
    100

    :else (- (/ (* new-value 100)
                original)
             100)))

(def colors-palette
  "From red to blue. Green is the middle"
  ["FF7800"
   "FF8200"
   "FF8c00"
   "FF9600"
   "FFa000"
   "FFaa00"
   "FFb400"
   "FFbe00"
   "FFc800"
   "FFd200"
   "FFdc00"
   "FFe600"
   "FFf000"
   "FFfa00"
   "fdff00"
   "d7ff00"
   "b0ff00"
   "8aff00"
   "65ff00" ; center
   "3eff00"
   "17ff00"
   "00ff10"
   "00ff36"
   "00ff5c"
   "00ff83"
   "00ffa8"
   "00ffd0"
   "00fff4"
   "00e4ff"
   "00d4ff"
   "00c4ff"
   "00b4ff"
   "00a4ff"
   "0094ff"
   "0084ff"
   "0074ff"
   "0064ff"])

(def nb-color
  (count colors-palette))

(defn- get-positive-color [x]
  (* x
    (/ (/ nb-color 2)
       100)))

(defn get-color-idx [x]
  (let [idx (if (pos? x)
              (dec (- (/ nb-color 2)  (get-positive-color x)))
              (dec (+ (/ nb-color 2)  (get-positive-color (- x)))))]
    (cond
      (> idx nb-color) (dec nb-color)
      (< idx 0) 0
      :else idx)))

(defn get-cell-style [x]
  (let [color-idx (int (get-color-idx x))]
    (if (> nb-color color-idx -1)
      {:background-color (str "#" (nth colors-palette color-idx))}
      (println "Error on color-idx: " (pr-str {:x x :color-idx color-idx})))))

(defn render []
  (let [{:keys [ui/stats ui/stats-h-t-h ui/stats-d-t-d ui/stats-w-t-w]} @(rf/subscribe [::subs/stats])
        stats-h-t-h-by-metric (into {} (map (fn [{:keys [metric stats]}] {metric stats}) stats-h-t-h))
        stats-d-t-d-by-metric (into {} (map (fn [{:keys [metric stats]}] {metric stats}) stats-d-t-d))
        stats-w-t-w-by-metric (into {} (map (fn [{:keys [metric stats]}] {metric stats}) stats-w-t-w))]
    (when stats
      [:<>
       [:h3 "Stats"]
       [:table {:class "table table-striped table-bordered"}
        [:thead
         [:tr
          [:th {:colSpan 1} "Timeseries"]
          [:th {:colSpan 3} "Now"]
          [:th {:colSpan 3} "Hour to hour"]
          [:th {:colSpan 3} "Day to day"]
          [:th {:colSpan 3} "Week to week"]]
         [:tr
          [:th ""] [:th "Min"] [:th "Max"] [:th "Avg"] [:th "Min"] [:th "Max"] [:th "Avg"] [:th "Min"] [:th "Max"] [:th "Avg"] [:th "Min"] [:th "Max"] [:th "Avg"]]]
        [:tbody
         (for [{:keys [metric stats]} stats
               :let [display-metric (dissoc metric :__name__)]]
           [:tr {:key (str display-metric)}
            [:td (str display-metric)]
            [:td (pad-str (:min stats) 3)]
            [:td (pad-str (:max stats) 3)]
            [:td (pad-str (:avg stats) 3)]
            ; H to H
            [:td {:title (str "absolute: " (get-in stats-h-t-h-by-metric [metric :min]))
                  :style (get-cell-style (to-percent (:min stats) (get-in stats-h-t-h-by-metric [metric :min])))}
             (pad-str (to-percent (:min stats) (get-in stats-h-t-h-by-metric [metric :min])))]
            [:td {:title (str "absolute: " (get-in stats-h-t-h-by-metric [metric :max]))
                  :style (get-cell-style (to-percent (:max stats) (get-in stats-h-t-h-by-metric [metric :max])))}
             (pad-str (to-percent (:max stats) (get-in stats-h-t-h-by-metric [metric :max])))]
            [:td {:title (str "absolute: " (get-in stats-h-t-h-by-metric [metric :avg]))
                  :style (get-cell-style (to-percent (:avg stats) (get-in stats-h-t-h-by-metric [metric :avg])))}
             (pad-str (to-percent (:avg stats) (get-in stats-h-t-h-by-metric [metric :avg])))]
            ; D to D
            [:td {:title (str "absolute: " (get-in stats-d-t-d-by-metric [metric :min]))
                  :style (get-cell-style (to-percent (:min stats) (get-in stats-d-t-d-by-metric [metric :min])))}
             (pad-str (to-percent (:min stats) (get-in stats-d-t-d-by-metric [metric :min])))]
            [:td {:title (str "absolute: " (get-in stats-d-t-d-by-metric [metric :max]))
                  :style (get-cell-style (to-percent (:max stats) (get-in stats-d-t-d-by-metric [metric :max])))}
             (pad-str (to-percent (:max stats) (get-in stats-d-t-d-by-metric [metric :max])))]
            [:td {:title (str "absolute: " (get-in stats-d-t-d-by-metric [metric :avg]))
                  :style (get-cell-style (to-percent (:avg stats) (get-in stats-d-t-d-by-metric [metric :avg])))}
             (pad-str (to-percent (:avg stats) (get-in stats-d-t-d-by-metric [metric :avg])))]
            ; W to W
            [:td {:title (str "absolute: " (get-in stats-w-t-w-by-metric [metric :min]))
                  :style (get-cell-style (to-percent (:min stats) (get-in stats-w-t-w-by-metric [metric :min])))}
             (pad-str (to-percent (:min stats) (get-in stats-w-t-w-by-metric [metric :min])))]
            [:td {:title (str "absolute: " (get-in stats-w-t-w-by-metric [metric :max]))
                  :style (get-cell-style (to-percent (:max stats) (get-in stats-w-t-w-by-metric [metric :max])))}
             (pad-str (to-percent (:max stats) (get-in stats-w-t-w-by-metric [metric :max])))]
            [:td {:title (str "absolute: " (get-in stats-w-t-w-by-metric [metric :avg]))
                  :style (get-cell-style (to-percent (:avg stats) (get-in stats-w-t-w-by-metric [metric :avg])))}
             (pad-str (to-percent (:avg stats) (get-in stats-w-t-w-by-metric [metric :avg])))]])]]])))
