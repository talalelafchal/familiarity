package inf.usi.ch

import java.io.{BufferedWriter, File, FileWriter}
import java.util.StringTokenizer

import ch.usi.inf.reveal.parsing.artifact.ArtifactSerializer
import com.aliasi.lm.CompiledTokenizedLM

import scala.collection.mutable.ListBuffer
import scala.io.Source

/**
  * Created by Talal on 03.03.17.
  */
object LanguageModelEvaluator {

  def listLog2Probability(compiledTokenizedLM: CompiledTokenizedLM, testListFileName: String, stormedDataPath: String): List[Double] = {
    val testingListOfAllFilesName = new File(testListFileName)
    val testingSet: List[String] = Source.fromFile(testingListOfAllFilesName).getLines().toList
    testingSet.map(x => getLogProbability(compiledTokenizedLM, jsonFileToText(x, stormedDataPath)))
  }


  private def jsonFileToText(fileName: String, stormedDataPath: String): String = {
    val testingFile = new File(stormedDataPath, fileName)
    val fileArtifact = ArtifactSerializer.deserializeFromFile(testingFile)
    val artifactText = fileArtifact.toText
    artifactText
  }

  def writeListToCSVFile(list: List[Double], filePath: String) = {
    val listFile = new File(filePath)
    val listbf = new BufferedWriter(new FileWriter(listFile))
    listbf.write("probability\n")
    list.foreach(x => listbf.write(x.toString + '\n'))
    listbf.close()
  }


  private def getLogProbability(compiledTokenizedLM: CompiledTokenizedLM, text: String): Double = {
    compiledTokenizedLM.log2Estimate(text)
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

  def getProbListFiles(compiledTokenizedLM: CompiledTokenizedLM, nGram: Int, numberOfFiles : Int, testListFileName: String, stormedDataPath: String): List[Double] = {
    val testingListOfAllFilesName = new File(testListFileName)
    val testingSet: List[String] = Source.fromFile(testingListOfAllFilesName).getLines().toList.take(numberOfFiles)
    testingSet.map(file => getProb(nGramList(jsonFileToText(file, stormedDataPath), nGram), compiledTokenizedLM))
  }


}
