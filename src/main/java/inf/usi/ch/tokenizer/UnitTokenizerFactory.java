package inf.usi.ch.tokenizer;

import com.aliasi.tokenizer.Tokenizer;
import com.aliasi.tokenizer.TokenizerFactory;


import java.io.Serializable;

/**
 * Created by Talal on 30.03.17.
 */
public class UnitTokenizerFactory implements TokenizerFactory, Serializable {
    public static final UnitTokenizerFactory INSTANCE = new UnitTokenizerFactory();

    private static String[] tokenList = {};

    private UnitTokenizerFactory() {}

    public void setTokens(String[] tokenList) {
        UnitTokenizerFactory.tokenList = tokenList;
    }

    public Tokenizer tokenizer(char[] ch, int start, int length) {
            return new TokenListTokenizer(tokenList);
    }

}
