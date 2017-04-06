package inf.usi.ch

import java.io.File

import ANTLRTokenizerFactory.ANTLRTokenizerFactory
import ch.usi.inf.reveal.parsing.artifact.ArtifactSerializer
import ch.usi.inf.reveal.parsing.model.java.JavaASTNode
import ch.usi.inf.reveal.parsing.model.{HASTNode, HASTNodeSequence, TextFragmentNode}
import ch.usi.inf.reveal.parsing.units.CodeTaggedUnit
import com.aliasi.lm.TokenizedLM
import ch.usi.inf.reveal.parsing.model.Implicits._
import scala.util.{Failure, Success, Try}


/**
  * Created by Talal on 30.03.17.
  */
object Test extends App {

  val stormedDataPath = "/Users/Talal/Tesi/stormed-dataset"
  val tokenizerFactory = ANTLRTokenizerFactory.INSTANCE

  val tokenizedLM = new TokenizedLM(tokenizerFactory, 3)


  val file = new File(stormedDataPath, "123.json")

  val artifact = ArtifactSerializer.deserializeFromFile(file)
  println(artifact.question.title)



    // get code units
    val codeUnits = (artifact.question.informationUnits ++ artifact.answers.flatMap {
      _.informationUnits
    }).filter(_.isInstanceOf[CodeTaggedUnit])
    // map each units to HASTNode
    val hastNodeSeq = codeUnits.map(_.astNode)
    //  train JavaCode
    hastNodeSeq.foreach(x => trainJavaCode(x, tokenizedLM))




  private def trainJavaCode(hASTNode: HASTNode, tokenizedLM: TokenizedLM): Unit = hASTNode match {

    case nodeSequence: HASTNodeSequence => nodeSequence.fragments.foreach(node => trainJavaCode(node, tokenizedLM))

    case textNode: TextFragmentNode =>
      tokenizerFactory.setStateIsNonJavaCode()
      tokenizedLM.handle(textNode.text)

    case javaNode: JavaASTNode => val code = Try(javaNode.toCode)
      code match {
        case Success(javaCode) =>
          println(javaCode)
          println("-"*50)
          tokenizerFactory.setStateIsJavaCode()
          tokenizedLM.handle(javaCode)
        case Failure(f) => println(" failed JavaNode" + f)
      }

    case otherNode: HASTNode => val code = Try(otherNode.toCode)
      code match {
        case Success(s) =>
          tokenizerFactory.setStateIsNonJavaCode()
          tokenizedLM.handle(s)
        case Failure(f) => println(" failed otherNode" + f)
      }
  }

}
