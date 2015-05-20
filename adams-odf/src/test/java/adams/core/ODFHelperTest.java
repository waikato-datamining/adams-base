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
 * ODFHelperTest.java
 * Copyright (C) 2013 University of Waikato, Hamilton, New Zealand
 */

package adams.core;

import junit.framework.Test;
import junit.framework.TestSuite;
import adams.env.Environment;
import adams.test.AbstractTestHelper;
import adams.test.AdamsTestCase;
import adams.test.TestHelper;

/**
 * Tests the adams.core.ODFHelper class. Run from commandline with: <br><br>
 * java adams.core.ODFHelperTest
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class ODFHelperTest
  extends AdamsTestCase {

  /**
   * Initializes the test.
   *
   * @param name	the name of the test
   */
  public ODFHelperTest(String name) {
    super(name);
  }

  /**
   * Returns the test helper class to use.
   *
   * @return		the helper class instance
   */
  @Override
  protected AbstractTestHelper newTestHelper() {
    return new TestHelper(this, "adams/core/data");
  }

  /**
   * Tests the cell positions.
   */
  public void testCellPositions() {
    int row = 0;
    int col = 0;
    String pos = ODFHelper.getCellPosition(row, col);
    assertEquals("position differs", "A1", pos);

    row = 1;
    col = 2;
    pos = ODFHelper.getCellPosition(row, col);
    assertEquals("position differs", "C2", pos);

    row = 1;
    col = 25;
    pos = ODFHelper.getCellPosition(row, col);
    assertEquals("position differs", "Z2", pos);

    row = 1;
    col = 26;
    pos = ODFHelper.getCellPosition(row, col);
    assertEquals("position differs", "AA2", pos);

    row = 2;
    col = 51;
    pos = ODFHelper.getCellPosition(row, col);
    assertEquals("position differs", "AZ3", pos);

    row = 2;
    col = 52;
    pos = ODFHelper.getCellPosition(row, col);
    assertEquals("position differs", "BA3", pos);

    row = 2;
    col = 701;
    pos = ODFHelper.getCellPosition(row, col);
    assertEquals("position differs", "ZZ3", pos);

    row = 2;
    col = 702;
    pos = ODFHelper.getCellPosition(row, col);
    assertEquals("position differs", "AAA3", pos);

    row = 20;
    col = 900;
    pos = ODFHelper.getCellPosition(row, col);
    assertEquals("position differs", "AHQ21", pos);

    row = 23;
    col = 1000;
    pos = ODFHelper.getCellPosition(row, col);
    assertEquals("position differs", "ALM24", pos);

    row = 23;
    col = 26 + 676 + 17576 + 1;
    pos = ODFHelper.getCellPosition(row, col);
    assertEquals("position differs", "AAAB24", pos);

    row = 23;
    col = 26 + 676 + 17576 + 456976 + 1;
    pos = ODFHelper.getCellPosition(row, col);
    assertEquals("position differs", "AAAAB24", pos);

    row = 23;
    col = 26 + 676 + 17576 + 456976 + 11881376 + 1;
    pos = ODFHelper.getCellPosition(row, col);
    assertEquals("position differs", "AAAAAB24", pos);
  }
  
  /**
   * Tests the getCellLocation(String) method.
   */
  public void testCellLocations() {
    try {
      int row = 0;
      int col = 0;
      int[] loc = ODFHelper.getCellLocation("A1");
      assertEquals("row differs", row, loc[0]);
      assertEquals("col differs", col, loc[1]);

      row = 1;
      col = 2;
      loc = ODFHelper.getCellLocation("C2");
      assertEquals("row differs", row, loc[0]);
      assertEquals("col differs", col, loc[1]);

      row = 1;
      col = 25;
      loc = ODFHelper.getCellLocation("Z2");
      assertEquals("row differs", row, loc[0]);
      assertEquals("col differs", col, loc[1]);

      row = 1;
      col = 26;
      loc = ODFHelper.getCellLocation("AA2");
      assertEquals("row differs", row, loc[0]);
      assertEquals("col differs", col, loc[1]);

      row = 2;
      col = 51;
      loc = ODFHelper.getCellLocation("AZ3");
      assertEquals("row differs", row, loc[0]);
      assertEquals("col differs", col, loc[1]);

      row = 2;
      col = 52;
      loc = ODFHelper.getCellLocation("BA3");
      assertEquals("row differs", row, loc[0]);
      assertEquals("col differs", col, loc[1]);

      row = 2;
      col = 701;
      loc = ODFHelper.getCellLocation("ZZ3");
      assertEquals("row differs", row, loc[0]);
      assertEquals("col differs", col, loc[1]);

      row = 2;
      col = 702;
      loc = ODFHelper.getCellLocation("AAA3");
      assertEquals("row differs", row, loc[0]);
      assertEquals("col differs", col, loc[1]);

      row = 20;
      col = 900;
      loc = ODFHelper.getCellLocation("AHQ21");
      assertEquals("row differs", row, loc[0]);
      assertEquals("col differs", col, loc[1]);

      row = 23;
      col = 1000;
      loc = ODFHelper.getCellLocation("ALM24");
      assertEquals("row differs", row, loc[0]);
      assertEquals("col differs", col, loc[1]);

      row = 23;
      col = 26 + 676 + 17576 + 1;
      loc = ODFHelper.getCellLocation("AAAB24");
      assertEquals("row differs", row, loc[0]);
      assertEquals("col differs", col, loc[1]);

      row = 23;
      col = 26 + 676 + 17576 + 456976 + 1;
      loc = ODFHelper.getCellLocation("AAAAB24");
      assertEquals("row differs", row, loc[0]);
      assertEquals("col differs", col, loc[1]);

      row = 23;
      col = 26 + 676 + 17576 + 456976 + 11881376 + 1;
      loc = ODFHelper.getCellLocation("AAAAAB24");
      assertEquals("row differs", row, loc[0]);
      assertEquals("col differs", col, loc[1]);
    }
    catch (Exception e) {
      e.printStackTrace();
      fail(e.toString());
    }
  }

  /**
   * Returns a test suite.
   *
   * @return		the test suite
   */
  public static Test suite() {
    return new TestSuite(ODFHelperTest.class);
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
