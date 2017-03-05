package inf.usi.ch

import java.io.{File, FileOutputStream, ObjectOutputStream}

import ch.usi.inf.reveal.parsing.artifact.ArtifactSerializer
import com.aliasi.lm.{CompiledTokenizedLM, TokenizedLM}
import com.aliasi.tokenizer.IndoEuropeanTokenizerFactory
import com.aliasi.util.AbstractExternalizable

import scala.io.Source

/**
  * Created by Talal on 03.03.17.
  */
object LanguageModel {

  private val tokenizerFactory = IndoEuropeanTokenizerFactory.INSTANCE

  def train(nGram: Int, trainingSetFilePath: String, stormedDataFolderPath: String): TokenizedLM = {
    val tokenizedLM = new TokenizedLM(tokenizerFactory, nGram)
    val file = new File(trainingSetFilePath)
    val trainingSet = Source.fromFile(file).getLines().toList
    trainingSet.foreach { fileName =>
      val file = new File(stormedDataFolderPath, fileName)
      println(fileName)
      tokenizedLM.handle(jsonArtifact2Text(file))
    }
    tokenizedLM
  }


  private def jsonArtifact2Text(jsonFile: File) = {
    val artifact = ArtifactSerializer.deserializeFromFile(jsonFile)
    artifact.toText
  }

  def serializeTLM(lm : TokenizedLM, fileName : String )={
    val outputStream = new ObjectOutputStream(new FileOutputStream(fileName))
    lm.compileTo(outputStream)
  }

  def deserializeTLM (serializedTLMFile : String ): CompiledTokenizedLM ={
    val file = new File(serializedTLMFile)
    AbstractExternalizable.readObject(file).asInstanceOf[CompiledTokenizedLM]
  }

}
