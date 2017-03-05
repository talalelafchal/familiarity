package inf.usi.ch

import com.aliasi.lm.CompiledTokenizedLM

/**
  * Created by Talal on 03.03.17.
  */
object App extends App {


  val stormedDataPath = "/Users/Talal/Tesi/stormed-dataset"
//  createLM(6, "lm6Gram.dat")
  createAvProbCSVFile("lm6Gram.dat",6,"R/androidPbAV6Gram.csv","R/swingPbAV6Gram.csv")


  def generateAndroidSwingFileList() = {
    val idTagList = IdTagList.deserialize("complete_data.dat")
    val androidList = Classifier.strictTagClassifier(idTagList, "android", "swing")
    val swingList = Classifier.strictTagClassifier(idTagList, "swing", "android")
    val (androidTrainingSet, androidTestingSet) = Classifier.getTrainingAndTestingSet(androidList, 0.9)
    Classifier.writeSetListToFile("/Users/Talal/Desktop/familiarity/SwingSets", "swingSet.txt", swingList)
    Classifier.writeSetListToFile("/Users/Talal/Desktop/familiarity/AndroidSets", "androidTrainingList.txt", androidTrainingSet)
    Classifier.writeSetListToFile("/Users/Talal/Desktop/familiarity/AndroidSets", "androidTestingList.txt", androidTestingSet)
  }


  def createLM(nGram: Int, lmFileName:String) = {
    val lm = LanguageModel.train(nGram, "/Users/Talal/Desktop/familiarity/AndroidSets/androidTrainingList.txt", stormedDataPath)
    LanguageModel.serializeTLM(lm, lmFileName)
  }

  def createProbCSVFile(lMFileName:String, androidCSVFileName:String, swingCSVFileName:String) = {
    val lm: CompiledTokenizedLM = LanguageModel.deserializeTLM(lMFileName)
    val androidProb: List[Double] = LanguageModelEvaluator.setLog2Probability(lm, "/Users/Talal/Desktop/familiarity/AndroidSets/androidTestingList.txt", stormedDataPath)
    LanguageModelEvaluator.writeListToCSVFile(androidProb, androidCSVFileName)
    val swingProb: List[Double] = LanguageModelEvaluator.setLog2Probability(lm, "/Users/Talal/Desktop/familiarity/SwingSets/swingList.txt", stormedDataPath)
    LanguageModelEvaluator.writeListToCSVFile(swingProb, swingCSVFileName)
  }

  def createAvProbCSVFile(lmPath:String,nGram:Int,androidCSVFileName:String, swingCSVFileName:String) = {
    val lm: CompiledTokenizedLM = LanguageModel.deserializeTLM(lmPath)
    //android
    val androidProb: List[Double] = LanguageModelEvaluator.getAverageProbList1000Files(lm, nGram, "/Users/Talal/Desktop/familiarity/AndroidSets/androidTestingList.txt", stormedDataPath)
    LanguageModelEvaluator.writeListToCSVFile(androidProb, androidCSVFileName)
    //swing
    val swingProb: List[Double] = LanguageModelEvaluator.getAverageProbList1000Files(lm, nGram, "/Users/Talal/Desktop/familiarity/SwingSets/swingList.txt", stormedDataPath)
    LanguageModelEvaluator.writeListToCSVFile(swingProb, swingCSVFileName)
  }

}
