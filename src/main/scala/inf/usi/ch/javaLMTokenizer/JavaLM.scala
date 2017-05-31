package inf.usi.ch.javaLMTokenizer

import java.io.{File, FileOutputStream, ObjectOutputStream}

import ch.usi.inf.reveal.parsing.artifact.ArtifactSerializer
import ch.usi.inf.reveal.parsing.model.{HASTNode, HASTNodeSequence, TextFragmentNode}
import ch.usi.inf.reveal.parsing.units.CodeTaggedUnit
import com.aliasi.lm.{CompiledTokenizedLM, TokenizedLM}
import ch.usi.inf.reveal.parsing.model.Implicits._
import ch.usi.inf.reveal.parsing.model.java.JavaASTNode
import com.aliasi.tokenizer.IndoEuropeanTokenizerFactory
import com.aliasi.util.AbstractExternalizable
import inf.usi.ch.tokenizer.{JavaANTLRTokenizer, HASTTokenizer, UnitTokenizerFactory}

import scala.io.Source
import scala.util.{Failure, Success, Try}

/**
  * Created by Talal on 03.04.17.
  */
class JavaLM {
  private val DEFAULT_TOKENIZER_FACTORY = IndoEuropeanTokenizerFactory.INSTANCE

  private val tokenizerFactory = UnitTokenizerFactory.INSTANCE

  def train(nGram: Int, trainingSetFilePath: String, stormedDataFolderPath: String, fileNumber: Int): TokenizedLM = {
    val tokenizedLM = new TokenizedLM(tokenizerFactory, nGram)
    val file = new File(trainingSetFilePath)
    val trainingSet = Source.fromFile(file).getLines().toList.take(fileNumber)
    // train on each file
    trainingSet.foreach { fileName =>
      val file = new File(stormedDataFolderPath, fileName)
      val artifact = ArtifactSerializer.deserializeFromFile(file)
      // get code units
      val codeUnits = (artifact.question.informationUnits ++ artifact.answers.flatMap {
        _.informationUnits
      }).filter(_.isInstanceOf[CodeTaggedUnit])
      // map each units to HASTNode
      val hastNodeSeq = codeUnits.map(_.astNode)
      //  train JavaCode
      hastNodeSeq.foreach(x => trainJavaCode(x, tokenizedLM))

    }
    println("training set size : " + trainingSet.size)
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

  private def trainJavaCode(hASTNode: HASTNode, tokenizedLM: TokenizedLM): Unit = {
    val tokens = HASTTokenizer.tokenize(hASTNode)
    val string = hASTNode.toCode
    trainModel(tokens, string, tokenizedLM)
  }


  private def trainModel(tokens: Array[String], cs: CharSequence, tokenizedLM: TokenizedLM) = {
    tokenizerFactory.setTokens(tokens)
    tokenizedLM.handle(cs)
  }

}
