package inf.usi.ch

import java.io.{BufferedWriter, File, FileWriter}

import com.aliasi.lm.TokenizedLM
import inf.usi.ch.javaAntlerLMTokenizer.{JavaLM, JavaLMEvaluator}


/**
  * Created by Talal on 03.03.17.
  */
object SetUP extends App {

  val stormedDataPath = "/Users/Talal/Tesi/stormed-dataset"

  val lm = createJavaLM(3, 10000)

  def createJavaLM(nGram: Int, fileNumber: Int): TokenizedLM = {
    val lm = JavaLM.train(nGram, "/Users/Talal/Tesi/familiarity/AndroidSets/androidTrainingList.txt", stormedDataPath, fileNumber)
    lm
  }

  createJavaNGramCSVFIle("New/java10000.csv", lm, 3)

  def createJavaNGramCSVFIle(filePath: String, javaLm: TokenizedLM, nGram: Int) = {
    val androidProbList: List[Double] = JavaLMEvaluator.getProbListFiles(javaLm, nGram, 1000, "AndroidSets/androidTestingList.txt", stormedDataPath)
    println(" android list size " + androidProbList.size)
    val swingProbList: List[Double] = JavaLMEvaluator.getProbListFiles(javaLm, nGram, 1000, "SwingSets/swingList.txt", stormedDataPath)
    println(" swing list size " + swingProbList.size)
    val listFile = new File(filePath)
    val listbf = new BufferedWriter(new FileWriter(listFile))
    listbf.write("android,swing\n")
    for (i <- 0 until androidProbList.size) {
      listbf.write(androidProbList(i) + "," + swingProbList(i) + '\n')
    }
    for (i <- androidProbList.size until swingProbList.size) {
      listbf.write("" + "," + swingProbList(i) + '\n')
    }

    listbf.close()
  }


}
