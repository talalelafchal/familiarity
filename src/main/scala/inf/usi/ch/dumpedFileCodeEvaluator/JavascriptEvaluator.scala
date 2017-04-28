package inf.usi.ch.dumpedFileCodeEvaluator

import java.io.File

import com.aliasi.lm.TokenizedLM

/**
  * Created by Talal on 27.04.17.
  */
trait JavascriptEvaluator {


  type Probability = Double

  type Token = String
  type NGram = Array[Token]

  protected def getListOfFiles(dir: String): List[File] = {
    val d = new File(dir)
    if (d.exists && d.isDirectory) {
      d.listFiles.filter(_.isFile).toList
    } else {
      List[File]()
    }
  }
  protected def buildNGrams(tokens: Array[Token], nGramLength: Int): List[NGram] = {
    tokens.sliding(nGramLength).toList
  }

  protected def computeProbability(nGram: NGram, lm: TokenizedLM): Probability = {
    lm.processLog2Probability(nGram)
  }
}
