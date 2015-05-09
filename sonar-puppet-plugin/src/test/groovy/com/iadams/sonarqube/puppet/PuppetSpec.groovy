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
package com.iadams.sonarqube.puppet

import org.sonar.api.config.Settings
import spock.lang.Specification

/**
 * Created by iwarapter
 */
class PuppetSpec extends Specification {


    def "check language properties are set"(){
        given:
        Puppet language = new Puppet(new Settings())

        expect:
        language.getKey() == 'pp'
        language.getName() == 'Puppet'
        language.getFileSuffixes().size() == 1
        language.getFileSuffixes() == ['pp']
    }

    def "add custom file suffixes"() {
        given:
        def props = [:]
        props.put(PuppetPlugin.FILE_SUFFIXES_KEY, 'pp, puppet')

        Settings settings = new Settings()
        settings.addProperties(props)
        Puppet language = new Puppet(settings)

        expect:
        language.getFileSuffixes().size() == 2
        language.getFileSuffixes().contains('pp')
        language.getFileSuffixes().contains('puppet')
    }
}