library(plotly)
setwd("/Users/Talal/Tesi/familiarity/R")

#Natural Language
androidCrossValidation <- read.csv("CrossValidation/androidCrossValidation.csv")
swingCrossValidation <- read.csv("CrossValidation/swingCrossValidation.csv")

# Code
androidCode <- read.csv("Code/androidCodePbAV3Gram1000Files.csv")
swingCode <- read.csv("Code/swingCodePbAV3Gram1000Files.csv")

p <- plot_ly(type = 'box') %>%
  add_boxplot(y = androidNL$probability[1:1000],
              line = list(color = 'rgb(7,40,89)'),
              name = "Android NL") %>%
  add_boxplot(y = swingNL$probability[1:1000], 
              name = "Swing NL",
              marker = list(color = 'rgb(107,174,214)'),
              line = list(color = 'rgb(107,174,214)')) %>%
  add_boxplot(y = androidCode$probability[1:1000],
              line = list(color = 'rgb(7,40,89)'),
              name = "Android Code") %>%
  add_boxplot(y = swingCode$probability[1:1000], 
              name = "Swing Code",
              marker = list(color = 'rgb(107,174,214)'),
              line = list(color = 'rgb(107,174,214)')) %>%
  layout(title = "NL vs Code")
p

chart_link = plotly_POST(p, filename="code-NL-Android-Swing")