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
 * BaseDateExpressionTest.java
 * Copyright (C) 2010-2013 University of Waikato, Hamilton, New Zealand
 */
package adams.parser;

import java.text.SimpleDateFormat;
import java.util.Date;

import junit.framework.Test;
import junit.framework.TestSuite;
import adams.core.base.BaseDate;
import adams.env.Environment;

/**
 * Tests the adams.parser.BaseDateExpression class. Run from commandline with: <br><br>
 * java adams.parser.BaseDateExpressionTest
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class BaseDateExpressionTest
  extends AbstractExpressionEvaluatorTestCase<Date, BaseDateExpression> {

  /**
   * Initializes the test.
   *
   * @param name	the name of the test
   */
  public BaseDateExpressionTest(String name) {
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
	  "1999-12-31 +1 DAY",
	  "1999-12-31 +1 WEEK",
	  "1999-12-31 +1 MONTH",
	  "1999-12-31 +1 YEAR",
	  "1999-12-31 -1 DAY",
	  "1999-12-31 -1 WEEK",
	  "1999-12-31 -1 MONTH",
	  "1999-12-31 -1 YEAR",
	  "1999-12-31",
	  "START +1 DAY",
	  "END -1 DAY",
	  "1999-12-31 -(7*2) YEAR -(1 + 1 + 1) MONTH -(3 * 2) DAY",
	  "1999-12-31 -(7*2) YEAR -(3 * 2) DAY -(1 + 1 + 1) MONTH",
	  "1999-12-31 -(7*2) YEAR -(3 % 2) DAY",
	  "1999-12-31 -(7*2) YEAR -(3 ^ 2) DAY",
	  "1999-12-31 -(7*2) YEAR -(6 / 2) DAY",
	  "1999-12-31 abs(-3) YEAR",
	  "1999-12-31 log(exp(3)) YEAR",
	  "1999-12-31 rint(1.5) YEAR",
	  "1999-12-31 floor(1.5) YEAR",
	  "1999-12-31 ceil(1.5) YEAR",
	}
    };
  }

  /**
   * Returns the setups to use in the regression test.
   *
   * @return		the setups
   */
  @Override
  protected BaseDateExpression[] getRegressionSetups() {
    return new BaseDateExpression[]{new BaseDateExpression()};
  }

  /**
   * Tests setitng the START variable programmatically.
   */
  public static void testStart() {
    String expr = "START +2 DAY +1 MONTH";
    SimpleDateFormat format = new SimpleDateFormat(BaseDate.FORMAT);
    String startStr = "1901-01-01";
    Date start = null;
    Date parsed = null;
    try {
      start = format.parse(startStr);
    }
    catch (Exception e) {
      fail("Failed to generate Date object from '" + startStr + "': " + e);
    }
    try {
      parsed = BaseDateExpression.evaluate(expr, start, null);
    }
    catch (Exception e) {
      fail("Failed to parse expression '" + expr + "': " + e);
    }
    assertNotNull("Generated null date", parsed);
    assertEquals("Generated different date", "1901-02-03", format.format(parsed));
  }

  /**
   * Tests setitng the END variable programmatically.
   */
  public static void testEnd() {
    String expr = "END -2 DAY -1 MONTH";
    SimpleDateFormat format = new SimpleDateFormat(BaseDate.FORMAT);
    String endStr = "1901-10-27";
    Date end = null;
    Date parsed = null;
    try {
      end = format.parse(endStr);
    }
    catch (Exception e) {
      fail("Failed to generate Date object from '" + endStr + "': " + e);
    }
    try {
      parsed = BaseDateExpression.evaluate(expr, null, end);
    }
    catch (Exception e) {
      fail("Failed to parse expression '" + expr + "': " + e);
    }
    assertNotNull("Generated null date", parsed);
    assertEquals("Generated different date", "1901-09-25", format.format(parsed));
  }

  /**
   * Returns a test suite.
   *
   * @return		the test suite
   */
  public static Test suite() {
    return new TestSuite(BaseDateExpressionTest.class);
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
