package inf.usi.ch.crossValidation

import java.io.{BufferedWriter, File, FileWriter}
import java.util.StringTokenizer

import ch.usi.inf.reveal.parsing.artifact.{ArtifactSerializer, StackOverflowArtifact}
import ch.usi.inf.reveal.parsing.units.{CodeTaggedUnit, NaturalLanguageTaggedUnit}
import com.aliasi.lm.CompiledTokenizedLM


import scala.collection.mutable.ListBuffer
import scala.io.Source


/**
  * Created by Talal on 10.03.17.
  */
object CrossValidation  {

  def evalTesting(testingListFileName: String, stormedDataPath: String, codeLM: CompiledTokenizedLM, naturalLM: CompiledTokenizedLM, numberOfFiles: Int): List[Double] = {
    val testingListOfAllFilesName = new File(testingListFileName)
    val testingSet: List[String] = Source.fromFile(testingListOfAllFilesName).getLines().toList.take(numberOfFiles)
    val avgList = testingSet.map(x => artifactAvg(getArtifact(x, stormedDataPath), codeLM, naturalLM))
    avgList
  }

  private def getArtifact(jsonFileName: String, stormedDataPath: String): StackOverflowArtifact = {
    val file = new File(stormedDataPath, jsonFileName)
    ArtifactSerializer.deserializeFromFile(file)
  }


  private def artifactAvg(artifact: StackOverflowArtifact, codeLM: CompiledTokenizedLM, naturalLM: CompiledTokenizedLM): Double = {
    val codeUnits = artifact.units.filter(_.isInstanceOf[CodeTaggedUnit])
    val nlUnits = artifact.units.filter(_.isInstanceOf[NaturalLanguageTaggedUnit])
    val evalCode = codeUnits.map(x => evaluateToken(x.rawText, 3, codeLM)).flatten
    val evalNL = nlUnits.map(x => evaluateToken(x.rawText, 3, naturalLM)).flatten
    val evalUnion = (evalCode.union(evalNL))
    evalUnion.sum / evalUnion.size
  }

  private def evaluateToken(code: String, nGram: Int, languageModel: CompiledTokenizedLM): List[Double] = {
    val listOfNGram = nGramList(code, nGram)
    listOfNGram.map(x => languageModel.log2Estimate(x))
  }

  private def nGramList(token: String, nGram: Int): List[String] = {
    var tokenBuffer = new ListBuffer[String]()
    val stringTokenized = new StringTokenizer(token)
    while (stringTokenized.hasMoreElements()) {
      tokenBuffer += (stringTokenized.nextToken())
    }
    tokenBuffer.toList.sliding(nGram).toList.map(x => x.mkString(" "))
  }

  def writeListToCSVFile(list: List[Double], filePath: String) = {
    val listFile = new File(filePath)
    val listbf = new BufferedWriter(new FileWriter(listFile))
    listbf.write("probability\n")
    list.foreach(x => listbf.write(x.toString + '\n'))
    listbf.close()
  }

}
