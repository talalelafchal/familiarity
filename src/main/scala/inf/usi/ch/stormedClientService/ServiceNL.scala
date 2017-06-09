package inf.usi.ch.stormedClientService


import com.aliasi.lm.TokenizedLM
import inf.usi.ch.naturalLanguageModel.NaturalLanguageModel

/**
  * Created by Talal on 06.06.17.
  */
object ServiceNL extends NaturalLanguageModel {

  def train(tokenizedLM: TokenizedLM, text: String) = {

    val filteredTextList = List(text).map(removeStopWord)
    //    // filter units with less than 3 words
    val filteredStopWords = filteredTextList.filter {
      _.split("\\s+").length >= 3
    }
    filteredStopWords.foreach(tokenizedLM.handle(_))
    //  println(trainingSet.size)
  }

}
