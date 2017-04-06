library(plotly)
setwd("/Users/Talal/Tesi/familiarity/R/BiLMValidationSum")

#CrossValidation
android <- read.csv("javaAndroid.csv")
swing <- read.csv("javaSwing.csv")
swift <- read.csv("swift.csv")
perl <- read.csv("perl.csv")
matlab <- read.csv("matLab.csv")


p <- plot_ly(type = 'box') %>%
  add_boxplot(y = android$probability,
              line = list(color = 'rgb(7,40,89)'),
              name = "Android ") %>%
  
  add_boxplot(y = swing$probability, 
              name = "Swing",
              marker = list(color = 'rgb(107,174,214)'),
              line = list(color = 'rgb(107,174,214)')) %>%
  
  add_boxplot(y = swift$probability,
              line = list(color = 'rgb(7,40,89)'),
              name = "Swift") %>%
  
  add_boxplot(y = perl$probability, 
              name = "Perl",
              marker = list(color = 'rgb(0,0,0)'),
              line = list(color = 'rgb(250,5,10)')) %>%
  add_boxplot(y = matlab$probability, 
              name = "MatLab",
              marker = list(color = 'rgb(0,0,0)'),
              line = list(color = 'rgb(10,250,10)')) %>%
  
  layout(title = "BiLanguage model validation")
p

chart_link = plotly_POST(p, filename="biLanguageModel")