/*
 * SonarQube Puppet Plugin
 * The MIT License (MIT)
 *
 * Copyright (c) 2015 Iain Adams and David RACODON
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
package com.iadams.sonarqube.puppet.highlighter

import com.google.common.base.Charsets
import com.iadams.sonarqube.puppet.PuppetConfiguration
import org.sonar.api.source.Highlightable
import spock.lang.Specification

class PuppetHighlighterSpec extends Specification {

  private final PuppetHighlighter highlighter = new PuppetHighlighter(new PuppetConfiguration(Charsets.UTF_8))
  private final Highlightable highlightable = Mock()
  private final Highlightable.HighlightingBuilder builder = Mock()

  def "no highlighting"() {
    given:
    highlightable.newHighlighting() >> builder

    when:
    highlighter.highlight(highlightable, '')

    then:
    1 * builder.done()
    0 * builder.highlight(_, _, _)
  }

  def "highlighting comments"() {
    given:
    highlightable.newHighlighting() >> builder

    when:
    highlighter.highlight(highlightable, '# my comments')

    then:
    1 * builder.done()
    1 * builder.highlight(0, 13, 'cd')
  }

  def "highlighting multiline comments"() {
    given:
    highlightable.newHighlighting() >> builder

    when:
    highlighter.highlight(highlightable, '# my comments\n#my comments 2')

    then:
    1 * builder.done()
    1 * builder.highlight(0, 13, 'cd')
    1 * builder.highlight(14, 28, 'cd')
  }

  def "highlighting single quoted string"() {
    given:
    highlightable.newHighlighting() >> builder

    when:
    highlighter.highlight(highlightable, "  'blabla' ")

    then:
    1 * builder.done()
    1 * builder.highlight(2, 10, 'p')
  }

  def "highlighting double quoted string"() {
    given:
    highlightable.newHighlighting() >> builder

    when:
    highlighter.highlight(highlightable, '  "blabla" ')

    then:
    1 * builder.done()
    1 * builder.highlight(2, 10, 'p')
  }

  def "highlighting variable"() {
    given:
    highlightable.newHighlighting() >> builder

    when:
    highlighter.highlight(highlightable, '$var=10')

    then:
    1 * builder.done()
    1 * builder.highlight(0, 4, 'a')
  }

  def "highlighting keyword"() {
    given:
    highlightable.newHighlighting() >> builder

    when:
    highlighter.highlight(highlightable, ' class abc {}')

    then:
    1 * builder.done()
    1 * builder.highlight(1, 6, 'k')
  }

  def "multiple highlighting"() {
    given:
    highlightable.newHighlighting() >> builder

    when:
    highlighter.highlight(highlightable, "#my comments\nclass abc {\n\$var='abc'}")

    then:
    1 * builder.done()
    1 * builder.highlight(0, 12, 'cd')
    1 * builder.highlight(13, 18, 'k')
    1 * builder.highlight(30, 35, 'p')
    1 * builder.highlight(25, 29, 'a')
  }

}
