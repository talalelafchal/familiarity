package ANTLRTokenizerFactory;

import com.aliasi.tokenizer.Tokenizer;
import com.aliasi.tokenizer.TokenizerFactory;


import java.io.Serializable;

/**
 * Created by Talal on 30.03.17.
 */
public class ANTLRTokenizerFactory implements TokenizerFactory,Serializable {
    public static final ANTLRTokenizerFactory INSTANCE = new ANTLRTokenizerFactory();
    static final ANTLRTokenizerFactory FACTORY;

    static {
        FACTORY = INSTANCE;
    }

    public ANTLRTokenizerFactory() {
    }

    public Tokenizer tokenizer(char[] ch, int start, int length) {
        return new ANTLRTokenizer(ch);
    }

}
