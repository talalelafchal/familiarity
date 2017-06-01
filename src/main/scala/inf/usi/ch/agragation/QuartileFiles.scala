package inf.usi.ch.agragation

import java.io.{BufferedWriter, File, FileWriter}

import inf.usi.ch.javaLMTokenizer.JavaNGramCounter
import inf.usi.ch.javascript.{JavascriptCodeNGramCounter, JavascriptNLNGramCounter}
import inf.usi.ch.naturalLanguageModel.NaturalLanguageNGramCounter
import inf.usi.ch.util.NGramCountXFile

/**
  * Created by Talal on 30.05.17.
  */
object QuartileFiles extends App {

  val stormedDataPath = "/Users/Talal/Tesi/stormed-dataset"

  val androidFilesListPath = "AndroidSets/androidTestingList.txt"
  val swingFilesListPath = "SwingSets/swingList.txt"
  val javaFilesListPath = "JavaSet/javaSet.txt"
  val javascriptFilesPath = "JavaScriptFiles"

  createQuartileFilesList()


  def writeIntersectionToFiles(intersectionList: Seq[NGramCountXFile]) = {
    val androidList = intersectionList.filter(x => x.language.equals("android"))
    val javaList = intersectionList.filter(x => x.language.equals("java"))
    val swingList = intersectionList.filter(x => x.language.equals("swing"))
    val javascript = intersectionList.filter(x => x.language.equals("javascript"))

    writeListToFile("AndroidQuartileSet/androidQuartileList.txt", androidList)
    writeListToFile("JavaQuartileSet/javaQuartileList.txt", javaList)
    writeListToFile("SwingQuartileSet/swingQuartileList.txt", swingList)
    writeListToFile("JavascriptQuartileSet/javaScriptQuartileList.txt", javascript)
  }

  def createQuartileFilesList() = {

    val androidCodeTestingFiles: Seq[NGramCountXFile] = getStormedCodeNGramCountFiles("android", stormedDataPath, androidFilesListPath, 3, 1000)
    val swingCodeTestingFiles = getStormedCodeNGramCountFiles("swing", stormedDataPath, swingFilesListPath, 3, 1000)
    val javaCodeTestingFiles = getStormedCodeNGramCountFiles("java", stormedDataPath, javaFilesListPath, 3, 1000)
    val javascriptCodeTestingFiles = getJavascriptCodeNGramCountFiles(javascriptFilesPath, 3)

    val allCodeTestingFiles = androidCodeTestingFiles ++ swingCodeTestingFiles ++ javaCodeTestingFiles ++ javascriptCodeTestingFiles


    val androidNLTestingFiles = getStormedNLNGramCountFiles("android", stormedDataPath, androidFilesListPath, 3, 1000)
    val swingNLTestingFiles = getStormedNLNGramCountFiles("swing", stormedDataPath, swingFilesListPath, 3, 1000)
    val javaNLTestingFiles = getStormedNLNGramCountFiles("java", stormedDataPath, javaFilesListPath, 3, 1000)
    val javascriptNLTestingFiles = getJavascriptNLNGramCountFiles(javascriptFilesPath, 3)

    val allNlTestingFiles = androidNLTestingFiles ++ swingNLTestingFiles ++ javaNLTestingFiles ++ javascriptNLTestingFiles

    val intersectionList = new NGramQuartile().getIntersection(allCodeTestingFiles, allNlTestingFiles)

    writeIntersectionToFiles(intersectionList)


    println("done")
  }

  def writeListToFile(filePath: String, androidTestingFiles: Seq[NGramCountXFile]) = {
    val file = new File(filePath)
    val bufferWriter = new BufferedWriter(new FileWriter(file))
    androidTestingFiles.foreach(x => {
      bufferWriter.write(x.fileName + "\n")
    })
    bufferWriter.close()
  }


  def getStormedCodeNGramCountFiles(language: String, stormedDataPath: String, filesListPath: String, nGram: Int, numberOfFiles: Int): Seq[NGramCountXFile] = {
    val nGramCounter = new JavaNGramCounter
    val codeCountList = nGramCounter.getNGramCount(language, filesListPath, numberOfFiles, stormedDataPath, nGram)
    codeCountList

  }

  def getStormedNLNGramCountFiles(language: String, stormedDataPath: String, filesListPath: String, nGram: Int, numberOfFiles: Int): Seq[NGramCountXFile] = {
    val nlCounter = new NaturalLanguageNGramCounter
    val nLCountList = nlCounter.getNGramCount(language, nGram, numberOfFiles, filesListPath, stormedDataPath)
    nLCountList

  }


  def getJavascriptCodeNGramCountFiles(filesPath: String, nGram: Int): Seq[NGramCountXFile] = {
    val javascriptCodeNGramCounter = new JavascriptCodeNGramCounter
    val javaScriptCodeCountList = javascriptCodeNGramCounter.getNGramCount(nGram, filesPath)
    javaScriptCodeCountList
  }


  def getJavascriptNLNGramCountFiles(filesPath: String, nGram: Int): Seq[NGramCountXFile] = {
    val javascriptNLNGramCounter = new JavascriptNLNGramCounter
    val javascriptNLCountList = javascriptNLNGramCounter.getNGramCount(nGram, filesPath)
    javascriptNLCountList
  }


}
