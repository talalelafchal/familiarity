library(plotly)
setwd("/Users/Talal/Tesi/familiarity/Distribution")
dataJavascript <- read.csv("javascriptWordsFrequency.csv")
dataAndroid <- read.csv("androidWordsFrequencyFiltered.csv")


xa <- list(
  title = "#words in one unit"
)
ya <- list(
  title = "frequency"
)


pjavascript <- plot_ly( x = dataJavascript$words, y = dataJavascript$count,  type = 'scatter' )%>%
  layout(xaxis = xa, yaxis = ya , title = "javascript")




pandroid<- plot_ly( x = dataAndroid$words, y = dataAndroid$count, type = 'scatter' )%>%
  layout(xaxis = xa, yaxis = ya , title = "android")

pandroid
pjavascript