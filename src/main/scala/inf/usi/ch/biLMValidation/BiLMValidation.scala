package inf.usi.ch.biLMValidation

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
object BiLMValidation{

  def evalTesting(testingListFileName: String, stormedDataPath: String, codeLM: CompiledTokenizedLM, naturalLM: CompiledTokenizedLM, numberOfFiles: Int): List[Double] = {
    val testingListOfAllFilesName = new File(testingListFileName)
    val testingSet: List[String] = Source.fromFile(testingListOfAllFilesName).getLines().toList.take(numberOfFiles)
    val probList = testingSet.map(fileName => artifactProb(getArtifact(fileName, stormedDataPath), codeLM, naturalLM))
    probList
  }

  private def getArtifact(jsonFileName: String, stormedDataPath: String): StackOverflowArtifact = {
    val file = new File(stormedDataPath, jsonFileName)
    ArtifactSerializer.deserializeFromFile(file)
  }


  private def artifactProb(artifact: StackOverflowArtifact, codeLM: CompiledTokenizedLM, naturalLM: CompiledTokenizedLM): Double = {
    val codeUnits = artifact.units.filter(_.isInstanceOf[CodeTaggedUnit])
    val nlUnits = artifact.units.filter(_.isInstanceOf[NaturalLanguageTaggedUnit])
    val evalCode = codeUnits.map(x => evaluateToken(x.rawText, 3, codeLM)).sum
    val evalNL = nlUnits.map(x => evaluateToken(x.rawText, 3, naturalLM)).sum
    evalCode + evalNL
  }

  private def evaluateToken(code: String, nGram: Int, languageModel: CompiledTokenizedLM): Double = {
    val listOfNGram = nGramList(code, nGram)
    listOfNGram.map(x => languageModel.log2Estimate(x)).sum
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


  //AllNgram

  def evalAllNGramTesting(testingListFileName: String, stormedDataPath: String, codeLM: CompiledTokenizedLM, naturalLM: CompiledTokenizedLM, numberOfFiles: Int): List[Double] = {
    val testingListOfAllFilesName = new File(testingListFileName)
    val testingSet: List[String] = Source.fromFile(testingListOfAllFilesName).getLines().toList.take(numberOfFiles)
    val probList = testingSet.map(fileName => artifactAllNGramProb(getArtifact(fileName, stormedDataPath), codeLM, naturalLM))
    probList.flatten
  }



  private def evaluateAllNGramToken(code: String, nGram: Int, languageModel: CompiledTokenizedLM): List[Double] = {
    val listOfNGram = nGramList(code, nGram)
    listOfNGram.map(x => languageModel.log2Estimate(x))
  }

  private def artifactAllNGramProb(artifact: StackOverflowArtifact, codeLM: CompiledTokenizedLM, naturalLM: CompiledTokenizedLM): List[Double] = {
    val codeUnits = artifact.units.filter(_.isInstanceOf[CodeTaggedUnit])
    val nlUnits = artifact.units.filter(_.isInstanceOf[NaturalLanguageTaggedUnit])
    val evalCode: List[Double] = codeUnits.map(x => evaluateAllNGramToken(x.rawText, 3, codeLM)).flatten.toList
    val evalNL: List[Double] = nlUnits.map(x => evaluateAllNGramToken(x.rawText, 3, naturalLM)).flatten.toList
    evalCode.union(evalNL)
  }

  //CodeNGram
  def evalAllCodeNGramTesting(testingListFileName: String, stormedDataPath: String, codeLM: CompiledTokenizedLM,numberOfFiles: Int): List[Double] = {
    val testingListOfAllFilesName = new File(testingListFileName)
    val testingSet: List[String] = Source.fromFile(testingListOfAllFilesName).getLines().toList.take(numberOfFiles)
    val probList = testingSet.map(fileName => artifactAllCodeNGramProb(getArtifact(fileName, stormedDataPath), codeLM))
    probList.flatten
  }



  private def evaluateAllCodeNGramToken(code: String, nGram: Int, languageModel: CompiledTokenizedLM): List[Double] = {
    val listOfNGram = nGramList(code, nGram)
    listOfNGram.map(x => languageModel.log2Estimate(x))
  }

  private def artifactAllCodeNGramProb(artifact: StackOverflowArtifact, codeLM: CompiledTokenizedLM): List[Double] = {
    val codeUnits = artifact.units.filter(_.isInstanceOf[CodeTaggedUnit])
    val evalCode: List[Double] = codeUnits.map(x => evaluateAllCodeNGramToken(x.rawText, 3, codeLM)).flatten.toList
    evalCode
  }

  //NlNGram

  def evalAllNLNGramTesting(testingListFileName: String, stormedDataPath: String,naturalLM: CompiledTokenizedLM, numberOfFiles: Int): List[Double] = {
    val testingListOfAllFilesName = new File(testingListFileName)
    val testingSet: List[String] = Source.fromFile(testingListOfAllFilesName).getLines().toList.take(numberOfFiles)
    val probList = testingSet.map(fileName => artifactAllNLNGramProb(getArtifact(fileName, stormedDataPath), naturalLM))
    probList.flatten
  }



  private def evaluateAllNLNGramToken(code: String, nGram: Int, languageModel: CompiledTokenizedLM): List[Double] = {
    val listOfNGram = nGramList(code, nGram)
    listOfNGram.map(x => languageModel.log2Estimate(x))
  }

  private def artifactAllNLNGramProb(artifact: StackOverflowArtifact,naturalLM: CompiledTokenizedLM): List[Double] = {
    val nlUnits = artifact.units.filter(_.isInstanceOf[NaturalLanguageTaggedUnit])
    val evalNL: List[Double] = nlUnits.map(x => evaluateAllNLNGramToken(x.rawText, 3, naturalLM)).flatten.toList
    evalNL
  }



}
