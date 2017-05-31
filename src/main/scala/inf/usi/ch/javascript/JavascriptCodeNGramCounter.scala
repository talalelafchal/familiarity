package inf.usi.ch.javascript

import inf.usi.ch.tokenizer.JavascriptANTLRTokenizer
import inf.usi.ch.util.NGramCountXFile

/**
  * Created by Talal on 22.05.17.
  */
class JavascriptCodeNGramCounter extends JavascriptCodeEvaluator {

  def getNGramCount(nGram: Int, folderPath: String): Seq[NGramCountXFile] = {
    val filesList = getListOfFiles(folderPath)
    val NGramList = filesList.map( file =>  NGramCountXFile(file.getName, getNGramForFile(nGram, folderPath, file.getName)))
    NGramList
  }



  protected def getNGramForFile(nGram: Int, folderPath: String, fileName: String): Int = {

    val listCode : List[String]= getCodeList(folderPath, fileName)

    val tokenizedList: Seq[NGram] = listCode.map(x => new JavascriptANTLRTokenizer(x.toCharArray).tokenize())
    val nGramList: Seq[NGram] = tokenizedList.flatMap(x => buildNGrams(x, nGram))
    nGramList.size
  }

}
