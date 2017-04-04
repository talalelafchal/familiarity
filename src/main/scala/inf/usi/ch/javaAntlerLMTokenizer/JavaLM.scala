package inf.usi.ch.javaAntlerLMTokenizer

import java.io.{File, FileOutputStream, ObjectOutputStream}

import ANTLRTokenizerFactory.ANTLRTokenizerFactory
import ch.usi.inf.reveal.parsing.artifact.ArtifactSerializer
import ch.usi.inf.reveal.parsing.model.{HASTNode, HASTNodeSequence, TextFragmentNode}
import ch.usi.inf.reveal.parsing.units.CodeTaggedUnit
import com.aliasi.lm.{CompiledTokenizedLM, TokenizedLM}
import ch.usi.inf.reveal.parsing.model.Implicits._
import ch.usi.inf.reveal.parsing.model.java.JavaASTNode
import com.aliasi.util.AbstractExternalizable

import scala.io.Source
import scala.util.{Failure, Success, Try}

/**
  * Created by Talal on 03.04.17.
  */
object JavaLM {

  private val tokenizerFactory = ANTLRTokenizerFactory.INSTANCE


  def train(nGram: Int, trainingSetFilePath: String, stormedDataFolderPath: String, fileNumber: Int): TokenizedLM = {
    val tokenizedLM = new TokenizedLM(tokenizerFactory, nGram)
    val file = new File(trainingSetFilePath)
    val trainingSet = Source.fromFile(file).getLines().toList.take(fileNumber)
    // train on each file
    trainingSet.foreach { fileName =>
      val file = new File(stormedDataFolderPath, fileName)
      println(fileName)
      val artifact = ArtifactSerializer.deserializeFromFile(file)
      // get code units
      val codeUnits = (artifact.question.informationUnits ++ artifact.answers.flatMap {
        _.informationUnits
      }).filter(_.isInstanceOf[CodeTaggedUnit])

      val hastNodeSeq = codeUnits.map(_.astNode)
      //  train JavaCode
      hastNodeSeq.foreach(x => trainJavaCode(x, tokenizedLM))


    }
    println(trainingSet.size)
    tokenizedLM
  }


  def serializeTLM(lm: TokenizedLM, fileName: String) = {
    val outputStream = new ObjectOutputStream(new FileOutputStream(fileName))
    lm.compileTo(outputStream)
  }

  def deserializeTLM(serializedTLMFile: String): CompiledTokenizedLM = {
    val file = new File(serializedTLMFile)
    AbstractExternalizable.readObject(file).asInstanceOf[CompiledTokenizedLM]
  }


  private def trainJavaCode(hASTNode: HASTNode, tokenizedLM: TokenizedLM): Unit = hASTNode match {

    case nodeSequence: HASTNodeSequence => nodeSequence.fragments.foreach(node => trainJavaCode(node, tokenizedLM))

    case textNode: TextFragmentNode =>
      tokenizerFactory.setStateIsNonJavaCode()
      tokenizedLM.handle(textNode.text)

    case javaNode: JavaASTNode => val code = Try(javaNode.toCode)
      code match {
        case Success(a) =>
          tokenizerFactory.setStateIsJavaCode()
          tokenizedLM.handle(a)
        case Failure(f) => println(" check failed JavaNode" + f)
      }

    case otherNode: Any => val code = Try(otherNode.toCode)
      code match {
        case Success(s) =>
          tokenizerFactory.setStateIsNonJavaCode()
          tokenizedLM.handle(s)
        case Failure(f) => println(" check failed otherNode" + f)
      }
  }

}
