package inf.usi.ch.naturalLanguageModel

import java.io.File

import scala.io.Source

/**
  * Created by Talal on 22.05.17.
  */
class NaturalLanguageNGramCounter extends NaturalLanguageModelEvaluator{

  def getNgramCount(nGram: Int, numberOfFiles: Int, testListFileName: String, stormedDataPath: String):Seq[Int] = {
    val testingListOfAllFilesName = new File(testListFileName)
    val testingSet: List[String] = Source.fromFile(testingListOfAllFilesName).getLines().toList.take(numberOfFiles)
    val nGramList = testingSet.map(file => getNGram(nGram,file,stormedDataPath))
    nGramList
  }

  private def getNGram(nGram: Int, stormedDataPath: String, fileName: String): Int = {
    val listNl: Seq[String] = jsonFileToText(stormedDataPath, fileName)

    val tokenizedList: Seq[Array[Token]] = listNl.map(x => getTokensList(x))

    // at least 3 tokens
    val filterdeTokenizedList = tokenizedList.filter(x => x.size >= nGram)

    val nGramList: Seq[NGram] = filterdeTokenizedList.flatMap(x => buildNGrams(x, nGram))
    nGramList.size
  }

}
