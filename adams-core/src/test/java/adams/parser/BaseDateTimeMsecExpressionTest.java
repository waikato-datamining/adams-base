/*
 *   This program is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

/**
 * BaseDateTimeMsecExpressionTest.java
 * Copyright (C) 2015 University of Waikato, Hamilton, New Zealand
 */
package adams.parser;

import adams.core.base.BaseDateTimeMsec;
import adams.env.Environment;
import junit.framework.Test;
import junit.framework.TestSuite;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Tests the adams.parser.BaseDateTimeMsecExpression class. Run from commandline with: <br><br>
 * java adams.parser.BaseDateTimeMsecExpressionTest
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class BaseDateTimeMsecExpressionTest
  extends AbstractExpressionEvaluatorTestCase<Date, BaseDateTimeMsecExpression> {

  /**
   * Initializes the test.
   *
   * @param name	the name of the test
   */
  public BaseDateTimeMsecExpressionTest(String name) {
    super(name);
  }

  /**
   * Returns the expressions used in the regression test.
   *
   * @return		the data
   */
  @Override
  protected String[][] getRegressionExpressions() {
    return new String[][]{
	{
	  "-INF",
	  "+INF",
	  "1999-12-31 01:02:03.123 +1 MILLISECOND",
	  "1999-12-31 01:02:03.123 +1 SECOND",
	  "1999-12-31 01:02:03.123 +1 MINUTE",
	  "1999-12-31 01:02:03.123 +1 HOUR",
	  "1999-12-31 01:02:03.123 +1 DAY",
	  "1999-12-31 01:02:03.123 +1 WEEK",
	  "1999-12-31 01:02:03.123 +1 MONTH",
	  "1999-12-31 01:02:03.123 +1 YEAR",
	  "1999-12-31 01:02:03.123 -1 SECOND",
	  "1999-12-31 01:02:03.123 -1 MINUTE",
	  "1999-12-31 01:02:03.123 -1 HOUR",
	  "1999-12-31 01:02:03.123 -1 DAY",
	  "1999-12-31 01:02:03.123 -1 WEEK",
	  "1999-12-31 01:02:03.123 -1 MONTH",
	  "1999-12-31 01:02:03.123 -1 YEAR",
	  "1999-12-31 01:02:03.123",
	  "START +1 HOUR +1 DAY",
	  "END -1 HOUR -1 DAY",
	  "1999-12-31 01:02:03.123 -(7*2) YEAR -(1 + 1 + 1) MONTH -(3 * 2) DAY +(3 % 2) HOUR",
	  "1999-12-31 01:02:03.123 -(7*2) YEAR -(3 % 2) DAY",
	  "1999-12-31 01:02:03.123 -(7*2) YEAR -(3 ^ 2) DAY",
	  "1999-12-31 01:02:03.123 -(7*2) YEAR -(6 / 2) DAY",
	  "1999-12-31 01:02:03.123 abs(-3) YEAR",
	  "1999-12-31 01:02:03.123 log(exp(3)) YEAR",
	  "1999-12-31 01:02:03.123 rint(1.5) YEAR",
	  "1999-12-31 01:02:03.123 floor(1.5) YEAR",
	  "1999-12-31 01:02:03.123 ceil(1.5) YEAR",
	}
    };
  }

  /**
   * Returns the setups to use in the regression test.
   *
   * @return		the setups
   */
  @Override
  protected BaseDateTimeMsecExpression[] getRegressionSetups() {
    return new BaseDateTimeMsecExpression[]{new BaseDateTimeMsecExpression()};
  }

  /**
   * Tests setitng the START variable programmatically.
   */
  public static void testStart() {
    String expr = "START +2 DAY +1 MONTH +1 HOUR";
    SimpleDateFormat format = new SimpleDateFormat(BaseDateTimeMsec.FORMAT);
    String startStr = "1901-01-01 12:34:00.123";
    Date start = null;
    Date parsed = null;
    try {
      start = format.parse(startStr);
    }
    catch (Exception e) {
      fail("Failed to generate Date object from '" + startStr + "': " + e);
    }
    try {
      parsed = BaseDateTimeMsecExpression.evaluate(expr, start, null);
    }
    catch (Exception e) {
      fail("Failed to parse expression '" + expr + "': " + e);
    }
    assertNotNull("Generated null date", parsed);
    assertEquals("Generated different date", "1901-02-03 13:34:00.123", format.format(parsed));
  }

  /**
   * Tests setitng the END variable programmatically.
   */
  public static void testEnd() {
    String expr = "END -2 DAY -1 MONTH -1 HOUR";
    SimpleDateFormat format = new SimpleDateFormat(BaseDateTimeMsec.FORMAT);
    String endStr = "1901-10-27 12:34:00.123";
    Date end = null;
    Date parsed = null;
    try {
      end = format.parse(endStr);
    }
    catch (Exception e) {
      fail("Failed to generate Date object from '" + endStr + "': " + e);
    }
    try {
      parsed = BaseDateTimeMsecExpression.evaluate(expr, null, end);
    }
    catch (Exception e) {
      fail("Failed to parse expression '" + expr + "': " + e);
    }
    assertNotNull("Generated null date", parsed);
    assertEquals("Generated different date", "1901-09-25 11:34:00.123", format.format(parsed));
  }

  /**
   * Returns a test suite.
   *
   * @return		the test suite
   */
  public static Test suite() {
    return new TestSuite(BaseDateTimeMsecExpressionTest.class);
  }

  /**
   * Runs the test from commandline.
   *
   * @param args	ignored
   */
  public static void main(String[] args) {
    Environment.setEnvironmentClass(Environment.class);
    runTest(suite());
  }
}
