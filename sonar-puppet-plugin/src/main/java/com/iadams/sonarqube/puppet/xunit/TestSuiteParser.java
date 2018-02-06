/*
 * SonarQube Puppet Plugin
 * Copyright (C) 2011-2018 SonarSource SA
 * mailto:info AT sonarsource DOT com
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package com.iadams.sonarqube.puppet.xunit;

import org.codehaus.staxmate.in.ElementFilter;
import org.codehaus.staxmate.in.SMHierarchicCursor;
import org.codehaus.staxmate.in.SMInputCursor;
import org.sonar.api.utils.ParsingUtils;
import com.iadams.sonarqube.puppet.parser.StaxParser.XmlStreamHandler;
import javax.xml.stream.XMLStreamException;
import java.text.ParseException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class TestSuiteParser implements XmlStreamHandler {

  private Map<String, TestSuite> testSuites = new HashMap<>();

  @Override
  public void stream(SMHierarchicCursor rootCursor) throws XMLStreamException {
    SMInputCursor testSuiteCursor = rootCursor.constructDescendantCursor(new ElementFilter("testsuite"));
    while (testSuiteCursor.getNext() != null) {
      String testSuiteClassName = testSuiteCursor.getAttrValue("name");

      SMInputCursor testCaseCursor = testSuiteCursor.childElementCursor("testcase");
      while (testCaseCursor.getNext() != null) {
        String testClassName = getClassname(testCaseCursor, testSuiteClassName);
        TestSuite report = testSuites.get(testClassName);
        if (report == null) {
          report = new TestSuite(testClassName);
          testSuites.put(testClassName, report);
        }
        report.addTestCase(parseTestCaseTag(testCaseCursor));
      }
    }
  }

  /**
   * Returns successfully parsed reports as a collection of TestSuite objects.
   */
  public Collection<TestSuite> getParsedReports() {
    return testSuites.values();
  }

  private static String getClassname(SMInputCursor testCaseCursor, String defaultClassname) throws XMLStreamException {
    String testClassName = testCaseCursor.getAttrValue("classname");
    return testClassName == null ? defaultClassname : testClassName;
  }

  private static TestCase parseTestCaseTag(SMInputCursor testCaseCursor) throws XMLStreamException {
    // TODO: get a decent grammar for the junit format and check the
    // logic inside this method against it.

    String name = parseTestCaseName(testCaseCursor);
    Double time = parseTime(testCaseCursor);
    String status = "ok";
    String stack = "";
    String msg = "";

    SMInputCursor childCursor = testCaseCursor.childElementCursor();
    if (childCursor.getNext() != null) {
      String elementName = childCursor.getLocalName();
      if ("skipped".equals(elementName)) {
        status = "skipped";
      } else if ("failure".equals(elementName)) {
        status = "failure";
        msg = childCursor.getAttrValue("message");
        stack = childCursor.collectDescendantText();
      } else if ("error".equals(elementName)) {
        status = "error";
        msg = childCursor.getAttrValue("message");
        stack = childCursor.collectDescendantText();
      }
    }
    return new TestCase(name, time.intValue(), status, stack, msg);
  }

  private static double parseTime(SMInputCursor testCaseCursor) throws XMLStreamException {
    double time;
    try {
      Double tmp = ParsingUtils.parseNumber(testCaseCursor.getAttrValue("time"), Locale.ENGLISH);
      time = ParsingUtils.scaleValue(tmp * 1000, 3);
    } catch (ParseException e) {
      throw new XMLStreamException(e);
    }
    return time;
  }

  private static String parseTestCaseName(SMInputCursor testCaseCursor) throws XMLStreamException {
    String name = testCaseCursor.getAttrValue("name");
    String classname = testCaseCursor.getAttrValue("CLASSNAME");
    if (classname != null) {
      name = classname + "/" + name;
    }
    return name;
  }

}
