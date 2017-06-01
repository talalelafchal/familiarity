library(plotly)
setwd("/Users/Talal/Tesi/familiarity/R/NLAggregation")
data10  <- read.csv("nLMedianAggregation10.csv")
data100  <- read.csv("nLMedianAggregation100.csv")
data1000  <- read.csv("nLMedianAggregation1000.csv")
data10000  <- read.csv("nLMedianAggregation10000.csv")
data100000  <- read.csv("nLMedianAggregation100000.csv")



p10 <- plot_ly(type = 'box', y = data10$androidNL,
               line = list(color = 'rgb(250,25,25)'),
               name = "Android10 ") %>%
  
  add_boxplot(y = data10$swingNL,
              name = "Swing10",
              marker = list(color = 'rgb(107,174,214)'),
              line = list(color = 'rgb(107,174,214)')) %>%
  
  add_boxplot(y = data10$javaNL,
              name = "Java10",
              marker = list(color = 'rgb(10,250,10)'),
              line = list(color = 'rgb(10,250,10)')) %>%
  
  add_boxplot(y = data10$javascriptNL,
              line = list(color = 'rgb(25,25,25)'),
              name = "JavaScript10 ") %>%
  
  layout(
    yaxis = list(range = c(0, -2200)))


p100 <- plot_ly(type = 'box', y = data100$androidNL,
                line = list(color = 'rgb(250,25,25)'),
                name = "Android100 ") %>%
  
  
  add_boxplot(y = data100$swingNL,
              name = "Swing100",
              marker = list(color = 'rgb(107,174,214)'),
              line = list(color = 'rgb(107,174,214)')) %>%
  
  add_boxplot(y = data100$javaNL,
              name = "Java100",
              marker = list(color = 'rgb(10,250,10)'),
              line = list(color = 'rgb(10,250,10)')) %>%
  
  add_boxplot(y = data100$javascriptNL,
              line = list(color = 'rgb(25,25,25)'),
              name = "JavaScript100 ") %>%
  
  layout(
    yaxis = list(range = c(0, -2200)))


p1000 <- plot_ly(type = 'box', y = data1000$androidNL,
                 line = list(color = 'rgb(250,25,25)'),
                 name = "Android1000 ") %>%
  
  
  add_boxplot(y = data1000$swingNL,
              name = "Swing1000",
              marker = list(color = 'rgb(107,174,214)'),
              line = list(color = 'rgb(107,174,214)')) %>%
  
  add_boxplot(y = data1000$javaNL,
              name = "Java1000",
              marker = list(color = 'rgb(10,250,10)'),
              line = list(color = 'rgb(10,250,10)')) %>%
  
  add_boxplot(y = data1000$javascriptNL,
              line = list(color = 'rgb(25,25,25)'),
              name = "JavaScript1000 ") %>%
  
  layout(
    yaxis = list(range = c(0, -2200)))

p10000 <- plot_ly(type = 'box', y = data10000$androidNL,
                  line = list(color = 'rgb(250,25,25)'),
                  name = "Android10000 ") %>%
  
  
  add_boxplot(y = data10000$swingNL,
              name = "Swing10000",
              marker = list(color = 'rgb(107,174,214)'),
              line = list(color = 'rgb(107,174,214)')) %>%
  
  add_boxplot(y = data10000$javaNL,
              name = "Java10000",
              marker = list(color = 'rgb(10,250,10)'),
              line = list(color = 'rgb(10,250,10)')) %>%
  
  add_boxplot(y = data10000$javascriptNL,
              line = list(color = 'rgb(25,25,25)'),
              name = "JavaScript10000 ") %>%
  
  layout(
    yaxis = list(range = c(0, -2200)))


p100000 <- plot_ly(type = 'box', y = data100000$androidNL,
                   line = list(color = 'rgb(250,25,25)'),
                   name = "Android100000 ") %>%
  
  
  add_boxplot(y = data100000$swingNL,
              name = "Swing100000",
              marker = list(color = 'rgb(107,174,214)'),
              line = list(color = 'rgb(107,174,214)')) %>%
  
  add_boxplot(y = data100000$javaNL,
              name = "Java100000",
              marker = list(color = 'rgb(10,250,10)'),
              line = list(color = 'rgb(10,250,10)')) %>%
  
  add_boxplot(y = data100000$javascriptNL,
              line = list(color = 'rgb(25,25,25)'),
              name = "JavaScript100000 ") %>%
  
  layout(
    yaxis = list(range = c(0, -2200)))

p <- subplot(p10, p100,p1000,p10000,p100000)
p

chart_link = plotly_POST(p, filename="NlAgregationByMedian")