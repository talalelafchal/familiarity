library(plotly)
setwd("/Users/Talal/Tesi/familiarity/NgramCount")
dataAndroidCode <- read.csv("androidCodeNGramCount.csv")
dataAndroidNL <- read.csv("androidNLNGramCount.csv")

dataSwingCode <- read.csv("swingCodeNGramCount.csv")
dataSwingNL <- read.csv("swingNLNGramCount.csv")

dataJavaCode <- read.csv("javaCodeNGramCount.csv")
dataJavaNL <- read.csv("javaNLNGramCount.csv")

dataJavascriptCode <- read.csv("javascriptCodeNGramCount.csv")
dataJavascriptNL <- read.csv("javascriptNLNGramCount.csv")


xa <- list(
  title = "#Ngram in one Document"
)
ya <- list(
  title = "frequency"
)


pAndroidCode <- plot_ly( x = dataAndroidCode$nGram, y = dataAndroidCode$frequency,  type = 'scatter' )%>%
  layout(xaxis = xa, yaxis = ya , title = "Android Code")

pAndroidNL<- plot_ly( x = dataAndroidNL$nGram, y = dataAndroidNL$frequency, type = 'scatter' )%>%
  layout(xaxis = xa, yaxis = ya , title = "Android NL")



pSwingCode <- plot_ly( x = dataSwingCode$nGram, y = dataSwingCode$frequency,  type = 'scatter' )%>%
  layout(xaxis = xa, yaxis = ya , title = "Swing Code")

pSwingNL<- plot_ly( x = dataSwingNL$nGram, y = dataSwingNL$frequency, type = 'scatter' )%>%
  layout(xaxis = xa, yaxis = ya , title = "Swing NL")





pJavaCode <- plot_ly( x = dataJavaCode$nGram, y = dataJavaCode$frequency,  type = 'scatter' )%>%
  layout(xaxis = xa, yaxis = ya , title = "Java Code")

pJavaNL<- plot_ly( x = dataJavaNL$nGram, y = dataJavaNL$frequency, type = 'scatter' )%>%
  layout(xaxis = xa, yaxis = ya , title = "Java NL")


pJavascriptCode <- plot_ly( x = dataJavascriptCode$nGram, y = dataJavascriptCode$frequency,  type = 'scatter' )%>%
  layout(xaxis = xa, yaxis = ya , title = "Javascript Code")

pJavascriptNL<- plot_ly( x = dataJavascriptNL$nGram, y = dataJavascriptNL$frequency, type = 'scatter' )%>%
  layout(xaxis = xa, yaxis = ya , title = "Javascript NL") 



pAndroidCode
pAndroidNL

pSwingCode
pSwingNL

pJavaCode
pJavaNL

pJavascriptCode
pJavascriptNL
