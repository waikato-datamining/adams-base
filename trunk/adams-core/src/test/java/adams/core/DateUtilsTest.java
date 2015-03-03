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
 * DateUtilsTest.java
 * Copyright (C) 2012-2013 University of Waikato, Hamilton, New Zealand
 */

package adams.core;

import java.util.Calendar;
import java.util.Date;

import junit.framework.Test;
import junit.framework.TestSuite;
import adams.env.Environment;
import adams.test.AdamsTestCase;

/**
 * Tests the adams.core.DateUtils class. Run from commandline with: <p/>
 * java adams.core.DateUtilsTest
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class DateUtilsTest
  extends AdamsTestCase {

  /**
   * Initializes the test.
   *
   * @param name	the name of the test
   */
  public DateUtilsTest(String name) {
    super(name);
  }
  
  /**
   * Tests the getTimestampFormatter() method.
   */
  public void testTimestampFormatter() {
    assertEquals("date format differs", Constants.TIMESTAMP_FORMAT, DateUtils.getTimestampFormatter().toPattern());
  }
  
  /**
   * Tests the isValid() method.
   */
  public void testIsValid() {
    String pattern = "EE dd-MM-yyyy";
    assertTrue("pattern '" + pattern + "' should be valid", DateUtils.isValid(pattern));
    
    pattern = "ee dd-MM-yyyy";
    assertFalse("pattern '" + pattern + "' should not be valid", DateUtils.isValid(pattern));
  }
  
  /**
   * Tests the earlier() method.
   */
  public void testEarlier() {
    DateFormat dformat = new DateFormat(Constants.TIMESTAMP_FORMAT);
    Date date = dformat.parse("2012-02-07 13:51:00");

    Date earlier = dformat.parse("2012-02-07 11:51:00");
    assertEquals("dates differ (hours)", earlier, DateUtils.earlier(date, Calendar.HOUR, 2));
    
    earlier = dformat.parse("2012-02-05 13:51:00");
    assertEquals("dates differ (days)", earlier, DateUtils.earlier(date, Calendar.HOUR, 2 * 24));

    earlier = dformat.parse("2011-12-07 13:51:00");
    assertEquals("dates differ (months)", earlier, DateUtils.earlier(date, Calendar.MONTH, 2));

    earlier = dformat.parse("2010-02-07 13:51:00");
    assertEquals("dates differ (years)", earlier, DateUtils.earlier(date, Calendar.YEAR, 2));
  }
  
  /**
   * Tests the later() method.
   */
  public void testLater() {
    DateFormat dformat = new DateFormat(Constants.TIMESTAMP_FORMAT);
    Date date = dformat.parse("2012-02-07 13:51:00");

    Date later = dformat.parse("2012-02-07 15:51:00");
    assertEquals("dates differ (hours)", later, DateUtils.later(date, Calendar.HOUR, 2));
    
    later = dformat.parse("2012-02-09 13:51:00");
    assertEquals("dates differ (days)", later, DateUtils.later(date, Calendar.HOUR, 2 * 24));

    later = dformat.parse("2012-04-07 13:51:00");
    assertEquals("dates differ (months)", later, DateUtils.later(date, Calendar.MONTH, 2));

    later = dformat.parse("2014-02-07 13:51:00");
    assertEquals("dates differ (years)", later, DateUtils.later(date, Calendar.YEAR, 2));
  }
  
  /**
   * Tests the set(...) method.
   */
  public void testSet() {
    DateFormat dformat = new DateFormat(Constants.TIMESTAMP_FORMAT);
    Date date = dformat.parse("2012-02-07 13:51:00");
    
    assertNull("should have been null (feburary has max 29 days)", DateUtils.set(date, Calendar.DAY_OF_MONTH, 30));
    assertNotNull("should have been not null (hour can have 59 minutes)", DateUtils.set(date, Calendar.MINUTE, 59));
  }
  
  /**
   * Tests the isBefore(...) method.
   */
  public void testIsBefore() {
    DateFormat dformat = new DateFormat(Constants.TIMESTAMP_FORMAT);
    Date date1 = dformat.parse("2012-02-07 13:51:00");
    Date date2 = dformat.parse("2012-02-07 13:52:00");
    
    assertFalse("date should have been before", DateUtils.isBefore(date1, date2));
    assertTrue("date should not have been before", DateUtils.isBefore(date2, date1));
  }
  
  /**
   * Tests the isAfter(...) method.
   */
  public void testIsAfter() {
    DateFormat dformat = new DateFormat(Constants.TIMESTAMP_FORMAT);
    Date date1 = dformat.parse("2012-02-07 13:51:00");
    Date date2 = dformat.parse("2012-02-07 13:52:00");
    
    assertTrue("date should have been after", DateUtils.isAfter(date1, date2));
    assertFalse("date should not have been after", DateUtils.isAfter(date2, date1));
  }
  
  /**
   * Tests the difference(...) method.
   */
  public void testDifference() {
    DateFormat dformat = new DateFormat(Constants.TIMESTAMP_FORMAT);
    Date date1 = dformat.parse("2012-02-07 13:51:00");
    Date date2 = dformat.parse("2012-02-07 13:52:00");
    
    assertEquals("date should differ", (long) 60000, (long) DateUtils.difference(date1, date2));
  }

  /**
   * Returns a test suite.
   *
   * @return		the test suite
   */
  public static Test suite() {
    return new TestSuite(DateUtilsTest.class);
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
