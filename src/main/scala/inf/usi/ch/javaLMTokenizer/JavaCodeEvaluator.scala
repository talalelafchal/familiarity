package inf.usi.ch.javaLMTokenizer

import ch.usi.inf.reveal.parsing.model.HASTNode
import com.aliasi.lm.TokenizedLM
import inf.usi.ch.tokenizer.HASTTokenizer

/**
  * Created by Talal on 12.06.17.
  */
trait JavaCodeEvaluator {
  type Probability = Double

  type Token = String
  type NGram = Array[Token]

  protected def buildNGrams(tokens: Array[Token], nGramLength: Int): List[NGram] = {
    tokens.sliding(nGramLength).toList
  }

  protected def computeProbability(nGram: NGram, lm: TokenizedLM): Probability = {

    val familiarity = lm.processLog2Probability(nGram)
    //println( nGram.foreach(x => print(" "+x+" "))  + "  -> " + familiarity)
    familiarity
  }

  protected def addHASTNodeProbToList(hastNode: HASTNode, nGramLength: Int, lm: TokenizedLM): List[Probability] = {

    val tokens: Array[String] = HASTTokenizer.tokenize(hastNode)
    val nGrams: List[NGram] = buildNGrams(tokens, nGramLength)
    val probabilityList: List[Probability] = nGrams.map { ngram => computeProbability(ngram, lm) }

    probabilityList
  }

}
