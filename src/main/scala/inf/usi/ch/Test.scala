package inf.usi.ch

import java.io.File

import ch.usi.inf.reveal.parsing.artifact.ArtifactSerializer
import ch.usi.inf.reveal.parsing.model.HASTNode
import ch.usi.inf.reveal.parsing.units.CodeTaggedUnit
import com.aliasi.lm.TokenizedLM
import ch.usi.inf.reveal.parsing.model.Implicits._
import inf.usi.ch.javaAntlerLMTokenizer.JavaLMEvaluator
import inf.usi.ch.tokenizer.{HASTTokenizer, UnitTokenizerFactory}

/**
  * Created by Talal on 30.03.17.
  */
object Test extends App {

  val stormedDataPath = "/Users/Talal/Tesi/stormed-dataset"
  val tokenizerFactory = UnitTokenizerFactory.INSTANCE

  val tokenizedLM = new TokenizedLM(tokenizerFactory, 3)



  val file = new File(stormedDataPath, "123.json")

  val artifact = ArtifactSerializer.deserializeFromFile(file)
  println(s"${artifact.id}:${artifact.question.title}")


  // get code units
      val codeUnits = (artifact.question.informationUnits ++ artifact.answers.flatMap {
        _.informationUnits
      }).filter(_.isInstanceOf[CodeTaggedUnit])


  // map each units to HASTNode
  val hastNodeSeq = codeUnits.map(_.astNode)

  //  train JavaCode
   val lm = hastNodeSeq.foreach(x => trainJavaCode(x, tokenizedLM))


  private def trainJavaCode(hASTNode: HASTNode, tokenizedLM: TokenizedLM): Unit = {
    val tokens = HASTTokenizer.tokenize(hASTNode)
    println("tokens created")
    println(tokens.mkString("", " ", ""))
    val string = hASTNode.toCode
    trainModel(tokens, string, tokenizedLM)
  }


  private def trainModel(tokens: Array[String], cs: CharSequence, tokenizedLM: TokenizedLM) = {
    tokenizerFactory.setTokens(tokens)
    tokenizedLM.handle(cs)
  }

}
