package inf.usi.ch

import java.io.File

import ch.usi.inf.reveal.parsing.artifact.ArtifactSerializer
import ch.usi.inf.reveal.parsing.units.{InformationUnit, NaturalLanguageTaggedUnit}
import com.aliasi.lm.TokenizedLM
import com.aliasi.tokenizer.IndoEuropeanTokenizerFactory

import scala.io.Source

/**
  * Created by Talal on 30.03.17.
  */
object Test extends App {

  val stormedDataPath = "/Users/Talal/Tesi/stormed-dataset"

  val a = "Switzerland (/ˈswɪtsərlənd/), officially the Swiss Confederation, is a federal republic in Europe. " +
    "It consists of 26 cantons, and the city of Bern is the seat of the federal authorities.[1][2][note 4] " +
    "The country is situated in western-Central Europe,[note 5] and is bordered by Italy to the south, " +
    "France to the west, Germany to the north, and Austria and Liechtenstein to the east. Switzerland is a landlocked country " +
    "geographically divided between the Alps, the Swiss Plateau and the Jura, spanning an area of 41,285 km2 (15,940 sq mi). " +
    "While the Alps occupy the greater part of the territory, the Swiss population of approximately eight million people is " +
    "concentrated mostly on the plateau, " +
    "where the largest cities are to be found: among them are the two global cities and economic centres Zürich and Geneva."

  val stopWords = new File("stopwords.txt")

  val stopWordsList: Seq[String] = Source.fromFile(stopWords).getLines().toList

  //
  //
  //  val tokenizerFactory = new IndoEuropeanTokenizerFactory()
  //  val aCharArray = a.toCharArray
  //  tokenizerFactory.tokenizer(aCharArray, 0, aCharArray.length).tokenize().filter(x => !excludeList.contains(x)).foreach(x => println(x))

  private val tokenizerFactory = IndoEuropeanTokenizerFactory.INSTANCE
  val tokenizedLM = new TokenizedLM(tokenizerFactory, 3)

  val testingFile = new File(stormedDataPath, "123.json")
  val artifact = ArtifactSerializer.deserializeFromFile(testingFile)
  val nlUnits = (artifact.question.informationUnits ++ artifact.answers.flatMap {
    _.informationUnits
  }).filter(_.isInstanceOf[NaturalLanguageTaggedUnit])

  nlUnits.foreach(x => tokenizedLM.handle(x.rawText))

  val list = nlUnits.map(_.rawText)


   val filtered = list.map(x => removeStopWord(x)
  )

//  filtered.foreach(println)

  val text = "You are also missing the function to grab an activity. Again, this is simply what I'm doing in my code and a few examples I saw online"
  println(removeStopWord(text))


  def removeStopWord(text: String) = {
    text.split(Array(',','.',' ',':',';','?','!')).toList.filterNot(x => stopWordsList.contains(x.toLowerCase())).mkString(" ")
  }

}
