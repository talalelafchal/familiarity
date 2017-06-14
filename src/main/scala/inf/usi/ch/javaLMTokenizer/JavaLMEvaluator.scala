package inf.usi.ch.javaLMTokenizer

import java.io.{BufferedWriter, File, FileWriter}

import ch.usi.inf.reveal.parsing.artifact.ArtifactSerializer
import ch.usi.inf.reveal.parsing.model.{HASTNode}
import ch.usi.inf.reveal.parsing.units.CodeTaggedUnit
import com.aliasi.lm.TokenizedLM
import inf.usi.ch.tokenizer.{HASTTokenizer}
import scala.io.Source

/**
  * Created by Talal on 03.04.17.
  */
class JavaLMEvaluator extends JavaCodeEvaluator{




  protected def jsonFileToUnitsHASTNodes(fileName: String, stormedDataPath: String): Seq[HASTNode] = {
    val testingFile = new File(stormedDataPath, fileName)
    val artifact = ArtifactSerializer.deserializeFromFile(testingFile)

    val allUnits = artifact.question.informationUnits ++ artifact.answers.flatMap(_.informationUnits)
    val codeUnits = allUnits.filter(_.isInstanceOf[CodeTaggedUnit])
    codeUnits.map(_.astNode)
  }


  def writeListToCSVFile(list: List[Double], filePath: String) = {
    val listFile = new File(filePath)
    val listbf = new BufferedWriter(new FileWriter(listFile))
    listbf.write("probability\n")
    list.foreach(x => listbf.write(x.toString + '\n'))
    listbf.close()
  }




  def getProbListFiles(lm: TokenizedLM, nGram: Int, numberOfFiles: Int, testListFileName: String, stormedDataPath: String): Seq[Double] = {
    val testingListOfAllFileNames = new File(testListFileName)
    val testingSet: List[String] = Source.fromFile(testingListOfAllFileNames).getLines().take(numberOfFiles).toList
    val listOfUnitsHASTNodes: Seq[HASTNode] = testingSet.flatMap { file => jsonFileToUnitsHASTNodes(file, stormedDataPath) }
    val ngramProbabilities: Seq[Probability] = listOfUnitsHASTNodes.flatMap { hASTNode => addHASTNodeProbToList(hASTNode, nGram, lm) }

    ngramProbabilities
  }

  def getQuartileProbListFiles(lm: TokenizedLM, nGram: Int, testListFileName: String, stormedDataPath: String): Seq[Seq[Double]] = {
    val testingListOfAllFileNames = new File(testListFileName)
    val testingSet: List[String] = Source.fromFile(testingListOfAllFileNames).getLines().toList
    val listOfUnitsHASTNodes: Seq[Seq[HASTNode]] = testingSet.map { file => jsonFileToUnitsHASTNodes(file, stormedDataPath) }
    val ngramProbabilities: Seq[Seq[Probability]] = listOfUnitsHASTNodes.map { hASTNodeList => hASTNodeList.flatMap( hASTNode => addHASTNodeProbToList(hASTNode, nGram, lm)) }

    ngramProbabilities
  }


}
