package inf.usi.ch.javaLMTokenizer

import java.io.File

import ch.usi.inf.reveal.parsing.model.HASTNode
import inf.usi.ch.tokenizer.HASTTokenizer

import scala.io.Source

/**
  * Created by Talal on 22.05.17.
  */
class JavaNGramCounter extends JavaLMEvaluator {


  private def getNgram(hastNodeSeq: Seq[HASTNode], nGram: Int) = {

    val tokensSeq: Seq[Array[String]] = hastNodeSeq.map(hastNode => HASTTokenizer.tokenize(hastNode))
    val ngramsSeq: Seq[List[NGram]] = tokensSeq.map(token => buildNGrams(token, nGram))

    val nGramsCount = ngramsSeq.flatten.length
    nGramsCount
  }

  def getNGramCount(testListFileName: String, numberOfFiles: Integer, stormedDataPath: String, nGram: Int): Seq[Int] = {
    val testingListOfAllFileNames = new File(testListFileName)
    val testingSet: List[String] = Source.fromFile(testingListOfAllFileNames).getLines().take(numberOfFiles).toList
    val listOfUnitsHASTNodes: Seq[Seq[HASTNode]] = testingSet.map(file => jsonFileToUnitsHASTNodes(file, stormedDataPath))

    val nGramCount: Seq[Int] = listOfUnitsHASTNodes.map { hASTNodeSeq => getNgram(hASTNodeSeq, nGram) }
    nGramCount


  }

}
