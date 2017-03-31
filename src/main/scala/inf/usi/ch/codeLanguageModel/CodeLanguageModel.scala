package inf.usi.ch.codeLanguageModel

import java.io.{File, FileOutputStream, ObjectOutputStream}

import ch.usi.inf.reveal.parsing.artifact.ArtifactSerializer
import ch.usi.inf.reveal.parsing.units.{CodeTaggedUnit, NaturalLanguageTaggedUnit}
import com.aliasi.lm.{CompiledTokenizedLM, TokenizedLM}
import com.aliasi.tokenizer.IndoEuropeanTokenizerFactory
import com.aliasi.util.AbstractExternalizable

import scala.io.Source

/**
  * Created by Talal on 06.03.17.
  */
object CodeLanguageModel {

  private val tokenizerFactory = IndoEuropeanTokenizerFactory.INSTANCE

  //  val codeUnits = artifact.units.filter{_.isInstanceOf[CodeTaggedUnit]}
  //  val nlUnits = artifact.units.filter{_.isInstanceOf[NaturalLanguageTaggedUnit]}


  def train(nGram: Int, trainingSetFilePath: String, stormedDataFolderPath: String, fileNumber : Int): TokenizedLM = {
    val tokenizedLM = new TokenizedLM(tokenizerFactory, nGram)
    val file = new File(trainingSetFilePath)
    val trainingSet = Source.fromFile(file).getLines().toList.take(fileNumber)
    trainingSet.foreach { fileName =>
      val file = new File(stormedDataFolderPath, fileName)
      println(fileName)
      val artifact = ArtifactSerializer.deserializeFromFile(file)
      val codeUnits = (artifact.question.informationUnits ++ artifact.answers.flatMap { _.informationUnits }).filter(_.isInstanceOf[CodeTaggedUnit])
      codeUnits.foreach(x => tokenizedLM.handle(x.rawText))

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


}
