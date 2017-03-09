package inf.usi.ch.codeLanguageModel

import ch.usi.inf.reveal.parsing.artifact.ArtifactSerializer
import ch.usi.inf.reveal.parsing.model.java.MethodDeclaratorNode
import ch.usi.inf.reveal.parsing.model.visitors.{IdentifierNodeVisitor, MethodDeclaratorNodeVisitor, TypeNodeVisitor}
import ch.usi.inf.reveal.parsing.units.{CodeIdentifiersMetaInformation, CodeMethodDeclaratorsMetaInformation, CodeTaggedUnit, NaturalLanguageTaggedUnit}

/**
  * Created by Talal on 06.03.17.
  */
object codeLanguageModelTokenize extends App {

  val stormedDataPath = "/Users/Talal/Tesi/stormed-dataset"

  val artifact = ArtifactSerializer.deserializeFromFile(stormedDataPath+ "/123.json")
//
//  val list = artifact.units.filter(x => x.isInstanceOf[NaturalLanguageTaggedUnit])
//  println(artifact.toText)
//  println("++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++")
//
//  list.foreach(x => println(x.rawText))


  //Instatiates a visitor collecting nodes in a List
  val typeListVisitor = TypeNodeVisitor.list()
  val identifierListVisitor = IdentifierNodeVisitor.list()
  val methodListVisitor = MethodDeclaratorNodeVisitor.list()

  //a list of IdentifierNode collect by visiting the artifact
  val collectedIdentifier = identifierListVisitor(List(), artifact)
  val collectedType = typeListVisitor(List(), artifact)
  val collectedMethod = methodListVisitor(List(), artifact)
//  println(collectedType.foreach(x => println(x.name)))


  println(collectedMethod.foreach(x => println(x.parameters.parameters.head.formalParameters.head.parameterType.name)))



}
