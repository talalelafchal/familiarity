library(plotly)
setwd("/Users/Talal/Tesi/familiarity/AndroidSwingJavaJavascriptFormattedPunctuationCSVFiles")
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


p10 <- plot_ly(type = 'box', y = data10$android,
              line = list(color = 'rgb(250,25,25)'),
              name = "Android10 ") %>%

  
  add_boxplot(y = data10$swing,
              name = "Swing10",
              marker = list(color = 'rgb(107,174,214)'),
              line = list(color = 'rgb(107,174,214)')) %>%
  
  add_boxplot(y = data10$java,
              name = "Java10",
              marker = list(color = 'rgb(10,250,10)'),
              line = list(color = 'rgb(10,250,10)')) %>%
  
  add_boxplot(y = data10$javascript,
              line = list(color = 'rgb(25,25,25)'),
              name = "JavaScript10 ") %>%
  
  layout(
    yaxis = list(range = c(0, -80)))

p100 <- plot_ly(type = 'box', y = data100$android,
               line = list(color = 'rgb(250,25,25)'),
               name = "Android10 ") %>%
  
  
  add_boxplot(y = data100$swing,
              name = "Swing100",
              marker = list(color = 'rgb(107,174,214)'),
              line = list(color = 'rgb(107,174,214)')) %>%
  
  add_boxplot(y = data100$java,
              name = "Java10",
              marker = list(color = 'rgb(10,250,10)'),
              line = list(color = 'rgb(10,250,10)')) %>%
  
  add_boxplot(y = data100$javascript,
              line = list(color = 'rgb(25,25,25)'),
              name = "JavaScript100 ") %>%
  
  layout(
    yaxis = list(range = c(0, -80)))
  

p1000 <- plot_ly(type = 'box', y = data1000$android,
               line = list(color = 'rgb(250,25,25)'),
               name = "Android1000 ") %>%
  
  
  add_boxplot(y = data1000$swing,
              name = "Swing1000",
              marker = list(color = 'rgb(107,174,214)'),
              line = list(color = 'rgb(107,174,214)')) %>%
  
  add_boxplot(y = data1000$java,
              name = "Java1000",
              marker = list(color = 'rgb(10,250,10)'),
              line = list(color = 'rgb(10,250,10)')) %>%
  
  add_boxplot(y = data1000$javascript,
              line = list(color = 'rgb(25,25,25)'),
              name = "JavaScript1000 ") %>%
  
  layout(
    yaxis = list(range = c(0, -80)))

p10000 <- plot_ly(type = 'box', y = data10$android,
               line = list(color = 'rgb(250,25,25)'),
               name = "Android10000 ") %>%
  
  
  add_boxplot(y = data10000$swing,
              name = "Swing10000",
              marker = list(color = 'rgb(107,174,214)'),
              line = list(color = 'rgb(107,174,214)')) %>%
  
  add_boxplot(y = data10000$java,
              name = "Java10000",
              marker = list(color = 'rgb(10,250,10)'),
              line = list(color = 'rgb(10,250,10)')) %>%
  
  add_boxplot(y = data10000$javascript,
              line = list(color = 'rgb(25,25,25)'),
              name = "JavaScript10000 ") %>%
  
  layout(
    yaxis = list(range = c(0, -80)))
p100000 <- plot_ly(type = 'box', y = data100000$android,
               line = list(color = 'rgb(250,25,25)'),
               name = "Android100000 ") %>%
  
  
  add_boxplot(y = data100000$swing,
              name = "Swing100000",
              marker = list(color = 'rgb(107,174,214)'),
              line = list(color = 'rgb(107,174,214)')) %>%
  
  add_boxplot(y = data100000$java,
              name = "Java100000",
              marker = list(color = 'rgb(10,250,10)'),
              line = list(color = 'rgb(10,250,10)')) %>%
  
  add_boxplot(y = data100000$javascript,
              line = list(color = 'rgb(25,25,25)'),
              name = "JavaScript100000 ") %>%
  
  layout(
    yaxis = list(range = c(0, -80)))

p <- subplot(p10, p100,p1000,p10000,p100000)
p

chart_link = plotly_POST(p, filename="Removed-All-Punctuations-Andorid-Swift-Java-Javascript")