package inf.usi.ch

import java.io.File

import ch.usi.inf.reveal.parsing.model.java.JavaASTNode
import ch.usi.inf.reveal.parsing.model.{HASTNode, HASTNodeSequence, TextFragmentNode}
import com.aliasi.lm.TokenizedLM
import com.aliasi.tokenizer.IndoEuropeanTokenizerFactory
import inf.usi.ch.stormedClientService._
import inf.usi.ch.tokenizer.UnitTokenizerFactory
import ch.usi.inf.reveal.parsing.model.Implicits._
import scala.io.Source
import scala.util.Try

/**
  * Created by Talal on 09.06.17.
  */
object experimentSetup extends App {
  private val key = "B8DBDD69F4612953166D624A69DCEDAB344C315CA2D2383725BD08661C6B7183"
  private val nlTokenizerFactory = IndoEuropeanTokenizerFactory.INSTANCE
  private val codeTokenizerFactory = UnitTokenizerFactory.INSTANCE

  private val nl = new TokenizedLM(nlTokenizerFactory, 3)
  private val code = new TokenizedLM(codeTokenizerFactory,3)


  val tutorialFiles = getListOfFiles("tutorial")

  tutorialFiles.foreach( file => trainFile(file.getPath))

  println("training done")


  def trainFile(tutorialFile : String) = {
    val file = Source.fromFile(tutorialFile)
    val codeToParse = file.mkString.trim
    val result: Response = StormedService.parse(codeToParse, key)
    val astNodeResult: Seq[HASTNode] = getHASTNode(result)
    astNodeResult.foreach(tokenizeAndTrain(_))
  }

  def getListOfFiles(dir: String):List[File] = {
    val d = new File(dir)
    if (d.exists && d.isDirectory) {
      d.listFiles.filter(_.isFile).toList
    } else {
      List[File]()
    }
  }

  def getHASTNode(result: Response): Seq[HASTNode] = {
    result match {
      case ParsingResponse(result, quota, status) =>
        val nodeTypes: Seq[HASTNode] = result
        nodeTypes
      case ErrorResponse(message, status) =>
        println(status + ": " + message)
        Seq()
    }

  }




  def tokenizeAndTrain(hASTNode: HASTNode): Unit = {

    hASTNode match {
      case nodeSequence: HASTNodeSequence => {
        nodeSequence.fragments.foreach(x => tokenizeAndTrain(x))
      }

      case textNode: TextFragmentNode => {
        val text = textNode.text
        ServiceNL.train(nl,text)

      }

      case javaNode: JavaASTNode => {
        ServiceJavaLM.train(code,javaNode)
      }

      case _ => {
        val defaultCode = Try(hASTNode.toCode).get
        ServiceNL.train(nl,defaultCode)
      }

    }

  }



}
