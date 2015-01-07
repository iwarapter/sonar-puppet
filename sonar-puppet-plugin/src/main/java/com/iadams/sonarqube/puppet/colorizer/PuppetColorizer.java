package com.iadams.sonarqube.puppet.colorizer;

import com.google.common.collect.Lists;
import com.iadams.sonarqube.puppet.Puppet;
import com.iadams.sonarqube.puppet.api.PuppetKeyword;
import org.sonar.api.web.CodeColorizerFormat;
import org.sonar.colorizer.*;

import java.util.List;

/**
 * Created by iwarapter
 */
public class PuppetColorizer extends CodeColorizerFormat {

    private List<Tokenizer> tokenizers;
    private static final String END_TAG = "</span>";

    public PuppetColorizer() {
        super(Puppet.KEY);
    }

    @Override
    public List<Tokenizer> getTokenizers() {
        if (tokenizers == null) {
            tokenizers = Lists.newArrayList();
            tokenizers.add(new KeywordsTokenizer("<span class=\"k\">", END_TAG, PuppetKeyword.keywordValues()));
            tokenizers.add(new PuppetDocStringTokenizer("<span class=\"s\">", END_TAG));
            tokenizers.add(new StringTokenizer("<span class=\"s\">", END_TAG));
            tokenizers.add(new PuppetDocTokenizer("<span class=\"cd\">", END_TAG));
            tokenizers.add(new CDocTokenizer("<span class=\"cd\">", END_TAG));
            tokenizers.add(new CppDocTokenizer("<span class=\"cppd\">", END_TAG));
        }
        return tokenizers;
    }
}
