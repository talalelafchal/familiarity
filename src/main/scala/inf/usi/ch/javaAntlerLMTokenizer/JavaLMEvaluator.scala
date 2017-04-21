package inf.usi.ch.javaAntlerLMTokenizer

import java.io.{BufferedWriter, File, FileWriter}

import antlr4.JavaLexer
import ch.usi.inf.reveal.parsing.artifact.ArtifactSerializer
import ch.usi.inf.reveal.parsing.model.{HASTNode}
import ch.usi.inf.reveal.parsing.units.CodeTaggedUnit
import com.aliasi.lm.TokenizedLM
import inf.usi.ch.tokenizer.{HASTTokenizer}
import org.antlr.v4.runtime.{ANTLRInputStream, CommonTokenStream}

import scala.collection.mutable.ListBuffer
import scala.io.Source

/**
  * Created by Talal on 03.04.17.
  */
class JavaLMEvaluator {

  type Probability = Double

  type Token = String
  type NGram = Array[Token]


  private def jsonFileToUnitsHASTNodes(fileName: String, stormedDataPath: String): Seq[HASTNode] = {
    val testingFile = new File(stormedDataPath, fileName)
    val artifact = ArtifactSerializer.deserializeFromFile(testingFile)

    val allUnits = artifact.question.informationUnits ++ artifact.answers.flatMap(_.informationUnits)
    val codeUnits = allUnits.filter(_.isInstanceOf[CodeTaggedUnit])
    codeUnits.map(_.astNode)
  }




  def writeListToCSVFile(list: List[Double], filePath: String) = {
    val listFile = new File(filePath)
    val listbf = new BufferedWriter(new FileWriter(listFile))
    listbf.write("probability\n")
    list.foreach(x => listbf.write(x.toString + '\n'))
    listbf.close()
  }


  protected def buildNGrams(tokens: Array[Token], nGramLength: Int): List[NGram] = {
    tokens.sliding(nGramLength).toList
  }

  protected def computeProbability(ngram: NGram, lm: TokenizedLM): Probability = {
    lm.processLog2Probability(ngram)
  }

  private def addHASTNodeProbToList(hastNode: HASTNode, nGramLength: Int, lm: TokenizedLM): List[Probability] = {

    val tokens: Array[String] = HASTTokenizer.tokenize(hastNode)
    val ngrams: List[NGram] = buildNGrams(tokens, nGramLength)
    val probabilityList: List[Probability] = ngrams.map { ngram => computeProbability(ngram, lm) }

    probabilityList
  }

  def getProbListFiles(lm: TokenizedLM, nGram: Int, numberOfFiles: Int, testListFileName: String, stormedDataPath: String): Seq[Double] = {
    val testingListOfAllFileNames = new File(testListFileName)
    val testingSet: List[String] = Source.fromFile(testingListOfAllFileNames).getLines().take(numberOfFiles).toList
    val listOfUnitsHASTNodes: Seq[HASTNode] = testingSet.flatMap { file => jsonFileToUnitsHASTNodes(file, stormedDataPath) }
    val ngramProbabilities: Seq[Probability] = listOfUnitsHASTNodes.flatMap { hASTNode => addHASTNodeProbToList(hASTNode, nGram, lm) }

    ngramProbabilities
  }




}
