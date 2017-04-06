package inf.usi.ch

import java.io._


/**
  * Created by Talal on 03.03.17.
  */
object Classifier {

  def tagIntersection(questionPerTagList: List[QuestionPerTag]) = {
    val tagList = questionPerTagList.map(x => x.tag)
    var counter = 0;
    for {s <- tagList
         if s.contains("android") && s.contains("swing")} {
      counter += 1
      println(s)
    }
    counter
  }

  def tagClassifier(questionTagList: List[QuestionPerTag], tag: String): List[String] = {
    questionTagList.filter(x => x.tag.contains(tag)).map(x => x.id + ".json")
  }

  def strictTagClassifier(questionTagList: List[QuestionPerTag], tag: String, excludedTag: String): List[String] = {
    questionTagList.filter(x => x.tag.contains(tag) && !x.tag.contains(excludedTag)).map(x => x.id + ".json")
  }

  def writeSetListToFile(filePath: String, fileName: String, setList: List[String]): Unit = {
    val file = new File(filePath, fileName)
    val bw = new BufferedWriter(new FileWriter(file))
    setList.foreach(fileId => bw.write(fileId + "\n"))
    bw.close()
  }

  def getTrainingAndTestingSet(list: List[String], trainingListSize: Double): (List[String], List[String]) = {
    val trainingSet = scala.util.Random.shuffle(list).take((list.size * trainingListSize).toInt)
    val testingSet = list.diff(trainingSet)
    (trainingSet, testingSet)
  }

}
