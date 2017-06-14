package inf.usi.ch.stormedClientService

import com.aliasi.lm.TokenizedLM
import inf.usi.ch.javascript.{FileEvaluator}

/**
  * Created by Talal on 12.06.17.
  */
class ServiceNGramAggregation extends FileEvaluator{



  def aggregateJavascriptCodeByMean(codeLm: TokenizedLM, nGram: Int, filesFolderPath: String, lowerBound : Int): Seq[Double] = {
    val filesList = getListOfFiles(filesFolderPath)
    val evaluator = new ServiceJavaScriptEvaluator()
    val allFilesProbList: Seq[Seq[Double]] = filesList.map(file => evaluator.getProbListForFile(codeLm, nGram,filesFolderPath,file.getName))
    val meanList: Seq[Double] = allFilesProbList.map(probabilitiesListXFile => getMean(probabilitiesListXFile, lowerBound))
    meanList
  }



  def aggregateJavascriptCodeByMedian(codeLm: TokenizedLM, nGram: Int, filesFolderPath: String, lowerBound : Int): Seq[Double] = {
    val filesList = getListOfFiles(filesFolderPath)
    val evaluator = new ServiceJavaScriptEvaluator()
    val allFilesProbList: Seq[Seq[Double]] = filesList.map(file => evaluator.getProbListForFile(codeLm, nGram,filesFolderPath,file.getName))
    val medianList: Seq[Double] = allFilesProbList.map(probabilitiesListXFile => getMedian(probabilitiesListXFile, lowerBound))
    medianList
  }


  def aggregateNLByMean(codeLm: TokenizedLM, nGram: Int, filesFolderPath: String, lowerBound : Int): Seq[Double] = {
    val filesList = getListOfFiles(filesFolderPath)
    val evaluator = new ServiceNLEvaluator()
    val allFilesProbList: Seq[Seq[Double]] = filesList.map(file => evaluator.getProbListForFile(codeLm, nGram,filesFolderPath,file.getName))
    val meanList: Seq[Double] = allFilesProbList.map(probabilitiesListXFile => getMean(probabilitiesListXFile, lowerBound))
    meanList
  }

  def aggregateNLByMedian(codeLm: TokenizedLM, nGram: Int, filesFolderPath: String, lowerBound : Int): Seq[Double] = {
    val filesList = getListOfFiles(filesFolderPath)
    val evaluator = new ServiceNLEvaluator()
    val allFilesProbList: Seq[Seq[Double]] = filesList.map(file => evaluator.getProbListForFile(codeLm, nGram,filesFolderPath,file.getName))
    val medianList: Seq[Double] = allFilesProbList.map(probabilitiesListXFile => getMedian(probabilitiesListXFile, lowerBound))
    medianList
  }

  def aggregateJavaCodeByMean(codeLm: TokenizedLM, nGram: Int, filesFolderPath: String, lowerBound : Int): Seq[Double] = {
    val filesList = getListOfFiles(filesFolderPath)
    val evaluator = new ServiceJavaEvaluator()
    val allFilesProbList: Seq[Seq[Double]] = filesList.map(file => evaluator.getProbListForFile(codeLm, nGram,filesFolderPath,file.getName))
    val meanList: Seq[Double] = allFilesProbList.map(probabilitiesListXFile => getMean(probabilitiesListXFile, lowerBound))
    meanList
  }

  def aggregateJavaCodeByMedian(codeLm: TokenizedLM, nGram: Int, filesFolderPath: String, lowerBound : Int): Seq[Double] = {
    val filesList = getListOfFiles(filesFolderPath)
    val evaluator = new ServiceJavaEvaluator()
    val allFilesProbList: Seq[Seq[Double]] = filesList.map(file => evaluator.getProbListForFile(codeLm, nGram,filesFolderPath,file.getName))
    val medianList: Seq[Double] = allFilesProbList.map(probabilitiesListXFile => getMedian(probabilitiesListXFile, lowerBound))
    medianList
  }




  private def mean(slicedList: List[List[Double]]): Double = {
    val sumList = slicedList.map(list => list.sum)
    val mean = (sumList.sum) / sumList.size
    mean
  }

  private def median(slicedList: List[List[Double]]): Double = {
    val sumList: Seq[Double] = slicedList.map(list => list.sum)
    val sortedList = sumList.sortWith(_ < _)
    val med = sortedList.length / 2
    if (sortedList.size % 2 != 0) {
      val median = sortedList(med)
      return median
    }
    val median = (sortedList(med) + sortedList(med - 1)) / 2.0
    median
  }

  private def getMean(probabilitiesListXFile: Seq[Double], lowerBound: Int): Double = {
    if (lowerBound == probabilitiesListXFile.size) {
      return probabilitiesListXFile.sum
    }
    else {
      val slicedList = sliceByLowerBound(probabilitiesListXFile.toList, lowerBound)
      return mean(slicedList)
    }
  }


  private def getMedian(probabilitiesListXFile: Seq[Double], lowerBound: Int): Double = {
    if (lowerBound == probabilitiesListXFile.size) {
      return probabilitiesListXFile.sum
    }
    else {
      val slicedList = sliceByLowerBound(probabilitiesListXFile.toList, lowerBound)
      return median(slicedList)
    }
  }

  private def sliceByLowerBound(list: List[Double], lowerBound: Int): List[List[Double]] = {

    val slicedList = list.grouped(lowerBound).toList
    val lastElement = slicedList.size - 1
    if (slicedList(lastElement).size < lowerBound) {
      val index = list.size - lowerBound
      val tail = list.drop(index)
      return slicedList.dropRight(1) ::: List(tail)
    }
    slicedList
  }

}
