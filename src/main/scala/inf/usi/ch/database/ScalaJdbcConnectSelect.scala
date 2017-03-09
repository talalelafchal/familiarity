package inf.usi.ch.database

import java.io.{BufferedWriter, File, FileWriter}
import java.sql.{Connection, DriverManager}

import org.jsoup.Jsoup

import scala.collection.mutable.ListBuffer


/**
  * Created by Talal on 07.03.17.
  */
object ScalaJdbcConnectSelect extends App {
  val url = "jdbc:postgresql://localhost:5432/stackoverflow_dump_dec16"
  val driver = "org.postgresql.Driver"
  val sqlQuerySwift =
    """SELECT * FROM posts WHERE tags like '%&lt;swift&gt;%' and post_type_id =1
    LIMIT 1000"""
  val username = "student_account"
  val password = "Student4Thesis"


  val swiftQuestionId: List[String] = get1000SwiftQuestionsId()
  println(swiftQuestionId)
  var swiftIdconnection: Connection = _
  var swiftAnswerconnection: Connection = _

  getSwiftAnswers()


  def get1000SwiftQuestionsId() = {

    val swiftIdList = new ListBuffer[String]
    try {
      Class.forName(driver)
      swiftIdconnection = DriverManager.getConnection(url, username, password)
      val statement = swiftIdconnection.createStatement
      val rs = statement.executeQuery(sqlQuerySwift)
      while (rs.next) {
        val id = rs.getString("id")
        val title = rs.getString("title")
        val bodyToSkip = Jsoup.parse(rs.getString("body")).text()
        val body = Jsoup.parse(bodyToSkip).text()
        writeQuestionToFile(id, title, body)
        swiftIdList += id
      }
    } catch {
      case e: Exception => e.printStackTrace
    }
    swiftIdconnection.close
    swiftIdList.toList
  }


  def getSwiftAnswers(): Unit = {

    try {
      Class.forName(driver)
      swiftAnswerconnection = DriverManager.getConnection(url, username, password)
      val statement = swiftAnswerconnection.createStatement
      swiftQuestionId.foreach(questionId => {

        try {
          val rs = statement.executeQuery("SELECT body FROM posts\nWHERE parent_id = " + questionId)
          var stringAnsewers = ""
          while (rs.next) {
            val bodyToSkip = Jsoup.parse(rs.getString("body")).text()
            val body = Jsoup.parse(bodyToSkip).text()
            stringAnsewers = stringAnsewers + "\n" + body

          }
          writeAnswerToFile(questionId, stringAnsewers)
        } catch {
          case e: Exception => e.printStackTrace
        }


      })
      swiftAnswerconnection.close

    }
  }


  def writeQuestionToFile(id: String, title: String, body: String) = {
    val file = new File("SwiftFiles", id + ".txt")
    val bw = new BufferedWriter(new FileWriter(file))
    bw.write(title + "\n" + body)
    bw.close()
  }

  def writeAnswerToFile(questionId: String, body: String) = {
    val file = new File("SwiftFiles", questionId + ".txt")
    val bw = new BufferedWriter(new FileWriter(file, true))
    println(questionId + "  ---->    " + body)
    bw.write(body)
    bw.close()
  }


}
