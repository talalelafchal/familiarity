package inf.usi.ch

import java.io.File

import ch.usi.inf.reveal.parsing.model.java.JavaASTNode
import ch.usi.inf.reveal.parsing.model.{HASTNode, HASTNodeSequence, TextFragmentNode}
import com.aliasi.lm.TokenizedLM
import com.aliasi.tokenizer.IndoEuropeanTokenizerFactory
import inf.usi.ch.stormedClientService._
import inf.usi.ch.tokenizer.UnitTokenizerFactory
import ch.usi.inf.reveal.parsing.model.Implicits._
import inf.usi.ch.javaLMTokenizer.{JavaLM, JavaNGramCounter}
import inf.usi.ch.naturalLanguageModel.NaturalLanguageModel
import inf.usi.ch.util.NGramCountXFile

import scala.io.Source
import scala.util.Try

/**
  * Created by Talal on 09.06.17.
  */
object experimentSetup extends App {

  val stormedDataPath = "/Users/Talal/Tesi/stormed-dataset"

  val androidTestingPath = "ExperimentDiscussions/Android"
  val javascriptTestingPath = "ExperimentDiscussions/Cordova"

  private val key = "B8DBDD69F4612953166D624A69DCEDAB344C315CA2D2383725BD08661C6B7183"
  private val nlTokenizerFactory = IndoEuropeanTokenizerFactory.INSTANCE
  private val codeTokenizerFactory = UnitTokenizerFactory.INSTANCE
  private val nlTutorial = new TokenizedLM(nlTokenizerFactory, 3)
  private val codeTutorial = new TokenizedLM(codeTokenizerFactory, 3)
  //
  //
  val tutorialFiles = getListOfFiles("tutorial")

  tutorialFiles.foreach(file => trainFile(file.getPath))


  val tuple = getLoweBoundTuple(androidTestingPath, javascriptTestingPath)
  val codeLowerBound = tuple._1
  val nlLowerBound = tuple._2

  //val tokenizerLM = createJavaLM(3, 10000)
  val tokenizerLM = createNaturalLanguageLM(3, 10000)


  println("-" * 100)
  println("#" * 10 + "tutorial" + "#" * 10)
  println("# of LM symbols =  " + nlTutorial.symbolTable().numSymbols())
  //  println("# of LM symbols =  " + codeTutorial.symbolTable().numSymbols())
  //codeAggregation(codeTutorial)
  nlAggregation(nlTutorial)


  println("-" * 100)
  println("#" * 10 + "10 000 files" + "#" * 10)
  println("# of LM symbols =  " + tokenizerLM.symbolTable().numSymbols())
  //codeAggregation(tokenizerLM)
  nlAggregation(tokenizerLM)


  def createJavaLM(nGram: Int, fileNumber: Int): TokenizedLM = {
    val lm = new JavaLM().train(nGram, "/Users/Talal/Tesi/familiarity/AndroidSets/androidTrainingList.txt", stormedDataPath, fileNumber)
    lm
  }

  def createNaturalLanguageLM(nGram: Int, fileNumber: Int): TokenizedLM = {
    val lm = new NaturalLanguageModel().train(nGram, "/Users/Talal/Tesi/familiarity/AndroidSets/androidTrainingList.txt", stormedDataPath, fileNumber)
    lm
  }

  //mean

  def codeAggregation(code: TokenizedLM) = {


    val javascriptCodeAggregationMean: Seq[Double] = new ServiceNGramAggregation().aggregateJavascriptCodeByMean(code, 3, javascriptTestingPath, codeLowerBound)
    val androidCodeAggregationMean: Seq[Double] = new ServiceNGramAggregation().aggregateJavaCodeByMean(code, 3, androidTestingPath, codeLowerBound)


    //median
    val javascriptCodeAggregationMedian: Seq[Double] = new ServiceNGramAggregation().aggregateJavascriptCodeByMedian(code, 3, javascriptTestingPath, codeLowerBound)
    val androidCodeAggregationMedian: Seq[Double] = new ServiceNGramAggregation().aggregateJavaCodeByMedian(code, 3, androidTestingPath, codeLowerBound)


    println("-" * 100)
    println("\t javascriptCodeAggregationMean")
    println()
    javascriptCodeAggregationMean.foreach(x => println("\t" + x))
    println()
    println("\t androidCodeAggregationMean")
    println()
    androidCodeAggregationMean.foreach(x => println("\t" + x))
  }

  def nlAggregation(nl: TokenizedLM): Unit = {
    val javascriptNLAggregationMean: Seq[Double] = new ServiceNGramAggregation().aggregateNLByMean(nl, 3, javascriptTestingPath, nlLowerBound)
    val androidNLAggregationMean: Seq[Double] = new ServiceNGramAggregation().aggregateNLByMean(nl, 3, androidTestingPath, nlLowerBound)


    val javascriptNLAggregationMedian: Seq[Double] = new ServiceNGramAggregation().aggregateNLByMedian(nl, 3, javascriptTestingPath, nlLowerBound)
    val androidNLAggregationMedian: Seq[Double] = new ServiceNGramAggregation().aggregateNLByMedian(nl, 3, androidTestingPath, nlLowerBound)


    println("-" * 100)
    println("\t javascriptNLAggregationMean")
    println()
    javascriptNLAggregationMean.foreach(x => println("\t" + x))
    println()
    println("\t androidNLAggregationMean")
    println()
    androidNLAggregationMean.foreach(x => println("\t" + x))
  }


  def getLoweBoundTuple(androidTestingPath: String, javascriptTestingPath: String): (Int, Int) = {

    val androidCodeList: Seq[NGramCountXFile] = new ServiceJavaNGramCounter().getNGramCount(3, androidTestingPath)
    println("Android")
    androidCodeList.foreach(x => println("# code NGram : " + x.nGramCount))

    val javascriptCodeList = new ServiceJavascriptNGramCounter().getNGramCount(3, javascriptTestingPath)
    println("javascript")
    javascriptCodeList.foreach(x => println("# code NGram : " + x.nGramCount))

    val orderedCodeList = (androidCodeList ++ javascriptCodeList).sortWith(_.nGramCount < _.nGramCount)
    val codeLowerBound = orderedCodeList(0).nGramCount
    println("code lowerBound = " + codeLowerBound)


    val androidNLList = new ServiceNLNGramCounter().getNGramCount(3, androidTestingPath, "android")
    println("Android")
    androidNLList.foreach(x => println("# nl NGram : " + x.nGramCount))

    val javascriptNLList = new ServiceNLNGramCounter().getNGramCount(3, androidTestingPath, "javascript")
    println("javascript")
    javascriptNLList.foreach(x => println("# nl NGram : " + x.nGramCount))

    val orderedNLList = (androidNLList ++ javascriptNLList).sortWith(_.nGramCount < _.nGramCount)
    val nLLowerBound = orderedNLList(0).nGramCount

    println("nl lowerBound = " + nLLowerBound)
    (codeLowerBound, nLLowerBound)
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
