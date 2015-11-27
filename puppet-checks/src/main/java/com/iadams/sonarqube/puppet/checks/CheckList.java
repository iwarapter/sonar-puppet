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

  private CheckList() {
  }

  public static List<Class> getChecks() {
    return ImmutableList.<Class>of(
      ArrowsAlignmentCheck.class,
      AutoLoaderLayoutCheck.class,
      BOMCheck.class,
      BooleanInversionCheck.class,
      CaseWithoutDefaultCheck.class,
      ClassAndDefineNamingConventionCheck.class,
      CollapsibleIfStatementsCheck.class,
      CommentConventionCheck.class,
      CommentRegularExpressionCheck.class,
      ComplexExpressionCheck.class,
      DocumentClassesAndDefinesCheck.class,
      DeprecatedNodeInheritanceCheck.class,
      DeprecatedOperatorsCheck.class,
      DuplicateConditionCheck.class,
      DuplicatedHashKeysCheck.class,
      DuplicatedParametersCheck.class,
      EmptyBlocksCheck.class,
      EnsureOrderingCheck.class,
      ExcessSpacesWhenAccessingHashesArraysCheck.class,
      FaultyQuoteUsageCheck.class,
      FileEnsurePropertyIsValidCheck.class,
      FileModeCheck.class,
      FileNameCheck.class,
      FixmeTagPresenceCheck.class,
      IfStatementFormattingCheck.class,
      IfStatementWithoutElseClauseCheck.class,
      ImportStatementUsedCheck.class,
      IndentationCheck.class,
      InheritsAcrossNamespaceCheck.class,
      LineLengthCheck.class,
      LintIgnoreCheck.class,
      LiteralBooleanInComparisonCheck.class,
      MetadataJsonFilePresentCheck.class,
      MissingNewLineAtEndOfFileCheck.class,
      NestedClassesOrDefinesCheck.class,
      NestedCasesAndSelectorsCheck.class,
      NestedIfStatementsCheck.class,
      NoopUsageCheck.class,
      NosonarTagPresenceCheck.class,
      OneIncludePerLineCheck.class,
      ParsingErrorCheck.class,
      PuppetURLModulesCheck.class,
      QuotedBooleanCheck.class,
      QuotedEnumerableCheck.class,
      ReadmeFilePresentCheck.class,
      RequiredParametersFirstCheck.class,
      ResourceDefaultFirstCheck.class,
      ResourceDefaultNamingConventionCheck.class,
      ResourceWithSelectorCheck.class,
      RightToLeftChainingArrowsCheck.class,
      SelectorWithoutDefaultCheck.class,
      SingleQuotedStringContainingVariablesCheck.class,
      TabCharacterCheck.class,
      TestsDirectoryPresentCheck.class,
      TodoTagPresenceCheck.class,
      TooComplexClassesAndDefinesCheck.class,
      TrailingCommasCheck.class,
      TrailingWhitespaceCheck.class,
      UnquotedNodeNameCheck.class,
      UnquotedResourceTitleCheck.class,
      UselessIfStatementParenthesesCheck.class,
      UserResourceLiteralNameCheck.class,
      UserResourcePasswordNotSetCheck.class,
      VariableNamingConventionCheck.class,
      VariableNotEnclosedInBracesCheck.class,
      XPathCheck.class
      );
  }
}
