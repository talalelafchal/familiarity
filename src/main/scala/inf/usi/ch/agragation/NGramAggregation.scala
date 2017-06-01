package inf.usi.ch.agragation

import com.aliasi.lm.TokenizedLM
import inf.usi.ch.javaLMTokenizer.JavaLMEvaluator
import inf.usi.ch.javascript.{JavascriptCodeEvaluator, JavascriptNLEvaluator}
import inf.usi.ch.naturalLanguageModel.{NaturalLanguageModel, NaturalLanguageModelEvaluator}

/**
  * Created by Talal on 30.05.17.
  */
class NGramAggregation {

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


  def aggregateJavascriptCodeByMean(codeLm: TokenizedLM, nGram: Int, filesFolderPath: String, filesListPath: String, lowerBound : Int): Seq[Double] = {
    val allFilesProbList: Seq[Seq[Double]] = new JavascriptCodeEvaluator().getQuartileProbList(codeLm, nGram, filesFolderPath, filesListPath)
    val meanList: Seq[Double] = allFilesProbList.map(probabilitiesListXFile => getMean(probabilitiesListXFile, lowerBound))
    meanList
  }


  def aggregateJavascriptCodeByMedian(codeLm: TokenizedLM, nGram: Int, filesFolderPath: String, filesListPath: String, lowerBound :Int): Seq[Double] = {
    val allFilesProbList: Seq[Seq[Double]] = new JavascriptCodeEvaluator().getQuartileProbList(codeLm, nGram, filesFolderPath, filesListPath)
    val medianList: Seq[Double] = allFilesProbList.map(probabilitiesListXFile => getMedian(probabilitiesListXFile, lowerBound))
    medianList
  }


  def aggregateJavascriptNLByMean(nlLm: TokenizedLM, nGram: Int, filesFolderPath: String, filesListPath: String,lowerBound:Int): Seq[Double] = {
    val allFilesProbList: Seq[Seq[Double]] = new JavascriptNLEvaluator().getQuartileProbList(nlLm, nGram, filesFolderPath, filesListPath)
    val meanList: Seq[Double] = allFilesProbList.map(probabilitiesListXFile => getMean(probabilitiesListXFile, lowerBound))
    meanList
  }

  def aggregateJavascriptNLByMedian(nlLm: TokenizedLM, nGram: Int, filesFolderPath: String, filesListPath: String, lowerBound:Int): Seq[Double] = {
    val allFilesProbList: Seq[Seq[Double]] = new JavascriptNLEvaluator().getQuartileProbList(nlLm, nGram, filesFolderPath, filesListPath)
    val medianList: Seq[Double] = allFilesProbList.map(probabilitiesListXFile => getMedian(probabilitiesListXFile, lowerBound))
    medianList
  }


  def aggregateStormedJavaCodeByMean(codeLm: TokenizedLM, nGram: Int, androidTestingQuartileSet: String, stormedDataPath: String, lowerBound: Int): Seq[Double] = {
    val allFilesProbList: Seq[Seq[Double]] = new JavaLMEvaluator().getQuartileProbListFiles(codeLm, nGram, androidTestingQuartileSet, stormedDataPath)
    val meanList: Seq[Double] = allFilesProbList.map(probabilitiesListXFile => getMean(probabilitiesListXFile, lowerBound))
    meanList
  }


  def aggregateStormedJavaCodeByMedian(codeLm: TokenizedLM, nGram: Int, androidTestingQuartileSet: String, stormedDataPath: String, lowerBound: Int): Seq[Double] = {
    val allFilesProbList: Seq[Seq[Double]] = new JavaLMEvaluator().getQuartileProbListFiles(codeLm, nGram, androidTestingQuartileSet, stormedDataPath)
    val medianList: Seq[Double] = allFilesProbList.map(probabilitiesListXFile => getMedian(probabilitiesListXFile, lowerBound))
    medianList
  }


  def aggregateStormedNLByMean(nlLm: TokenizedLM, nGram: Int, androidTestingQuartileSet: String, stormedDataPath: String, lowerBound: Int): Seq[Double] = {
    val allFilesProbList: Seq[Seq[Double]] = new NaturalLanguageModelEvaluator().getQuartileProbListFiles(nlLm, nGram, androidTestingQuartileSet, stormedDataPath)
    val meanList: Seq[Double] = allFilesProbList.map(probabilitiesListXFile => getMean(probabilitiesListXFile, lowerBound))
    meanList
  }

  def aggregateStormedNLByMedian(nlLm: TokenizedLM, nGram: Int, androidTestingQuartileSet: String, stormedDataPath: String, lowerBound: Int): Seq[Double] = {
    val allFilesProbList: Seq[Seq[Double]] = new NaturalLanguageModelEvaluator().getQuartileProbListFiles(nlLm, nGram, androidTestingQuartileSet, stormedDataPath)
    val medianList: Seq[Double] = allFilesProbList.map(probabilitiesListXFile => getMedian(probabilitiesListXFile, lowerBound))
    medianList
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
