package inf.usi.ch.tokenizer;

import antlr4JavaScript.ECMAScriptLexer;
import com.aliasi.tokenizer.Tokenizer;
import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;

/**
 * Created by Talal on 25.04.17.
 */
public class JavascriptANTLRTokenizer extends Tokenizer {
    CommonTokenStream tokens;
    int tokensSize;
    int index = 0;

    public JavascriptANTLRTokenizer(char[]ch) {
        String input  = new String(ch);
        ECMAScriptLexer lexer = new ECMAScriptLexer(new ANTLRInputStream(input));
        tokens = new CommonTokenStream(lexer);
        tokens.fill();
        // last token is always <EOF>
        tokensSize = tokens.size()-1;
    }


    @Override
    public String nextToken() {
        if(index == tokensSize){
            return null;
        }
        String tokenString = tokens.get(index).getText();
        index++;

        return tokenString;
    }
}
