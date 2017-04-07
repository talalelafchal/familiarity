package inf.usi.ch

import java.io.{BufferedWriter, File, FileWriter}

import com.aliasi.lm.TokenizedLM
import inf.usi.ch.javaAntlerLMTokenizer.{JavaLM, JavaLMEvaluator}


/**
  * Created by Talal on 03.03.17.
  */
object Setup extends App {

  val stormedDataPath = "/Users/Talal/Tesi/stormed-dataset"

  def createJavaLM(nGram: Int, fileNumber: Int): TokenizedLM = {
    val lm = JavaLM.train(nGram, "/Users/Talal/Tesi/familiarity/AndroidSets/androidTrainingList.txt", stormedDataPath, fileNumber)
    lm
  }

  def buildCSVRepresentation(doubleList1: Seq[Double], doubleList2: Seq[Double]): Seq[(String, String)] = {
    val stringList1 = doubleList1.map { _.toString }
    val stringList2 = doubleList2.map { _.toString }

    stringList1.zipAll(stringList2,"","")
  }

  def createJavaNGramCSVFIle(filePath: String, javaLm: TokenizedLM, nGram: Int) = {
    val androidProbList: Seq[Double] = JavaLMEvaluator.getProbListFiles(javaLm, nGram, 1000, "AndroidSets/androidTrainingList.txt", stormedDataPath)
    println(" android list size " + androidProbList.size)

    val swingProbList: Seq[Double] = JavaLMEvaluator.getProbListFiles(javaLm, nGram, 1000, "SwingSets/swingList.txt", stormedDataPath)
    println(" swing list size " + swingProbList.size)

    val csvEntries: Seq[(String,String)] = Seq(("android","swing")) ++ buildCSVRepresentation(androidProbList,swingProbList)

    val listFile = new File(filePath)
    val listBufferWriter = new BufferedWriter(new FileWriter(listFile))
    csvEntries.foreach( entry => listBufferWriter.write(s"${entry._1},${entry._2}\n"))
    listBufferWriter.close()
  }

  val lm = createJavaLM(3, 1000)
  createJavaNGramCSVFIle("New/javaTraining1000.csv", lm, 3)

}
