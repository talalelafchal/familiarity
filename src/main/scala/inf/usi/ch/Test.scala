package inf.usi.ch

import java.io.{BufferedWriter, File, FileWriter}

import ANTLRTokenizerFactory.ANTLRTokenizerFactory
import antlr4.JavaLexer
import ch.usi.inf.reveal.parsing.artifact.ArtifactSerializer
import ch.usi.inf.reveal.parsing.model.java.JavaASTNode
import ch.usi.inf.reveal.parsing.model.{HASTNode, HASTNodeSequence, TextFragmentNode}
import ch.usi.inf.reveal.parsing.units.CodeTaggedUnit
import com.aliasi.lm.{CompiledTokenizedLM, TokenizedLM}
import inf.usi.ch.javaAntlerLMTokenizer.{JavaLM, JavaLMEvaluator}
import ch.usi.inf.reveal.parsing.model.Implicits._
import org.antlr.v4.runtime.{ANTLRInputStream, CommonTokenStream}

import scala.collection.mutable.ListBuffer
import scala.io.Source
import scala.util.{Failure, Success, Try}


/**
  * Created by Talal on 30.03.17.
  */
object Test extends App {

  val stormedDataPath = "/Users/Talal/Tesi/stormed-dataset"

  val lm = createJavaLM(3, 1000)

  private var probBufferList = new ListBuffer[Double]()

  createJavaNGramCSVFIle("R/1000/javaNGramTesting1000.csv", lm, 3)

  def createJavaLM(nGram: Int, fileNumber: Int): TokenizedLM = {
    val lm = JavaLM.train(nGram, "/Users/Talal/Tesi/familiarity/AndroidSets/androidTrainingList.txt", stormedDataPath, fileNumber)
    lm
  }

  def getProbListFiles(lm: TokenizedLM, nGram: Int, numberOfFiles: Int, testListFileName: String, stormedDataPath: String): List[Double] = {
    val testingListOfAllFilesName = new File(testListFileName)
    val testingSet: List[String] = Source.fromFile(testingListOfAllFilesName).getLines().toList.take(numberOfFiles)
    // populate problist
    testingSet.foreach(file => {
      jsonFileToHASTNode(file, stormedDataPath).
        foreach(hastNode => addHASTNodeProbToList(hastNode, nGram, lm))
    })
    val list = probBufferList.toList
    probBufferList = new ListBuffer[Double]()
    list
  }


  private def addHASTNodeProbToList(hastNode: HASTNode, nGram: Int, lM: TokenizedLM): Unit = hastNode match {

    case nodeSequence: HASTNodeSequence => nodeSequence.fragments.
      foreach(node => addHASTNodeProbToList(node, nGram, lM))

    case textFragmentNode: TextFragmentNode => val splitedText = textFragmentNode.text.split("\\s+").toList
      if (splitedText.size >= nGram) {
        ANTLRTokenizerFactory.INSTANCE.setStateIsNonJavaCode();
        // create NGrams
        val listOfNGramStrings = splitedText.sliding(nGram).toList.map(x => x.mkString(" "))
        //add probability to prob ListBuffer
        listOfNGramStrings.foreach(string => probBufferList += lM.log2Estimate(string))
      }
    case javaNode: JavaASTNode => val code = Try(javaNode.toCode)
      code match {
        case Success(codeString) => val nGramList = getNGramListFromANTLR(codeString, nGram)
          ANTLRTokenizerFactory.INSTANCE.setStateIsJavaCode();
          nGramList.foreach(x => probBufferList += lM.log2Estimate(x))
        case Failure(f) => println("failure : " + f)
      }
    case otherNode: Any => val code = Try(otherNode.toCode)
      code match {
        case Success(s) =>
          val splitedCode = s.split("\\s+").toList
          if (splitedCode.size >= nGram) {
            // create NGrams
            val listOfNGramStrings = splitedCode.sliding(nGram).toList.map(x => x.mkString(" "))
            //add probability to prob ListBuffer
            ANTLRTokenizerFactory.INSTANCE.setStateIsNonJavaCode();
            listOfNGramStrings.foreach(string => probBufferList += lM.log2Estimate(string))
          }
        case Failure(f) => println("failure : " + f)
      }

  }


  private def jsonFileToHASTNode(fileName: String, stormedDataPath: String): Seq[HASTNode] = {
    val testingFile = new File(stormedDataPath, fileName)
    val artifact = ArtifactSerializer.deserializeFromFile(testingFile)

    val codeUnits = (artifact.question.informationUnits ++ artifact.answers.flatMap {
      _.informationUnits
    }).
      filter(_.isInstanceOf[CodeTaggedUnit])
    codeUnits.map(_.astNode)

  }

  private def getNGramListFromANTLR(codeString: String, nGram: Int): List[String] = {
    val lexer = new JavaLexer(new ANTLRInputStream(codeString))
    val tokens = new CommonTokenStream(lexer)
    tokens.fill()
    //ignore tokens with less than 3 NGram
    if (tokens.size() < nGram + 1) {
      List()
    }
    else {
      var tokensBuffer = new ListBuffer[String]()
      var index = 0
      while (index < tokens.size() - 1) {
        tokensBuffer += tokens.get(index).getText
        index = index + 1
      }
      val tokensList = tokensBuffer.toList

      val nGramStringList = tokensList.sliding(nGram).toList.map(x => x.mkString(" "))
      nGramStringList
    }


  }


  def createJavaNGramCSVFIle(filePath: String, javaLm: TokenizedLM, nGram: Int) = {
    val androidProbList: List[Double] = getProbListFiles(javaLm, nGram, 1000, "AndroidSets/androidTrainingList.txt", stormedDataPath)
    println(" android list size " + androidProbList.size)
    val swingProbList: List[Double] = getProbListFiles(javaLm, nGram, 1000, "SwingSets/swingList.txt", stormedDataPath)
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

}
