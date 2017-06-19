package inf.usi.ch

import java.io.File

import ch.usi.inf.reveal.parsing.model.java.JavaASTNode
import ch.usi.inf.reveal.parsing.model.{HASTNode, HASTNodeSequence, TextFragmentNode}
import com.aliasi.lm.TokenizedLM
import com.aliasi.tokenizer.IndoEuropeanTokenizerFactory
import inf.usi.ch.stormedClientService._
import inf.usi.ch.tokenizer.{JavaANTLRTokenizer, UnitTokenizerFactory}
import ch.usi.inf.reveal.parsing.model.Implicits._
import com.kennycason.fleschkincaid.FleschKincaid
import inf.usi.ch.javaLMTokenizer.{JavaLM, JavaNGramCounter}
import inf.usi.ch.naturalLanguageModel.NaturalLanguageModel
import inf.usi.ch.util.{NGramCountXFile, ResultPerFile}
import org.jsoup.Jsoup
import org.jsoup.nodes.{Document, Element}
import org.jsoup.parser.Parser

import scala.io.Source
import scala.util.Try

/**
  * Created by Talal on 09.06.17.
  */
object experimentSetup extends App {

  val stormedDataPath = "/Users/Talal/Tesi/stormed-dataset"

  val androidTestingPath = "ExperimentDiscussions2/Android"
  val javascriptTestingPath = "ExperimentDiscussions2/Cordova"

  val androidList = getListOfFiles(androidTestingPath)
  val cordovaList = getListOfFiles(javascriptTestingPath)
  val allFiles = androidList ::: cordovaList

  private val key = "B8DBDD69F4612953166D624A69DCEDAB344C315CA2D2383725BD08661C6B7183"
  private val nlTokenizerFactory = IndoEuropeanTokenizerFactory.INSTANCE
  private val codeTokenizerFactory = UnitTokenizerFactory.INSTANCE
  private val nlTutorial = new TokenizedLM(nlTokenizerFactory, 3)
  private val codeTutorial = new TokenizedLM(codeTokenizerFactory, 3)
  private val javaLm = new JavaLM()
  //
  //
  trainGistFile("GistFiles/AndroidBluetoothFiles/file0.java")


  val tutorialFiles = getListOfFiles("tutorial")
  val gistBluetoothFiles = getListOfFiles("GistFiles/AndroidBluetoothFiles")
  val gistCameraFiles = getListOfFiles("GistFiles/AndroidCameraFiles")
  val gistHttpClientFiles = getListOfFiles("GistFiles/AndroidHttpClientFiles")
  val gistMapFiles = getListOfFiles("GistFiles/AndroidMapFiles")
  tutorialFiles.foreach(file => trainFile(file.getPath))
  gistBluetoothFiles.foreach(file => trainGistFile(file.getPath))
  gistCameraFiles.foreach(file => trainGistFile(file.getPath))


  val tuple = getLoweBoundTuple(androidTestingPath, javascriptTestingPath)
  val codeLowerBound = tuple._1
  val nlLowerBound = tuple._2
  getResult()


  def getResult() = {

    println("-" * 100)
    println("#" * 10 + "tutorial" + "#" * 10)

    val nlMeanResult = nlAggregationMean(nlTutorial)
    val nlMedianResult = nlAggregationMedian(nlTutorial)
    val codeMeanResult = codeAggregationMean(codeTutorial)
    val codeMedianResult = codeAggregationMedian(codeTutorial)


    val codeReadability: Seq[Double] = calculateCodeReadability()
    val nlReadability: Seq[Double] = calculateNlReadability()


    val resultList: Seq[ResultPerFile] = for (i <- 0 to nlMeanResult.length - 1)
      yield ResultPerFile(nlMeanResult(i)._2.getName, codeMeanResult(i)._1,
        nlMeanResult(i)._1, codeMedianResult(i)._1, nlMedianResult(i)._1, nlReadability(i), codeReadability(i))

    resultList.foreach(println(_))

  }


  def createJavaLM(nGram: Int, fileNumber: Int): TokenizedLM = {
    val lm = new JavaLM().train(nGram, "/Users/Talal/Tesi/familiarity/AndroidSets/androidTrainingList.txt", stormedDataPath, fileNumber)
    lm
  }

  def createNaturalLanguageLM(nGram: Int, fileNumber: Int): TokenizedLM = {
    val lm = new NaturalLanguageModel().train(nGram, "/Users/Talal/Tesi/familiarity/AndroidSets/androidTrainingList.txt", stormedDataPath, fileNumber)
    lm
  }

  def calculateCodeReadability() = {
    val codeList = allFiles.map(getCode)
    val codeReadabilityList = codeList.map(getCodeListReadability)
    codeReadabilityList
  }


  def calculateNlReadability() = {
    val textList: Seq[String] = allFiles.map(getText)
    val textReadabilityList = textList.map(new FleschKincaid().calculate)
    textReadabilityList
  }

  def getCodeListReadability(list: Seq[String]) = {
    val pattern1 = "([\\W])(\\s)"

    val formattedString = list.map(x => x.replaceAll(pattern1, "$1\n"))
    val codeReadabilityResult = formattedString.map(raykernel.apps.readability.eval.Main.getReadability)
    val median =  (codeReadabilityResult.sum) / codeReadabilityResult.size
    median
  }


  def getCode(file: File): List[String] = {
    val postString = Source.fromFile(file).getLines().mkString
    val doc: Document = Jsoup.parse(postString, "", Parser.xmlParser())

    //Code
    val code: List[AnyRef] = doc.select(">code").toArray.toList
    val codeStringList: List[String] = code.map(x =>
      x.asInstanceOf[Element].text())

    //PreCode
    val preCode: List[AnyRef] = doc.select(">pre").toArray.toList
    val preCodeStringList: List[String] = preCode.map(x =>
      x.asInstanceOf[Element].text())

    val codeString = codeStringList ::: preCodeStringList
    codeString
  }

  def getText(file: File): String = {
    val postString = Source.fromFile(file).getLines().mkString
    val doc: Document = Jsoup.parse(postString, "", Parser.xmlParser())
    //text
    val text: List[AnyRef] = doc.select(">*").not("pre").not("code").toArray().toList
    val textStringList: List[String] = text.map(x =>
      x.asInstanceOf[Element].text())
    textStringList.mkString("\n")
  }

  def codeAggregationMean(code: TokenizedLM) = {


    val javascriptCodeAggregationMean: Seq[Double] = new ServiceNGramAggregation().aggregateJavascriptCodeByMean(code, 3, javascriptTestingPath, codeLowerBound)
    val androidCodeAggregationMean: Seq[Double] = new ServiceNGramAggregation().aggregateJavaCodeByMean(code, 3, androidTestingPath, codeLowerBound)


    val androidAggregatedList = androidCodeAggregationMean.zip(androidList)
    val javascriptAggregatedList = javascriptCodeAggregationMean.zip(cordovaList)
    androidAggregatedList ++ javascriptAggregatedList

  }

  def codeAggregationMedian(code: TokenizedLM) = {

    val javascriptCodeAggregationMedian: Seq[Double] = new ServiceNGramAggregation().aggregateJavascriptCodeByMedian(code, 3, javascriptTestingPath, codeLowerBound)
    val androidCodeAggregationMedian: Seq[Double] = new ServiceNGramAggregation().aggregateJavaCodeByMedian(code, 3, androidTestingPath, codeLowerBound)


    val androidAggregatedList = androidCodeAggregationMedian.zip(androidList)
    val javascriptAggregatedList = javascriptCodeAggregationMedian.zip(cordovaList)
    androidAggregatedList ++ javascriptAggregatedList

  }

  def nlAggregationMean(nl: TokenizedLM): Seq[(Double, File)] = {
    val javascriptNLAggregationMean: Seq[Double] = new ServiceNGramAggregation().aggregateNLByMean(nl, 3, javascriptTestingPath, nlLowerBound)
    val androidNLAggregationMean: Seq[Double] = new ServiceNGramAggregation().aggregateNLByMean(nl, 3, androidTestingPath, nlLowerBound)

    val androidAggregatedList: Seq[(Double, File)] = androidNLAggregationMean.zip(androidList)
    val javascriptAggregatedList = javascriptNLAggregationMean.zip(cordovaList)
    androidAggregatedList ++ javascriptAggregatedList
  }


  def nlAggregationMedian(nl: TokenizedLM): Seq[(Double, File)] = {

    val javascriptNLAggregationMedian: Seq[Double] = new ServiceNGramAggregation().aggregateNLByMedian(nl, 3, javascriptTestingPath, nlLowerBound)
    val androidNLAggregationMedian: Seq[Double] = new ServiceNGramAggregation().aggregateNLByMedian(nl, 3, androidTestingPath, nlLowerBound)

    val androidAggregatedList: Seq[(Double, File)] = androidNLAggregationMedian.zip(androidList)
    val javascriptAggregatedList = javascriptNLAggregationMedian.zip(cordovaList)
    androidAggregatedList ++ javascriptAggregatedList
  }


  def getLoweBoundTuple(androidTestingPath: String, javascriptTestingPath: String): (Int, Int) = {

    val androidCodeList: Seq[NGramCountXFile] = new ServiceJavaNGramCounter().getNGramCount(3, androidTestingPath)
    println("Android")
    androidCodeList.foreach(x => println("# code NGram : " + x.nGramCount + "\t" + x.fileName))

    val javascriptCodeList = new ServiceJavascriptNGramCounter().getNGramCount(3, javascriptTestingPath)
    println("javascript")
    javascriptCodeList.foreach(x => println("# code NGram : " + x.nGramCount + "\t" + x.fileName))

    val orderedCodeList = (androidCodeList ++ javascriptCodeList).sortWith(_.nGramCount < _.nGramCount)
    val codeLowerBound = orderedCodeList(0).nGramCount
    println("code lowerBound = " + codeLowerBound)


    val androidNLList = new ServiceNLNGramCounter().getNGramCount(3, androidTestingPath, "android")
    println("Android")
    androidNLList.foreach(x => println("# nl NGram : " + x.nGramCount + "\t" + x.fileName))

    val javascriptNLList = new ServiceNLNGramCounter().getNGramCount(3, javascriptTestingPath, "javascript")
    println("javascript")
    javascriptNLList.foreach(x => println("# nl NGram : " + x.nGramCount + "\t" + x.fileName))

    val orderedNLList = (androidNLList ++ javascriptNLList).sortWith(_.nGramCount < _.nGramCount)
    val nLLowerBound = orderedNLList(0).nGramCount

    println("nl lowerBound = " + nLLowerBound)
    (codeLowerBound, nLLowerBound)
  }


  def trainGistFile(tutorialFile: String) = {
    val file = Source.fromFile(tutorialFile)
    val codeString = file.mkString.replaceAll("[^\\w\"]", " ")
    val tokens = new JavaANTLRTokenizer(codeString.toCharArray).tokenize()
    javaLm.trainGist(tokens, codeString, codeTutorial)

  }


  def trainFile(tutorialFile: String) = {
    val file = Source.fromFile(tutorialFile)
    val codeToParse = file.mkString.trim
    val result: Response = StormedService.parse(codeToParse, key)
    val astNodeResult: Seq[HASTNode] = getHASTNode(result)
    astNodeResult.foreach(tokenizeAndTrain(_))
  }

  def getListOfFiles(dir: String): List[File] = {
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
        ServiceNL.train(nlTutorial, text)

      }

      case javaNode: JavaASTNode => {
        ServiceJavaLM.train(codeTutorial, javaNode)
      }

      case _ => {
        val defaultCode = Try(hASTNode.toCode).get
        ServiceNL.train(nlTutorial, defaultCode)
      }

    }

  }


}
