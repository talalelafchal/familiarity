package inf.usi.ch

import java.io.{BufferedWriter, File, FileWriter}

import com.aliasi.lm.CompiledTokenizedLM
import inf.usi.ch.biLMValidation.BiLMValidation
import inf.usi.ch.codeLanguageModel.CodeLanguageModel
import inf.usi.ch.javaAntlerLMTokenizer.{JavaLM, JavaLMEvaluator}
import inf.usi.ch.parser.DiscussionParser

/**
  * Created by Talal on 15.03.17.
  */
object CSVFileMaker extends App {

  val stormedDataPath = "/Users/Talal/Tesi/stormed-dataset"
  //  val codeLm3Gram = CodeLanguageModel.deserializeTLM("codeLm3Gram.dat")
  //  val naturalLm3Gram = NaturalLanguageModel.deserializeTLM("naturalLm3Gram.dat")


  //  val code10Lm = CodeLanguageModel.deserializeTLM("modelLanguage/codeLm10Files.dat")
  //  val code100Lm = CodeLanguageModel.deserializeTLM("modelLanguage/codeLm100Files.dat")
  //  val code1000Lm = CodeLanguageModel.deserializeTLM("modelLanguage/codeLm1000Files.dat")
  //  val code10000Lm = CodeLanguageModel.deserializeTLM("modelLanguage/codeLm10000Files.dat")


//  val java10Lm = JavaLM.deserializeTLM("modelLanguage/javaLm10Files.dat")
//  val java100Lm = CodeLanguageModel.deserializeTLM("modelLanguage/javaLm100Files.dat")
  val java1000Lm = CodeLanguageModel.deserializeTLM("modelLanguage/javaLm1000Files.dat")
//  val java10000Lm = CodeLanguageModel.deserializeTLM("modelLanguage/javaLm10000Files.dat")

//  createJavaNGramCSVFIle("R/10/javaNGram10.csv", java10Lm,3)
//  createJavaNGramCSVFIle("R/100/javaNGram100.csv", java100Lm,3)
  createJavaNGramCSVFIle("R/1000/javaNGramTesting1000.csv", java1000Lm,3)
//  createJavaNGramCSVFIle("R/10000/javaNGram10000.csv", java10000Lm,3)


//  JavaLMEvaluator.getTopLeast100(java1000Lm, 3, 1000, "AndroidSets/androidTestingList.txt", stormedDataPath,"TopLeast/javaAndroidTop.csv","TopLeast/javaAndroidLeast.csv" )
//  JavaLMEvaluator.getTopLeast100(java1000Lm, 3, 1000, "SwingSets/swingList.txt", stormedDataPath,"TopLeast/javaSwingTop.csv","TopLeast/javaSwingLeast.csv" )



  //  DiscussionParser.topLeastToFile("TopLeast/perlTop.csv","TopLeast/perlLeast.csv",DiscussionParser.getTopLeastNGramProbabilityTupleList("PerlFiles",code100Lm))
  //  DiscussionParser.topLeastToFile("TopLeast/matlabTop.csv","TopLeast/matlabLeast.csv",DiscussionParser.getTopLeastNGramProbabilityTupleList("MatLabFiles",code100Lm))

  //  DiscussionParser.topLeastToFile("TopLeast/androidTop.csv","TopLeast/androidLeast.csv",BiLMValidation.evalAllCodeTupleNGramTesting("AndroidSets/androidTestingList.txt", stormedDataPath, code100Lm, 1000))
  //  DiscussionParser.topLeastToFile("TopLeast/swingTop.csv","TopLeast/swingLeast.csv",BiLMValidation.evalAllCodeTupleNGramTesting("SwingSets/swingList.txt", stormedDataPath, code100Lm, 1000))


  //  val nl10Lm = CodeLanguageModel.deserializeTLM("modelLanguage/naturalLm10Files.dat")
  //  val nl100Lm = CodeLanguageModel.deserializeTLM("modelLanguage/naturalLm100Files.dat")
  //  val nl1000Lm = CodeLanguageModel.deserializeTLM("modelLanguage/naturalLm1000Files.dat")
  //  val nl10000Lm = CodeLanguageModel.deserializeTLM("modelLanguage/naturalLm10000Files.dat")

  //
  //  createAllNGramCSVFile("R/10/allNGram10.csv",code10Lm,nl10Lm)
  //  createAllNGramCSVFile("R/100/allNGram100.csv",code100Lm,nl100Lm)
  //  createAllNGramCSVFile("R/1000/allNGram1000.csv",code1000Lm,nl1000Lm)
  //  createAllNGramCSVFile("R/10000/allNGram10000.csv",code10000Lm,nl10000Lm)

  //  createAllCodeNGramCSVFile("R/10/allCodeNGram10.csv", code10Lm)
  //  println("code 10")
  //  createAllCodeNGramCSVFile("R/100/allCodeNGram100.csv", code100Lm)
  //  println("code 100")
  //  createAllCodeNGramCSVFile("R/1000/allCodeNGram1000.csv", code1000Lm)
  //  println("code 1000")
  //  createAllCodeNGramCSVFile("R/10000/allCodeNGram10000.csv", code10000Lm)
  //  println("code 10000")
  //
  //  createAllNLNGramCSVFIle("R/10/allNLNGram10.csv", nl10Lm)
  //  println("NL 10")
  //  createAllNLNGramCSVFIle("R/100/allNLNGram100.csv", nl100Lm)
  //  println("NL 100")
  //  createAllNLNGramCSVFIle("R/1000/allNLNGram1000.csv", nl1000Lm)
  //  println("NL 1000")
  //  createAllNLNGramCSVFIle("R/10000/allNLNGram10000.csv", nl10000Lm)
  //  println("NL 10000")


  def createJavaNGramCSVFIle(filePath: String, javaLm: CompiledTokenizedLM, nGram:Int) = {
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


  def createAllNGramCSVFile(filePath: String, codeLm: CompiledTokenizedLM, naturalLm: CompiledTokenizedLM) {
    val swingAllNGramList: List[Double] = BiLMValidation.evalAllNGramTesting("SwingSets/swingList.txt", stormedDataPath, codeLm, naturalLm, 1000)
    val androidAllNGramList: List[Double] = BiLMValidation.evalAllNGramTesting("AndroidSets/androidTestingList.txt", stormedDataPath, codeLm, naturalLm, 1000)
    val swiftAllNGramList: List[Double] = DiscussionParser.getDocumentsAllNGramProbabilityList("SwiftFiles", codeLm, naturalLm)
    val perlAllNGramList: List[Double] = DiscussionParser.getDocumentsAllNGramProbabilityList("PerlFiles", codeLm, naturalLm)
    val matLabAllNGramList: List[Double] = DiscussionParser.getDocumentsAllNGramProbabilityList("MatLabFiles", codeLm, naturalLm)

    val listFile = new File(filePath)
    val listbf = new BufferedWriter(new FileWriter(listFile))
    listbf.write("android,swing,swift,perl,matlab\n")
    //start from the smaller one
    for (i <- 0 until matLabAllNGramList.size) {
      listbf.write(androidAllNGramList(i) + "," + swingAllNGramList(i) + "," + swiftAllNGramList(i) + "," + perlAllNGramList(i) + "," + matLabAllNGramList(i) + '\n')
    }
    for (i <- matLabAllNGramList.size until perlAllNGramList.size) {
      listbf.write(androidAllNGramList(i) + "," + swingAllNGramList(i) + "," + swiftAllNGramList(i) + "," + perlAllNGramList(i) + "," + "" + '\n')
    }
    for (i <- perlAllNGramList.size until androidAllNGramList.size) {
      listbf.write(androidAllNGramList(i) + "," + swingAllNGramList(i) + "," + swiftAllNGramList(i) + "," + "" + "," + "" + '\n')
    }
    for (i <- androidAllNGramList.size until swiftAllNGramList.size) {
      listbf.write("" + "," + swingAllNGramList(i) + "," + swiftAllNGramList(i) + "," + "" + "," + "" + '\n')
    }

    for (i <- swiftAllNGramList.size until swingAllNGramList.size) {
      listbf.write("" + "," + swingAllNGramList(i) + "," + "" + "," + "" + "," + "" + '\n')
    }
    listbf.close()

  }


  def createAllCodeNGramCSVFile(filePath: String, codeLm: CompiledTokenizedLM): Unit = {
    val swingAllCodeNGramList: List[Double] = BiLMValidation.evalAllCodeNGramTesting("SwingSets/swingList.txt", stormedDataPath, codeLm, 1000)
    val androidAllCodeNGramList: List[Double] = BiLMValidation.evalAllCodeNGramTesting("AndroidSets/androidTestingList.txt", stormedDataPath, codeLm, 1000)
    val swiftAllCodeNGramList: List[Double] = DiscussionParser.getDocumentsAllCodeNGramProbabilityList("SwiftFiles", codeLm)
    val perlAllCodeNGramList: List[Double] = DiscussionParser.getDocumentsAllCodeNGramProbabilityList("PerlFiles", codeLm)
    val matLabAllCodeNGramList: List[Double] = DiscussionParser.getDocumentsAllCodeNGramProbabilityList("MatLabFiles", codeLm)

    println(List(swiftAllCodeNGramList.size, swingAllCodeNGramList.size, androidAllCodeNGramList.size, perlAllCodeNGramList.size, matLabAllCodeNGramList.size))

    val listFile = new File(filePath)
    val listbf = new BufferedWriter(new FileWriter(listFile))
    listbf.write("android,swing,swift,perl,matlab\n")
    //start from the smaller one
    for (i <- 0 until matLabAllCodeNGramList.size) {
      listbf.write(androidAllCodeNGramList(i) + "," + swingAllCodeNGramList(i) + "," + swiftAllCodeNGramList(i) + "," + perlAllCodeNGramList(i) + "," + matLabAllCodeNGramList(i) + '\n')
    }
    for (i <- matLabAllCodeNGramList.size until perlAllCodeNGramList.size) {
      listbf.write(androidAllCodeNGramList(i) + "," + swingAllCodeNGramList(i) + "," + swiftAllCodeNGramList(i) + "," + perlAllCodeNGramList(i) + "," + "" + '\n')
    }
    for (i <- perlAllCodeNGramList.size until swiftAllCodeNGramList.size) {
      listbf.write(androidAllCodeNGramList(i) + "," + swingAllCodeNGramList(i) + "," + swiftAllCodeNGramList(i) + "," + "" + "," + "" + '\n')
    }
    for (i <- swiftAllCodeNGramList.size until androidAllCodeNGramList.size) {
      listbf.write(androidAllCodeNGramList(i) + "," + swingAllCodeNGramList(i) + "," + "" + "," + "" + "," + "" + '\n')
    }

    for (i <- androidAllCodeNGramList.size until swingAllCodeNGramList.size) {
      listbf.write("" + "," + swingAllCodeNGramList(i) + "," + "" + "," + "" + "," + "" + '\n')
    }
    listbf.close()

  }

  def createAllNLNGramCSVFIle(filePath: String, naturalLm: CompiledTokenizedLM) = {
    val swingAllNlNGramList: List[Double] = BiLMValidation.evalAllNLNGramTesting("SwingSets/swingList.txt", stormedDataPath, naturalLm, 1000)
    val androidAllNLNGramList: List[Double] = BiLMValidation.evalAllNLNGramTesting("AndroidSets/androidTestingList.txt", stormedDataPath, naturalLm, 1000)
    val swiftAllNLNGramList: List[Double] = DiscussionParser.getDocumentsAllNLNGramProbabilityList("SwiftFiles", naturalLm)
    val perlAllNLNGramList: List[Double] = DiscussionParser.getDocumentsAllNLNGramProbabilityList("PerlFiles", naturalLm)
    val matLabAllNLNGramList: List[Double] = DiscussionParser.getDocumentsAllNLNGramProbabilityList("MatLabFiles", naturalLm)

    println(List(swiftAllNLNGramList.size, swingAllNlNGramList.size, androidAllNLNGramList.size, perlAllNLNGramList.size, matLabAllNLNGramList.size))

    val listFile = new File(filePath)
    val listbf = new BufferedWriter(new FileWriter(listFile))
    listbf.write("android,swing,swift,perl,matlab\n")
    //start from the smaller one
    for (i <- 0 until perlAllNLNGramList.size) {
      listbf.write(androidAllNLNGramList(i) + "," + swingAllNlNGramList(i) + "," + swiftAllNLNGramList(i) + "," + perlAllNLNGramList(i) + "," + matLabAllNLNGramList(i) + '\n')
    }
    for (i <- perlAllNLNGramList.size until matLabAllNLNGramList.size) {
      listbf.write(androidAllNLNGramList(i) + "," + swingAllNlNGramList(i) + "," + swiftAllNLNGramList(i) + "," + "" + "," + matLabAllNLNGramList(i) + '\n')
    }
    for (i <- matLabAllNLNGramList.size until androidAllNLNGramList.size) {
      listbf.write(androidAllNLNGramList(i) + "," + swingAllNlNGramList(i) + "," + swiftAllNLNGramList(i) + "," + "" + "," + "" + '\n')
    }
    for (i <- androidAllNLNGramList.size until swiftAllNLNGramList.size) {
      listbf.write("" + "," + swingAllNlNGramList(i) + "," + swiftAllNLNGramList(i) + "," + "" + "," + "" + '\n')
    }

    for (i <- swiftAllNLNGramList.size until swingAllNlNGramList.size) {
      listbf.write("" + "," + swingAllNlNGramList(i) + "," + "" + "," + "" + "," + "" + '\n')
    }
    listbf.close()

  }


}
