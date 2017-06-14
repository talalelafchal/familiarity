package inf.usi.ch.javaLMTokenizer

import java.io.File

import ch.usi.inf.reveal.parsing.model.HASTNode
import inf.usi.ch.tokenizer.HASTTokenizer
import inf.usi.ch.util.NGramCountXFile

import scala.io.Source

/**
  * Created by Talal on 22.05.17.
  */
class JavaNGramCounter extends JavaLMEvaluator {


  private def getNGram(hastNodeSeq: Seq[HASTNode], nGram: Int) = {

    val tokensSeq: Seq[Array[String]] = hastNodeSeq.map(hastNode => HASTTokenizer.tokenize(hastNode))
    val nGramsSeq: Seq[List[NGram]] = tokensSeq.map(token => buildNGrams(token, nGram))

    val nGramsCount = nGramsSeq.flatten.length
    nGramsCount
  }



  def getNGramCount(language: String, testListFileName: String, numberOfFiles: Integer, stormedDataPath: String, nGram: Int): Seq[NGramCountXFile] = {
    val testingListOfAllFileNames = new File(testListFileName)
    val testingSet: List[String] = Source.fromFile(testingListOfAllFileNames).getLines().take(numberOfFiles).toList
    val listOfUnitsHASTNodes: Seq[(Seq[HASTNode], String)] = testingSet.map(file => (jsonFileToUnitsHASTNodes(file, stormedDataPath), file))

    val nGramCount: Seq[NGramCountXFile] = listOfUnitsHASTNodes.map { hASTNodeSeq => NGramCountXFile(language,hASTNodeSeq._2, getNGram(hASTNodeSeq._1, nGram)) }
    nGramCount


  }


  def getNGramCount(language: String, testListFileName: String, stormedDataPath: String, nGram: Int): Seq[NGramCountXFile] = {
    val testingListOfAllFileNames = new File(testListFileName)
    val testingSet: List[String] = Source.fromFile(testingListOfAllFileNames).getLines().toList
    val listOfUnitsHASTNodes: Seq[(Seq[HASTNode], String)] = testingSet.map(file => (jsonFileToUnitsHASTNodes(file, stormedDataPath), file))

    val nGramCount: Seq[NGramCountXFile] = listOfUnitsHASTNodes.map { hASTNodeSeq => NGramCountXFile(language,hASTNodeSeq._2, getNGram(hASTNodeSeq._1, nGram)) }
    nGramCount


  }

}
