package inf.usi.ch.naturalLanguageModel

import java.io.{File, FileOutputStream, ObjectOutputStream}

import ch.usi.inf.reveal.parsing.artifact.ArtifactSerializer
import ch.usi.inf.reveal.parsing.units.NaturalLanguageTaggedUnit
import com.aliasi.lm.{CompiledTokenizedLM, TokenizedLM}
import com.aliasi.tokenizer.IndoEuropeanTokenizerFactory
import com.aliasi.util.AbstractExternalizable

import scala.io.Source

/**
  * Created by Talal on 10.03.17.
  */
object NaturalLanguageModel {

  private val tokenizerFactory = IndoEuropeanTokenizerFactory.INSTANCE


  def train(nGram: Int, trainingSetFilePath: String, stormedDataFolderPath: String): TokenizedLM = {
    val tokenizedLM = new TokenizedLM(tokenizerFactory, nGram)
    val file = new File(trainingSetFilePath)
    val trainingSet = Source.fromFile(file).getLines().toList
    trainingSet.foreach { fileName =>
      val file = new File(stormedDataFolderPath, fileName)
      println(fileName)
      val artifact = ArtifactSerializer.deserializeFromFile(file)
      val nlUnits = artifact.units.filter(_.isInstanceOf[NaturalLanguageTaggedUnit])
      nlUnits.foreach(x => tokenizedLM.handle(x.rawText))

    }
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
