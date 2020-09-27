(ns clofana.views.home.core)

(defn render []
  [:div
   [:div.jumbotron
    [:div.container
     [:h1 "Prometheus explorer"]
     [:p "This is a web application to explore Prometheus metrics"]
     [:p "Configure the Prometheus target with environment variables: PROM_URL, PROM_USER, PROM_PASSWORD"]
     [:p [:a {:class "btn btn-primary btn-lg" :href "https://prometheus.io/" :role "button"} "Learn more »"]]]]

   [:div.container
    [:div.row
     [:div.col-md-4
      [:h2 "Catalog"]
      [:p "List all metrics availaible from the instance you are connected on a data table"]]

     [:div.col-md-4
      [:h2 "Explore with a graph"]
      [:p "Make a graph of the metrics you wrote and for the time selected"]]

     [:div.col-md-4
      [:h2 "Explore with numbers"]
      [:p "Compute the hours to hours, day to day and week to week data for the query you wrote."]
      [:p "The Display is using the same color chart as a thermometre"]]]
    [:hr]]])
