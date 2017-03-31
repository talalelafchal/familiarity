package inf.usi.ch


import ANTLRTokenizerFactory.ANTLRTokenizerFactory
import ch.usi.inf.reveal.parsing.artifact.ArtifactSerializer
import ch.usi.inf.reveal.parsing.model.HASTNodeSequence
import ch.usi.inf.reveal.parsing.units.CodeTaggedUnit
import com.aliasi.lm.TokenizedLM



/**
  * Created by Talal on 30.03.17.
  */
object Test extends App {
  val input = "public class HelloWorld {\n    public static void main(String[] args) {\n        System.out.println(\"Hello, World\");\n        //this is a comment\n }}"
  val text = "He is going home today"
  val nGram = 3;
  private val tokenizerFactory = ANTLRTokenizerFactory.INSTANCE
  val tokenizedLM = new TokenizedLM(tokenizerFactory, nGram)
  tokenizedLM.handle(input)
  //println(tokenizedLM.log2Estimate("is going home"))

  val artifact = ArtifactSerializer.deserializeFromFile("/Users/Talal/Tesi/stormed-dataset/123.json")
  val code = artifact.units.filter(_.isInstanceOf[CodeTaggedUnit])
  val informationUnit : Seq[HASTNodeSequence]= code.filter(_.astNode.isInstanceOf[HASTNodeSequence])


}
