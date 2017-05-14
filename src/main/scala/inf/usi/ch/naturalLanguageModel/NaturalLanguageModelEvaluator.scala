package inf.usi.ch.naturalLanguageModel

import java.io.File

import ch.usi.inf.reveal.parsing.artifact.ArtifactSerializer
import ch.usi.inf.reveal.parsing.units.{InformationUnit, NaturalLanguageTaggedUnit}
import com.aliasi.lm.TokenizedLM
import com.aliasi.tokenizer.IndoEuropeanTokenizerFactory

import scala.io.Source


/**
  * Created by Talal on 10.03.17.
  */
class NaturalLanguageModelEvaluator extends NaturalLanguage{


  type Probability = Double

  type Token = String
  type NGram = Array[Token]


  def getProbListFiles(lm: TokenizedLM, nGram: Int, numberOfFiles: Int, testListFileName: String, stormedDataPath: String): Seq[Double] = {
    val testingListOfAllFilesName = new File(testListFileName)
    val testingSet: List[String] = Source.fromFile(testingListOfAllFilesName).getLines().toList.take(numberOfFiles)
    val probList = testingSet.flatMap(file => getProbListForFile(lm,nGram,file,stormedDataPath))
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
    textListWithNoStopWord

  }

  private def getProbListForFile(lm: TokenizedLM, nGram: Int, stormedDataPath: String, fileName: String): Seq[Double] = {
    val listNl: Seq[String] = jsonFileToText(stormedDataPath, fileName)

    val tokenizedList: Seq[Array[Token]] = listNl.map(x => getTokensList(x))

    // at least 3 tokens
    val filterdeTokenizedList = tokenizedList.filter(x => x.size >= nGram)

    val nGramList: Seq[NGram] = filterdeTokenizedList.flatMap(x => buildNGrams(x, nGram))
    val probabilityList: Seq[Probability] = nGramList.map(x => computeProbability(x, lm))
    probabilityList
  }

  private def buildNGrams(tokens: Array[Token], nGramLength: Int): List[NGram] = {
    tokens.sliding(nGramLength).toList
  }

  private def computeProbability(nGram: NGram, lm: TokenizedLM): Probability = {
    lm.processLog2Probability(nGram)
  }

  private def getTokensList(text : String): Array[Token] = {
    val tokenizerFactory = new IndoEuropeanTokenizerFactory()
    val aCharArray = text.toCharArray
    val tokensList=tokenizerFactory.tokenizer(aCharArray, 0, aCharArray.length).tokenize()
    filterTokensList(tokensList)
  }

  private def filterTokensList(tokensList : Array[Token]): Array[Token] ={
    val excludeList = List(".", ",", "(", ")", ";", ":", "!", "?")
    val filteredList = tokensList.filter(x => !excludeList.contains(x))
    filteredList
  }



}
