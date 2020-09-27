(ns clofana.views.explore.form
  (:require [clojure.string :as string]
            [clofana.views.explore.events :as events]
            [clofana.views.explore.subs :as subs]
            [fork.core :as fork]
            [re-frame.core :as rf]))

(defn metric-form* [{:keys [values
                            form-id
                            handle-change
                            handle-blur
                            _touched
                            errors
                            submitting?
                            _state
                            handle-submit] :as _form-cfg}]
  (let [start-options         [1 3 6 12 24]
        query-failure?     @(rf/subscribe [::subs/query-failure?])]
    [:form
     {:id form-id
      :class "row"
      :on-submit handle-submit}
     [:div.col
      [:div {:class "row"}
       [:div {:class "col-12 form-group"}
        [:label {:for "query_0"} "Query"]
        [:input
         {:id "query_0"
          :name "query_0"
          :class (str "form-control" (when query-failure? " is-invalid"))
          :placeholder "metric query"
          :value (get values "query_0")
          :on-change handle-change
          :on-blur handle-blur
          :type "text"}]
        [:div {:id "query_0" :class "invalid-feedback"}
          "Invalid query"]]]

      [:div {:class "row"}
       [:div {:class "col-4 form-group"}
        [:label {:for "start"} "Last hours"]
        [:select {:class "form-control"
                  :name "start"
                  :defaultValue (get values "start")
                  :on-change handle-change}
         (for [x start-options]
           [:option {:key x} x])]]

       [:div {:class "col-8 form-group"}
        [:button
         {:type "submit"
          :class "btn btn-lg btn-secondary float-right"
          :disabled (or submitting? (seq errors))
          :style {:align "right"}}
         "Run"]]]]]))

(defn clean-metric [metric type]
  (if (= type "counter")
    (str "rate(" metric "[5m])")
    metric))

(defn metric-form [metric type]
  (let [metric' (clean-metric metric type)]
    [fork/form {:path :form-metric
                :form-id "metric"
                :initial-values {"query_0" metric'
                                 "start" 1}
                :prevent-default? true
                :clean-on-unmount? true
                :on-submit (fn [e]
                             ; regroup query_x into a query vector
                             (let [query-keys (->> (keys (:values e))
                                                   (filter #(string/starts-with? % "query_")))
                                   queries (mapv #(get (:values e) %) query-keys)
                                   values {:start (get (:values e) "start")
                                           :queries queries}]

                               (rf/dispatch [::events/query values])))}
      metric-form*]))
