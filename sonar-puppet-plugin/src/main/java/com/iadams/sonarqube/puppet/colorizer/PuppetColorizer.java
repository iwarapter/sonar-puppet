/*
 * SonarQube Puppet Plugin
 * The MIT License (MIT)
 *
 * Copyright (c) 2015 Iain Adams
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package com.iadams.sonarqube.puppet.colorizer;

import com.google.common.collect.Lists;
import com.iadams.sonarqube.puppet.Puppet;
import com.iadams.sonarqube.puppet.api.PuppetKeyword;
import org.sonar.api.web.CodeColorizerFormat;
import org.sonar.colorizer.*;

import java.util.List;

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
