package inf.usi.ch

import java.io.{BufferedWriter, File, FileWriter}
import inf.usi.ch.biLMValidation.BiLMValidation
import inf.usi.ch.codeLanguageModel.CodeLanguageModel
import inf.usi.ch.naturalLanguageModel.NaturalLanguageModel
import inf.usi.ch.parser.DiscussionParser

/**
  * Created by Talal on 15.03.17.
  */
object CSVFileMaker extends App {

  val stormedDataPath = "/Users/Talal/Tesi/stormed-dataset"
  val codeLm3Gram = CodeLanguageModel.deserializeTLM("codeLm3Gram.dat")
  val naturalLm3Gram = NaturalLanguageModel.deserializeTLM("naturalLm3Gram.dat")

//  createAllNGramCSVFile("R/AllNGram/allNGram.csv")
//  createAllCodeNGramCSVFile("R/AllCodeNGram/allCodeNGram.csv")
  createAllNLNGramCSVFIle("R/AllNLNGram/allNLNGram.csv")
//  testing()


  def createCSVFile(filePath: String) = {

    val swiftList: List[Double] = DiscussionParser.getDocumentsProbabilityList("SwiftFiles", codeLm3Gram, naturalLm3Gram)
    val perlList: List[Double] = DiscussionParser.getDocumentsProbabilityList("PerlFiles", codeLm3Gram, naturalLm3Gram)
    val matLabList: List[Double] = DiscussionParser.getDocumentsProbabilityList("MatLabFiles", codeLm3Gram, naturalLm3Gram)
    val swingList: List[Double] = BiLMValidation.evalTesting("SwingSets/swingList.txt", stormedDataPath, codeLm3Gram, naturalLm3Gram, 1000)
    val androidList: List[Double] = BiLMValidation.evalTesting("AndroidSets/androidTestingList.txt", stormedDataPath, codeLm3Gram, naturalLm3Gram, 1000)

    val listFile = new File(filePath)
    val listbf = new BufferedWriter(new FileWriter(listFile))
    listbf.write("android,swing,swift,perl,matlab\n")
    for (i <- 0 until androidList.size) {
      listbf.write(androidList(i) + "," + swingList(i) + "," + swiftList(i) + "," + perlList(i) + "," + matLabList(i) + '\n')
    }
    listbf.close()
  }

  def createAllNGramCSVFile(filePath: String) {
    val swingAllNGramList: List[Double] = BiLMValidation.evalAllNGramTesting("SwingSets/swingList.txt", stormedDataPath, codeLm3Gram, naturalLm3Gram, 1000)
    val androidAllNGramList: List[Double] = BiLMValidation.evalAllNGramTesting("AndroidSets/androidTestingList.txt", stormedDataPath, codeLm3Gram, naturalLm3Gram, 1000)
    val swiftAllNGramList: List[Double] = DiscussionParser.getDocumentsAllNGramProbabilityList("SwiftFiles", codeLm3Gram, naturalLm3Gram)
    val perlAllNGramList: List[Double] = DiscussionParser.getDocumentsAllNGramProbabilityList("PerlFiles", codeLm3Gram, naturalLm3Gram)
    val matLabAllNGramList: List[Double] = DiscussionParser.getDocumentsAllNGramProbabilityList("MatLabFiles", codeLm3Gram, naturalLm3Gram)

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


  def createAllCodeNGramCSVFile(filePath: String): Unit ={
    val swingAllCodeNGramList: List[Double] = BiLMValidation.evalAllCodeNGramTesting("SwingSets/swingList.txt", stormedDataPath, codeLm3Gram, 1000)
    val androidAllCodeNGramList: List[Double] = BiLMValidation.evalAllCodeNGramTesting("AndroidSets/androidTestingList.txt", stormedDataPath, codeLm3Gram, 1000)
    val swiftAllCodeNGramList: List[Double] = DiscussionParser.getDocumentsAllCodeNGramProbabilityList("SwiftFiles", codeLm3Gram)
    val perlAllCodeNGramList: List[Double] = DiscussionParser.getDocumentsAllCodeNGramProbabilityList("PerlFiles", codeLm3Gram)
    val matLabAllCodeNGramList: List[Double] = DiscussionParser.getDocumentsAllCodeNGramProbabilityList("MatLabFiles", codeLm3Gram)

    println(List(swiftAllCodeNGramList.size,swingAllCodeNGramList.size,androidAllCodeNGramList.size,perlAllCodeNGramList.size,matLabAllCodeNGramList.size))

    val listFile = new File(filePath)
    val listbf = new BufferedWriter(new FileWriter(listFile))
    listbf.write("android,swing,swift,perl,matlab\n")
    //start from the smaller one
    for (i <- 0 until matLabAllCodeNGramList.size) {
      listbf.write(androidAllCodeNGramList(i) + "," + swingAllCodeNGramList(i) + "," + swiftAllCodeNGramList(i) + "," + perlAllCodeNGramList(i) + "," + matLabAllCodeNGramList(i) + '\n')println(i)
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

  def createAllNLNGramCSVFIle(filePath: String)={
    val swingAllNlNGramList: List[Double] = BiLMValidation.evalAllNLNGramTesting("SwingSets/swingList.txt", stormedDataPath, naturalLm3Gram, 1000)
    val androidAllNLNGramList: List[Double] = BiLMValidation.evalAllNLNGramTesting("AndroidSets/androidTestingList.txt", stormedDataPath, naturalLm3Gram, 1000)
    val swiftAllNLNGramList: List[Double] = DiscussionParser.getDocumentsAllNLNGramProbabilityList("SwiftFiles", naturalLm3Gram)
    val perlAllNLNGramList: List[Double] = DiscussionParser.getDocumentsAllNLNGramProbabilityList("PerlFiles", naturalLm3Gram)
    val matLabAllNLNGramList: List[Double] = DiscussionParser.getDocumentsAllNLNGramProbabilityList("MatLabFiles", naturalLm3Gram)

    println(List(swiftAllNLNGramList.size,swingAllNlNGramList.size,androidAllNLNGramList.size,perlAllNLNGramList.size,matLabAllNLNGramList.size))

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

  def testing()={
    val swingAllNGramList: List[Double] = BiLMValidation.evalAllNGramTesting("SwingSets/swingList.txt", stormedDataPath, codeLm3Gram, naturalLm3Gram, 1000)
    val androidAllNGramList: List[Double] = BiLMValidation.evalAllNGramTesting("AndroidSets/androidTestingList.txt", stormedDataPath, codeLm3Gram, naturalLm3Gram, 1000)
    val swiftAllNGramList: List[Double] = DiscussionParser.getDocumentsAllNGramProbabilityList("SwiftFiles", codeLm3Gram, naturalLm3Gram)
    val perlAllNGramList: List[Double] = DiscussionParser.getDocumentsAllNGramProbabilityList("PerlFiles", codeLm3Gram, naturalLm3Gram)
    val matLabAllNGramList: List[Double] = DiscussionParser.getDocumentsAllNGramProbabilityList("MatLabFiles", codeLm3Gram, naturalLm3Gram)

    val allNGramListSize = List(swingAllNGramList.size,androidAllNGramList.size,swiftAllNGramList.size,perlAllNGramList.size,matLabAllNGramList.size)
    println(allNGramListSize)


    val swingAllCodeNGramList: List[Double] = BiLMValidation.evalAllCodeNGramTesting("SwingSets/swingList.txt", stormedDataPath, codeLm3Gram, 1000)
    val androidAllCodeNGramList: List[Double] = BiLMValidation.evalAllCodeNGramTesting("AndroidSets/androidTestingList.txt", stormedDataPath, codeLm3Gram, 1000)
    val swiftAllNCodeGramList: List[Double] = DiscussionParser.getDocumentsAllCodeNGramProbabilityList("SwiftFiles", codeLm3Gram)
    val perlAllCodeNGramList: List[Double] = DiscussionParser.getDocumentsAllCodeNGramProbabilityList("PerlFiles", codeLm3Gram)
    val matLabAllCodeNGramList: List[Double] = DiscussionParser.getDocumentsAllCodeNGramProbabilityList("MatLabFiles", codeLm3Gram)

    val codeListSize : List[Int] = List(swingAllCodeNGramList.size,androidAllCodeNGramList.size,swiftAllNCodeGramList.size,perlAllCodeNGramList.size,matLabAllCodeNGramList.size)


    val swingAllNlNGramList: List[Double] = BiLMValidation.evalAllNLNGramTesting("SwingSets/swingList.txt", stormedDataPath, naturalLm3Gram, 1000)
    val androidAllNLNGramList: List[Double] = BiLMValidation.evalAllNLNGramTesting("AndroidSets/androidTestingList.txt", stormedDataPath, naturalLm3Gram, 1000)
    val swiftAllNLNGramList: List[Double] = DiscussionParser.getDocumentsAllNLNGramProbabilityList("SwiftFiles", naturalLm3Gram)
    val perlAllNLNGramList: List[Double] = DiscussionParser.getDocumentsAllNLNGramProbabilityList("PerlFiles", naturalLm3Gram)
    val matLabAllNLNGramList: List[Double] = DiscussionParser.getDocumentsAllNLNGramProbabilityList("MatLabFiles", naturalLm3Gram)

    val nlListSize = List(swingAllNlNGramList.size,androidAllNLNGramList.size,swiftAllNLNGramList.size,perlAllNLNGramList.size,matLabAllNLNGramList.size)

    for(i <- 0 until nlListSize.size){
      val size = nlListSize(i) + codeListSize(i)
      println(size)
    }



  }



}
