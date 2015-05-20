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
 * DateFormatTest.java
 * Copyright (C) 2013 University of Waikato, Hamilton, New Zealand
 */

package adams.core;

import junit.framework.Test;
import junit.framework.TestSuite;
import adams.env.Environment;
import adams.test.AdamsTestCase;

/**
 * Tests the adams.core.DateFormat class. Run from commandline with: <br><br>
 * java adams.core.DateFormatTest
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class DateFormatTest
  extends AdamsTestCase {

  /**
   * Initializes the test.
   *
   * @param name	the name of the test
   */
  public DateFormatTest(String name) {
    super(name);
  }

  /**
   * Tests parsing using {@link Constants#DATE_FORMAT}.
   */
  public void testParseDate() {
    DateFormat df = new DateFormat(Constants.DATE_FORMAT);
    assertTrue(df.check("2012-01-01"));
    assertTrue(df.check("12-01-01"));
    assertFalse(df.check("00:01"));
  }

  /**
   * Tests parsing using {@link Constants#TIMESTAMP_FORMAT}.
   */
  public void testParseTimestamp() {
    DateFormat df = new DateFormat(Constants.TIMESTAMP_FORMAT);
    assertTrue(df.check("2012-01-01 01:02:03"));
    assertTrue(df.check("12-01-01 01:02:03"));
    assertFalse(df.check("2012-01-01"));
    assertFalse(df.check("00:01"));
  }

  /**
   * Tests parsing using {@link Constants#TIMESTAMP_FORMAT_ISO8601}.
   */
  public void testParseTimestampIso() {
    DateFormat df = new DateFormat(Constants.TIMESTAMP_FORMAT_ISO8601);
    assertTrue(df.check("2012-01-01T01:02:03"));
    assertTrue(df.check("12-01-01T01:02:03"));
    assertFalse(df.check("2012-01-01 01:02:03"));
    assertFalse(df.check("2012-01-01"));
    assertFalse(df.check("00:01"));
  }

  /**
   * Tests parsing using {@link Constants#TIMESTAMP_FORMAT_MSECS}.
   */
  public void testParseTimestampMsecs() {
    DateFormat df = new DateFormat(Constants.TIMESTAMP_FORMAT_MSECS);
    assertTrue(df.check("2012-01-01 01:02:03.123"));
    assertTrue(df.check("12-01-01 01:02:03.123"));
    assertFalse(df.check("2012-01-01 01:02:03"));
    assertFalse(df.check("2012-01-01"));
    assertFalse(df.check("00:01"));
  }

  /**
   * Tests parsing using {@link Constants#TIME_FORMAT}.
   */
  public void testParseTime() {
    DateFormat df = new DateFormat(Constants.TIME_FORMAT);
    assertFalse(df.check("2012-01-01"));
    assertFalse(df.check("12-01-01"));
    assertTrue(df.check("00:01:02"));
    assertFalse(df.check("00:01"));
  }

  /**
   * Tests parsing using {@link Constants#TIME_FORMAT_MSECS}.
   */
  public void testParseTimeMsecs() {
    DateFormat df = new DateFormat(Constants.TIME_FORMAT_MSECS);
    assertFalse(df.check("2012-01-01"));
    assertFalse(df.check("12-01-01"));
    assertTrue(df.check("00:01:02.123"));
    assertFalse(df.check("00:01"));
    assertFalse(df.check("00:01:02"));
  }
  
  /**
   * Returns a test suite.
   *
   * @return		the test suite
   */
  public static Test suite() {
    return new TestSuite(DateFormatTest.class);
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
