package inf.usi.ch.naturalLanguageModel

import java.io.{BufferedWriter, File, FileWriter}
import java.util.StringTokenizer

import ch.usi.inf.reveal.parsing.artifact.ArtifactSerializer
import ch.usi.inf.reveal.parsing.units. NaturalLanguageTaggedUnit
import com.aliasi.lm.CompiledTokenizedLM

import scala.collection.mutable.ListBuffer
import scala.io.Source

/**
  * Created by Talal on 10.03.17.
  */
class NaturalLanguageModelEvaluator {

  private def jsonFileToText(fileName: String, stormedDataPath: String): String = {
    val testingFile = new File(stormedDataPath, fileName)
    val artifact = ArtifactSerializer.deserializeFromFile(testingFile)
    var nlString = ""
    val nlUnits = (artifact.question.informationUnits ++ artifact.answers.flatMap { _.informationUnits }).filter(_.isInstanceOf[NaturalLanguageTaggedUnit])
    nlUnits.foreach(x => {
      nlString = nlString + "\n" + x.rawText
    })
    println(nlString)
    nlString
  }

  def writeListToCSVFile(list: List[Double], filePath: String) = {
    val listFile = new File(filePath)
    val listbf = new BufferedWriter(new FileWriter(listFile))
    listbf.write("probability\n")
    list.foreach(x => listbf.write(x.toString + '\n'))
    listbf.close()
  }


  def nGramList(token: String, nGram: Int): List[String] = {
    var tokenBuffer = new ListBuffer[String]()
    val stringTokenized = new StringTokenizer(token)
    while (stringTokenized.hasMoreElements()) {
      tokenBuffer += (stringTokenized.nextToken())
    }
    tokenBuffer.toList.sliding(nGram).toList.map(x => x.mkString(" "))
  }

  def getProb(nGramList: List[String], languageModel: CompiledTokenizedLM): Double = {
    val probList = nGramList.map(x => languageModel.log2Estimate(x))
    probList.sum
  }

  def getAllNgramProb(nGramList: List[String], languageModel: CompiledTokenizedLM): List[Double] = {
    val probList = nGramList.map(x => languageModel.log2Estimate(x))
    probList
  }

  def getProbListFiles(compiledTokenizedLM: CompiledTokenizedLM, nGram: Int, numberOfFiles: Int, testListFileName: String, stormedDataPath: String): List[Double] = {
    val testingListOfAllFilesName = new File(testListFileName)
    val testingSet: List[String] = Source.fromFile(testingListOfAllFilesName).getLines().toList.take(numberOfFiles)
    testingSet.map(file => getProb(nGramList(jsonFileToText(file, stormedDataPath), nGram), compiledTokenizedLM))
  }


}
