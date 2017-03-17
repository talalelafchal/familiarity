library(plotly)
setwd("/Users/Talal/Tesi/familiarity/R")

androidProb6Gram <- read.csv("androidPbAV6Gram10000Files.csv")
swingProb6Gram <- read.csv("swingPbAV6Gram10000Files.csv")
swiftProb6Gram <- read.csv("swiftPbAV6Gram1000Files.csv")

androidProb3Gram <- read.csv("androidPbAV3Gram10000Files.csv")
swingProb3Gram <- read.csv("swingPbAV3Gram10000Files.csv")
swiftProb3Gram <- read.csv("swiftPbAV3Gram1000Files.csv")

p <- plot_ly(type = 'box') %>%
  add_boxplot(y = androidProb3Gram$probability[1:1000],
              line = list(color = 'rgb(7,40,89)'),
              name = "Android 3-Gram") %>%
  add_boxplot(y = swingProb3Gram$probability[1:1000], 
              name = "Swing 3-Gram",
              marker = list(color = 'rgb(107,174,214)'),
              line = list(color = 'rgb(107,174,214)')) %>%
  
  add_boxplot(y = swiftProb3Gram$probability[1:1000], 
              name = "Swift 3-Gram",
              marker = list(color = 'rgb(255,0,0)'),
              line = list(color = 'rgb(255,138,138)')) %>%
  
  
  add_boxplot(y = androidProb6Gram$probability[1:1000],
              line = list(color = 'rgb(7,40,89)'),
              name = "Android 6-Gram ") %>%
  add_boxplot(y = swingProb6Gram$probability[1:1000], 
              name = "Swing 6-Gram",
              marker = list(color = 'rgb(107,174,214)'),
              line = list(color = 'rgb(107,174,214)')) %>%
  add_boxplot(y = swiftProb6Gram$probability[1:1000], 
              name = "Swift 6-Gram",
              marker = list(color = 'rgb(255,0,0)'),
              line = list(color = 'rgb(255,138,138)')) %>%
  
  layout(title = "3-Gram vs 6-Gram")
p

chart_link = plotly_POST(p, filename="3gramVs6Gram")
