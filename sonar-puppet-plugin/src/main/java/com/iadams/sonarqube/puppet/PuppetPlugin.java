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
package com.iadams.sonarqube.puppet;

import com.google.common.collect.ImmutableList;
import com.iadams.sonarqube.puppet.colorizer.PuppetColorizer;
import com.iadams.sonarqube.puppet.cpd.PuppetCpdMapping;
import com.iadams.sonarqube.puppet.metrics.PuppetLanguageMetrics;
import com.iadams.sonarqube.puppet.ui.PuppetResourcesWidget;

import java.util.List;

import org.sonar.api.SonarPlugin;
import org.sonar.api.config.PropertyDefinition;
import org.sonar.api.resources.Qualifiers;

public class PuppetPlugin extends SonarPlugin {

  public static final String FILE_SUFFIXES_KEY = "sonar.puppet.file.suffixes";

  public List getExtensions() {
    return ImmutableList.of(

      PropertyDefinition.builder(FILE_SUFFIXES_KEY)
        .name("File Suffixes")
        .description("Comma-separated list of suffixes of Puppet files to analyze.")
        .category("Puppet")
        .onQualifiers(Qualifiers.PROJECT)
        .defaultValue("pp")
        .build(),

      Puppet.class,
      PuppetColorizer.class,
      PuppetCpdMapping.class,
      PuppetProfile.class,
      PuppetSquidSensor.class,
      PuppetRuleRepository.class,
      PuppetLanguageMetrics.class,
      PuppetResouresDecorator.class,
      PuppetCommonRulesEngine.class,
      PuppetResourcesWidget.class
      );
  }
}
