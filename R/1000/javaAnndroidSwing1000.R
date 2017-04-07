library(plotly)
setwd("/Users/Talal/Tesi/familiarity/R/1000")

data <- read.csv("javaNGram1000.csv")
dataTesting <- read.csv("javaNGramTesting1000.csv")
median(data$android,na.rm = TRUE)
median(data$swing,na.rm = TRUE)


#effsize::cliff.delta(data$android,data$swing)

p <- plot_ly(data ,y= ~android,type = 'box', name = 'Android') %>%
  add_trace(y = ~swing, name = 'Swing') %>%
  add_boxplot(y = dataTesting$android,
              line = list(color = 'rgb(7,40,89)'),
              name = "Android Training Set ") %>%
  
  layout(title = 'Java 1000')

p