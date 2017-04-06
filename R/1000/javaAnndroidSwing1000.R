library(plotly)
setwd("/Users/Talal/Tesi/familiarity/R/1000")

data <- read.csv("javaNGram1000.csv")
median(data$android,na.rm = TRUE)
median(data$swing,na.rm = TRUE)


effsize::cliff.delta(data$android,data$swing)

p <- plot_ly(data ,y= ~android,type = 'box', name = 'Android') %>%
  add_trace(y = ~swing, name = 'Swing') %>%
  
  layout(title = 'Java 1000')

p