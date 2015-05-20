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
 * GPSDecimalMinutesTest.java
 * Copyright (C) 2013 University of Waikato, Hamilton, New Zealand
 */

package adams.data.gps;

import junit.framework.Test;
import junit.framework.TestSuite;
import adams.env.Environment;
import adams.test.AdamsTestCase;

/**
 * Tests the adams.data.gps.GPSDecimalMinutes class. Run from commandline with: <br><br>
 * java adams.data.gps.GPSDecimalMinutesTest
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class GPSDecimalMinutesTest
  extends AdamsTestCase {

  /**
   * Initializes the test.
   *
   * @param name	the name of the test
   */
  public GPSDecimalMinutesTest(String name) {
    super(name);
  }

  /**
   * Tests the fromString methods method.
   */
  protected void performFromStringTest(String s, double lat, double lon, boolean swapped) {
    GPSDecimalMinutes 	gpsAct;
    GPSDecimalMinutes 	gpsRef;
    
    gpsAct = new GPSDecimalMinutes(s, swapped);
    gpsRef = new GPSDecimalMinutes(new Coordinate(lat), new Coordinate(lon));
    assertEquals("Latitude differs", gpsRef.getLatitude().toDecimal(), gpsAct.getLatitude().toDecimal());
    assertEquals("Longitude differs", gpsRef.getLongitude().toDecimal(), gpsAct.getLongitude().toDecimal());
  }

  /**
   * Tests the fromString method.
   */
  public void testFromString() {
    performFromStringTest("N 51 12.6 W0 35.4", 51.21, 0.59, false);
    performFromStringTest("N 51 12.6 E0 35.4", 51.21, -0.59, false);
    performFromStringTest("S51 12.6 0 35.4", -51.21, 0.59, false);
    performFromStringTest("S51 12.6 E0 35.4", -51.21, -0.59, false);

    performFromStringTest("W0 35.4 N 51 12.6", 51.21, 0.59, true);
    performFromStringTest("E0 35.4 N 51 12.6", 51.21, -0.59, true);
    performFromStringTest("0 35.4 S51 12.6", -51.21, 0.59, true);
    performFromStringTest("E0 35.4 S51 12.6", -51.21, -0.59, true);
  }

  /**
   * Tests the toString methods method.
   */
  protected void performToStringTest(double lat, double lon, String s) {
    GPSDecimalMinutes 	gps;
    
    gps = new GPSDecimalMinutes(new Coordinate(lat), new Coordinate(lon));
    assertEquals("String differs", s, gps.toString());
  }

  /**
   * Tests the toString method.
   */
  public void testToString() {
    performToStringTest(51.21, 0.59, "N51 12.6 W0 35.4");
    performToStringTest(51.21, -0.59, "N51 12.6 E0 35.4");
    performToStringTest(-51.21, -0.59, "S51 12.6 E0 35.4");
  }
  
  /**
   * Returns a test suite.
   *
   * @return		the test suite
   */
  public static Test suite() {
    return new TestSuite(GPSDecimalMinutesTest.class);
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
