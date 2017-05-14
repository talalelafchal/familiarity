package inf.usi.ch.javascript

import java.io.File

import org.jsoup.Jsoup
import org.jsoup.nodes.{Element}
import org.jsoup.parser.Parser

import scala.io.Source

/**
  * Created by Talal on 02.05.17.
  */
object JavascriptNLWordsDistribution extends JavascriptEvaluator {


  def getWordsDistribution(folderPath: String): Seq[Int] = {
    val filesList = getListOfFiles(folderPath)
    filesList.flatMap(file => getNumberOfWordsPerUnit(folderPath,file.getName))
  }

  private def getNumberOfWordsPerUnit(folderPath: String, fileName: String): Seq[Int] = {
    val file = new File(folderPath, fileName)
    val postString = Source.fromFile(file).getLines().mkString
    val nl = Jsoup.parse(postString, "", Parser.xmlParser()).select(">*").not("pre").not("code").toArray().toList
    val nlStringList: List[String] = nl.map(x =>
      x.asInstanceOf[Element].text())
    val countList = nlStringList.map(numberOfWords)
    countList
  }

  private def numberOfWords( paragraph : String)={
    val filteredParagraph = removeStopWord(paragraph)
    filteredParagraph.split(" ").length
  }





}
