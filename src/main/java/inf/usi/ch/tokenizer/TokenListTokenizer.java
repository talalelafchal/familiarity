package inf.usi.ch.tokenizer;

import com.aliasi.tokenizer.Tokenizer;

/**
 * Created by Talal on 07.04.17.
 */
public class TokenListTokenizer extends Tokenizer {
    private String[] tokenList = null;
    int index = 0;

    public TokenListTokenizer(String[] tokenList) {
        this.tokenList = tokenList;
    }

    @Override
    public String nextToken() {
        if (index < tokenList.length) {
                String nextToken =  tokenList[index];
                index++;
                return nextToken;
            } else
                return null;

    }

}
