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
 * BaseTimeExpressionTest.java
 * Copyright (C) 2010-2013 University of Waikato, Hamilton, New Zealand
 */
package adams.parser;

import java.text.SimpleDateFormat;
import java.util.Date;

import junit.framework.Test;
import junit.framework.TestSuite;
import adams.core.base.BaseTime;
import adams.env.Environment;

/**
 * Tests the adams.parser.BaseTimeExpression class. Run from commandline with: <p/>
 * java adams.parser.BaseTimeExpressionTest
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class BaseTimeExpressionTest
  extends AbstractExpressionEvaluatorTestCase<Date, BaseTimeExpression> {

  /**
   * Initializes the test.
   *
   * @param name	the name of the test
   */
  public BaseTimeExpressionTest(String name) {
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
	  "01:02:03 +1 SECOND",
	  "01:02:03 +1 MINUTE",
	  "01:02:03 +1 HOUR",
	  "01:02:03 -1 SECOND",
	  "01:02:03 -1 MINUTE",
	  "01:02:03 -1 HOUR",
	  "01:02:03",
	  "START +1 HOUR",
	  "END -1 HOUR",
	  "11:02:03 -(3 + 2) HOUR",
	  "11:02:03 -(3 % 2) HOUR",
	  "11:02:03 -(3 ^ 2) HOUR",
	  "11:02:03 -(6 / 2) HOUR",
	  "11:02:03 +(3 * 2) HOUR",
	  "11:02:03 abs(-3) HOUR",
	  "11:02:03 log(exp(3)) HOUR",
	  "11:02:03 rint(1.5) HOUR",
	  "11:02:03 floor(1.5) HOUR",
	  "11:02:03 ceil(1.5) HOUR",
	}
    };
  }

  /**
   * Returns the setups to use in the regression test.
   *
   * @return		the setups
   */
  @Override
  protected BaseTimeExpression[] getRegressionSetups() {
    return new BaseTimeExpression[]{new BaseTimeExpression()};
  }

  /**
   * Tests setitng the START variable programmatically.
   */
  public static void testStart() {
    String expr = "START +1 HOUR +1 SECOND";
    SimpleDateFormat format = new SimpleDateFormat(BaseTime.FORMAT);
    String startStr = "12:34:00";
    Date start = null;
    Date parsed = null;
    try {
      start = format.parse(startStr);
    }
    catch (Exception e) {
      fail("Failed to generate Date object from '" + startStr + "': " + e);
    }
    try {
      parsed = BaseDateTimeExpression.evaluate(expr, start, null);
    }
    catch (Exception e) {
      fail("Failed to parse expression '" + expr + "': " + e);
    }
    assertNotNull("Generated null date", parsed);
    assertEquals("Generated different date", "13:34:01", format.format(parsed));
  }

  /**
   * Tests setitng the END variable programmatically.
   */
  public static void testEnd() {
    String expr = "END -1 HOUR -1 SECOND";
    SimpleDateFormat format = new SimpleDateFormat(BaseTime.FORMAT);
    String endStr = "12:34:00";
    Date end = null;
    Date parsed = null;
    try {
      end = format.parse(endStr);
    }
    catch (Exception e) {
      fail("Failed to generate Date object from '" + endStr + "': " + e);
    }
    try {
      parsed = BaseDateTimeExpression.evaluate(expr, null, end);
    }
    catch (Exception e) {
      fail("Failed to parse expression '" + expr + "': " + e);
    }
    assertNotNull("Generated null date", parsed);
    assertEquals("Generated different date", "11:33:59", format.format(parsed));
  }

  /**
   * Returns a test suite.
   *
   * @return		the test suite
   */
  public static Test suite() {
    return new TestSuite(BaseTimeExpressionTest.class);
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
