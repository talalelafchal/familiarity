package ANTLRTokenizerFactory;

import com.aliasi.tokenizer.IndoEuropeanTokenizerFactory;
import com.aliasi.tokenizer.Tokenizer;
import com.aliasi.tokenizer.TokenizerFactory;


import java.io.Serializable;

/**
 * Created by Talal on 30.03.17.
 */
public class ANTLRTokenizerFactory implements TokenizerFactory, Serializable {
    public static final ANTLRTokenizerFactory INSTANCE = new ANTLRTokenizerFactory();
    private static final IndoEuropeanTokenizerFactory DEFAULT_TOKENIZER_FACTORY = IndoEuropeanTokenizerFactory.INSTANCE;
    static final ANTLRTokenizerFactory FACTORY;

    private boolean stateIsJavaCode = false;

    static {
        FACTORY = INSTANCE;
    }

    public ANTLRTokenizerFactory() {
    }

    public void setStateIsJavaCode() {
        this.stateIsJavaCode = true;
    }

    public void setStateIsNonJavaCode() {
        this.stateIsJavaCode = false;
    }




    public Tokenizer tokenizer(char[] ch, int start, int length) {
        if (stateIsJavaCode) {
            return new ANTLRTokenizer(ch);
        } else {
            return DEFAULT_TOKENIZER_FACTORY.tokenizer(ch, start, length);
        }
    }

}
