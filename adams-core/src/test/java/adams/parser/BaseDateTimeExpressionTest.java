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

/*
 * BaseDateTimeExpressionTest.java
 * Copyright (C) 2010-2017 University of Waikato, Hamilton, New Zealand
 */
package adams.parser;

import adams.core.BusinessDays;
import adams.core.base.BaseDateTime;
import adams.env.Environment;
import junit.framework.Test;
import junit.framework.TestSuite;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Tests the adams.parser.BaseDateTimeExpression class. Run from commandline with: <br><br>
 * java adams.parser.BaseDateTimeExpressionTest
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class BaseDateTimeExpressionTest
  extends AbstractExpressionEvaluatorTestCase<Date, BaseDateTimeExpression> {

  /**
   * Initializes the test.
   *
   * @param name	the name of the test
   */
  public BaseDateTimeExpressionTest(String name) {
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
	  "1999-12-31 01:02:03 +1 SECOND",
	  "1999-12-31 01:02:03 +1 MINUTE",
	  "1999-12-31 01:02:03 +1 HOUR",
	  "1999-12-31 01:02:03 +1 DAY",
	  "1999-12-31 01:02:03 +1 WEEK",
	  "1999-12-31 01:02:03 +1 MONTH",
	  "1999-12-31 01:02:03 +1 YEAR",
	  "1999-12-31 01:02:03 -1 SECOND",
	  "1999-12-31 01:02:03 -1 MINUTE",
	  "1999-12-31 01:02:03 -1 HOUR",
	  "1999-12-31 01:02:03 -1 DAY",
	  "1999-12-31 01:02:03 -1 WEEK",
	  "1999-12-31 01:02:03 -1 MONTH",
	  "1999-12-31 01:02:03 -1 YEAR",
	  "1999-12-31 01:02:03",
	  "START +1 HOUR +1 DAY",
	  "END -1 HOUR -1 DAY",
	  "1999-12-31 01:02:03 -(7*2) YEAR -(1 + 1 + 1) MONTH -(3 * 2) DAY +(3 % 2) HOUR",
	  "1999-12-31 01:02:03 -(7*2) YEAR -(3 % 2) DAY",
	  "1999-12-31 01:02:03 -(7*2) YEAR -(3 ^ 2) DAY",
	  "1999-12-31 01:02:03 -(7*2) YEAR -(6 / 2) DAY",
	  "1999-12-31 01:02:03 abs(-3) YEAR",
	  "1999-12-31 01:02:03 log(exp(3)) YEAR",
	  "1999-12-31 01:02:03 rint(1.5) YEAR",
	  "1999-12-31 01:02:03 floor(1.5) YEAR",
	  "1999-12-31 01:02:03 ceil(1.5) YEAR",
	  "1999-09-13 01:02:03 +5 BUSINESSDAY",  // Monday
	  "1999-09-13 01:02:03 -5 BUSINESSDAY",  // Monday
	  "1999-09-13 01:02:03 +1 BUSINESSDAY",  // Monday
	  "1999-09-13 01:02:03 -1 BUSINESSDAY",  // Monday
	  "1999-09-12 01:02:03 +1 BUSINESSDAY",  // Sunday
	  "1999-09-12 01:02:03 -1 BUSINESSDAY",  // Sunday
	  "1999-09-11 01:02:03 +1 BUSINESSDAY",  // Saturday
	  "1999-09-11 01:02:03 -1 BUSINESSDAY",  // Saturday
	  "1999-09-10 01:02:03 +1 BUSINESSDAY",  // Friday
	  "1999-09-10 01:02:03 -1 BUSINESSDAY",  // Friday
	}
    };
  }

  /**
   * Returns the setups to use in the regression test.
   *
   * @return		the setups
   */
  @Override
  protected BaseDateTimeExpression[] getRegressionSetups() {
    return new BaseDateTimeExpression[]{new BaseDateTimeExpression()};
  }

  /**
   * Tests setting the START variable programmatically.
   */
  public void testStart() {
    String expr = "START +2 DAY +1 MONTH +1 HOUR";
    SimpleDateFormat format = new SimpleDateFormat(BaseDateTime.FORMAT);
    String startStr = "1901-01-01 12:34:00";
    Date start = null;
    Date parsed = null;
    try {
      start = format.parse(startStr);
    }
    catch (Exception e) {
      fail("Failed to generate Date object from '" + startStr + "': " + e);
    }
    try {
      parsed = BaseDateTimeExpression.evaluate(expr, start, null, BusinessDays.MONDAY_TO_FRIDAY);
    }
    catch (Exception e) {
      fail("Failed to parse expression '" + expr + "': " + e);
    }
    assertNotNull("Generated null date", parsed);
    assertEquals("Generated different date", "1901-02-03 13:34:00", format.format(parsed));
  }

  /**
   * Tests setting the END variable programmatically.
   */
  public void testEnd() {
    String expr = "END -2 DAY -1 MONTH -1 HOUR";
    SimpleDateFormat format = new SimpleDateFormat(BaseDateTime.FORMAT);
    String endStr = "1901-10-27 12:34:00";
    Date end = null;
    Date parsed = null;
    try {
      end = format.parse(endStr);
    }
    catch (Exception e) {
      fail("Failed to generate Date object from '" + endStr + "': " + e);
    }
    try {
      parsed = BaseDateTimeExpression.evaluate(expr, null, end, BusinessDays.MONDAY_TO_FRIDAY);
    }
    catch (Exception e) {
      fail("Failed to parse expression '" + expr + "': " + e);
    }
    assertNotNull("Generated null date", parsed);
    assertEquals("Generated different date", "1901-09-25 11:34:00", format.format(parsed));
  }

  /**
   * Returns a test suite.
   *
   * @return		the test suite
   */
  public static Test suite() {
    return new TestSuite(BaseDateTimeExpressionTest.class);
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
