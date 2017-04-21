library(plotly)
setwd("/Users/Talal/Tesi/familiarity/New/")
data10  <- read.csv("java10.csv")
data100  <- read.csv("java100.csv")
data1000  <- read.csv("java1000.csv")
data10000  <- read.csv("java10000.csv")
data100000  <- read.csv("java100000.csv")


wilcox.test(data10$android,data10$swing,alternative="two.side",paired=FALSE)
wilcox.test(data100$android,data100$swing,alternative="two.side",paired=FALSE)
wilcox.test(data1000$android,data1000$swing,alternative="two.side",paired=FALSE)
wilcox.test(data10000$android,data10000$swing,alternative="two.side",paired=FALSE)
wilcox.test(data100000$android,data100000$swing,alternative="two.side",paired=FALSE)


p <- plot_ly(type = 'box') %>%
  add_boxplot(y = data10$android,
              line = list(color = 'rgb(250,25,25)'),
              name = "Android10 ") %>%
  
  add_boxplot(y = data10$swing,
              name = "Swing10",
              marker = list(color = 'rgb(107,174,214)'),
              line = list(color = 'rgb(107,174,214)')) %>%
  
  add_boxplot(y = data100$android,
              line = list(color = 'rgb(250,25,25)'),
              name = "Android100 ") %>%
  
  add_boxplot(y = data100$swing,
              name = "Swing100",
              marker = list(color = 'rgb(107,174,214)'),
              line = list(color = 'rgb(107,174,214)')) %>%
  add_boxplot(y = data1000$android,
              line = list(color = 'rgb(250,25,25)'),
              name = "Android1000 ") %>%
  
  add_boxplot(y = data1000$swing,
              name = "Swing1000",
              marker = list(color = 'rgb(107,174,214)'),
              line = list(color = 'rgb(107,174,214)')) %>%
  
  add_boxplot(y = data10000$android,
              line = list(color = 'rgb(250,25,25)'),
              name = "Android10000 ") %>%
  
  add_boxplot(y = data10000$swing,
              name = "Swing10000",
              marker = list(color = 'rgb(107,174,214)'),
              line = list(color = 'rgb(107,174,214)')) %>%
  add_boxplot(y = data100000$android,
              line = list(color = 'rgb(250,25,25)'),
              name = "Android100000 ") %>%
  add_boxplot(y = data100000$swing,
              name = "Swing100000",
              marker = list(color = 'rgb(107,174,214)'),
              line = list(color = 'rgb(107,174,214)')) %>%
  layout(
    yaxis = list(range = c(0, -100)))

p

chart_link = plotly_POST(p, filename="JAVA-Andorid-Swift")