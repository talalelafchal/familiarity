package inf.usi.ch.fileLength

import java.io.{BufferedWriter, File, FileWriter}
import java.util.StringTokenizer

import ch.usi.inf.reveal.parsing.artifact.{ArtifactSerializer, StackOverflowArtifact}
import ch.usi.inf.reveal.parsing.units.{CodeTaggedUnit, NaturalLanguageTaggedUnit}
import org.jsoup.Jsoup
import org.jsoup.parser.Parser
import org.jsoup.select.Elements

import scala.collection.mutable.ListBuffer
import scala.io.Source

/**
  * Created by Talal on 16.03.17.
  */
object FileLength extends App {

  val stormedDataPath = "/Users/Talal/Tesi/stormed-dataset"
  val androidLengthList : List[Int] = listNGramLength("AndroidSets/androidTestingList.txt", stormedDataPath, 1000)
  val swingLengthList : List[Int]= listNGramLength("SwingSets/swingList.txt", stormedDataPath, 1000)
  val swiftLengthList : List[Int] = getStackOverFlowNGramLengthList("SwiftFiles")
  val perlLengthList : List[Int] = getStackOverFlowNGramLengthList("PerlFiles")
  val matlabLengthList : List[Int] = getStackOverFlowNGramLengthList("MatLabFiles")

  createCSVFile("R/BiLMValidationSum/allFilesLength.csv")

  def createCSVFile(filePath: String) = {
    val listFile = new File(filePath)
    val listbf = new BufferedWriter(new FileWriter(listFile))
    listbf.write("android,swing,swift,perl,matlab\n")
    for (i <- 0 until androidLengthList.size) {
      listbf.write(androidLengthList(i) + "," + swingLengthList(i) + "," + swiftLengthList(i) + "," + perlLengthList(i) + "," + matlabLengthList(i)+'\n')
    }
    listbf.close()
  }


  private def numberOfNGram(token: String, nGram: Int): Int = {
    var tokenBuffer = new ListBuffer[String]()
    val stringTokenized = new StringTokenizer(token)
    while (stringTokenized.hasMoreElements()) {
      tokenBuffer += (stringTokenized.nextToken())
    }
    tokenBuffer.toList.sliding(nGram).toList.map(x => x.mkString(" ")).size
  }

  def listNGramLength(testingListFileName: String, stormedDataPath: String, numberOfFiles: Int): List[Int] = {
    val testingListOfAllFilesName = new File(testingListFileName)
    val testingSet: List[String] = Source.fromFile(testingListOfAllFilesName).getLines().toList.take(numberOfFiles)
    val lengthList = testingSet.map(fileName => getStormedDiscussionNGramLength(getArtifact(fileName, stormedDataPath)))
    lengthList
  }



  private def getArtifact(jsonFileName: String, stormedDataPath: String): StackOverflowArtifact = {
    val file = new File(stormedDataPath, jsonFileName)
    ArtifactSerializer.deserializeFromFile(file)
  }

  private def getStormedDiscussionNGramLength(artifact: StackOverflowArtifact): Int = {
    val codeUnits = artifact.units.filter(_.isInstanceOf[CodeTaggedUnit])
    val nlUnits = artifact.units.filter(_.isInstanceOf[NaturalLanguageTaggedUnit])
    val codeSize = codeUnits.map(x => numberOfNGram(x.rawText, 3)).sum
    val nlSize = nlUnits.map(x => numberOfNGram(x.rawText, 3)).sum
    codeSize + nlSize

  }

  private def getListOfFiles(dir: String): List[File] = {
    val d = new File(dir)
    if (d.exists && d.isDirectory) {
      d.listFiles.filter(_.isFile).toList
    } else {
      List[File]()
    }
  }

  private def getStackOverFlowNGramLength(file: File): Int = {
    val postString = Source.fromFile(file).getLines().mkString
    val doc = Jsoup.parse(postString, "", Parser.xmlParser())
    var codeLength: Int = 0
    var textLength: Int = 0

    //Code
    val code: Elements = doc.select(">code")
    val codeIterator = code.iterator()
    while (codeIterator.hasNext) {
      val code = codeIterator.next().text()
      codeLength += numberOfNGram(code, 3)

    }

    //PreCode
    val preCode: Elements = doc.select(">pre")
    val preCodeIterator = preCode.iterator()
    while (preCodeIterator.hasNext) {
      val pre = preCodeIterator.next().text()
      codeLength += numberOfNGram(pre, 3)
    }

    // Text
    val notCode: Elements = doc.select(">*").not("pre").not("code")
    val notCodeIterator = notCode.iterator()
    while (notCodeIterator.hasNext) {
      val text = notCodeIterator.next().text()
      textLength += numberOfNGram(text, 3)
    }

    val totalLength = textLength + codeLength
    totalLength
  }
  def getStackOverFlowNGramLengthList(filesDir: String): List[Int] = {
    val filesList = getListOfFiles(filesDir)
    val lengthList = filesList.map(x => {
      val file = new File(filesDir, x.getName)
      getStackOverFlowNGramLength(file)
    })
    lengthList
  }

}
