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
package com.iadams.sonarqube.puppet.checks

import com.iadams.sonarqube.puppet.PuppetAstScanner
import org.sonar.squidbridge.api.SourceFile
import org.sonar.squidbridge.checks.CheckMessagesVerifier
import spock.lang.Specification

class FileModeCheckSpec extends Specification {

    private static final String MESSAGE_OCTAL = "Set the file mode to a 4-digit octal value surrounded by single quotes.";
    private static final String MESSAGE_DOUBLE_QUOTES = "Replace double quotes by single quotes.";
    private static final String MESSAGE_INVALID = "Update the file mode to a valid value surrounded by single quotes.";

    def "validate check"() {
        given:
        final FileModeCheck check = new FileModeCheck();
        SourceFile file = PuppetAstScanner.scanSingleFile(new File("src/test/resources/checks/file_mode.pp"), check);

        expect:
        CheckMessagesVerifier.verify(file.getCheckMessages())
                .next().atLine(2).withMessage(MESSAGE_INVALID)
                .next().atLine(22).withMessage(MESSAGE_INVALID)
                .next().atLine(32).withMessage(MESSAGE_INVALID)
                .next().atLine(44).withMessage(MESSAGE_INVALID)
                .next().atLine(52).withMessage(MESSAGE_OCTAL)
                .next().atLine(56).withMessage(MESSAGE_OCTAL)
                .next().atLine(60).withMessage(MESSAGE_OCTAL)
                .next().atLine(64).withMessage(MESSAGE_OCTAL)
                .next().atLine(68).withMessage(MESSAGE_DOUBLE_QUOTES)
                .next().atLine(72).withMessage(MESSAGE_INVALID)
                .next().atLine(76).withMessage(MESSAGE_DOUBLE_QUOTES)
                .next().atLine(80).withMessage(MESSAGE_INVALID)
                .noMore();
    }
}
