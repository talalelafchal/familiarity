package inf.usi.ch.stormedClientService

import java.io.File

import com.aliasi.lm.TokenizedLM
import inf.usi.ch.javascript.FileEvaluator
import inf.usi.ch.tokenizer.JavaANTLRTokenizer
import org.jsoup.Jsoup
import org.jsoup.nodes.{Document, Element}
import org.jsoup.parser.Parser

import scala.io.Source


/**
  * Created by Talal on 09.06.17.
  */
class ServiceJavaEvaluator extends FileEvaluator {



  def getProbListForFile(lm: TokenizedLM, nGram: Int, folderPath: String, fileName: String): Seq[Double] = {

    val listCode = getCodeList(folderPath, fileName)

    val tokenizedList: Seq[NGram] = listCode.map(x => new JavaANTLRTokenizer(x.toCharArray).tokenize())
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
