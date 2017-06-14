package inf.usi.ch.stormedClientService

import java.io.File

import inf.usi.ch.tokenizer.{ JavascriptANTLRTokenizer}
import inf.usi.ch.util.NGramCountXFile

import scala.collection.immutable.Seq

/**
  * Created by Talal on 12.06.17.
  */
class ServiceJavascriptNGramCounter extends ServiceJavaScriptEvaluator{
  val JAVASCRIPT = "javascript"

  def getNGramCount(nGram: Int, folderPath: String): Seq[NGramCountXFile] = {
    val filesList: Seq[File] = getListOfFiles(folderPath)
    val NGramList = filesList.map( file =>  NGramCountXFile(JAVASCRIPT,file.getName, getNGramForFile(nGram, folderPath, file.getName)))
    NGramList
  }

  protected def getNGramForFile(nGram: Int, folderPath: String, fileName: String): Int = {

    val listCode : List[String]= getCodeList(folderPath, fileName)

    val tokenizedList: Seq[NGram] = listCode.map(x => new JavascriptANTLRTokenizer(x.toCharArray).tokenize())
    val nGramList: Seq[NGram] = tokenizedList.flatMap(x => buildNGrams(x, nGram))
    nGramList.size
  }
}
