package inf.usi.ch

import java.io.{BufferedWriter, File, FileWriter}

import ch.usi.inf.reveal.parsing.artifact.ArtifactSerializer
import ch.usi.inf.reveal.parsing.model.HASTNode
import ch.usi.inf.reveal.parsing.units.CodeTaggedUnit
import inf.usi.ch.tokenizer.{HASTTokenizer, JavascriptANTLRTokenizer}
import org.jsoup.Jsoup
import org.jsoup.nodes.{Document, Element}
import org.jsoup.parser.Parser

import scala.io.Source

/**
  * Created by Talal on 27.04.17.
  */
object IntersectionJavaAndJavascript extends App{

  type Probability = Double

  type Token = String
  type NGram = Array[Token]
  val stormedDataPath = "/Users/Talal/Tesi/stormed-dataset"
  getIntersection()




  def getIntersection() = {
    val javascriptList = getJavascriptNGramList(3,"JavaScriptFiles").map(_.toList)
    val javaList = getJavaNGramSeq(3, 1000, "JavaSet/javaSet.txt", stormedDataPath).map(_.toList)

    val intersection = javaList.intersect(javascriptList)
    println(intersection.size)
    writeListToFile(intersection.map{_.toString},"IntersectionJavaAndJavascript/intersection.txt")
    //intersection.flatMap{_toString}
  }

  private def getListOfFiles(dir: String): List[File] = {
    val d = new File(dir)
    if (d.exists && d.isDirectory) {
      d.listFiles.filter(_.isFile).toList
    } else {
      List[File]()
    }
  }

  def buildNGrams(tokens: Array[Token], nGramLength: Int): List[NGram] = {
    tokens.sliding(nGramLength).toList
  }

  protected def getCodeList(folderPath: String, fileName: String): List[String] = {
    val file = new File(folderPath, fileName)
    val postString = Source.fromFile(file).getLines().mkString
    val doc = Jsoup.parse(postString, "", Parser.xmlParser())

    //Code
    val codeStringList = getCodeStringLIst(doc,">code")

    //PreCode
    val preCodStringList = getCodeStringLIst(doc,">pre")

    codeStringList ::: preCodStringList

  }


  def getCodeStringLIst(doc: Document, string: String): List[String] = {
    val code: List[AnyRef] = doc.select(string).toArray.toList
    val codeStringList: List[String] = code.map(x =>
      x.asInstanceOf[Element].text().replaceAll("[^\\w\"]", " "))
    codeStringList
  }

  private def getNGramSeq(nGram : Int ,folderPath: String, fileName: String): Seq[NGram] = {
    val listCode = getCodeList(folderPath,fileName)
    val tokenizedList: Seq[NGram] = listCode.map(x => new JavascriptANTLRTokenizer(x.toCharArray).tokenize())
    val nGramList = tokenizedList.flatMap(x=>buildNGrams(x,nGram))
    nGramList

  }


  def getJavascriptNGramList(nGram : Int, folderPath: String): Seq[NGram] ={
    val filesList = getListOfFiles(folderPath)
    val nGramSeq = filesList.flatMap(file => getNGramSeq(nGram,folderPath,file.getName))
    println(nGramSeq.size)
    nGramSeq
  }








  def getJavaNGramSeq(nGram: Int, numberOfFiles: Int, testListFileName: String, stormedDataPath: String): Seq[NGram] = {
    val testingListOfAllFileNames = new File(testListFileName)
    val testingSet: List[String] = Source.fromFile(testingListOfAllFileNames).getLines().take(numberOfFiles).toList

    val listOfUnitsHASTNodes: Seq[HASTNode] = testingSet.flatMap { file => jsonFileToUnitsHASTNodes(file, stormedDataPath) }
    val ngramSeq: Seq[NGram] = listOfUnitsHASTNodes.flatMap { hASTNode => addHASTNodeProbToList(hASTNode, nGram) }
    println(ngramSeq.size)
    ngramSeq
  }

  private def jsonFileToUnitsHASTNodes(fileName: String, stormedDataPath: String): Seq[HASTNode] = {
    val testingFile = new File(stormedDataPath, fileName)
    val artifact = ArtifactSerializer.deserializeFromFile(testingFile)

    val allUnits = artifact.question.informationUnits ++ artifact.answers.flatMap(_.informationUnits)
    val codeUnits = allUnits.filter(_.isInstanceOf[CodeTaggedUnit])
    codeUnits.map(_.astNode)
  }

  private def addHASTNodeProbToList(hastNode: HASTNode, nGramLength: Int): Seq[NGram] = {

    val tokens: Array[String] = HASTTokenizer.tokenize(hastNode)
    val ngrams: List[NGram] = buildNGrams(tokens, nGramLength)
    ngrams
  }


  private def writeListToFile(list: Seq[String], filePath: String) = {
    val listFile = new File(filePath)
    val listbf = new BufferedWriter(new FileWriter(listFile))
    listbf.write("intersection\n")
    list.foreach(x => listbf.write(x + '\n'))
    listbf.close()
  }

}
