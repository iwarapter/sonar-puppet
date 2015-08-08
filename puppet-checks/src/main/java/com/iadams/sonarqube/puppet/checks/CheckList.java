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
package com.iadams.sonarqube.puppet.checks;

import com.google.common.collect.ImmutableList;

import java.util.List;

public final class CheckList {

  public static final String REPOSITORY_KEY = "puppet";

  public static final String SONARQUBE_WAY_PROFILE = "SonarQube Way";

  public static List<Class> getChecks() {
    return ImmutableList.<Class>of(
      ArrowsAlignmentCheck.class,
      CaseWithoutDefaultCheck.class,
      ClassAndDefineNamingConventionCheck.class,
      CommentConventionCheck.class,
      CommentRegularExpressionCheck.class,
      DocumentClassesAndDefinesCheck.class,
      DeprecatedNodeInheritanceCheck.class,
      DuplicatedParametersCheck.class,
      EnsureOrderingCheck.class,
      FaultyDoubleQuotedStringCheck.class,
      FileEnsurePropertyIsValidCheck.class,
      FileModeCheck.class,
      FileNameCheck.class,
      FixmeTagPresenceCheck.class,
      IfStatementWithoutElseClauseCheck.class,
      ImportStatementUsedCheck.class,
      InheritsAcrossNamespaceCheck.class,
      LineLengthCheck.class,
      MissingNewLineAtEndOfFileCheck.class,
      NestedClassesOrDefinesCheck.class,
      NosonarTagPresenceCheck.class,
      ParsingErrorCheck.class,
      QuotedBooleanCheck.class,
      RequiredParametersFirstCheck.class,
      ResourceWithSelectorCheck.class,
      RightToLeftChainingArrowsCheck.class,
      SelectorWithoutDefaultCheck.class,
      SingleQuotedStringContainingVariablesCheck.class,
      TabCharacterCheck.class,
      TodoTagPresenceCheck.class,
      TrailingWhitespaceCheck.class,
      UnquotedResourceTitleCheck.class,
      UserResourceLiteralNameCheck.class,
      UserResourcePasswordNotSetCheck.class,
      VariableNamingConventionCheck.class,
      VariableNotEnclosedInBracesCheck.class,
      XPathCheck.class
    );
  }
}
