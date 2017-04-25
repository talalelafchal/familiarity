package inf.usi.ch.setSelection

import inf.usi.ch.IdTagList

/**
  * Created by Talal on 25.04.17.
  */
object JavaDiscussionSet extends App{

  generateAndroidSwingFileList()

  def generateAndroidSwingFileList() = {
    val idTagList = IdTagList.deserialize("complete_data.dat")
    val javaList = Classifier.StrictClassifier(idTagList, "java")
    Classifier.writeSetListToFile("/Users/Talal/Tesi/familiarity/JavaSet", "javaSet.txt", javaList)
  }

}

