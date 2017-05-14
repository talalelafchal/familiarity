package inf.usi.ch.naturalLanguageModel

import java.io.File

import ch.usi.inf.reveal.parsing.artifact.ArtifactSerializer
import ch.usi.inf.reveal.parsing.units.{InformationUnit, NaturalLanguageTaggedUnit}

import scala.io.Source

/**
  * Created by Talal on 02.05.17.
  */
object AndroidNLWordsDistribution extends NaturalLanguage {


  def gteWordsDistribution(testListFileName: String, stormedDataPath: String, numberOfFiles: Int): Seq[Int] = {
    val testingListOfAllFilesName = new File(testListFileName)
    val testingSet: List[String] = Source.fromFile(testingListOfAllFilesName).getLines().toList.take(numberOfFiles)
    val probList = testingSet.flatMap(file => getDistributionList(file, stormedDataPath))
    probList
  }

  private def jsonFileToText(fileName: String, stormedDataPath: String): Seq[String] = {
    val testingFile = new File(stormedDataPath, fileName)
    val artifact = ArtifactSerializer.deserializeFromFile(testingFile)
    val nlUnits: Seq[InformationUnit] = (artifact.question.informationUnits ++ artifact.answers.flatMap {
      _.informationUnits
    }).filter(_.isInstanceOf[NaturalLanguageTaggedUnit])

    val textList: Seq[String] = nlUnits.map(_.rawText)
    val textListWithNoStopWord = textList.map(removeStopWord)

//    nlUnits.foreach { unit =>
//      val cleanText: String = removeStopWord(unit.rawText)
//      if (cleanText.split("\\s+").length == 1) {
//        println("==== unit: " + unit.id)
//        println(unit.rawText)
//        println("==== after stopword removal:")
//        println(cleanText)
//        println("====")
//      }
//    }

    textListWithNoStopWord.filter { _.split("\\s+").length >= 3  }


  }

  private def getDistributionList(fileName: String, stormedDataPath: String): Seq[Int] = {
    val listNl: Seq[String] = jsonFileToText(fileName,stormedDataPath)
    listNl.map{_.trim}.distinct.map(getWordCount)
  }

  private def getWordCount(unit: String)={
    val length = unit.split("\\s+").length
    length
  }

}
