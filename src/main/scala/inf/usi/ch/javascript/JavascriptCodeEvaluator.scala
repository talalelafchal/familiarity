package inf.usi.ch.javascript

import java.io.File

import com.aliasi.lm.TokenizedLM
import inf.usi.ch.tokenizer.JavascriptANTLRTokenizer
import org.jsoup.Jsoup
import org.jsoup.nodes.{Document, Element}
import org.jsoup.parser.Parser

import scala.io.Source

/**
  * Created by Talal on 26.04.17.
  */
class JavascriptCodeEvaluator extends JavascriptEvaluator {


  def getProbListFiles(lm: TokenizedLM, nGram: Int, folderPath: String): Seq[Double] = {
    val filesList = getListOfFiles(folderPath)
    val probabilityList = filesList.flatMap(file => getProbListForFile(lm, nGram, folderPath, file.getName))
    probabilityList
  }


  def getQuartileProbList(lm: TokenizedLM, nGram: Int, filesFolderPath: String, filesListPath: String): Seq[Seq[Double]] = {
    val filesList = getQuartileListOfFiles(filesFolderPath,filesListPath)
    val probabilityList = filesList.map(file => getProbListForFile(lm, nGram, filesFolderPath, file.getName))
    probabilityList
  }




  protected def getProbListForFile(lm: TokenizedLM, nGram: Int, folderPath: String, fileName: String): Seq[Double] = {

    val listCode = getCodeList(folderPath, fileName)

    val tokenizedList: Seq[NGram] = listCode.map(x => new JavascriptANTLRTokenizer(x.toCharArray).tokenize())
    val nGramList = tokenizedList.flatMap(x => buildNGrams(x, nGram))
    val probabilityList = nGramList.map(x => computeProbability(x, lm))
    probabilityList

  }


  protected def getCodeList(folderPath: String, fileName: String): List[String] = {
    val file = new File(folderPath, fileName)
    val postString = Source.fromFile(file).getLines().mkString
    val doc: Document = Jsoup.parse(postString, "", Parser.xmlParser())

    //Code
    val codeStringList = getCodeStringLIst(doc, ">code")

    //PreCode
    val preCodStringList = getCodeStringLIst(doc, ">pre")

    codeStringList ::: preCodStringList

  }

  private def getCodeStringLIst(doc: Document, string: String) = {
    val code: List[AnyRef] = doc.select(string).toArray.toList
    val codeStringList: List[String] = code.map(x =>
      x.asInstanceOf[Element].text().replaceAll("[^\\w\"]", " "))
    codeStringList
  }
}