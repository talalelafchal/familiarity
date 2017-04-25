package inf.usi.ch

import java.io.File

import antlr4JavaScript.ECMAScriptLexer
import com.aliasi.lm.TokenizedLM
import inf.usi.ch.codeLanguageModel.CodeLanguageModelEvaluator
import inf.usi.ch.javaAntlerLMTokenizer.JavaLM
import inf.usi.ch.tokenizer.{JavaANTLRTokenizer, JavascriptANTLRTokenizer}
import org.antlr.v4.runtime.{ANTLRInputStream, CommonTokenStream}
import org.jsoup.Jsoup
import org.jsoup.parser.Parser
import org.jsoup.select.Elements

import scala.io.Source


/**
  * Created by Talal on 30.03.17.
  */
object Test extends App {

  type Probability = Double

  type Token = String
  type NGram = Array[Token]


  val stormedDataPath = "/Users/Talal/Tesi/stormed-dataset"

  def createJavaLM(nGram: Int, fileNumber: Int): TokenizedLM = {
    val lm = JavaLM.train(nGram, "/Users/Talal/Tesi/familiarity/AndroidSets/androidTrainingList.txt", stormedDataPath, fileNumber)
    lm
  }


  val lm = createJavaLM(3,1000)

  val file = new File("JavascriptFiles", "1793845.txt")
  val postString = Source.fromFile(file).getLines().mkString
  val doc = Jsoup.parse(postString, "", Parser.xmlParser())

  //Code
  val code: Elements = doc.select(">code")
  val codeIterator = code.iterator()
  while (codeIterator.hasNext) {
    val code = codeIterator.next().text()
    println("code : " + code)
  }

  //PreCode
  val preCode: Elements = doc.select(">pre")
  val preCodeIterator = preCode.iterator()
  while (preCodeIterator.hasNext) {
    val code = preCodeIterator.next().text().replaceAll("[^a-zA-Z0-9 ]", " ")

    val tokens = new JavascriptANTLRTokenizer(code.toCharArray).tokenize()
    val ngramsList = buildNGrams(tokens,3)
    val probabilityList = ngramsList.map(x => computeProbability(x,lm))
    println(probabilityList)


  }

  protected def buildNGrams(tokens: Array[Token], nGramLength: Int): List[NGram] = {
    tokens.sliding(nGramLength).toList
  }

  protected def computeProbability(ngram: NGram, lm: TokenizedLM): Probability = {
    lm.processLog2Probability(ngram)
  }


}
