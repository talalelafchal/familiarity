setwd("/Users/Talal/Tesi/familiarity/R/BiLMValidationSum")
data <- read.csv("allFilesLength.csv")
android = data$android
android = as.data.frame(android)

swing = data$swing
swing = as.data.frame(swing)

swift = data$swift
swift = as.data.frame(swift)

perl = data$pel
perl = as.data.frame(perl)

matlab = data$matlab
matlab = as.data.frame(matlab)

p <- plot_ly(data ,y= ~android, name = 'Android',type = 'box') %>%
  add_trace(y = ~swing, name = 'Swing') %>%
  add_trace(y = ~swift, name = 'Swift') %>%
  add_trace(y = ~pel, name = 'Perl') %>%
  add_trace(y = ~matlab, name = 'MatLAb') %>%
  layout(title = 'Length')

p
chart_link = plotly_POST(p, filename="length")
