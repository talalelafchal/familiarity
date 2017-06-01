package inf.usi.ch

import java.io.{BufferedWriter, File, FileWriter}

import com.aliasi.lm.TokenizedLM
import inf.usi.ch.agragation.NGramAggregation
import inf.usi.ch.javaLMTokenizer.{JavaLM, JavaNGramCounter}
import inf.usi.ch.javascript.{JavascriptCodeEvaluator, JavascriptCodeNGramCounter, JavascriptNLNGramCounter}
import inf.usi.ch.naturalLanguageModel.{NaturalLanguageModel, NaturalLanguageNGramCounter}
import inf.usi.ch.util.NGramCountXFile

import scala.io.Source


/**
  * Created by Talal on 30.03.17.
  */
object Test extends App {
  val stormedDataPath = "/Users/Talal/Tesi/stormed-dataset"
  val androidTrainingListPath = "AndroidSets/androidTrainingList.txt"


  val javascriptFilesFolderPath = "JavaScriptFiles"
  val javascriptFilesListPath = "JavascriptQuartileSet/javaScriptQuartileList.txt"


  val androidTestingQuartileSet = "AndroidQuartileSet/androidQuartileList.txt"
  val javaQuartileSet = "JavaQuartileSet/javaQuartileList.txt"
  val swingQuartileSet = "SwingQuartileSet/swingQuartileList.txt"

  val nlLm = createNaturalLanguageLM(3, 10)

  val codeLm = createJavaLM(3,10)

//  val javascriptNlAggregation: Seq[Double] = new NGramAggregation().aggregateJavascriptNLByMean(nlLm,3,javascriptFilesFolderPath,javascriptFilesListPath)

//  val androidNLAggregationByMedian: Seq[Double] = new NGramAggregation().aggregateStormedNLByMedian(nlLm, 3, androidTestingQuartileSet, stormedDataPath)
//
//
//  val swingNLAggregationByMedian: Seq[Double] = new NGramAggregation().aggregateStormedNLByMedian(nlLm, 3, swingQuartileSet, stormedDataPath)
//
//
//  val javaNLAggregationByMedian: Seq[Double] = new NGramAggregation().aggregateStormedNLByMedian(nlLm, 3, javaQuartileSet, stormedDataPath)


//  val androidCodeAggregationByMedian: Seq[Double] = new NGramAggregation().aggregateStormedJavaCodeByMedian(codeLm, 3, androidTestingQuartileSet, stormedDataPath)
//
//
//  val swingCodeAggregationByMedian: Seq[Double] = new NGramAggregation().aggregateStormedJavaCodeByMedian(codeLm, 3, swingQuartileSet, stormedDataPath)
//
//
//
//  val javaCodeAggregationByMedian: Seq[Double] = new NGramAggregation().aggregateStormedJavaCodeByMedian(codeLm, 3, javaQuartileSet, stormedDataPath)
//
//
//  val javascriptCodeAggregation: Seq[Double] = new NGramAggregation().aggregateJavascriptCodeByMedian(codeLm ,3,javascriptFilesFolderPath,javascriptFilesListPath)
//



  def createNaturalLanguageLM(nGram: Int, fileNumber: Int): TokenizedLM = {
    val lm = new NaturalLanguageModel().train(nGram, androidTrainingListPath, stormedDataPath, fileNumber)
    lm
  }

  def createJavaLM(nGram: Int, fileNumber: Int): TokenizedLM = {
    val lm = new JavaLM().train(nGram, androidTrainingListPath, stormedDataPath, fileNumber)
    lm
  }

  // android
  //
//    val stormedDataPath = "/Users/Talal/Tesi/stormed-dataset"
//    val javaNGramCounter = new JavaNGramCounter
//    val nGramNlCounter = new NaturalLanguageNGramCounter
  //  //
  //    val javascriptCodeNGramCounter = new JavascriptCodeNGramCounter
  //    val javascriptNLNGramCounter = new JavascriptNLNGramCounter

  //android

  //    val androidCodeCountList =  javaNGramCounter.getNGramCount("AndroidSets/androidTestingList.txt",1000,stormedDataPath,3)
  //    val androidNLCountList = nGramNlCounter.getNGramCount(3,1000,"AndroidSets/androidTestingList.txt",stormedDataPath)
  //  writeDistributionToFile("NGramCount/androidCodeNGramCount.csv",androidCodeCountList)
  //  writeDistributionToFile("NGramCount/androidNLNGramCount.csv",androidNLCountList)


  // swing
  //  val swingCodeCountList = javaNGramCounter.getNGramCount("SwingSets/swingList.txt", 1000, stormedDataPath, 3)
  //  val swingNLCountList = nGramNlCounter.getNgramCount(3, 1000, "SwingSets/swingList.txt", stormedDataPath)
  //  writeDistributionToFile("NGramCount/swingCodeNGramCount.csv", swingCodeCountList)
  //  writeDistributionToFile("NGramCount/swingNLNGramCount.csv", swingNLCountList)


  //java

  //  val javaCodeCountList = javaNGramCounter.getNGramCount("JavaSet/javaSet.txt", 1000, stormedDataPath, 3)
  //  val javaNLCountList = nGramNlCounter.getNGramCount(3, 1000, "JavaSet/javaSet.txt", stormedDataPath)
  //   writeDistributionToFile("NGramCount/test.csv", javaCodeCountList)
  //writeDistributionToFile("NGramCount/javaNLNGramCountXFile.csv", javaNLCountList)


  //java script
  //    val javaScriptCodeCountList = javascriptCodeNGramCounter.getNGramCount(3, "JavaScriptFiles")
  //  //  writeDistributionToFile("NGramCount/javascriptCodeNGramCountXFile.csv", javaScriptCodeCountList)
  //    val javascriptNLCountList = javascriptNLNGramCounter.getNGramCount(3, "JavascriptFiles")
  //  writeDistributionToFile("NGramCount/javascriptNLNGramCountXFile.csv", javascriptNLCountList)
  //
  //  //
  //    def writeDistributionToFile(filePath: String, list: Seq[NGramCountXFile]) = {
  //
  //      val file = new File(filePath)
  //      val bufferWriter = new BufferedWriter(new FileWriter(file))
  //      bufferWriter.write("count" + "\n")
  //      list.foreach(x => {
  //        bufferWriter.write(x.nGramCount + "\n")
  //      })
  //      bufferWriter.close()
  //    }

  //  def orderNGramCountXFileList(list: Seq[NGramCountXFile]): Seq[NGramCountXFile] = {
  //
  //    val sortedList = list.sortWith(_.nGramCount < _.nGramCount)
  //    sortedList
  //  }
  //
  //  def getQuartile(orderedList: Seq[NGramCountXFile]) = {
  //    val firstQuartileIndex = ((orderedList.length - 1) * (0.25)).toInt
  //    val thirdQuartileIndex = ((orderedList.length - 1) * (0.75)).toInt
  //    orderedList.slice(firstQuartileIndex, thirdQuartileIndex + 1)
  //  }
  //
  //  val orderedCodeList = orderNGramCountXFileList(javaScriptCodeCountList)
  //  val orderedNLList = orderNGramCountXFileList(javascriptNLCountList)
  //
  //
  //  val q1q3CodeList = getQuartile(orderedCodeList)
  //  val q1q3NLList = getQuartile(orderedNLList)
  //  val intersection = listIntersection(q1q3CodeList, q1q3NLList)
  //  println("code " + q1q3CodeList.size)
  //  println("nl " + q1q3NLList.size)
  //  println("intersection " + intersection.size)
  //
  //
  //  def listIntersection(q1q3CodeList: Seq[NGramCountXFile], q1q3NLList: Seq[NGramCountXFile]): Seq[NGramCountXFile] = {
  //    val codeFilesList = q1q3CodeList.map(x => x.fileName)
  //    val intersection = for {x <- q1q3NLList if codeFilesList.contains(x.fileName)} yield x
  //    intersection
  //  }


}
