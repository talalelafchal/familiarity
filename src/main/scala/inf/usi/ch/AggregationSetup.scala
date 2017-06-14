package inf.usi.ch

import java.io.{BufferedWriter, File, FileWriter}

import com.aliasi.lm.TokenizedLM
import inf.usi.ch.agragation.NGramAggregation
import inf.usi.ch.javaLMTokenizer.{JavaLM, JavaNGramCounter}
import inf.usi.ch.javascript.{JavascriptCodeNGramCounter, JavascriptNLNGramCounter}
import inf.usi.ch.naturalLanguageModel.{NaturalLanguageModel, NaturalLanguageNGramCounter}
import inf.usi.ch.util.NGramCountXFile



/**
  * Created by Talal on 30.05.17.
  */
object AggregationSetup extends App {

  val stormedDataPath = "/Users/Talal/Tesi/stormed-dataset"
  val androidTrainingListPath = "AndroidSets/androidTrainingList.txt"

  val androidTestingQuartileSet = "AndroidQuartileSet/androidQuartileList.txt"
  val javaQuartileSet = "JavaQuartileSet/javaQuartileList.txt"
  val swingQuartileSet = "SwingQuartileSet/swingQuartileList.txt"


  val javascriptFilesFolderPath = "JavaScriptFiles"
  val javascriptFilesListPath = "JavascriptQuartileSet/javaScriptQuartileList.txt"


  val lowerBoundTuple = getLoweBoundTuple(androidTestingQuartileSet, javaQuartileSet, swingQuartileSet, javascriptFilesListPath)
  val codeLowerBound = lowerBoundTuple._1
  val nlLowerBound = lowerBoundTuple._2

  val trainingFilesNumber = 100000

  val codeLm = createJavaLM(3, trainingFilesNumber)
  val nlLm = createNaturalLanguageLM(3, trainingFilesNumber)


  createAggregationCodeProbabilityByMeanCSVFIle("R/CodeAggregation/codeMeanAggregation" + trainingFilesNumber + ".csv", codeLm, 3)
  createAggregationNLProbabilityByMeanCSVFIle("R/NLAggregation/nLMeanAggregation" + trainingFilesNumber + ".csv", nlLm, 3)

  createAggregationCodeProbabilityByMedianCSVFIle("R/CodeAggregation/codeMedianAggregation"+trainingFilesNumber+".csv",codeLm,3)
  createAggregationNLProbabilityByMedianCSVFIle("R/NLAggregation/nLMedianAggregation" + trainingFilesNumber + ".csv", nlLm, 3)


  def getLoweBoundTuple(androidTestingQuartileSet: String, javaQuartileSet: String, swingQuartileSet: String, javascriptFilesListPath: String): (Int, Int) = {
    val androidCodeList: Seq[NGramCountXFile] = new JavaNGramCounter().getNGramCount("android", androidTestingQuartileSet, stormedDataPath, 3)
    val swingCodeList = new JavaNGramCounter().getNGramCount("swing", swingQuartileSet, stormedDataPath, 3)
    val javaCodeList = new JavaNGramCounter().getNGramCount("java", javaQuartileSet, stormedDataPath, 3)
    val javascriptCodeList = new JavascriptCodeNGramCounter().getNGramQuartileCount(3, javascriptFilesFolderPath, javascriptFilesListPath)

    val orderedCodeList = (androidCodeList ++ swingCodeList ++ javaCodeList ++ javascriptCodeList).sortWith(_.nGramCount < _.nGramCount)
    val codeLowerBound = orderedCodeList(0).nGramCount
    println("code lowerBound = " + codeLowerBound)


    val androidNLList = new NaturalLanguageNGramCounter().getNGramCount("android", 3, androidTestingQuartileSet, stormedDataPath)
    val swingNLList = new NaturalLanguageNGramCounter().getNGramCount("swing", 3, swingQuartileSet, stormedDataPath)
    val javaNLList = new NaturalLanguageNGramCounter().getNGramCount("java", 3, javaQuartileSet, stormedDataPath)
    val javascriptNLList = new JavascriptNLNGramCounter().getQuartileNGramCount(3, javascriptFilesFolderPath, javascriptFilesListPath)

    val orderedNLList = (androidNLList ++ swingNLList ++ javaNLList ++ javascriptNLList).sortWith(_.nGramCount < _.nGramCount)
    val nLLowerBound = orderedNLList(0).nGramCount

    println("nl lowerBound = " + nLLowerBound)

    (codeLowerBound, nLLowerBound)

  }


  def createAggregationNLProbabilityByMeanCSVFIle(filePath: String, nlLm: TokenizedLM, nGram: Int) = {

    val javascriptNlAggregation: Seq[Double] = new NGramAggregation().aggregateJavascriptNLByMean(nlLm, nGram, javascriptFilesFolderPath, javascriptFilesListPath, nlLowerBound)
    println(" javaScript nl tokens list size " + javascriptNlAggregation.size)

    val androidNLAggregationByMean: Seq[Double] = new NGramAggregation().aggregateStormedNLByMean(nlLm, nGram, androidTestingQuartileSet, stormedDataPath, nlLowerBound)
    println(" android nl tokens list size " + androidNLAggregationByMean.size)

    val swingNLAggregationByMean: Seq[Double] = new NGramAggregation().aggregateStormedNLByMean(nlLm, nGram, swingQuartileSet, stormedDataPath, nlLowerBound)
    println(" swing nl tokens list size " + swingNLAggregationByMean.size)

    val javaNLAggregationByMean: Seq[Double] = new NGramAggregation().aggregateStormedNLByMean(nlLm, nGram, javaQuartileSet, stormedDataPath, nlLowerBound)
    println(" java tokens nl list size " + javaNLAggregationByMean.size)


    val csvEntries: Seq[(String, String, String, String)] = Seq(("androidNL", "swingNL", "javaNL", "javascriptNL")) ++
      buildCSVRepresentation(androidNLAggregationByMean, swingNLAggregationByMean, javaNLAggregationByMean, javascriptNlAggregation)

    val listFile = new File(filePath)
    val listBufferWriter = new BufferedWriter(new FileWriter(listFile))
    csvEntries.foreach(entry => listBufferWriter.write(s"${entry._1},${entry._2},${entry._3},${entry._4}\n"))
    listBufferWriter.close()
  }


  def createAggregationCodeProbabilityByMeanCSVFIle(filePath: String, javaLm: TokenizedLM, nGram: Int) = {
    val androidCodeAggregationByMean: Seq[Double] = new NGramAggregation().aggregateStormedJavaCodeByMean(codeLm, 3, androidTestingQuartileSet, stormedDataPath, codeLowerBound)
    println(" android code tokens list size " + androidCodeAggregationByMean.size)

    val swingCodeAggregationByMean: Seq[Double] = new NGramAggregation().aggregateStormedJavaCodeByMean(codeLm, 3, swingQuartileSet, stormedDataPath, codeLowerBound)
    println(" swing code tokens list size " + swingCodeAggregationByMean.size)


    val javaCodeAggregationByMean: Seq[Double] = new NGramAggregation().aggregateStormedJavaCodeByMean(codeLm, 3, javaQuartileSet, stormedDataPath, codeLowerBound)
    println(" java code tokens list size " + javaCodeAggregationByMean.size)

    val javascriptCodeAggregation: Seq[Double] = new NGramAggregation().aggregateJavascriptCodeByMean(codeLm, 3, javascriptFilesFolderPath, javascriptFilesListPath, codeLowerBound)
    println(" javaScript code tokens list size " + javascriptCodeAggregation.size)

    val csvEntries: Seq[(String, String, String, String)] = Seq(("android", "swing", "java", "javascript")) ++ buildCSVRepresentation(androidCodeAggregationByMean,
      swingCodeAggregationByMean, javaCodeAggregationByMean, javascriptCodeAggregation)

    val listFile = new File(filePath)
    val listBufferWriter = new BufferedWriter(new FileWriter(listFile))
    csvEntries.foreach(entry => listBufferWriter.write(s"${entry._1},${entry._2},${entry._3},${entry._4}\n"))
    listBufferWriter.close()
  }


  def createAggregationNLProbabilityByMedianCSVFIle(filePath: String, nlLm: TokenizedLM, nGram: Int) = {

    val javascriptNlAggregationByMedian: Seq[Double] = new NGramAggregation().aggregateJavascriptNLByMedian(nlLm, nGram, javascriptFilesFolderPath, javascriptFilesListPath, nlLowerBound)
    println(" javaScript nl tokens list size " + javascriptNlAggregationByMedian.size)

    val androidNLAggregationByMedian: Seq[Double] = new NGramAggregation().aggregateStormedNLByMedian(nlLm, nGram, androidTestingQuartileSet, stormedDataPath, nlLowerBound)
    println(" android nl tokens list size " + androidNLAggregationByMedian.size)

    val swingNLAggregationByMedian: Seq[Double] = new NGramAggregation().aggregateStormedNLByMedian(nlLm, nGram, swingQuartileSet, stormedDataPath, nlLowerBound)
    println(" swing nl tokens list size " + swingNLAggregationByMedian.size)

    val javaNLAggregationByMedian: Seq[Double] = new NGramAggregation().aggregateStormedNLByMedian(nlLm, nGram, javaQuartileSet, stormedDataPath, nlLowerBound)
    println(" java tokens nl list size " + javaNLAggregationByMedian.size)


    val csvEntries: Seq[(String, String, String, String)] = Seq(("androidNL", "swingNL", "javaNL", "javascriptNL")) ++
      buildCSVRepresentation(androidNLAggregationByMedian, swingNLAggregationByMedian, javaNLAggregationByMedian, javascriptNlAggregationByMedian)

    val listFile = new File(filePath)
    val listBufferWriter = new BufferedWriter(new FileWriter(listFile))
    csvEntries.foreach(entry => listBufferWriter.write(s"${entry._1},${entry._2},${entry._3},${entry._4}\n"))
    listBufferWriter.close()
  }


  def createAggregationCodeProbabilityByMedianCSVFIle(filePath: String, javaLm: TokenizedLM, nGram: Int) = {
    val androidCodeAggregationByMedian: Seq[Double] = new NGramAggregation().aggregateStormedJavaCodeByMedian(codeLm, 3, androidTestingQuartileSet, stormedDataPath, codeLowerBound)
    println(" android code tokens list size " + androidCodeAggregationByMedian.size)

    val swingCodeAggregationByMedian: Seq[Double] = new NGramAggregation().aggregateStormedJavaCodeByMedian(codeLm, 3, swingQuartileSet, stormedDataPath, codeLowerBound)
    println(" swing code tokens list size " + swingCodeAggregationByMedian.size)


    val javaCodeAggregationByMedian: Seq[Double] = new NGramAggregation().aggregateStormedJavaCodeByMedian(codeLm, 3, javaQuartileSet, stormedDataPath, codeLowerBound)
    println(" java code tokens list size " + javaCodeAggregationByMedian.size)

    val javascriptCodeAggregation: Seq[Double] = new NGramAggregation().aggregateJavascriptCodeByMedian(codeLm, 3, javascriptFilesFolderPath, javascriptFilesListPath, codeLowerBound)
    println(" javaScript code tokens list size " + javascriptCodeAggregation.size)

    val csvEntries: Seq[(String, String, String, String)] = Seq(("android", "swing", "java", "javascript")) ++ buildCSVRepresentation(androidCodeAggregationByMedian,
      swingCodeAggregationByMedian, javaCodeAggregationByMedian, javascriptCodeAggregation)

    val listFile = new File(filePath)
    val listBufferWriter = new BufferedWriter(new FileWriter(listFile))
    csvEntries.foreach(entry => listBufferWriter.write(s"${entry._1},${entry._2},${entry._3},${entry._4}\n"))
    listBufferWriter.close()
  }


  def createJavaLM(nGram: Int, fileNumber: Int): TokenizedLM = {
    val lm = new JavaLM().train(nGram, androidTrainingListPath, stormedDataPath, fileNumber)
    lm
  }

  def createNaturalLanguageLM(nGram: Int, fileNumber: Int): TokenizedLM = {
    val lm = new NaturalLanguageModel().train(nGram, androidTrainingListPath, stormedDataPath, fileNumber)
    lm
  }

  def buildCSVRepresentation(doubleList1: Seq[Double], doubleList2: Seq[Double], doubleList3: Seq[Double], doubleList4: Seq[Double]): Seq[(String, String, String, String)] = {
    val stringList1 = doubleList1.map {
      _.toString
    }
    val stringList2 = doubleList2.map {
      _.toString
    }

    val stringList3 = doubleList3.map {
      _.toString
    }

    val stringList4 = doubleList4.map {
      _.toString
    }


    val zip1List: Seq[(String, String)] = stringList1.zipAll(stringList2, "", "")
    val zip2List: Seq[(String, String)] = stringList3.zipAll(stringList4, "", "")

    val listTuple4: Seq[(String, String, String, String)] = zipAllLists(zip1List, zip2List)
    listTuple4

  }

  private def zipAllLists(zip1List: Seq[(String, String)], zip2List: Seq[(String, String)]) = {
    val zipedList = zip1List.zipAll(zip2List, ("", ""), ("", ""))
    val listTuple4 = zipedList.map(x => (x._1._1, x._1._2, x._2._1, x._2._2))
    listTuple4
  }


}
