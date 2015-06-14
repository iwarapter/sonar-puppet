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

import groovyx.net.http.HTTPBuilder
import groovyx.net.http.HttpResponseException
import groovyx.net.http.Method

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
			throw new FunctionalSpecException("Cannot query the metrics, details: ${e.message}", e)
		}
	}

	static void activateRepositoryRules(String url, String profile = 'Default', String language = 'pp', String repository = 'Pplint'){
		try {
			String key = defaultProfileKey(url, profile, language)
			def http = new HTTPBuilder(url)
			http.request(Method.POST){ req->
				uri.path = '/api/qualityprofiles/activate_rules'
				uri.query = [ profile_key: key , repositories: repository]
				headers.'Authorization' =
						"Basic ${"admin:admin".bytes.encodeBase64().toString()}"
			}
		}
		catch( HttpResponseException e){
			throw new FunctionalSpecException("Cannot deactivate all the rules, details: ${e.message}", e)
		}
	}

	static void deactivateAllRules(String url, String profile = 'Default', String language = 'pp'){
		try {
			String key = defaultProfileKey(url, profile, language)
			def http = new HTTPBuilder(url)
			http.request(Method.POST){ req->
				uri.path = '/api/qualityprofiles/deactivate_rules'
				uri.query = [ profile_key: key ]
				headers.'Authorization' =
						"Basic ${"admin:admin".bytes.encodeBase64().toString()}"
			}
		}
		catch( HttpResponseException e){
			throw new FunctionalSpecException("Cannot deactivate all the rules, details: ${e.message}", e)
		}
	}

	static String defaultProfileKey(String url, String profile, String language){
		try {
			def http = new HTTPBuilder(url)
			def resp = http.get(path: '/api/rules/app')
			for(i in resp.qualityprofiles){
				if(i.lang == language && i.name == profile){
					return i.key
				}
			}
			throw new FunctionalSpecException("Unable to find default profile for $language $profile")
		}
		catch( HttpResponseException e){
			throw new FunctionalSpecException("Cannot query the metrics, details: ${e.message}", e)
		}
	}
}
