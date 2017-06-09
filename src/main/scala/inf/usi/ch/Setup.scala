package inf.usi.ch

import java.io.{BufferedWriter, File, FileWriter, Serializable}

import com.aliasi.lm.TokenizedLM
import inf.usi.ch.javascript._
import inf.usi.ch.javaLMTokenizer.{JavaLM, JavaLMEvaluator, JavaLMEvaluatorTopLeast}
import inf.usi.ch.naturalLanguageModel.{AndroidNLWordsDistribution, NaturalLanguageModel, NaturalLanguageModelEvaluator}


/**
  * Created by Talal on 03.03.17.
  */
object Setup extends App {

  val stormedDataPath = "/Users/Talal/Tesi/stormed-dataset"



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


  def createCodeProbabilityCSVFIle(filePath: String, javaLm: TokenizedLM, nGram: Int) = {
    val androidProbList: Seq[Double] = new JavaLMEvaluator().getProbListFiles(javaLm, nGram, 1000, "AndroidSets/androidTestingList.txt", stormedDataPath)
    println(" android code tokens list size " + androidProbList.size)

    val swingProbList: Seq[Double] = new JavaLMEvaluator().getProbListFiles(javaLm, nGram, 1000, "SwingSets/swingList.txt", stormedDataPath)
    println(" swing code tokens list size " + swingProbList.size)


    val javaProbList: Seq[Double] = new JavaLMEvaluator().getProbListFiles(javaLm, nGram, 1000, "JavaSet/javaSet.txt", stormedDataPath)
    println(" java code tokens list size " + javaProbList.size)

    val javascriptPobList: Seq[Double] = new JavascriptCodeEvaluator().getProbListFiles(javaLm, nGram, "JavaScriptFiles")
    println(" javaScript code tokens list size " + javascriptPobList.size)

    val csvEntries: Seq[(String, String, String, String)] = Seq(("android", "swing", "java", "javascript")) ++ buildCSVRepresentation(androidProbList, swingProbList, javaProbList, javascriptPobList)

    val listFile = new File(filePath)
    val listBufferWriter = new BufferedWriter(new FileWriter(listFile))
    csvEntries.foreach(entry => listBufferWriter.write(s"${entry._1},${entry._2},${entry._3},${entry._4}\n"))
    listBufferWriter.close()
  }

  def createJavaTopLeastCSVFile(javaLm: TokenizedLM, nGram: Int) = {
    val androidTopLeastList = new JavaLMEvaluatorTopLeast().getTopLeastFile(javaLm, nGram, 1000, "AndroidSets/androidTestingList.txt", stormedDataPath)
    val swingTopLeastList = new JavaLMEvaluatorTopLeast().getTopLeastFile(javaLm, nGram, 1000, "SwingSets/swingList.txt", stormedDataPath)

    val androidNgrams = androidTopLeastList.map { t => (t._1, t._2.mkString(" ")) }.distinct

    val androidTop = androidNgrams.take(100)
    val androidLeast = androidNgrams.drop(androidNgrams.size - 100)


    val swingNgrams = swingTopLeastList.map { t => (t._1, t._2.mkString(" ")) }.distinct
    val swingTop = swingNgrams.take(100)
    val swingLeast = swingNgrams.drop(swingNgrams.size - 100)

    //write to file
    writeTopLeastToFile("javaTokenizerNoPunctuationsTopLeast/androidTopLM10.txt", androidTop)
    writeTopLeastToFile("javaTokenizerNoPunctuationsTopLeast/androidLeastLM10.txt", androidLeast)
    writeTopLeastToFile("javaTokenizerNoPunctuationsTopLeast/swingTopLM10.txt", swingTop)
    writeTopLeastToFile("javaTokenizerNoPunctuationsTopLeast/swingLeastLM10.txt", swingLeast)
  }

  private def writeTopLeastToFile(filePath: String, topLeastList: Seq[(Double, String)]) = {
    val file = new File(filePath)
    val bufferWriter = new BufferedWriter(new FileWriter(file))
    topLeastList.foreach(x => {
      bufferWriter.write(x._2 + "    " + x._1 + "\n")
    })
    bufferWriter.close()
  }


  def createJavaTopLeastJavaScriptCSVFile(codeLm: TokenizedLM, nGram: Int): Unit = {
    val javascriptList = new JavascriptCodeEvaluatorTopLeast().getTopLeastFile(codeLm, 3, "JavaScriptFiles")
    val javascriptDistinct = javascriptList.map { t => (t._1, t._2.mkString(" ")) }.distinct
    val javascriptTop = javascriptDistinct.take(100)
    val javascriptLeast = javascriptDistinct.drop(javascriptDistinct.size - 100)

    writeTopLeastToFile("JavascriptTopLeast/javascriptTopLM10.txt", javascriptTop)
    writeTopLeastToFile("JavascriptTopLeast/javascriptLeastLM10.txt", javascriptLeast)


  }


  def createNaturalLanguageLM(nGram: Int, fileNumber: Int): TokenizedLM = {
    val lm = new NaturalLanguageModel().train(nGram, "/Users/Talal/Tesi/familiarity/AndroidSets/androidTrainingList.txt", stormedDataPath, fileNumber)
    lm
  }
  def createJavaLM(nGram: Int, fileNumber: Int): TokenizedLM = {
    val lm = new JavaLM().train(nGram, "/Users/Talal/Tesi/familiarity/AndroidSets/androidTrainingList.txt", stormedDataPath, fileNumber)
    lm
  }


  def createNaturalLanguageProbabilityCSVFile(filePath: String, naturalLanguageLm: TokenizedLM, nGram: Int) = {

    val javascriptNLPobList: Seq[Double] = new JavascriptNLEvaluator().getProbListFiles(naturalLanguageLm, nGram, "JavaScriptFiles")
    println(" javaScript nl tokens list size " + javascriptNLPobList.size)

    val androidNLProbList: Seq[Double] = new NaturalLanguageModelEvaluator().getProbListFiles(naturalLanguageLm, nGram, 1000, "AndroidSets/androidTestingList.txt", stormedDataPath)
    println(" android nl tokens list size " + androidNLProbList.size)

    val swingNLProbList: Seq[Double] = new NaturalLanguageModelEvaluator().getProbListFiles(naturalLanguageLm, nGram, 1000, "SwingSets/swingList.txt", stormedDataPath)
    println(" swing nl tokens list size " + swingNLProbList.size)

    val javaNLProbList: Seq[Double] = new NaturalLanguageModelEvaluator().getProbListFiles(naturalLanguageLm, nGram, 1000, "JavaSet/javaSet.txt", stormedDataPath)
    println(" java tokens nl list size " + javaNLProbList.size)


    val csvEntries: Seq[(String, String, String, String)] = Seq(("androidNL", "swingNL", "javaNL", "javascriptNL")) ++ buildCSVRepresentation(androidNLProbList, swingNLProbList, javaNLProbList, javascriptNLPobList)

    val listFile = new File(filePath)
    val listBufferWriter = new BufferedWriter(new FileWriter(listFile))
    csvEntries.foreach(entry => listBufferWriter.write(s"${entry._1},${entry._2},${entry._3},${entry._4}\n"))
    listBufferWriter.close()
  }





  def writeDistributionToFile(filePath: String, list: Seq[Int]) = {
    val frequencyList = list.map(x => (x, list.count(_ == x)))
    val file = new File(filePath)
    val bufferWriter = new BufferedWriter(new FileWriter(file))
    bufferWriter.write("words,count" + "\n")
    frequencyList.foreach(x => {
      bufferWriter.write(x._1 + "," + x._2 + "\n")
    })
    bufferWriter.close()
  }


  def createTopLeastJavaScriptNLCSVFile(codeLm: TokenizedLM, nGram: Int): Unit = {
    val javascriptList = new JavaScriptNLEvaluatorTopLeast().getTopLeastFile(codeLm, 3, "JavaScriptFiles")
    val javascriptDistinct = javascriptList.map { t => (t._1, t._2.mkString(" ")) }.distinct
    val javascriptTop = javascriptDistinct.take(100)
    val javascriptLeast = javascriptDistinct.drop(javascriptDistinct.size - 100)

    writeTopLeastToFile("JavaScriptNlTopLeast/javascriptTopLM10.txt", javascriptTop)
    writeTopLeastToFile("JavaScriptNlTopLeast/javascriptLeastLM10.txt", javascriptLeast)

  }
    val codeLm = createJavaLM(3, 1)
    createCodeProbabilityCSVFIle("test.csv", codeLm, 3)
    //createJavaTopLeastCSVFile(lm, 3)

  //  val naturalLanguageLm = createNaturalLanguageLM(3, 10)
    //createNaturalLanguageProbabilityCSVFile("NLAndroidSwingJavaJavascriptCSVFiles/nl100000.csv", naturalLanguageLm, 3)

//  val javascriptDistributionList = JavascriptNLWordsDistribution.getWordsDistribution("JavaScriptFiles")
//  writeDistributionToFile("Distribution/javascriptWordsFrequency.csv", javascriptDistributionList)

//    val androidDistributionList = AndroidNLWordsDistribution.gteWordsDistribution("AndroidSets/androidTestingList.txt",stormedDataPath,1000)
//    writeDistributionToFile("Distribution/androidWordsFrequencyFiltered.csv",androidDistributionList)


 //    createTopLeastJavaScriptNLCSVFile(naturalLanguageLm,3)





}