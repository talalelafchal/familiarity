package inf.usi.ch.javascript

import inf.usi.ch.util.NGramCountXFile

/**
  * Created by Talal on 22.05.17.
  */
class JavascriptNLNGramCounter extends JavascriptNLEvaluator{

  def getNGramCount(nGram: Int, folderPath: String): Seq[NGramCountXFile] = {
    val filesList = getListOfFiles(folderPath)
    val NGramList = filesList.map( file => NGramCountXFile(file.getName,getNGramForFile(nGram, folderPath, file.getName)))
    NGramList
  }



  protected def getNGramForFile(nGram: Int, folderPath: String, fileName: String): Int = {

    val listNl: Seq[String] = getNlList(folderPath, fileName)

    val tokenizedList: Seq[NGram] = listNl.map(x => getTokensList(x))
    val filterdeTokenizedList = tokenizedList.filter(x => x.size >= nGram)
    val nGramList: Seq[NGram] = filterdeTokenizedList.flatMap(x => buildNGrams(x, nGram))
    nGramList.size
  }

}
