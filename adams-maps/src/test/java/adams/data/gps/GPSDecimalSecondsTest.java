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
 * GPSDecimalSecondsTest.java
 * Copyright (C) 2013 University of Waikato, Hamilton, New Zealand
 */

package adams.data.gps;

import junit.framework.Test;
import junit.framework.TestSuite;
import adams.env.Environment;
import adams.test.AdamsTestCase;

/**
 * Tests the adams.data.gps.GPSDecimalSeconds class. Run from commandline with: <p/>
 * java adams.data.gps.GPSDecimalSecondsTest
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class GPSDecimalSecondsTest
  extends AdamsTestCase {

  /**
   * Initializes the test.
   *
   * @param name	the name of the test
   */
  public GPSDecimalSecondsTest(String name) {
    super(name);
  }

  /**
   * Tests the fromString methods method.
   */
  protected void performFromStringTest(String s, double lat, double lon, boolean swapped) {
    GPSDecimalSeconds 	gpsAct;
    GPSDecimalSeconds 	gpsRef;
    
    gpsAct = new GPSDecimalSeconds(s, swapped);
    gpsRef = new GPSDecimalSeconds(new Coordinate(lat), new Coordinate(lon));
    assertEquals("Latitude differs", gpsRef.getLatitude().toDecimal(), gpsAct.getLatitude().toDecimal());
    assertEquals("Longitude differs", gpsRef.getLongitude().toDecimal(), gpsAct.getLongitude().toDecimal());
  }

  /**
   * Tests the fromString method.
   */
  public void testFromString() {
    performFromStringTest("51 12 18.36 W0 35 7.8", 51.2051, 0.5855, false);
    performFromStringTest("51 12 18.36 E0 35 7.8", 51.2051, -0.5855, false);
    performFromStringTest("S51 12 18.36 E0 35 7.8", -51.2051, -0.5855, false);
    performFromStringTest("S51 12 18.36 W0 35 7.8", -51.2051, 0.5855, false);

    performFromStringTest("W0 35 7.8 51 12 18.36", 51.2051, 0.5855, true);
    performFromStringTest("E0 35 7.8 51 12 18.36", 51.2051, -0.5855, true);
    performFromStringTest("E0 35 7.8 S51 12 18.36", -51.2051, -0.5855, true);
    performFromStringTest("W0 35 7.8 S51 12 18.36", -51.2051, 0.5855, true);
  }

  /**
   * Tests the toString methods method.
   */
  protected void performToStringTest(double lat, double lon, String s) {
    GPSDecimalSeconds 	gps;
    
    gps = new GPSDecimalSeconds(new Coordinate(lat), new Coordinate(lon));
    assertEquals("String differs", s, gps.toString());
  }

  /**
   * Tests the toString method.
   */
  public void testToString() {
    performToStringTest(51.2051, 0.5855, "N51 12 18.36 W0 35 7.8");
    performToStringTest(51.2051, -0.5855, "N51 12 18.36 E0 35 7.8");
    performToStringTest(-51.2051, 0.5855, "S51 12 18.36 W0 35 7.8");
    performToStringTest(-51.2051, -0.5855, "S51 12 18.36 E0 35 7.8");
  }

  /**
   * Returns a test suite.
   *
   * @return		the test suite
   */
  public static Test suite() {
    return new TestSuite(GPSDecimalSecondsTest.class);
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
