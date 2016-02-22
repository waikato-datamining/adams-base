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
 * SpreadSheetUtilsTest.java
 * Copyright (C) 2013-2016 University of Waikato, Hamilton, New Zealand
 */
package adams.data.spreadsheet;

import adams.env.Environment;
import adams.test.AdamsTestCase;
import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * Tests the {@link SpreadSheetUtils} class.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class SpreadSheetUtilsTest
  extends AdamsTestCase {

  /**
   * Constructs the test case. Called by subclasses.
   *
   * @param name 	the name of the test
   */
  public SpreadSheetUtilsTest(String name) {
    super(name);
  }

  /**
   * Compares the two arrays.
   * 
   * @param expected	what should have been produced
   * @param generated	what actually was generated
   */
  protected void compare(String[] expected, String[] generated) {
    int		i;
    
    assertNotNull("shouldn't be null", generated);
    assertEquals("number of elements differ", expected.length, generated.length);
    for (i = 0; i < expected.length; i++)
      assertEquals("elements differ", expected[i], generated[i]);
  }
  
  /**
   * Tests the split method.
   */
  public void testSplit() {
    String 	s;
    
    s = "A,B,C";
    compare(new String[]{"A", "B", "C"}, SpreadSheetUtils.split(s, ','));
    
    s = "A,B,C";
    compare(new String[]{"A,B,C"}, SpreadSheetUtils.split(s, '-'));
    
    s = "A,\"B,D\",C";
    compare(new String[]{"A","\"B,D\"","C"}, SpreadSheetUtils.split(s, ','));
    
    s = "A-B-C";
    compare(new String[]{"A","B","C"}, SpreadSheetUtils.split(s, '-'));
    
    s = "A-\"B-D\"-C";
    compare(new String[]{"A","\"B-D\"","C"}, SpreadSheetUtils.split(s, '-'));
  }

  /**
   * Tests the cell positions.
   */
  public void testCellPositions() {
    int row = 0;
    int col = 0;
    String pos = SpreadSheetUtils.getCellPosition(row, col);
    assertEquals("position differs", "A2", pos);

    row = 1;
    col = 2;
    pos = SpreadSheetUtils.getCellPosition(row, col);
    assertEquals("position differs", "C3", pos);

    row = 1;
    col = 25;
    pos = SpreadSheetUtils.getCellPosition(row, col);
    assertEquals("position differs", "Z3", pos);

    row = 1;
    col = 26;
    pos = SpreadSheetUtils.getCellPosition(row, col);
    assertEquals("position differs", "AA3", pos);

    row = 2;
    col = 51;
    pos = SpreadSheetUtils.getCellPosition(row, col);
    assertEquals("position differs", "AZ4", pos);

    row = 2;
    col = 52;
    pos = SpreadSheetUtils.getCellPosition(row, col);
    assertEquals("position differs", "BA4", pos);

    row = 2;
    col = 701;
    pos = SpreadSheetUtils.getCellPosition(row, col);
    assertEquals("position differs", "ZZ4", pos);

    row = 2;
    col = 702;
    pos = SpreadSheetUtils.getCellPosition(row, col);
    assertEquals("position differs", "AAA4", pos);

    row = 20;
    col = 900;
    pos = SpreadSheetUtils.getCellPosition(row, col);
    assertEquals("position differs", "AHQ22", pos);

    row = 23;
    col = 1000;
    pos = SpreadSheetUtils.getCellPosition(row, col);
    assertEquals("position differs", "ALM25", pos);

    row = 23;
    col = 26 + 676 + 17576 + 1;
    pos = SpreadSheetUtils.getCellPosition(row, col);
    assertEquals("position differs", "AAAB25", pos);

    row = 23;
    col = 26 + 676 + 17576 + 456976 + 1;
    pos = SpreadSheetUtils.getCellPosition(row, col);
    assertEquals("position differs", "AAAAB25", pos);

    row = 23;
    col = 26 + 676 + 17576 + 456976 + 11881376 + 1;
    pos = SpreadSheetUtils.getCellPosition(row, col);
    assertEquals("position differs", "AAAAAB25", pos);
  }

  /**
   * Tests the getCellLocation(String) method.
   */
  public void testCellLocations() {
    try {
      int row = 0;
      int col = 0;
      int[] loc = SpreadSheetUtils.getCellLocation("A2");
      assertEquals("row differs", row, loc[0]);
      assertEquals("col differs", col, loc[1]);

      row = 1;
      col = 2;
      loc = SpreadSheetUtils.getCellLocation("C3");
      assertEquals("row differs", row, loc[0]);
      assertEquals("col differs", col, loc[1]);

      row = 1;
      col = 25;
      loc = SpreadSheetUtils.getCellLocation("Z3");
      assertEquals("row differs", row, loc[0]);
      assertEquals("col differs", col, loc[1]);

      row = 1;
      col = 26;
      loc = SpreadSheetUtils.getCellLocation("AA3");
      assertEquals("row differs", row, loc[0]);
      assertEquals("col differs", col, loc[1]);

      row = 2;
      col = 51;
      loc = SpreadSheetUtils.getCellLocation("AZ4");
      assertEquals("row differs", row, loc[0]);
      assertEquals("col differs", col, loc[1]);

      row = 2;
      col = 52;
      loc = SpreadSheetUtils.getCellLocation("BA4");
      assertEquals("row differs", row, loc[0]);
      assertEquals("col differs", col, loc[1]);

      row = 2;
      col = 701;
      loc = SpreadSheetUtils.getCellLocation("ZZ4");
      assertEquals("row differs", row, loc[0]);
      assertEquals("col differs", col, loc[1]);

      row = 2;
      col = 702;
      loc = SpreadSheetUtils.getCellLocation("AAA4");
      assertEquals("row differs", row, loc[0]);
      assertEquals("col differs", col, loc[1]);

      row = 20;
      col = 900;
      loc = SpreadSheetUtils.getCellLocation("AHQ22");
      assertEquals("row differs", row, loc[0]);
      assertEquals("col differs", col, loc[1]);

      row = 23;
      col = 1000;
      loc = SpreadSheetUtils.getCellLocation("ALM25");
      assertEquals("row differs", row, loc[0]);
      assertEquals("col differs", col, loc[1]);

      row = 23;
      col = 26 + 676 + 17576 + 1;
      loc = SpreadSheetUtils.getCellLocation("AAAB25");
      assertEquals("row differs", row, loc[0]);
      assertEquals("col differs", col, loc[1]);

      row = 23;
      col = 26 + 676 + 17576 + 456976 + 1;
      loc = SpreadSheetUtils.getCellLocation("AAAAB25");
      assertEquals("row differs", row, loc[0]);
      assertEquals("col differs", col, loc[1]);

      row = 23;
      col = 26 + 676 + 17576 + 456976 + 11881376 + 1;
      loc = SpreadSheetUtils.getCellLocation("AAAAAB25");
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
    return new TestSuite(SpreadSheetUtilsTest.class);
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
