package inf.usi.ch.javaLMTokenizer

import java.io.File

import ch.usi.inf.reveal.parsing.artifact.ArtifactSerializer
import ch.usi.inf.reveal.parsing.model.HASTNode
import ch.usi.inf.reveal.parsing.units.CodeTaggedUnit
import com.aliasi.lm.TokenizedLM
import inf.usi.ch.tokenizer.HASTTokenizer

import scala.io.Source

/**
  * Created by Talal on 11.04.17.
  */
class JavaLMEvaluatorTopLeast extends JavaLMEvaluator{

  private def jsonFileToUnitsHASTNodesTuple(fileName: String, stormedDataPath: String): (Seq[(HASTNode, String)]) = {
    val testingFile = new File(stormedDataPath, fileName)
    val artifact = ArtifactSerializer.deserializeFromFile(testingFile)
    val allUnits = artifact.question.informationUnits ++ artifact.answers.flatMap(_.informationUnits)
    val codeUnits = allUnits.filter(_.isInstanceOf[CodeTaggedUnit])
    codeUnits.map(unit => (unit.astNode, fileName))
  }
  def getTopLeastFile(lm: TokenizedLM, nGram: Int, numberOfFiles: Int, testListFileName: String, stormedDataPath: String): Seq[(Double, NGram,String)] = {
    val testingListOfAllFileNames = new File(testListFileName)
    val testingSet: List[String] = Source.fromFile(testingListOfAllFileNames).getLines().take(numberOfFiles).toList
    val listOfUnitsHASTNodesTuple: (Seq[(HASTNode, String)]) = testingSet.flatMap { file => jsonFileToUnitsHASTNodesTuple(file, stormedDataPath) }
    val topLeastList: Seq[(Probability, NGram, String)] = listOfUnitsHASTNodesTuple.flatMap { hASTNode => addHASTNodeToTopLeastList(hASTNode, nGram, lm) }
    val orderedList = scala.util.Sorting.stableSort(topLeastList, (e1: (Double, NGram, String), e2: (Double, NGram, String)) => e1._1 > e2._1).toSeq
    orderedList
  }

  private def addHASTNodeToTopLeastList(hastNodeTuple: (HASTNode, String), nGramLength: Int, lm: TokenizedLM): List[(Probability, NGram, String)] = {
    val tokens: Array[String] = HASTTokenizer.tokenize(hastNodeTuple._1)
    val ngrams: List[NGram] = buildNGrams(tokens, nGramLength)
    val tupleList: List[(Probability, NGram, String)] = ngrams.map { ngram => (computeProbability(ngram, lm), ngram, hastNodeTuple._2) }
    tupleList
  }

}
