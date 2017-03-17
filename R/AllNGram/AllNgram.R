setwd("/Users/Talal/Tesi/familiarity/R/AllNGram")

data <- read.csv("allNgram.csv")

p <- plot_ly(data ,y= ~android, name = 'Android',type = 'box') %>%
  add_trace(y = ~swing, name = 'Swing') %>%
  add_trace(y = ~swift, name = 'Swift') %>%
  add_trace(y = ~perl, name = 'Perl') %>%
  add_trace(y = ~matlab, name = 'MatLAb') %>%
  layout(title = 'All N-Gram')

p

chart_link = plotly_POST(p, filename="AllNGRAM")