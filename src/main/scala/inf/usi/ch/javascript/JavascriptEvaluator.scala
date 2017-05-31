package inf.usi.ch.javascript

import java.io.File

import com.aliasi.lm.TokenizedLM

import scala.collection.immutable.Seq
import scala.io.Source

/**
  * Created by Talal on 27.04.17.
  */
trait JavascriptEvaluator {

  type Probability = Double

  type Token = String
  type NGram = Array[Token]

  private val stopWords = new File("stopwords.txt")
  val stopWordsList: Seq[String] = Source.fromFile(stopWords).getLines().toList

  protected def getListOfFiles(dir: String): List[File] = {
    val d = new File(dir)
    if (d.exists && d.isDirectory) {
      d.listFiles.filter(_.isFile).toList
    } else {
      List[File]()
    }
  }


  protected def getQuartileListOfFiles(dir: String, filesListPath: String): List[File] = {
    val testingListOfAllFilesName = new File(filesListPath)
    val testingSet: List[String] = Source.fromFile(testingListOfAllFilesName).getLines().toList

    val d = new File(dir)
    if (d.exists && d.isDirectory) {
      d.listFiles.filter(file =>file.isFile && testingSet.contains(file.getName)).toList
    } else {
      List[File]()
    }
  }

  protected def buildNGrams(tokens: Array[Token], nGramLength: Int): List[NGram] = {

    tokens.sliding(nGramLength).toList
  }

  protected def computeProbability(nGram: NGram, lm: TokenizedLM): Probability = {
    lm.processLog2Probability(nGram)
  }

  protected def removeStopWord(text: String): String = {
    text.split(Array(',', '.', ' ', ':', ';', '?', '!','(',')')).toList.filterNot(x => stopWordsList.contains(x.toLowerCase())).mkString(" ")
  }
}
