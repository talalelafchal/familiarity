package inf.usi.ch.javascript

import com.aliasi.lm.TokenizedLM
import inf.usi.ch.tokenizer.JavascriptANTLRTokenizer


/**
  * Created by Talal on 02.05.17.
  */
class JavaScriptNLEvaluatorTopLeast extends JavascriptNLEvaluator {
  def getTopLeastFile(lm: TokenizedLM, nGram: Int, folderPath: String): Seq[(Double, NGram)] = {
    val filesList = getListOfFiles(folderPath)
    val probabilityList: Seq[(Probability, NGram)] = filesList.flatMap(file => getProbListAndTokenForFile(lm, nGram, folderPath, file.getName))
    val orderedList: Seq[(Probability, NGram)] = scala.util.Sorting.stableSort(probabilityList, (e1: (Double, NGram), e2: (Double, NGram)) => e1._1 > e2._1).toSeq
    orderedList
  }


  private def getProbListAndTokenForFile(lm: TokenizedLM, nGram: Int, folderPath: String, fileName: String): (Seq[(Double, NGram)]) = {

    val listNl: Seq[String] = getNlList(folderPath, fileName)

    val tokenizedList: Seq[Array[Token]] = listNl.map(x => getTokensList(x))
    // at least 3 tokens
    val filterdeTokenizedList = tokenizedList.filter(x => x.size >= nGram)

    val nGramList: Seq[NGram] = filterdeTokenizedList.flatMap(x => buildNGrams(x, nGram))
    val probabilityList = nGramList.map(x => computeProbabilityTopLeast(x, lm))
    probabilityList

  }

  private def computeProbabilityTopLeast(nGram: NGram, lm: TokenizedLM): (Probability, NGram) = {
    (lm.processLog2Probability(nGram), nGram)
  }
}
