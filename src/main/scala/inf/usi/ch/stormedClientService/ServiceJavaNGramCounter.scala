package inf.usi.ch.stormedClientService

import java.io.File

import inf.usi.ch.tokenizer.{JavaANTLRTokenizer, JavascriptANTLRTokenizer}
import inf.usi.ch.util.NGramCountXFile

import scala.collection.immutable.Seq

/**
  * Created by Talal on 12.06.17.
  */
class ServiceJavaNGramCounter extends ServiceJavaEvaluator {


  val ANDROID = "android"

  def getNGramCount(nGram: Int, folderPath: String): Seq[NGramCountXFile] = {
    val filesList: Seq[File] = getListOfFiles(folderPath)
    val NGramList = filesList.map( file =>  NGramCountXFile(ANDROID,file.getName, getNGramForFile(nGram, folderPath, file.getName)))
    NGramList
  }

  protected def getNGramForFile(nGram: Int, folderPath: String, fileName: String): Int = {

    val listCode : List[String]= getCodeList(folderPath, fileName)

    val tokenizedList: Seq[NGram] = listCode.map(x => new JavaANTLRTokenizer(x.toCharArray).tokenize())
    val nGramList: Seq[NGram] = tokenizedList.flatMap(x => buildNGrams(x, nGram))
    nGramList.size
  }
}
