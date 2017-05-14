package inf.usi.ch.javascript

import java.io.File

import ch.usi.inf.reveal.parsing.model.HASTNode
import com.aliasi.lm.TokenizedLM
import inf.usi.ch.tokenizer.JavascriptANTLRTokenizer

import scala.io.Source

/**
  * Created by Talal on 26.04.17.
  */
class JavascriptCodeEvaluatorTopLeast extends JavascriptCodeEvaluator{


  def getTopLeastFile(lm: TokenizedLM, nGram: Int, folderPath: String): Seq[(Double, NGram)] = {
    val filesList = getListOfFiles(folderPath)
    val probabilityList: Seq[(Probability, NGram)] = filesList.flatMap(file => getProbListAndTokenForFile(lm, nGram, folderPath, file.getName))
    val orderedList = scala.util.Sorting.stableSort(probabilityList, (e1: (Double, NGram), e2: (Double, NGram)) => e1._1 > e2._1).toSeq
    orderedList
  }


  private def getProbListAndTokenForFile(lm: TokenizedLM, nGram : Int ,folderPath: String, fileName: String): (Seq[(Double,NGram)]) = {

    val listCode = getCodeList(folderPath,fileName)

    val tokenizedList: Seq[NGram] = listCode.map(x => new JavascriptANTLRTokenizer(x.toCharArray).tokenize())
    val nGramList = tokenizedList.flatMap(x=>buildNGrams(x,nGram))
    val probabilityList = nGramList.map(x=>computeProbabilityTopLeast(x,lm))
    probabilityList

  }

  private def computeProbabilityTopLeast(ngram: NGram, lm: TokenizedLM): (Probability,NGram) = {
    (lm.processLog2Probability(ngram),ngram)
  }



}
