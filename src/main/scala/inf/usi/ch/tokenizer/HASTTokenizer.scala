package inf.usi.ch.tokenizer

import ch.usi.inf.reveal.parsing.model.{HASTNode, HASTNodeSequence, TextFragmentNode}
import ch.usi.inf.reveal.parsing.model.java.JavaASTNode
import com.aliasi.tokenizer.IndoEuropeanTokenizerFactory

import scala.util.{ Try}

/**
  * Created by Talal on 07.04.17.
  */
object HASTTokenizer {

  private val DEFAULT_TOKENIZER_FACTORY = IndoEuropeanTokenizerFactory.INSTANCE
  import ch.usi.inf.reveal.parsing.model.Implicits._

  def tokenize(hASTNode: HASTNode): Array[String] = {

    hASTNode match {
      case nodeSequence: HASTNodeSequence => {
        nodeSequence.fragments.flatMap {
          tokenize _
        }.toArray
      }

      case textNode: TextFragmentNode => {
        val text = textNode.text
        val array = DEFAULT_TOKENIZER_FACTORY.tokenizer(text.toCharArray, 0, text.length).tokenize()
        array
      }

      case javaNode: JavaASTNode => {
        //remove brackets and parenthesis and ;
        val code = Try(javaNode.toCode.replaceAll("[^a-zA-Z0-9 ]"," "))
        val tokens = new JavaANTLRTokenizer(code.get.toCharArray).tokenize()
        tokens
      }

      case _ => {
        val defaultCode = Try(hASTNode.toCode).get
        val array = DEFAULT_TOKENIZER_FACTORY.tokenizer(defaultCode.toCharArray, 0, defaultCode.length).tokenize()
        array
      }

    }

  }

}
