package inf.usi.ch.javaAntlerLMTokenizer

import java.io.{BufferedWriter, File, FileWriter}

import ANTLRTokenizerFactory.ANTLRTokenizerFactory
import antlr4.JavaLexer
import ch.usi.inf.reveal.parsing.artifact.ArtifactSerializer
import ch.usi.inf.reveal.parsing.model.java.JavaASTNode
import ch.usi.inf.reveal.parsing.model.{HASTNode, HASTNodeSequence, TextFragmentNode}
import ch.usi.inf.reveal.parsing.units.CodeTaggedUnit
import com.aliasi.lm.TokenizedLM
import ch.usi.inf.reveal.parsing.model.Implicits._
import org.antlr.v4.runtime.{ANTLRInputStream, CommonTokenStream}

import scala.collection.mutable.ListBuffer
import scala.io.Source
import scala.util.{Failure, Success, Try}

/**
  * Created by Talal on 03.04.17.
  */
object JavaLMEvaluator {

  private var probBufferList = new ListBuffer[Double]()
  private var probBufferListTuple = new ListBuffer[(Double, String)]()

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


  private def addHASTNodeProbToTupleList(hastNode: HASTNode, nGram: Int, lm: TokenizedLM): Unit = hastNode match {
    case nodeSequence: HASTNodeSequence => nodeSequence.fragments.
      foreach(node => addHASTNodeProbToTupleList(node, nGram, lm))


    case textFragmentNode: TextFragmentNode => val splitedText = textFragmentNode.text.split("\\s+").toList
      if (splitedText.size >= nGram) {

        // create NGrams
        val listOfNGramStrings = splitedText.sliding(nGram).toList.map(x => x.mkString(" "))
        //add probability to prob ListBuffer
        listOfNGramStrings.foreach(string => {
          val result = lm.log2Estimate(string)
          val tuple = (result, string + "   => textFragmentNode ")
          probBufferListTuple += tuple
        })
      }
    case javaNode: JavaASTNode => val code = Try(javaNode.toCode)
      code match {
        case Success(codeString) => val nGramList = getNGramListFromANTLR(codeString, nGram)
          nGramList.foreach(x => {
            val result = lm.log2Estimate(x)
            val tuple = (result, x + "   => javaNode ")
            probBufferListTuple += tuple
          })
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
            listOfNGramStrings.foreach(string => {
              val result = lm.log2Estimate(string)
              val tuple = (result, string + "   => otherNode ")
              probBufferListTuple += tuple
            })
          }
        case Failure(f) => println("failure : " + f)
      }

  }


  private def addHASTNodeProbToList(hastNode: HASTNode, nGram: Int, lm: TokenizedLM): Unit = hastNode match {

    case nodeSequence: HASTNodeSequence => nodeSequence.fragments.
      foreach(node => addHASTNodeProbToList(node, nGram, lm))

    case textFragmentNode: TextFragmentNode => val splitedText = textFragmentNode.text.split("\\s+").toList
      if (splitedText.size >= nGram) {
        ANTLRTokenizerFactory.INSTANCE.setStateIsNonJavaCode();
        // create NGrams
        val listOfNGramStrings = splitedText.sliding(nGram).toList.map(x => x.mkString(" "))
        //add probability to prob ListBuffer
        listOfNGramStrings.foreach(string => probBufferList += lm.log2Estimate(string))
      }
    case javaNode: JavaASTNode => val code = Try(javaNode.toCode)
      code match {
        case Success(codeString) => val nGramList = getNGramListFromANTLR(codeString, nGram)
//          ANTLRTokenizerFactory.INSTANCE.setStateIsJavaCode();
          nGramList.foreach(x => probBufferList += lm.log2Estimate(x))
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
            listOfNGramStrings.foreach(string => probBufferList += lm.log2Estimate(string))
          }
        case Failure(f) => println("failure : " + f)
      }

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


  def getTopLeast100(lm: TokenizedLM, nGram: Int, numberOfFiles: Int, testListFileName: String,
                     stormedDataPath: String, fileNameTop: String, fileNameLeast: String) = {

    val testingListOfAllFilesName = new File(testListFileName)
    val testingSet: List[String] = Source.fromFile(testingListOfAllFilesName).getLines().toList.take(numberOfFiles)
    // populate problist
    testingSet.foreach(file => {
      jsonFileToHASTNode(file, stormedDataPath).
        foreach(hastNode => addHASTNodeProbToTupleList(hastNode, nGram, lm))
    })

    //get top and least
    val ordered = scala.util.Sorting.stableSort(probBufferListTuple.toList, (e1: (Double, String), e2: (Double, String)) => e1._1 > e2._1).toList
    val top = ordered.take(100)
    val least = ordered.drop(ordered.size - 100)

    val topFile = new File(fileNameTop)
    val topbf = new BufferedWriter(new FileWriter(topFile))
    topbf.write("top" + '\n')
    top.foreach(x => topbf.write(x._1 + " , " + x._2 + '\n'))
    topbf.close()

    val leastFile = new File(fileNameLeast)
    val leastbf = new BufferedWriter(new FileWriter(leastFile))
    leastbf.write("least" + '\n')
    least.foreach(x => leastbf.write(x._1 + " , " + x._2 + '\n'))
    leastbf.close()

    // reset problist
    probBufferListTuple = new ListBuffer[(Double, String)]()

  }


}
