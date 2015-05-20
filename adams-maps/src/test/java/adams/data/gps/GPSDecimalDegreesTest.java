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
 * GPSDecimalDegreesTest.java
 * Copyright (C) 2013-2014 University of Waikato, Hamilton, New Zealand
 */

package adams.data.gps;

import junit.framework.Test;
import junit.framework.TestSuite;
import adams.env.Environment;
import adams.test.AdamsTestCase;

/**
 * Tests the adams.data.gps.GPSDecimalDegrees class. Run from commandline with: <br><br>
 * java adams.data.gps.GPSDecimalDegreesTest
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class GPSDecimalDegreesTest
  extends AdamsTestCase {

  /**
   * Initializes the test.
   *
   * @param name	the name of the test
   */
  public GPSDecimalDegreesTest(String name) {
    super(name);
  }

  /**
   * Tests the fromString methods method.
   */
  protected void performFromStringTest(String s, double lat, double lon, boolean swapped) {
    GPSDecimalDegrees 	gpsAct;
    GPSDecimalDegrees 	gpsRef;
    
    gpsAct = new GPSDecimalDegrees(s, swapped);
    gpsRef = new GPSDecimalDegrees(new Coordinate(lat), new Coordinate(lon));
    assertEquals("Latitude differs", gpsRef.getLatitude().toDecimal(), gpsAct.getLatitude().toDecimal());
    assertEquals("Longitude differs", gpsRef.getLongitude().toDecimal(), gpsAct.getLongitude().toDecimal());
  }
  
  /**
   * Tests the fromString method.
   */
  public void testFromString() {
    performFromStringTest("51.20523 W0.58551", 51.20523, 0.58551, false);
    performFromStringTest("-51.20523 0.58551", -51.20523, 0.58551, false);
    performFromStringTest("N51.20523 E0.58551", 51.20523, -0.58551, false);
    performFromStringTest("S51.20523 E0.58551", -51.20523, -0.58551, false);
    performFromStringTest("-51.20523 -0.58551", -51.20523, -0.58551, false);

    performFromStringTest("W0.58551 51.20523", 51.20523, 0.58551, true);
    performFromStringTest("0.58551 -51.20523", -51.20523, 0.58551, true);
    performFromStringTest("E0.58551 N51.20523", 51.20523, -0.58551, true);
    performFromStringTest("E0.58551 S51.20523", -51.20523, -0.58551, true);
    performFromStringTest("-0.58551 -51.20523", -51.20523, -0.58551, true);
  }

  /**
   * Tests the toString methods method.
   */
  protected void performToStringTest(double lat, double lon, String s) {
    GPSDecimalDegrees 	gps;
    
    gps = new GPSDecimalDegrees(new Coordinate(lat), new Coordinate(lon));
    assertEquals("String differs", s, gps.toString());
  }

  /**
   * Tests the toString method.
   */
  public void testToString() {
    performToStringTest(51.20523, 0.58551, "N51.20523 W0.58551");
    performToStringTest(51.20523, -0.58551, "N51.20523 E0.58551");
    performToStringTest(-51.20523, -0.58551, "S51.20523 E0.58551");
  }
  
  /**
   * Returns a test suite.
   *
   * @return		the test suite
   */
  public static Test suite() {
    return new TestSuite(GPSDecimalDegreesTest.class);
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
