package inf.usi.ch.stormedClientService

import ch.usi.inf.reveal.parsing.model.HASTNode
import com.aliasi.lm.TokenizedLM
import inf.usi.ch.javaLMTokenizer.JavaLM

/**
  * Created by Talal on 06.06.17.
  */
object ServiceJavaLM extends JavaLM {

  def train(tokenizedLM: TokenizedLM, hastNodeSeq: HASTNode) = {
    trainJavaCode(hastNodeSeq, tokenizedLM)
  }


}
