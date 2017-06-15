package inf.usi.ch.databaseQuery

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

  val username = "student_account"
  val password = "Student4Thesis"

  var connection: Connection = _
  var answersConnection: Connection = _

  //swift
  //  val sqlQuerySwift =
  //    """SELECT * FROM posts WHERE tags like '%&lt;swift&gt;%' and post_type_id =1
  //    LIMIT 1000"""
  //  val swiftQuestionId: List[String] = getQuestionsId("SwiftFiles",sqlQuerySwift)
  //  println(swiftQuestionId)
  //  getAnswers("SwiftFiles",swiftQuestionId)


  //perl
  //  val sqlQueryPerl =
  //    """SELECT * FROM posts WHERE tags like '%&lt;perl&gt;%' and post_type_id =1
  //    LIMIT 1000"""
  //  val perlQuestionId: List[String] = getQuestionsId("PerlFiles",sqlQueryPerl)
  //  println(perlQuestionId)
  //  getAnswers("PerlFiles",perlQuestionId)

  //Matlab
  //  val sqlQueryMatlab =
  //    """SELECT * FROM posts WHERE tags like '%&lt;matlab&gt;%' and post_type_id =1
  //    LIMIT 1000"""
  //  val matLabQuestionId: List[String] = getQuestionsId("MatLabFiles", sqlQueryMatlab)
  //  println(matLabQuestionId)
  //  getAnswers("MatLabFiles", matLabQuestionId)

  //JavaScript
  //  val sqlQueryJavaScript =
  //      """SELECT * FROM posts WHERE tags like '&lt;javascript&gt;' and post_type_id =1
  //      LIMIT 1000"""
  //    val javascriptQuestionId: List[String] = getQuestionsId("JavascriptFiles", sqlQueryJavaScript)
  //    println(javascriptQuestionId)
  //    getAnswers("JavascriptFiles", javascriptQuestionId)

  //Experiment

  val sqlQueryJavaScript =
    """SELECT * FROM posts WHERE id = 6068803 and post_type_id =1"""
  val questionId: List[String] = getQuestionsId("ExperimentDiscussions", sqlQueryJavaScript)
      println(questionId)
      getAnswers("ExperimentDiscussions", questionId)


  def getQuestionsId(directory: String, sqlQuery: String): List[String] = {

    val idList = new ListBuffer[String]
    try {
      Class.forName(driver)
      connection = DriverManager.getConnection(url, username, password)
      val statement = connection.createStatement
      val rs = statement.executeQuery(sqlQuery)
      while (rs.next) {
        val id = rs.getString("id")
        val title = rs.getString("title")
        println("id : " + id + " Title : " + title)
        val bodyToSkip = Jsoup.parse(rs.getString("body")).text()
        //val body = Jsoup.parse(bodyToSkip).text()
        writeQuestionToFile(directory, id, title, bodyToSkip)
        idList += id
      }
    } catch {
      case e: Exception => e.printStackTrace
    }
    connection.close
    idList.toList
  }


  def getAnswers(directory: String, parentQuestionId: List[String]): Unit = {

    try {
      Class.forName(driver)
      answersConnection = DriverManager.getConnection(url, username, password)
      val statement = answersConnection.createStatement
      parentQuestionId.foreach(questionId => {

        try {
          val rs = statement.executeQuery("SELECT body FROM posts\nWHERE parent_id = " + questionId)
          var stringAnswers = ""
          while (rs.next) {
            val bodyToSkip = Jsoup.parse(rs.getString("body")).text()
            stringAnswers = stringAnswers + "\n" + bodyToSkip

          }
          writeAnswerToFile(directory, questionId, stringAnswers)
        } catch {
          case e: Exception => e.printStackTrace
        }


      })
      answersConnection.close

    }
  }


  def writeQuestionToFile(dir: String, id: String, title: String, body: String) = {
    val file = new File(dir, id + ".txt")
    val bw = new BufferedWriter(new FileWriter(file))
    bw.write(title + "\n" + body)
    bw.close()
  }

  def writeAnswerToFile(dir: String, questionId: String, body: String) = {
    val file = new File(dir, questionId + ".txt")
    val bw = new BufferedWriter(new FileWriter(file, true))
    bw.write(body)
    bw.close()
  }


}
