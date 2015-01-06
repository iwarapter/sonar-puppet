package com.iadams.sonarqube.puppet.colorizer

import com.google.common.collect.Lists
//import com.iadams.sonarqube.puppet.api.PuppetKeyword
import com.iadams.sonarqube.puppet.core.Puppet
import org.sonar.api.web.CodeColorizerFormat
import org.sonar.colorizer.KeywordsTokenizer
import org.sonar.colorizer.Tokenizer

/**
 * Created by iwarapter
 */
class PuppetColorizer extends CodeColorizerFormat {

    private List<Tokenizer> tokenizers
    private static final END_TAG = '</span>'

    PuppetColorizer(){
        super(Puppet.KEY)
    }

    @Override
    List<Tokenizer> getTokenizers() {
        if (tokenizers == null) {
            tokenizers = Lists.newArrayList();
            //tokenizers.add(new KeywordsTokenizer("<span class=\"k\">", END_TAG, PuppetKeyword.keywordValues()));
            tokenizers.add(new PuppetDocStringTokenizer("<span class=\"s\">", END_TAG));
            tokenizers.add(new StringTokenizer("<span class=\"s\">", END_TAG));
            tokenizers.add(new PuppetDocTokenizer("<span class=\"cd\">", END_TAG));
        }
        return tokenizers;
    }
}
