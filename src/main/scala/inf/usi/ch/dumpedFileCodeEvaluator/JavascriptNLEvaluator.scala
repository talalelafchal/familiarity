package inf.usi.ch.dumpedFileCodeEvaluator

import java.io.File

import com.aliasi.lm.TokenizedLM
import com.aliasi.tokenizer.IndoEuropeanTokenizerFactory
import org.jsoup.Jsoup
import org.jsoup.nodes.{Document, Element}
import org.jsoup.parser.Parser

import scala.collection.immutable.Seq
import scala.io.Source

/**
  * Created by Talal on 27.04.17.
  */
class JavascriptNLEvaluator extends JavascriptEvaluator{

  private val stopWords = new File("stopwords.txt")
  val stopWordsList: Seq[String] = Source.fromFile(stopWords).getLines().toList


  def getProbListFiles(lm: TokenizedLM, nGram: Int, folderPath: String): Seq[Double] = {
    val filesList = getListOfFiles(folderPath)
    val probabilityList = filesList.flatMap(file => getProbListForFile(lm, nGram, folderPath, file.getName))
    probabilityList
  }

  private def getTokensList(text : String): Array[Token] = {
    val tokenizerFactory = new IndoEuropeanTokenizerFactory()
    val aCharArray = text.toCharArray
    val tokensList=tokenizerFactory.tokenizer(aCharArray, 0, aCharArray.length).tokenize()
    filterTokensList(tokensList)
  }

  private def filterTokensList(tokensList : Array[Token]): Array[Token] ={
    val excludeList = List(".", ",", "(", ")", ";", ":", "!", "?")
    val filteredList = tokensList.filter(x => !excludeList.contains(x))
    filteredList
  }


  protected def getProbListForFile(lm: TokenizedLM, nGram: Int, folderPath: String, fileName: String): Seq[Double] = {
    val listNl: Seq[String] = getNlList(folderPath, fileName)

    val tokenizedList: Seq[Array[Token]] = listNl.map(x => getTokensList(x))

    val nGramList: Seq[NGram] = tokenizedList.flatMap(x => buildNGrams(x, nGram))
    val probabilityList: Seq[Probability] = nGramList.map(x => computeProbability(x, lm))
    probabilityList

  }


  protected def getNlList(folderPath: String, fileName: String): Seq[String] = {
    val file = new File(folderPath, fileName)
    val postString = Source.fromFile(file).getLines().mkString
    val doc: Document = Jsoup.parse(postString, "", Parser.xmlParser())
    val nlStringList = getnlStringList(doc)
    val nlStrinListWithoutStopWorld = nlStringList.map(removeStopWord)
    nlStrinListWithoutStopWorld
  }

  private def getnlStringList(doc: Document): Seq[String] = {
    val nl: List[AnyRef] = doc.select(">*").not("pre").not("code").toArray().toList
    val nlStringList: List[String] = nl.map(x =>
      x.asInstanceOf[Element].text())
    nlStringList
  }



  private def removeStopWord(text: String): String = {
    text.split(Array(',', '.', ' ', ':', ';', '?', '!','(',')')).toList.filterNot(x => stopWordsList.contains(x.toLowerCase())).mkString(" ")
  }


}
