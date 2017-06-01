package inf.usi.ch.javascript

import java.io.File

import inf.usi.ch.tokenizer.JavascriptANTLRTokenizer
import inf.usi.ch.util.NGramCountXFile

import scala.collection.immutable.Seq

/**
  * Created by Talal on 22.05.17.
  */
class JavascriptCodeNGramCounter extends JavascriptCodeEvaluator {

  val javascript = "javascript"

  def getNGramCount(nGram: Int, folderPath: String): Seq[NGramCountXFile] = {
    val filesList: Seq[File] = getListOfFiles(folderPath)
    val NGramList = filesList.map( file =>  NGramCountXFile(javascript,file.getName, getNGramForFile(nGram, folderPath, file.getName)))
    NGramList
  }

  def getNGramQuartileCount(nGram: Int, folderPath: String,filesListPath:String): Seq[NGramCountXFile] = {
    val filesList: Seq[File] = getQuartileListOfFiles(folderPath,filesListPath)
    val NGramList = filesList.map( file =>  NGramCountXFile(javascript,file.getName, getNGramForFile(nGram, folderPath, file.getName)))
    NGramList
  }




  protected def getNGramForFile(nGram: Int, folderPath: String, fileName: String): Int = {

    val listCode : List[String]= getCodeList(folderPath, fileName)

    val tokenizedList: Seq[NGram] = listCode.map(x => new JavascriptANTLRTokenizer(x.toCharArray).tokenize())
    val nGramList: Seq[NGram] = tokenizedList.flatMap(x => buildNGrams(x, nGram))
    nGramList.size
  }

}
