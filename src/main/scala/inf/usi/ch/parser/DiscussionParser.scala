package inf.usi.ch.parser

import java.io.File

import com.aliasi.lm.CompiledTokenizedLM
import inf.usi.ch.codeLanguageModel.CodeLanguageModelEvaluator
import inf.usi.ch.naturalLanguageModel.NaturalLanguageModelEvaluator
import org.jsoup.Jsoup
import org.jsoup.parser.Parser
import org.jsoup.select.Elements

import scala.collection.mutable.ListBuffer
import scala.io.Source

/**
  * Created by Talal on 14.03.17.
  */
object DiscussionParser {

  def getDocumentsProbabilityList(filesDir: String, codeLm3Gram: CompiledTokenizedLM, naturalLm3Gram: CompiledTokenizedLM): List[Double] = {
    val filesList = getListOfFiles(filesDir)
    val probList = filesList.map(x => {
      val file = new File(filesDir, x.getName)
      getDocumentProbability(file, codeLm3Gram, naturalLm3Gram)
    })
    probList
  }


  private def getListOfFiles(dir: String): List[File] = {
    val d = new File(dir)
    if (d.exists && d.isDirectory) {
      d.listFiles.filter(_.isFile).toList
    } else {
      List[File]()
    }
  }


  private def getDocumentProbability(file: File, codeLm3Gram: CompiledTokenizedLM, naturalLm3Gram: CompiledTokenizedLM): Double = {
    val postString = Source.fromFile(file).getLines().mkString
    val doc = Jsoup.parse(postString, "", Parser.xmlParser())
    var prob: Double = 0.0

    //Code
    val code: Elements = doc.select(">code")
    val codeIterator = code.iterator()
    while (codeIterator.hasNext) {
      val code = codeIterator.next().text()
      val ngram = CodeLanguageModelEvaluator.nGramList(code, 3)
      prob += CodeLanguageModelEvaluator.getProb(ngram, codeLm3Gram)
    }

    //PreCode
    val preCode: Elements = doc.select(">pre")
    val preCodeIterator = preCode.iterator()
    while (preCodeIterator.hasNext) {
      val code = preCodeIterator.next().text()
      val ngram = CodeLanguageModelEvaluator.nGramList(code, 3)
      prob += CodeLanguageModelEvaluator.getProb(ngram, codeLm3Gram)
    }

    // Text
    val notCode: Elements = doc.select(">*").not("pre").not("code")
    val notCodeIterator = notCode.iterator()
    while (notCodeIterator.hasNext) {
      val text = notCodeIterator.next().text()
      val ngram = NaturalLanguageModelEvaluator.nGramList(text, 3)
      prob += NaturalLanguageModelEvaluator.getProb(ngram, naturalLm3Gram)
    }

    println(prob)
    prob
  }

  //all NGram
  private def getAllNGramDocumentProbability(file: File, codeLm3Gram: CompiledTokenizedLM, naturalLm3Gram: CompiledTokenizedLM): List[Double] = {
    val postString = Source.fromFile(file).getLines().mkString
    val doc = Jsoup.parse(postString, "", Parser.xmlParser())
    val listBuffer = new ListBuffer[Double]()

    //Code
    val code: Elements = doc.select(">code")
    val codeIterator = code.iterator()
    while (codeIterator.hasNext) {
      val code = codeIterator.next().text()
      val ngram = CodeLanguageModelEvaluator.nGramList(code, 3)
      listBuffer ++= CodeLanguageModelEvaluator.getAllNGramProb(ngram, codeLm3Gram)
    }


    //PreCode
    val preCode: Elements = doc.select(">pre")
    val preCodeIterator = preCode.iterator()
    while (preCodeIterator.hasNext) {
      val code = preCodeIterator.next().text()
      val ngram = CodeLanguageModelEvaluator.nGramList(code, 3)
      listBuffer ++= CodeLanguageModelEvaluator.getAllNGramProb(ngram, codeLm3Gram)
    }


    // Text
    val notCode: Elements = doc.select(">*").not("pre").not("code")
    val notCodeIterator = notCode.iterator()
    while (notCodeIterator.hasNext) {
      val text = notCodeIterator.next().text()
      val ngram = NaturalLanguageModelEvaluator.nGramList(text, 3)
      listBuffer ++= NaturalLanguageModelEvaluator.getAllNgramProb(ngram, naturalLm3Gram)
    }

    listBuffer.toList
  }
  def getDocumentsAllNGramProbabilityList(filesDir: String, codeLm3Gram: CompiledTokenizedLM, naturalLm3Gram: CompiledTokenizedLM): List[Double] = {
    val filesList = getListOfFiles(filesDir)
    val probList = filesList.map(x => {
      val file = new File(filesDir, x.getName)
      getAllNGramDocumentProbability(file, codeLm3Gram, naturalLm3Gram)
    })
    probList.flatten
  }




  //Code NGram
  private def getAllCodeNGramDocumentProbability(file: File, codeLm3Gram: CompiledTokenizedLM): List[Double] = {
    val postString = Source.fromFile(file).getLines().mkString
    val doc = Jsoup.parse(postString, "", Parser.xmlParser())
    val listBuffer = new ListBuffer[Double]()

    //Code
    val code: Elements = doc.select(">code")
    val codeIterator = code.iterator()
    while (codeIterator.hasNext) {
      val code = codeIterator.next().text()
      val nGram = CodeLanguageModelEvaluator.nGramList(code, 3)
      listBuffer ++= CodeLanguageModelEvaluator.getAllNGramProb(nGram, codeLm3Gram)
    }

    //PreCode
    val preCode: Elements = doc.select(">pre")
    val preCodeIterator = preCode.iterator()
    while (preCodeIterator.hasNext) {
      val code = preCodeIterator.next().text()
      val nGram = CodeLanguageModelEvaluator.nGramList(code, 3)
      listBuffer ++= CodeLanguageModelEvaluator.getAllNGramProb(nGram, codeLm3Gram)
    }

    listBuffer.toList
  }
  def getDocumentsAllCodeNGramProbabilityList(filesDir: String, codeLm3Gram: CompiledTokenizedLM): List[Double] = {
    val filesList = getListOfFiles(filesDir)
    val probList = filesList.map(x => {
      val file = new File(filesDir, x.getName)
      getAllCodeNGramDocumentProbability(file, codeLm3Gram)
    })
    probList.flatten
  }


  //NL NGram
  private def getAllNLNGramDocumentProbability(file: File,naturalLm3Gram: CompiledTokenizedLM): List[Double] = {
    val postString = Source.fromFile(file).getLines().mkString
    val doc = Jsoup.parse(postString, "", Parser.xmlParser())
    val listBuffer = new ListBuffer[Double]()

    // Text
    val notCode: Elements = doc.select(">*").not("pre").not("code")
    val notCodeIterator = notCode.iterator()
    while (notCodeIterator.hasNext) {
      val text = notCodeIterator.next().text()
      val nGram = NaturalLanguageModelEvaluator.nGramList(text, 3)
      listBuffer ++= NaturalLanguageModelEvaluator.getAllNgramProb(nGram, naturalLm3Gram)
    }

    listBuffer.toList
  }
  def getDocumentsAllNLNGramProbabilityList(filesDir: String,naturalLm3Gram: CompiledTokenizedLM): List[Double] = {
    val filesList = getListOfFiles(filesDir)
    val probList = filesList.map(x => {
      val file = new File(filesDir, x.getName)
      getAllNLNGramDocumentProbability(file, naturalLm3Gram)
    })
    probList.flatten
  }




}
