library(plotly)
setwd("/Users/Talal/Tesi/familiarity/R/AllNLNGram")

codeData <- read.csv("allNLNgram.csv")
p <- plot_ly(codeData ,y= ~android, name = 'Android',type = 'box') %>%
  add_trace(y = ~swing, name = 'Swing Code') %>%
  add_trace(y = ~swift, name = 'Swift Code') %>%
  add_trace(y = ~perl, name = 'Perl Code') %>%
  add_trace(y = ~matlab, name = 'MatLAb Code') %>%
  layout(title = 'All Natrual Language N-Gram')

p

chart_link = plotly_POST(p, filename="All-nl-nGram")