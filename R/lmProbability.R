library(plotly)
setwd("/Users/Talal/Desktop/familiarity/R")
androidProb <- read.csv("androidPbAV6Gram.csv")
swingProb <- read.csv("swingPbAV6Gram.csv")

p <- plot_ly(type = 'box') %>%
  add_boxplot(y = androidProb$probability[1:1000],
              line = list(color = 'rgb(7,40,89)'),
              name = "android Prob") %>%
  add_boxplot(y = swingProb$probability[1:1000], name = "swing Prob",marker = list(color = 'rgb(107,174,214)'),
              line = list(color = 'rgb(107,174,214)')) %>%

  layout(title = "Andorid and Swing probability")
p

chart_link = plotly_POST(p, filename="androidSwingProb")
