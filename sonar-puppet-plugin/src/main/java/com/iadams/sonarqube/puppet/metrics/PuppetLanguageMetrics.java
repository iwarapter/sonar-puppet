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
package com.iadams.sonarqube.puppet.metrics;

import org.sonar.api.measures.Metric;
import org.sonar.api.measures.Metrics;

import java.util.Arrays;
import java.util.List;

public final class PuppetLanguageMetrics implements Metrics {

	public static final String DOMAIN = "Puppet";

	public static final String PUPPET_RESOURCES_KEY = "puppet_resources";

	public static final Metric PUPPET_RESOURCES = new Metric.Builder(PuppetLanguageMetrics.PUPPET_RESOURCES_KEY, "Puppet Resources", Metric.ValueType.INT)
			.setDescription("Puppet Resources")
			.setDirection(Metric.DIRECTION_NONE)
			.setQualitative(false)
			.setDomain(PuppetLanguageMetrics.DOMAIN)
			.setHidden(false)
			.create();

	@Override
	public List<Metric> getMetrics() {
		return Arrays.asList(
				PuppetLanguageMetrics.PUPPET_RESOURCES
		);
	}
}