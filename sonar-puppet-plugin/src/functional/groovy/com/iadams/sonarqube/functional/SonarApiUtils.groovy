/*
 * Sonar Puppet Plugin
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
package com.iadams.sonarqube.functional

import groovy.json.JsonSlurper
import groovyx.net.http.ContentType
import groovyx.net.http.HTTPBuilder
import groovyx.net.http.HttpResponseException

/**
 * @author iwarapter
 */
final class SonarApiUtils {

	static void queryMetrics(String url, String project, Map<String, Float> metrics_to_query){
		try {
			def http = new HTTPBuilder(url)
			def resp = http.get(path: '/api/resources', query: [resource: project, metrics: metrics_to_query.keySet().join(',')])

			Map<String, Float> results = [:]
			resp.'msr'[0].each{
				results.put(it.key, it.val)
			}

			assert metrics_to_query.equals(results) : "Expected:\n$metrics_to_query\nReceived:\n$results\n\n" + metrics_to_query.minus(metrics_to_query.intersect(results))
		}
		catch( HttpResponseException e){
			assert false : "Cannot query the metrics, details: ${e.message}"
		}
	}
}
