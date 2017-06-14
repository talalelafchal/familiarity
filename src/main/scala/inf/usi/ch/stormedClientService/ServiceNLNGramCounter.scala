package inf.usi.ch.stormedClientService

import inf.usi.ch.util.NGramCountXFile

/**
  * Created by Talal on 12.06.17.
  */
class ServiceNLNGramCounter extends ServiceNLEvaluator {



  def getNGramCount(nGram: Int, folderPath: String, language : String): Seq[NGramCountXFile] = {
    val filesList = getListOfFiles(folderPath)
    val NGramList = filesList.map( file => NGramCountXFile(language,file.getName,getNGramForFile(nGram, folderPath, file.getName)))
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
