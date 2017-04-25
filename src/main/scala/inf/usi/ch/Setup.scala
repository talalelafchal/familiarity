package inf.usi.ch

import java.io.{BufferedWriter, File, FileWriter, Serializable}

import com.aliasi.lm.TokenizedLM
import inf.usi.ch.javaAntlerLMTokenizer.{JavaLM, JavaLMEvaluator, JavaLMEvaluatorTopLeast}


/**
  * Created by Talal on 03.03.17.
  */
object Setup extends App {

  val stormedDataPath = "/Users/Talal/Tesi/stormed-dataset"

  def createJavaLM(nGram: Int, fileNumber: Int): TokenizedLM = {
    val lm = JavaLM.train(nGram, "/Users/Talal/Tesi/familiarity/AndroidSets/androidTrainingList.txt", stormedDataPath, fileNumber)
    lm
  }



  def buildCSVRepresentation(doubleList1: Seq[Double], doubleList2: Seq[Double], doubleList3: Seq[Double]): Seq[(String, String, String)] = {
    val stringList1 = doubleList1.map {
      _.toString
    }
    val stringList2 = doubleList2.map {
      _.toString
    }

    val stringList3 = doubleList3.map {
      _.toString
    }

    val zip2List: Seq[(String, String)] = stringList1.zipAll(stringList2, "", "")

    val listTuple3 : Seq[(String,String,String)] = zipAllLists(zip2List,stringList3)
    listTuple3

  }

  private def zipAllLists(zip2List: Seq[(String, String)], stringList3: Seq[String]) = {
    val zipedList = zip2List.zipAll(stringList3,("",""),(""))
    val listTuple3 = zipedList.map(x =>(x._1._1,x._1._2,x._2))
    listTuple3
  }



  def createJavaNGramCSVFIle(filePath: String, javaLm: TokenizedLM, nGram: Int) = {
    val androidProbList: Seq[Double] = new JavaLMEvaluator().getProbListFiles(javaLm, nGram, 1000, "AndroidSets/androidTestingList.txt", stormedDataPath)
    println(" android tokens list size " + androidProbList.size)

    val swingProbList: Seq[Double] = new JavaLMEvaluator().getProbListFiles(javaLm, nGram, 1000, "SwingSets/swingList.txt", stormedDataPath)
    println(" swing tokens list size " + swingProbList.size)


    val javaProbList: Seq[Double] = new JavaLMEvaluator().getProbListFiles(javaLm, nGram, 100, "JavaSet/javaSet.txt", stormedDataPath)
    println(" java tokens list size " + javaProbList.size)

    val csvEntries: Seq[(String, String, String)] = Seq(("android", "swing", "java")) ++ buildCSVRepresentation(androidProbList, swingProbList, javaProbList)

    val listFile = new File(filePath)
    val listBufferWriter = new BufferedWriter(new FileWriter(listFile))
    csvEntries.foreach(entry => listBufferWriter.write(s"${entry._1},${entry._2},${entry._3}\n"))
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
    writeToFile("javaTokenizerNoPunctuationsTopLeast/androidTopLM10.txt", androidTop)
    writeToFile("javaTokenizerNoPunctuationsTopLeast/androidLeastLM10.txt", androidLeast)
    writeToFile("javaTokenizerNoPunctuationsTopLeast/swingTopLM10.txt", swingTop)
    writeToFile("javaTokenizerNoPunctuationsTopLeast/swingLeastLM10.txt", swingLeast)
  }

  private def writeToFile(filePath: String, topLeastList: Seq[(Double, String)]) = {
    val file = new File(filePath)
    val bufferWriter = new BufferedWriter(new FileWriter(file))
    topLeastList.foreach(x => {
      bufferWriter.write(x._2 + "    " + x._1 + "\n")
    })
    bufferWriter.close()
  }

  val lm = createJavaLM(3, 100)
    createJavaTopLeastCSVFile(lm, 3)
  //createJavaNGramCSVFIle("AndroidSwingJavaFormattedPunctuationCSVFiles/java100000.csv", lm, 3)

}
