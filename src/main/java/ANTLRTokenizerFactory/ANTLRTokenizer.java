package ANTLRTokenizerFactory;

import antlr4.JavaLexer;
import com.aliasi.tokenizer.Tokenizer;
import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;

/**
 * Created by Talal on 30.03.17.
 */
public class ANTLRTokenizer extends Tokenizer {
    CommonTokenStream tokens;
    int tokensSize;
    int index = 0;

    public ANTLRTokenizer(char[]ch){
        String input  = new String(ch);
        JavaLexer lexer = new JavaLexer(new ANTLRInputStream(input));
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
