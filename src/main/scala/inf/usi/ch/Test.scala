package inf.usi.ch

import inf.usi.ch.javaLMTokenizer.JavaNGramCounter
import inf.usi.ch.javascript.{JavascriptCodeNGramCounter, JavascriptNLNGramCounter}
import inf.usi.ch.naturalLanguageModel.NaturalLanguageNGramCounter


/**
  * Created by Talal on 30.03.17.
  */
object Test extends App {

  // android

  val stormedDataPath = "/Users/Talal/Tesi/stormed-dataset"
  val ngrmacounter = new JavaNGramCounter
  val ngramNlCounter = new NaturalLanguageNGramCounter

  val ngrmacounterJavaScript = new JavascriptCodeNGramCounter
  val ngrmacounterNlJavaScript = new JavascriptNLNGramCounter
  //java

  //val countList =  ngrmacounter.getNGramCount("AndroidSets/androidTestingList.txt",1000,stormedDataPath,3)
  //val countList = ngramNlCounter.getNgramCount(3,1000,"AndroidSets/androidTestingList.txt",stormedDataPath)



  // swing

  //  val countList =  ngrmacounter.getNGramCount("SwingSets/swingList.txt",1000,stormedDataPath,3)
//  val countList = ngramNlCounter.getNgramCount(3,1000,"SwingSets/swingList.txt",stormedDataPath)
 // println(countList.size)

  //java
  //  val countList =  ngrmacounter.getNGramCount("JavaSet/javaSet.txt",1000,stormedDataPath,3)
//   val countList = ngramNlCounter.getNgramCount(3,1000,"JavaSet/javaSet.txt",stormedDataPath)
//
//    println(countList.length)

  //java script
  //  val countlist = ngrmacounterJavaScript.getNGramCount(3,"JavaScriptFiles")
  //  println(countlist.size)
  val countlist = ngrmacounterNlJavaScript.getNGramCount(3,"JavascriptFiles")
  println(countlist)

}
