package com.iadams.sonarqube.puppet;

import com.google.common.collect.ImmutableList;
import com.iadams.sonarqube.puppet.api.PuppetKeyword;
import org.sonar.colorizer.KeywordsTokenizer;
import org.sonar.colorizer.Tokenizer;
import org.sonar.sslr.toolkit.Toolkit;

import java.util.List;

/**
 * Created by iwarapter
 */
public final class PuppetToolkit {

	private PuppetToolkit() {
	}

	public static void main(String[] args) {
		Toolkit toolkit = new Toolkit("SSLR :: Puppet :: Toolkit", new PuppetConfigurationModel());
		toolkit.run();
	}

	public static List<Tokenizer> getPuppetTokenizers() {
		return ImmutableList.of(
				(Tokenizer) new KeywordsTokenizer("<span class=\"k\">", "</span>", PuppetKeyword.keywordValues()));
	}
}
