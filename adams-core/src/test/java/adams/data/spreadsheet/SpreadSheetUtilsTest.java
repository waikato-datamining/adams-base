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
 * Copyright (C) 2013 University of Waikato, Hamilton, New Zealand
 */
package adams.data.spreadsheet;

import adams.test.AdamsTestCase;

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
}
