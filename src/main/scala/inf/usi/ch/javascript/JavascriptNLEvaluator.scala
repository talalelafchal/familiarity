package inf.usi.ch.javascript

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




  def getProbListFiles(lm: TokenizedLM, nGram: Int, folderPath: String): Seq[Double] = {
    val filesList = getListOfFiles(folderPath)
    val probabilityList = filesList.flatMap(file => getProbListForFile(lm, nGram, folderPath, file.getName))
    probabilityList
  }

  protected def getTokensList(text : String): Array[Token] = {
    val tokenizerFactory = new IndoEuropeanTokenizerFactory()
    val aCharArray = text.toCharArray
    val tokensList=tokenizerFactory.tokenizer(aCharArray, 0, aCharArray.length).tokenize()
    filterTokensList(tokensList)
  }

  protected def filterTokensList(tokensList : Array[Token]): Array[Token] ={
    val excludeList = List(".", ",", "(", ")", ";", ":", "!", "?")
    val filteredList = tokensList.filter(x => !excludeList.contains(x))
    filteredList
  }


  protected def getProbListForFile(lm: TokenizedLM, nGram: Int, folderPath: String, fileName: String): Seq[Double] = {
    val listNl: Seq[String] = getNlList(folderPath, fileName)

    val tokenizedList: Seq[Array[Token]] = listNl.map(x => getTokensList(x))
    // at least 3 tokens
    val filterdeTokenizedList = tokenizedList.filter(x => x.size >= nGram)

    val nGramList: Seq[NGram] = filterdeTokenizedList.flatMap(x => buildNGrams(x, nGram))
    val probabilityList: Seq[Probability] = nGramList.map(x => computeProbability(x, lm))
    probabilityList

  }


  protected def getNlList(folderPath: String, fileName: String): Seq[String] = {
    val file = new File(folderPath, fileName)
    val postString = Source.fromFile(file).getLines().mkString
    val doc: Document = Jsoup.parse(postString, "", Parser.xmlParser())
    val nlStringList = getNlStringList(doc)
    val nlStringListWithoutStopWorld = nlStringList.map(removeStopWord)
    nlStringListWithoutStopWorld
  }

  protected def getNlStringList(doc: Document): Seq[String] = {
    val nl: List[AnyRef] = doc.select(">*").not("pre").not("code").toArray().toList
    val nlStringList: List[String] = nl.map(x =>
      x.asInstanceOf[Element].text())
    nlStringList
  }


}
