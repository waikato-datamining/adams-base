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
 * CoordinateTest.java
 * Copyright (C) 2014 University of Waikato, Hamilton, New Zealand
 */

package adams.data.gps;

import junit.framework.Test;
import junit.framework.TestSuite;
import adams.env.Environment;
import adams.test.AdamsTestCase;

/**
 * Tests the adams.data.gps.Coordinate class. Run from commandline with: <p/>
 * java adams.data.gps.CoordinateTest
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class CoordinateTest
  extends AdamsTestCase {

  /**
   * Initializes the test.
   *
   * @param name	the name of the test
   */
  public CoordinateTest(String name) {
    super(name);
  }

  /**
   * Tests the decimal constructor.
   * 
   * @param coord	the decimal coordinate
   * @param deg		the expected degrees
   * @param min		the expected minutes
   * @param sec		the expected seconds
   */
  protected void performDecimalTest(double coord, int deg, int min, double sec) {
    assertEquals("decimals differ", coord, new Coordinate(coord).toDecimal());
    assertEquals("negative differs", (coord < 0), new Coordinate(coord).isNegative());
    assertEquals("degrees differ", deg, new Coordinate(coord).getDegree());
    assertEquals("minutes differ", min, new Coordinate(coord).getMinute());
    assertEquals("seconds differ", sec, new Coordinate(coord).getSecond());
  }
  
  /**
   * Tests the decimal constructor.
   */
  public void testDecimal() {
    performDecimalTest(51.2051, 51, 12, 18.36);
    performDecimalTest(-51.2051, 51, 12, 18.36);
    performDecimalTest(0.5855, 0, 35, 7.8);
    performDecimalTest(-0.5855, 0, 35, 7.8);
  }

  /**
   * Tests the decimal constructor.
   * 
   * @param neg		whether the coordinate is negative
   * @param deg		the degrees
   * @param min		the minutes
   * @param sec		the seconds
   * @param coord	the expected decimal coordinate
   */
  protected void performDegMinSecTest(boolean neg, int deg, int min, double sec, double coord) {
    assertEquals("degrees differ", deg, new Coordinate(neg, deg, min, sec).getDegree());
    assertEquals("minutes differ", min, new Coordinate(neg, deg, min, sec).getMinute());
    assertEquals("seconds differ", sec, new Coordinate(neg, deg, min, sec).getSecond());
    assertEquals("decimals differ", coord, new Coordinate(neg, deg, min, sec).toDecimal());
  }
  
  /**
   * Tests the degree/minute/second constructor.
   */
  public void testDegMinSec() {
    performDegMinSecTest(false, 51, 12, 18.36, 51.2051);
    performDegMinSecTest(true, 51, 12, 18.36, -51.2051);
  }
  
  /**
   * Returns a test suite.
   *
   * @return		the test suite
   */
  public static Test suite() {
    return new TestSuite(CoordinateTest.class);
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
