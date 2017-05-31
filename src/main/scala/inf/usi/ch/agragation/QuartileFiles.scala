package inf.usi.ch.agragation

import java.io.{BufferedWriter, File, FileWriter}

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


  def createQuartileFilesList() = {

    val androidTestingFiles = new NGramQuartile().getStormedQuartileFiles(stormedDataPath, androidFilesListPath, 3, 1000)
    val swingTestingFiles = new NGramQuartile().getStormedQuartileFiles(stormedDataPath, swingFilesListPath, 3, 1000)
    val javaTestingFiles = new NGramQuartile().getStormedQuartileFiles(stormedDataPath, javaFilesListPath, 3, 1000)
    val javascriptTestingFiles = new NGramQuartile().getJavascriptQuartileFiles(javascriptFilesPath, 3)

    writeListToFile("AndroidQuartileSet/androidQuartileList.txt", androidTestingFiles)
    writeListToFile("JavaQuartileSet/javaQuartileList.txt", javaTestingFiles)
    writeListToFile("SwingQuartileSet/swingQuartileList.txt", swingTestingFiles)
    writeListToFile("JavascriptQuartileSet/javaScriptQuartileList.txt",javascriptTestingFiles)

  }

  def writeListToFile(filePath: String, androidTestingFiles: Seq[NGramCountXFile]) = {
    val file = new File(filePath)
    val bufferWriter = new BufferedWriter(new FileWriter(file))
    androidTestingFiles.foreach(x => {
      bufferWriter.write(x.fileName + "\n")
    })
    bufferWriter.close()
  }



}
