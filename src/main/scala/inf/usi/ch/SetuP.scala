package inf.usi.ch

import com.aliasi.lm.CompiledTokenizedLM
import inf.usi.ch.codeLanguageModel.{CodeLanguageModel, CodeLanguageModelEvaluator}
import inf.usi.ch.biLMValidation.BiLMValidation
import inf.usi.ch.naturalLanguageModel.{NaturalLanguageModel, NaturalLanguageModelEvaluator}


import scala.io.Source

/**
  * Created by Talal on 03.03.17.
  */
object SetuP extends App {

  val stormedDataPath = "/Users/Talal/Tesi/stormed-dataset"

//  createBiMLProbCSVFile("R/BiLMValidationSum/android.csv","R/BiLMValidationSum/swing.csv")



//  createNaturalLM(3, "naturalLm3Gram.dat")
  //  createAvProbCSVFile("lm6Gram.dat",6,10000,"R/androidPbAV6Gram10000FIles.csv","R/swingPbAV6Gram10000Files.csv")
  //  createAvProbCSVFile("lm3Gram.dat",3,10000,"R/androidPbAV3Gram10000FIles.csv","R/swingPbAV3Gram10000Files.csv")



//  createNLAvProbCSVFile("naturalLm3Gram.dat",3,1000,"R/androidNLPbAV3Gram1000Files.csv","R/swingNLPbAV3Gram1000Files.csv")
//  createCodeAvProbCSVFile("codeLm3Gram.dat",3,1000,"R/androidCodePbAV3Gram1000Files.csv","R/swingCodePbAV3Gram1000Files.csv")
 // createAvProbSwift("lm6Gram.dat", 6)

  def createBiMLProbCSVFile(androidCsvFileName: String, swingCsvFilename : String) ={
    val codeLm3Gram = CodeLanguageModel.deserializeTLM("codeLm3Gram.dat")
    val naturalLm3Gram = NaturalLanguageModel.deserializeTLM("naturalLm3Gram.dat")

    //android
    val androidAvgList = BiLMValidation.evalTesting("AndroidSets/androidTestingList.txt", stormedDataPath, codeLm3Gram, naturalLm3Gram, 1000)
    BiLMValidation.writeListToCSVFile(androidAvgList,androidCsvFileName)

    //swing
    val swingAvgList = BiLMValidation.evalTesting("SwingSets/swingList.txt", stormedDataPath, codeLm3Gram, naturalLm3Gram, 1000)
    BiLMValidation.writeListToCSVFile(swingAvgList,swingCsvFilename)

  }




  def generateAndroidSwingFileList() = {
    val idTagList = IdTagList.deserialize("complete_data.dat")
    val androidList = Classifier.strictTagClassifier(idTagList, "android", "swing")
    val swingList = Classifier.strictTagClassifier(idTagList, "swing", "android")
    val (androidTrainingSet, androidTestingSet) = Classifier.getTrainingAndTestingSet(androidList, 0.9)
    Classifier.writeSetListToFile("/Users/Talal/Tesi/familiarity/SwingSets", "swingSet.txt", swingList)
    Classifier.writeSetListToFile("/Users/Talal/Tesi/familiarity/AndroidSets", "androidTrainingList.txt", androidTrainingSet)
    Classifier.writeSetListToFile("/Users/Talal/Tesi/familiarity/AndroidSets", "androidTestingList.txt", androidTestingSet)
  }


  def createCodeLM(nGram: Int, lmFileName: String) ={
    val lm = CodeLanguageModel.train(nGram,"/Users/Talal/Tesi/familiarity/AndroidSets/androidTrainingList.txt",stormedDataPath)
    CodeLanguageModel.serializeTLM(lm,lmFileName)
  }

  def createNaturalLM(nGram: Int, lmFileName: String) ={
    val lm = NaturalLanguageModel.train(nGram,"/Users/Talal/Tesi/familiarity/AndroidSets/androidTrainingList.txt",stormedDataPath)
    NaturalLanguageModel.serializeTLM(lm,lmFileName)
  }

  def createLM(nGram: Int, lmFileName: String) = {
    val lm = LanguageModel.train(nGram, "/Users/Talal/Tesi/familiarity/AndroidSets/androidTrainingList.txt", stormedDataPath)
    LanguageModel.serializeTLM(lm, lmFileName)
  }

  def createProbCSVFile(lMFileName: String, androidCSVFileName: String, swingCSVFileName: String) = {
    val lm: CompiledTokenizedLM = LanguageModel.deserializeTLM(lMFileName)
    val androidProb: List[Double] = LanguageModelEvaluator.listLog2Probability(lm, "/Users/Talal/Tesi/familiarity/AndroidSets/androidTestingList.txt", stormedDataPath)
    LanguageModelEvaluator.writeListToCSVFile(androidProb, androidCSVFileName)
    val swingProb: List[Double] = LanguageModelEvaluator.listLog2Probability(lm, "/Users/Talal/Tesi/familiarity/SwingSets/swingList.txt", stormedDataPath)
    LanguageModelEvaluator.writeListToCSVFile(swingProb, swingCSVFileName)
  }

  def createAvProbCSVFile(lmPath: String, nGram: Int, numberOfFiles: Int, androidCSVFileName: String, swingCSVFileName: String) = {
    val lm: CompiledTokenizedLM = LanguageModel.deserializeTLM(lmPath)
    //android
    val androidProb: List[Double] = LanguageModelEvaluator.getProbListFiles(lm, nGram, numberOfFiles, "/Users/Talal/Tesi/familiarity/AndroidSets/androidTestingList.txt", stormedDataPath)
    LanguageModelEvaluator.writeListToCSVFile(androidProb, androidCSVFileName)
    //swing
    val swingProb: List[Double] = LanguageModelEvaluator.getProbListFiles(lm, nGram, numberOfFiles, "/Users/Talal/Tesi/familiarity/SwingSets/swingList.txt", stormedDataPath)
    LanguageModelEvaluator.writeListToCSVFile(swingProb, swingCSVFileName)
  }


  def createCodeAvProbCSVFile(lmPath: String, nGram: Int, numberOfFiles: Int, androidCSVFileName: String, swingCSVFileName: String) = {
    val lm: CompiledTokenizedLM = LanguageModel.deserializeTLM(lmPath)
    //android
    val androidProb: List[Double] = CodeLanguageModelEvaluator.getProbListFiles(lm, nGram, numberOfFiles, "/Users/Talal/Tesi/familiarity/AndroidSets/androidTestingList.txt", stormedDataPath)
    CodeLanguageModelEvaluator.writeListToCSVFile(androidProb, androidCSVFileName)
    //swing
    val swingProb: List[Double] = CodeLanguageModelEvaluator.getProbListFiles(lm, nGram, numberOfFiles, "/Users/Talal/Tesi/familiarity/SwingSets/swingList.txt", stormedDataPath)
    CodeLanguageModelEvaluator.writeListToCSVFile(swingProb, swingCSVFileName)
  }

  def createNLAvProbCSVFile(lmPath: String, nGram: Int, numberOfFiles: Int, androidCSVFileName: String, swingCSVFileName: String) = {
    val lm: CompiledTokenizedLM = NaturalLanguageModel.deserializeTLM(lmPath)
    //android
    val androidProb: List[Double] = NaturalLanguageModelEvaluator.getProbListFiles(lm, nGram, numberOfFiles, "/Users/Talal/Tesi/familiarity/AndroidSets/androidTestingList.txt", stormedDataPath)
    NaturalLanguageModelEvaluator.writeListToCSVFile(androidProb, androidCSVFileName)
    //swing
    val swingProb: List[Double] = NaturalLanguageModelEvaluator.getProbListFiles(lm, nGram, numberOfFiles, "/Users/Talal/Tesi/familiarity/SwingSets/swingList.txt", stormedDataPath)
    NaturalLanguageModelEvaluator.writeListToCSVFile(swingProb, swingCSVFileName)
  }


  def createAvProbSwift(lmPath: String, nGram: Int) = {
    val list = IdTagList.getListOfFiles("SwiftFiles")
    val lm = LanguageModel.deserializeTLM(lmPath)
    val averageList = list.map(x => {
      val postString = Source.fromFile(x).getLines().mkString
      val listTokens : List[String] = LanguageModelEvaluator.nGramList(postString,nGram)
      val average : Double = LanguageModelEvaluator.getProb(listTokens, lm)
      println(average)
      average
    }
    )
    LanguageModelEvaluator.writeListToCSVFile(averageList,"R/swiftPbAV6Gram1000Files.csv")
  }

}
