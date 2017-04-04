package inf.usi.ch


import ANTLRTokenizerFactory.ANTLRTokenizerFactory
import antlr4.JavaLexer
import ch.usi.inf.reveal.parsing.artifact.ArtifactSerializer
import ch.usi.inf.reveal.parsing.model.java.JavaASTNode
import ch.usi.inf.reveal.parsing.model.{HASTNode, HASTNodeSequence, TextFragmentNode}
import ch.usi.inf.reveal.parsing.units.CodeTaggedUnit
import ch.usi.inf.reveal.parsing.model.Implicits._
import com.aliasi.lm.TokenizedLM
import org.antlr.v4.runtime.{ANTLRInputStream, CommonTokenStream}

import scala.collection.mutable.ListBuffer
import scala.util.{Failure, Success, Try}


/**
  * Created by Talal on 30.03.17.
  */
object Test extends App {
  //  val input = "public class HelloWorld {\n    public static void main(String[] args) {\n        System.out.println(\"Hello, World\");\n        //this is a comment\n }}"
  //  val text = "He is going home today"
  //  val nGram = 3;
  //  private val tokenizerFactory = ANTLRTokenizerFactory.INSTANCE
  //  val tokenizedLM = new TokenizedLM(tokenizerFactory, nGram)
  //
  //  //println(tokenizedLM.log2Estimate("is going home"))
  //
  //  val artifact = ArtifactSerializer.deserializeFromFile("/Users/Talal/Tesi/stormed-dataset/123.json")
  //  println(artifact.question.title)
  //
  //  val codeUnits = (artifact.question.informationUnits ++ artifact.answers.flatMap {
  //    _.informationUnits
  //  }).filter(_.isInstanceOf[CodeTaggedUnit])
  //
  //  //val code = artifact.units.filter(_.isInstanceOf[CodeTaggedUnit])
  //
  //  val hastNodeSeq = codeUnits.map(_.astNode);
  //  //  getJavaClass(hastNodeSeq(0))
  //  hastNodeSeq.foreach(x => getJavaClass(x, tokenizedLM))
  //
  //
  //  def getJavaClass(hASTNode: HASTNode, tokenizedLM: TokenizedLM): Unit = hASTNode match {
  //
  //    case nodeSequence: HASTNodeSequence => nodeSequence.fragments.foreach(node => getJavaClass(node, tokenizedLM))
  //
  //    case textNode: TextFragmentNode =>
  //      tokenizerFactory.setStateIsNonJavaCode()
  //      tokenizedLM.handle(textNode.text)
  //
  //    case javaNode: JavaASTNode => val code = Try(javaNode.toCode)
  //      code match {
  //        case Success(a) => println(" Java Node  ")
  //          println(a)
  //          tokenizerFactory.setStateIsJavaCode()
  //          tokenizedLM.handle(a)
  //        case Failure(f) => println("failed" + f)
  //      }
  //
  //    case otherNode : Any =>
  //      tokenizerFactory.setStateIsNonJavaCode()
  //      tokenizedLM.handle(otherNode.toCode)
  //  }


  val string = "public class HelloWorld {\n    public static void main(String[] args) {\n        System.out.println(\"Hello, World\");\n        //this is a comment\n\n    }\n}"

  getNgramListFromANTLR(string)

  def getNgramListFromANTLR(codeString: String) = {
    val lexer = new JavaLexer(new ANTLRInputStream(codeString))
    val tokens = new CommonTokenStream(lexer)
    tokens.fill()
    var tokensBuffer = new ListBuffer[String]()
    var index = 0
    while (index < tokens.size() - 1) {
      tokensBuffer += tokens.get(index).getText
      index = index + 1
    }
    val tokensList = tokensBuffer.toList

    val nGramStringList = tokensList.sliding(3).toList.map(x=>x.mkString(" "))
    println(nGramStringList)

  }


}
