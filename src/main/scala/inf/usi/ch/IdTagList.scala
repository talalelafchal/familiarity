package inf.usi.ch

import java.io._
import ch.usi.inf.reveal.parsing.artifact.ArtifactSerializer

/**
  * Created by Talal on 03.03.17.
  */
object IdTagList {

  def serializeQuestionTag( listIdTag : List[QuestionPerTag],filename: String) = {
    val storeList = new ObjectOutputStream(new FileOutputStream(filename))
    storeList.writeObject(listIdTag)
    storeList.close
  }

  def deserialize(datFilePath: String): List[QuestionPerTag] = {
    val fis = new FileInputStream(datFilePath)
    val ois = new ObjectInputStream(fis)
    val deserializedList = ois.readObject().asInstanceOf[List[QuestionPerTag]]
    ois.close()
    deserializedList
  }

  private def getArtifact(file: File): QuestionPerTag = {
    val jsonFilePath = file.toString
    println(file)
    val artifact = ArtifactSerializer.deserializeFromFile(jsonFilePath)
    val questionPerTag = new QuestionPerTag(artifact.question.tags, artifact.question.id)
    questionPerTag
  }


   def getListOfFiles(dir: String): List[File] = {
    val d = new File(dir)
    if (d.exists && d.isDirectory) {
      d.listFiles.filter(_.isFile).toList
    } else {
      List[File]()
    }
  }

  def getTagIdList(stormedDataFolderPath: String): List[QuestionPerTag] = {
    val filesList = getListOfFiles(stormedDataFolderPath)
    filesList.par.map(file => getArtifact(file)).toList

  }

}
