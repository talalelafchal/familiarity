package inf.usi.ch

import java.io.File

import ch.usi.inf.reveal.parsing.model.{HASTNode, HASTNodeSequence, TextFragmentNode}
import ch.usi.inf.reveal.parsing.model.java.JavaASTNode
import com.kennycason.fleschkincaid.FleschKincaid
import inf.usi.ch.javaLMTokenizer.JavaLM
import inf.usi.ch.stormedClientService._
import inf.usi.ch.tokenizer.{HASTTokenizer, UnitTokenizerFactory}
import ch.usi.inf.reveal.parsing.model.Implicits._
import com.aliasi.lm.TokenizedLM
import com.aliasi.tokenizer.IndoEuropeanTokenizerFactory
import inf.usi.ch.javascript.JavascriptCodeNGramCounter
import inf.usi.ch.util.NGramCountXFile
import org.jsoup.Jsoup
import org.jsoup.nodes.{Document, Element}
import org.jsoup.parser.Parser

import scala.io.Source
import scala.util.Try


/**
  * Created by Talal on 30.03.17.
  */
object Test extends App {

  //  val a = new FleschKincaid().calculate(" The score does not have a theoretical lower bound. It is possible to make the score as low as wanted by arbitrarily including words with many syllables. The sentence \"This sentence, taken as a reading passage unto itself, is being used to prove a point.\" has a readability of 74.1.")
  //  val b = new FleschKincaid().calculate("If you love reading these interesting stories for kids, click here and share them with all your young friends. Have an enjoyable time\nRead more at http://www.kidsgen.com/stories/#XyLePwbLlhzV8vHH.99")
  //  val c = new FleschKincaid().calculate("Billy always listens to his mother. He always does what she says. If his mother says, \"Brush your teeth,\" Billy brushes his teeth. If his mother says, \"Go to bed,\" Billy goes to bed. Billy is a very good boy. A good boy listens to his mother. His mother doesn't have to ask him again. She asks him to do something one time, and she doesn't ask again. Billy is a good boy. He does what his mother asks the first time. She doesn't have to ask again. She tells Billy, \"You are my best child.\" Of course Billy is her best child. Billy is her only child.")
  //  println(a)
  //  println(b)
  //  println(c)
  //
//    val readability = raykernel.apps.readability.eval.Main.getReadability("googleMap.addMarker(new MarkerOptions()\n        .position(latLng)\n        .title(\"My Spot\")\n        .snippet(\"This is my spot!\")\n        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));");
//    val b = "googleMap.addMarker(new MarkerOptions()\n.position(latLng)\n.title(\"My Spot\")\n.snippet(\"This is my spot!\")\n.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));"
//    println("formatted  => "  +raykernel.apps.readability.eval.Main.getReadability(b))
//    println("original  => " + readability)


//  val stormedDataPath = "/Users/Talal/Tesi/stormed-dataset"
//  val codeLm = createJavaLM(3, 1000)
//
//  println(codeLm.symbolTable().numSymbols())
//
//  def createJavaLM(nGram: Int, fileNumber: Int): TokenizedLM = {
//    val lm = new JavaLM().train(nGram, "/Users/Talal/Tesi/familiarity/AndroidSets/androidTrainingList.txt", stormedDataPath, fileNumber)
//    lm
//  }


    val postString = Source.fromFile("ExperimentDiscussions2/Android/6068803-Camera.txt").getLines().mkString
    val doc: Document = Jsoup.parse(postString, "", Parser.xmlParser())
    //text
    val text: List[AnyRef] = doc.select("p").toArray().toList
    val textStringList: List[String] = text.map(x =>
      x.asInstanceOf[Element].text())
    val t = textStringList.mkString("\n")
    println(t)



}
