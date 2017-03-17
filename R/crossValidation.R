library(plotly)
setwd("/Users/Talal/Tesi/familiarity/R")

#CrossValidation
androidBiLMValidation <- read.csv("BiLMValidationSum/androidBiLMValidationSum.csv")
swingBiLMValidation <- read.csv("BiLMValidationSum/swingBiLMValidationSum.csv")

#
android <- read.csv("androidPbAV3Gram10000FIles.csv")
swing <- read.csv("swingPbAV3Gram10000Files.csv")

p <- plot_ly(type = 'box') %>%
  add_boxplot(y = android$probability[1:1000],
              line = list(color = 'rgb(7,40,89)'),
              name = "Android UniLM ") %>%
  add_boxplot(y = swing$probability[1:1000], 
              name = "Swing UniLM",
              marker = list(color = 'rgb(107,174,214)'),
              line = list(color = 'rgb(107,174,214)')) %>%
  add_boxplot(y = androidBiLMValidation$probability[1:1000],
              line = list(color = 'rgb(7,40,89)'),
              name = "Android BiLM") %>%
  add_boxplot(y = swingBiLMValidation$probability[1:1000], 
              name = "Swing BiLM",
              marker = list(color = 'rgb(107,174,214)'),
              line = list(color = 'rgb(107,174,214)')) %>%
  layout(title = "UniLM vs BiLM")
p

chart_link = plotly_POST(p, filename="crossEvaluation")
