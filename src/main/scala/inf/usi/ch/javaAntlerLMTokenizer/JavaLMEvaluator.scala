package inf.usi.ch.javaAntlerLMTokenizer

import java.io.{BufferedWriter, File, FileWriter}

import antlr4.JavaLexer
import ch.usi.inf.reveal.parsing.artifact.ArtifactSerializer
import ch.usi.inf.reveal.parsing.model.java.JavaASTNode
import ch.usi.inf.reveal.parsing.model.{HASTNode, HASTNodeSequence, TextFragmentNode}
import ch.usi.inf.reveal.parsing.units.CodeTaggedUnit
import com.aliasi.lm.CompiledTokenizedLM
import ch.usi.inf.reveal.parsing.model.Implicits._
import org.antlr.v4.runtime.{ANTLRInputStream, CommonTokenStream}

import scala.collection.mutable.ListBuffer
import scala.io.Source
import scala.util.{Success, Try}

/**
  * Created by Talal on 03.04.17.
  */
object JavaLMEvaluator {

  private var probList = new ListBuffer[Double]()

  private def jsonFileToHASTNode(fileName: String, stormedDataPath: String): Seq[HASTNode] = {
    val testingFile = new File(stormedDataPath, fileName)
    val artifact = ArtifactSerializer.deserializeFromFile(testingFile)

    val codeUnits = (artifact.question.informationUnits ++ artifact.answers.flatMap {
      _.informationUnits
    }).
      filter(_.isInstanceOf[CodeTaggedUnit])
    codeUnits.map(_.astNode)

  }

  def writeListToCSVFile(list: List[Double], filePath: String) = {
    val listFile = new File(filePath)
    val listbf = new BufferedWriter(new FileWriter(listFile))
    listbf.write("probability\n")
    list.foreach(x => listbf.write(x.toString + '\n'))
    listbf.close()
  }


  private def getNGramListFromANTLR(codeString: String): List[String] = {
    val lexer = new JavaLexer(new ANTLRInputStream(codeString))
    val tokens = new CommonTokenStream(lexer)
    tokens.fill()
    var tokensBuffer = new ListBuffer[String]()
    var index = 0
    while (index < tokens.size() - 1) {
      tokensBuffer += tokens.get(index).getText
      index = index + 1
    }
    val tokensList = tokensBuffer.toList

    val nGramStringList = tokensList.sliding(3).toList.map(x => x.mkString(" "))
    nGramStringList

  }

  def addHASTNodeProbToList(hastNode: HASTNode, nGram: Int, compiledTokenizedLM: CompiledTokenizedLM): Unit = hastNode match {

    case nodeSequence: HASTNodeSequence => nodeSequence.fragments.
      foreach(node => addHASTNodeProbToList(node, nGram, compiledTokenizedLM))

    case textFragmentNode: TextFragmentNode => val splitedText = textFragmentNode.text.split("\\s+").toList
      if (splitedText.size >= 3) {
        // create NGrams
        val listOfNGramStrings = splitedText.sliding(nGram).toList.map(x => x.mkString(" "))
        //add probability to prob ListBuffer
        listOfNGramStrings.foreach(string => probList += compiledTokenizedLM.log2Estimate(string))
      }
    case javaNode: JavaASTNode => val code = Try(javaNode.toCode)
      code match {
        case Success(codeString) => val nGramList = getNGramListFromANTLR(codeString)
          nGramList.foreach(x => probList += compiledTokenizedLM.log2Estimate(x))
      }
    case otherNode: Any => val code = Try(otherNode.toCode)
      code match {
        case Success(s) =>
          val splitedCode = s.split("\\s+").toList
          if (splitedCode.size >= 3) {
            // create NGrams
            val listOfNGramStrings = splitedCode.sliding(nGram).toList.map(x => x.mkString(" "))
            //add probability to prob ListBuffer
            listOfNGramStrings.foreach(string => probList += compiledTokenizedLM.log2Estimate(string))
          }
      }

  }


  def getProbListFiles(compiledTokenizedLM: CompiledTokenizedLM, nGram: Int, numberOfFiles: Int, testListFileName: String, stormedDataPath: String): List[Double] = {
    val testingListOfAllFilesName = new File(testListFileName)
    val testingSet: List[String] = Source.fromFile(testingListOfAllFilesName).getLines().toList.take(numberOfFiles)
    // populate problist
    testingSet.foreach(file => {
      jsonFileToHASTNode(file, stormedDataPath).
        foreach(hastNode => addHASTNodeProbToList(hastNode, nGram, compiledTokenizedLM))
    })
    probList.toList
  }


}
