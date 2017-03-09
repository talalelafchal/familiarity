package inf.usi.ch

import com.aliasi.lm.CompiledTokenizedLM

import scala.io.Source

/**
  * Created by Talal on 03.03.17.
  */
object App extends App {
  val stormedDataPath = "/Users/Talal/Tesi/stormed-dataset"

  ////  createLM(6, "lm6Gram.dat")
  //  createAvProbCSVFile("lm6Gram.dat",6,10000,"R/androidPbAV6Gram10000FIles.csv","R/swingPbAV6Gram10000Files.csv")
  //  createAvProbCSVFile("lm3Gram.dat",3,10000,"R/androidPbAV3Gram10000FIles.csv","R/swingPbAV3Gram10000Files.csv")

  createAvProbSwift("lm6Gram.dat", 6)


  def generateAndroidSwingFileList() = {
    val idTagList = IdTagList.deserialize("complete_data.dat")
    val androidList = Classifier.strictTagClassifier(idTagList, "android", "swing")
    val swingList = Classifier.strictTagClassifier(idTagList, "swing", "android")
    val (androidTrainingSet, androidTestingSet) = Classifier.getTrainingAndTestingSet(androidList, 0.9)
    Classifier.writeSetListToFile("/Users/Talal/Tesi/familiarity/SwingSets", "swingSet.txt", swingList)
    Classifier.writeSetListToFile("/Users/Talal/Tesi/familiarity/AndroidSets", "androidTrainingList.txt", androidTrainingSet)
    Classifier.writeSetListToFile("/Users/Talal/Tesi/familiarity/AndroidSets", "androidTestingList.txt", androidTestingSet)
  }


  def createLM(nGram: Int, lmFileName: String) = {
    val lm = LanguageModel.train(nGram, "/Users/Talal/Tesi/familiarity/AndroidSets/androidTrainingList.txt", stormedDataPath)
    LanguageModel.serializeTLM(lm, lmFileName)
  }

  def createProbCSVFile(lMFileName: String, androidCSVFileName: String, swingCSVFileName: String) = {
    val lm: CompiledTokenizedLM = LanguageModel.deserializeTLM(lMFileName)
    val androidProb: List[Double] = LanguageModelEvaluator.setLog2Probability(lm, "/Users/Talal/Tesi/familiarity/AndroidSets/androidTestingList.txt", stormedDataPath)
    LanguageModelEvaluator.writeListToCSVFile(androidProb, androidCSVFileName)
    val swingProb: List[Double] = LanguageModelEvaluator.setLog2Probability(lm, "/Users/Talal/Tesi/familiarity/SwingSets/swingList.txt", stormedDataPath)
    LanguageModelEvaluator.writeListToCSVFile(swingProb, swingCSVFileName)
  }

  def createAvProbCSVFile(lmPath: String, nGram: Int, numberOfFiles: Int, androidCSVFileName: String, swingCSVFileName: String) = {
    val lm: CompiledTokenizedLM = LanguageModel.deserializeTLM(lmPath)
    //android
    val androidProb: List[Double] = LanguageModelEvaluator.getAverageProbListFiles(lm, nGram, numberOfFiles, "/Users/Talal/Tesi/familiarity/AndroidSets/androidTestingList.txt", stormedDataPath)
    LanguageModelEvaluator.writeListToCSVFile(androidProb, androidCSVFileName)
    //swing
    val swingProb: List[Double] = LanguageModelEvaluator.getAverageProbListFiles(lm, nGram, numberOfFiles, "/Users/Talal/Tesi/familiarity/SwingSets/swingList.txt", stormedDataPath)
    LanguageModelEvaluator.writeListToCSVFile(swingProb, swingCSVFileName)
  }

  def createAvProbSwift(lmPath: String, nGram: Int) = {
    val list = IdTagList.getListOfFiles("SwiftFiles")
    val lm = LanguageModel.deserializeTLM(lmPath)
    val averageList = list.map(x => {
      val postString = Source.fromFile(x).getLines().mkString
      val listTokens : List[String] = LanguageModelEvaluator.nGramList(postString,nGram)
      val average : Double = LanguageModelEvaluator.getAverageProb(listTokens, lm)
      println(average)
      average
    }
    )
    LanguageModelEvaluator.writeListToCSVFile(averageList,"R/swiftPbAV6Gram1000Files.csv")
  }

}
