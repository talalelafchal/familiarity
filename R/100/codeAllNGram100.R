library(plotly)
setwd("/Users/Talal/Tesi/familiarity/R/100")

data <- read.csv("allCodeNgram100.csv")
median(data$android,na.rm = TRUE)
median(data$swing,na.rm = TRUE)
median(data$swift,na.rm = TRUE)
median(data$perl,na.rm = TRUE)
median(data$matlab,na.rm = TRUE)

effsize::cliff.delta(data$android,data$swing)
effsize::cliff.delta(data$android,data$swift)
effsize::cliff.delta(data$android,data$perl)
effsize::cliff.delta(data$android,data$matlab)

p <- plot_ly(data ,y= ~android,type = 'box') %>%
  add_trace(y = ~swing, name = 'Swing') %>%
  add_trace(y = ~swift, name = 'Swift') %>%
  add_trace(y = ~perl, name = 'Perl') %>%
  add_trace(y = ~matlab, name = 'MatLAb') %>%
  layout(title = 'All Code N-Gram 100')

p

chart_link = plotly_POST(p, filename="AllCodeNGRAM100")