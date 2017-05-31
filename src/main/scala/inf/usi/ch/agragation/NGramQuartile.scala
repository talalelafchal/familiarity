package inf.usi.ch.agragation

import inf.usi.ch.javaLMTokenizer.JavaNGramCounter
import inf.usi.ch.javascript.{JavascriptCodeNGramCounter, JavascriptNLNGramCounter}
import inf.usi.ch.naturalLanguageModel.NaturalLanguageNGramCounter
import inf.usi.ch.util.NGramCountXFile

/**
  * Created by Talal on 30.05.17.
  */
class NGramQuartile {


  private def getQuartile(orderedList: Seq[NGramCountXFile]) = {
    val firstQuartileIndex = ((orderedList.length - 1) * (0.25)).toInt
    val thirdQuartileIndex = ((orderedList.length - 1) * (0.75)).toInt
    orderedList.slice(firstQuartileIndex, thirdQuartileIndex + 1)
  }

  private def listIntersection(q1q3CodeList: Seq[NGramCountXFile], q1q3NLList: Seq[NGramCountXFile]): Seq[NGramCountXFile] = {
    val nlFilesList = q1q3NLList.map(x => x.fileName)
    val intersection = for {x <- q1q3CodeList if nlFilesList.contains(x.fileName)} yield x
    intersection
  }

  private def orderNGramCountXFileList(list: Seq[NGramCountXFile]): Seq[NGramCountXFile] = {
    val sortedList = list.sortWith(_.nGramCount < _.nGramCount)
    sortedList
  }

  private def getIntersection(codeCountList: Seq[NGramCountXFile], nLCountList: Seq[NGramCountXFile]) = {
    val orderedCodeList = orderNGramCountXFileList(codeCountList)
    val orderedNLList = orderNGramCountXFileList(nLCountList)

    val q1q3CodeList = getQuartile(orderedCodeList)
    val q1q3NLList = getQuartile(orderedNLList)
    val intersection = listIntersection(q1q3CodeList, q1q3NLList)
    intersection
  }

  def getStormedQuartileFiles(stormedDataPath: String, filesListPath: String, nGram: Int, numberOfFiles: Int): Seq[NGramCountXFile] = {
    val nGramCounter = new JavaNGramCounter
    val nlCounter = new NaturalLanguageNGramCounter
    val codeCountList = nGramCounter.getNGramCount(filesListPath, numberOfFiles, stormedDataPath, nGram)
    val nLCountList = nlCounter.getNGramCount(nGram, numberOfFiles, filesListPath, stormedDataPath)

    getIntersection(codeCountList,nLCountList)

  }

  def getJavascriptQuartileFiles(filesPath: String, nGram: Int): Seq[NGramCountXFile] = {
    val javascriptCodeNGramCounter = new JavascriptCodeNGramCounter
    val javascriptNLNGramCounter = new JavascriptNLNGramCounter
    val javaScriptCodeCountList = javascriptCodeNGramCounter.getNGramCount(nGram, filesPath)
    val javascriptNLCountList = javascriptNLNGramCounter.getNGramCount(nGram, filesPath)
    getIntersection(javaScriptCodeCountList,javascriptNLCountList)
  }

}
