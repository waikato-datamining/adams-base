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
 * ExcelHelperTest.java
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
 * Tests the adams.core.ExcelHelper class. Run from commandline with: <p/>
 * java adams.core.ExcelHelperTest
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class ExcelHelperTest
  extends AdamsTestCase {

  /**
   * Initializes the test.
   *
   * @param name	the name of the test
   */
  public ExcelHelperTest(String name) {
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
    String pos = ExcelHelper.getCellPosition(row, col);
    assertEquals("position differs", "A1", pos);

    row = 1;
    col = 2;
    pos = ExcelHelper.getCellPosition(row, col);
    assertEquals("position differs", "C2", pos);

    row = 1;
    col = 25;
    pos = ExcelHelper.getCellPosition(row, col);
    assertEquals("position differs", "Z2", pos);

    row = 1;
    col = 26;
    pos = ExcelHelper.getCellPosition(row, col);
    assertEquals("position differs", "AA2", pos);

    row = 2;
    col = 51;
    pos = ExcelHelper.getCellPosition(row, col);
    assertEquals("position differs", "AZ3", pos);

    row = 2;
    col = 52;
    pos = ExcelHelper.getCellPosition(row, col);
    assertEquals("position differs", "BA3", pos);

    row = 2;
    col = 701;
    pos = ExcelHelper.getCellPosition(row, col);
    assertEquals("position differs", "ZZ3", pos);

    row = 2;
    col = 702;
    pos = ExcelHelper.getCellPosition(row, col);
    assertEquals("position differs", "AAA3", pos);

    row = 20;
    col = 900;
    pos = ExcelHelper.getCellPosition(row, col);
    assertEquals("position differs", "AHQ21", pos);

    row = 23;
    col = 1000;
    pos = ExcelHelper.getCellPosition(row, col);
    assertEquals("position differs", "ALM24", pos);

    row = 23;
    col = 26 + 676 + 17576 + 1;
    pos = ExcelHelper.getCellPosition(row, col);
    assertEquals("position differs", "AAAB24", pos);

    row = 23;
    col = 26 + 676 + 17576 + 456976 + 1;
    pos = ExcelHelper.getCellPosition(row, col);
    assertEquals("position differs", "AAAAB24", pos);

    row = 23;
    col = 26 + 676 + 17576 + 456976 + 11881376 + 1;
    pos = ExcelHelper.getCellPosition(row, col);
    assertEquals("position differs", "AAAAAB24", pos);
  }
  
  /**
   * Tests the getCellLocation(String) method.
   */
  public void testCellLocations() {
    try {
      int row = 0;
      int col = 0;
      int[] loc = ExcelHelper.getCellLocation("A1");
      assertEquals("row differs", row, loc[0]);
      assertEquals("col differs", col, loc[1]);

      row = 1;
      col = 2;
      loc = ExcelHelper.getCellLocation("C2");
      assertEquals("row differs", row, loc[0]);
      assertEquals("col differs", col, loc[1]);

      row = 1;
      col = 25;
      loc = ExcelHelper.getCellLocation("Z2");
      assertEquals("row differs", row, loc[0]);
      assertEquals("col differs", col, loc[1]);

      row = 1;
      col = 26;
      loc = ExcelHelper.getCellLocation("AA2");
      assertEquals("row differs", row, loc[0]);
      assertEquals("col differs", col, loc[1]);

      row = 2;
      col = 51;
      loc = ExcelHelper.getCellLocation("AZ3");
      assertEquals("row differs", row, loc[0]);
      assertEquals("col differs", col, loc[1]);

      row = 2;
      col = 52;
      loc = ExcelHelper.getCellLocation("BA3");
      assertEquals("row differs", row, loc[0]);
      assertEquals("col differs", col, loc[1]);

      row = 2;
      col = 701;
      loc = ExcelHelper.getCellLocation("ZZ3");
      assertEquals("row differs", row, loc[0]);
      assertEquals("col differs", col, loc[1]);

      row = 2;
      col = 702;
      loc = ExcelHelper.getCellLocation("AAA3");
      assertEquals("row differs", row, loc[0]);
      assertEquals("col differs", col, loc[1]);

      row = 20;
      col = 900;
      loc = ExcelHelper.getCellLocation("AHQ21");
      assertEquals("row differs", row, loc[0]);
      assertEquals("col differs", col, loc[1]);

      row = 23;
      col = 1000;
      loc = ExcelHelper.getCellLocation("ALM24");
      assertEquals("row differs", row, loc[0]);
      assertEquals("col differs", col, loc[1]);

      row = 23;
      col = 26 + 676 + 17576 + 1;
      loc = ExcelHelper.getCellLocation("AAAB24");
      assertEquals("row differs", row, loc[0]);
      assertEquals("col differs", col, loc[1]);

      row = 23;
      col = 26 + 676 + 17576 + 456976 + 1;
      loc = ExcelHelper.getCellLocation("AAAAB24");
      assertEquals("row differs", row, loc[0]);
      assertEquals("col differs", col, loc[1]);

      row = 23;
      col = 26 + 676 + 17576 + 456976 + 11881376 + 1;
      loc = ExcelHelper.getCellLocation("AAAAAB24");
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
    return new TestSuite(ExcelHelperTest.class);
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
