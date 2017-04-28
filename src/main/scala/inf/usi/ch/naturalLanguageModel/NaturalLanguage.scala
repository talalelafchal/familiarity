package inf.usi.ch.naturalLanguageModel

import java.io.File

import scala.io.Source

/**
  * Created by Talal on 28.04.17.
  */
trait NaturalLanguage {

  private val stopWords = new File("stopwords.txt")
  val stopWordsList: Seq[String] = Source.fromFile(stopWords).getLines().toList
  protected def removeStopWord(text: String): String = {

    text.split(Array(',', '.', ' ', ':', ';', '?', '!','(',')')).toList.filterNot(x => stopWordsList.contains(x.toLowerCase())).mkString(" ")
  }
}
