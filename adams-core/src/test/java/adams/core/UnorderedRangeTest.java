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
 * UnorderedRangeTest.java
 * Copyright (C) 2019 University of Waikato, Hamilton, New Zealand
 */

package adams.core;

import adams.env.Environment;
import adams.test.AdamsTestCase;
import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * Tests the adams.core.UnorderedRange class. Run from commandline with: <br><br>
 * java adams.core.UnorderedRangeTest
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public class UnorderedRangeTest
  extends AdamsTestCase {

  /**
   * Initializes the test.
   *
   * @param name	the name of the test
   */
  public UnorderedRangeTest(String name) {
    super(name);
  }

  /**
   * Returns a new default UnorderedRange instance.
   * 
   * @return		the new instance
   */
  protected UnorderedRange newRange() {
    return newRange(null);
  }

  /**
   * Returns a new UnorderedRange instance with no maximum set.
   * 
   * @param index	the index to use, null for default constructor
   * @return		the new instance
   */
  protected UnorderedRange newRange(String index) {
    return newRange(index, -1);
  }

  /**
   * Returns a new UnorderedRange instance.
   * 
   * @param index	the index to use, null for default constructor
   * @param max		the maximum to use
   * @return		the new instance
   */
  protected UnorderedRange newRange(String index, int max) {
    if (index == null)
      return new UnorderedRange();
    else
      return new UnorderedRange(index, max);
  }

  /**
   * Tests "hasRange".
   */
  public void testHasRange() {
    UnorderedRange	range;

    range = newRange();
    assertFalse(range.hasRange());

    range = newRange("");
    assertFalse(range.hasRange());

    range = newRange("1");
    assertTrue(range.hasRange());

    range = newRange("1-5");
    assertTrue(range.hasRange());

    range = newRange("1", 10);
    assertTrue(range.hasRange());

    range = newRange("1-5", 10);
    assertTrue(range.hasRange());

    range = newRange("100", 10);
    assertTrue(range.hasRange());

    range = newRange("20-100", 10);
    assertTrue(range.hasRange());
  }

  /**
   * Tests an uninitialized UnorderedRange object.
   */
  public void testUninitialized() {
    UnorderedRange	range;

    range = newRange();
    assertTrue(range.isEmpty());
    assertEquals(0, range.getIntIndices().length);
    assertEquals(false, range.isInRange(0));
  }

  /**
   * Tests setting the range and retrieving the string representation again.
   */
  public void testSetting() {
    UnorderedRange	range;
    String	str;

    range = newRange();

    str = "1";
    range.setRange(str);
    assertEquals("Range strings differ", str, range.getRange());

    str = "#1";
    range.setRange(str);
    assertEquals("Range strings differ", str, range.getRange());

    str = "1-10";
    range.setRange(str);
    assertEquals("Range strings differ", str, range.getRange());

    str = "#1-#10";
    range.setRange(str);
    assertEquals("Range strings differ", str, range.getRange());

    str = "first-10";
    range.setRange(str);
    assertEquals("Range strings differ", str, range.getRange());

    str = "first-#10";
    range.setRange(str);
    assertEquals("Range strings differ", str, range.getRange());

    str = "1-last";
    range.setRange(str);
    assertEquals("Range strings differ", str, range.getRange());

    str = "#1-last";
    range.setRange(str);
    assertEquals("Range strings differ", str, range.getRange());

    str = "first-last";
    range.setRange(str);
    assertEquals("Range strings differ", str, range.getRange());
  }

  /**
   * Tests no range set, but max set.
   */
  public void testNoRange() {
    UnorderedRange	range;

    range = newRange();
    range.setMax(10);
    assertTrue(range.isEmpty());
    assertEquals(0, range.getIntIndices().length);
    assertEquals(false, range.isInRange(0));
  }

  /**
   * Tests ALL range set.
   */
  public void testAllRange() {
    UnorderedRange	range;

    range = newRange(Range.ALL);
    assertFalse(range.isEmpty());
    assertTrue(range.isAllRange());
  }

  /**
   * Tests no max set, but range set.
   */
  public void testNoMmax() {
    UnorderedRange	range;

    range = newRange();
    range.setRange("1");
    assertEquals(0, range.getIntIndices().length);
    assertEquals(false, range.isInRange(0));
  }

  /**
   * Tests a single index.
   */
  public void testSingleIndex() {
    UnorderedRange	range;

    range = newRange();
    range.setRange("1");
    range.setMax(10);
    assertEquals(1, range.getIntIndices().length);
    assertEquals(true, range.isInRange(0));

    range = newRange();
    range.setRange("#1");
    range.setMax(10);
    assertEquals(1, range.getIntIndices().length);
    assertEquals(true, range.isInRange(0));
  }

  /**
   * Tests a single index (first).
   */
  public void testSingleIndexFirst() {
    UnorderedRange	range;

    range = newRange();
    range.setRange("first");
    range.setMax(10);
    assertEquals(1, range.getIntIndices().length);
    assertEquals(true, range.isInRange(0));
  }

  /**
   * Tests a single index (second).
   */
  public void testSingleIndexSecond() {
    UnorderedRange	range;

    range = newRange();
    range.setRange("second");
    range.setMax(10);
    assertEquals(1, range.getIntIndices().length);
    assertEquals(true, range.isInRange(1));
  }

  /**
   * Tests a single index (third).
   */
  public void testSingleIndexThird() {
    UnorderedRange	range;

    range = newRange();
    range.setRange("third");
    range.setMax(10);
    assertEquals(1, range.getIntIndices().length);
    assertEquals(true, range.isInRange(2));
  }

  /**
   * Tests a single index (last-2).
   */
  public void testSingleIndexLastMinus2() {
    UnorderedRange	range;

    range = newRange();
    range.setRange("last_2");
    range.setMax(10);
    assertEquals(1, range.getIntIndices().length);
    assertEquals(true, range.isInRange(7));
  }

  /**
   * Tests a single index (last-1).
   */
  public void testSingleIndexLastMinus1() {
    UnorderedRange	range;

    range = newRange();
    range.setRange("last_1");
    range.setMax(10);
    assertEquals(1, range.getIntIndices().length);
    assertEquals(true, range.isInRange(8));
  }

  /**
   * Tests a single index (last).
   */
  public void testSingleIndexLast() {
    UnorderedRange	range;

    range = newRange();
    range.setRange("last");
    range.setMax(10);
    assertEquals(1, range.getIntIndices().length);
    assertEquals(true, range.isInRange(9));
  }

  /**
   * Tests a multiple single index.
   */
  public void testMultipleSingleIndex() {
    UnorderedRange	range;

    range = newRange();
    range.setRange("1,2,6,8");
    range.setMax(10);
    assertEquals(4, range.getIntIndices().length);
    assertEquals(true, range.isInRange(0));
    assertEquals(true, range.isInRange(1));
    assertEquals(false, range.isInRange(2));
    assertEquals(false, range.isInRange(3));
    assertEquals(false, range.isInRange(4));
    assertEquals(true, range.isInRange(5));
    assertEquals(false, range.isInRange(6));
    assertEquals(true, range.isInRange(7));
    assertEquals(false, range.isInRange(8));
    assertEquals(false, range.isInRange(9));
    assertEqualsArrays(new int[]{0,1,5,7}, range.getIntIndices());

    range = newRange();
    range.setRange("8,2,6,1");
    range.setMax(10);
    assertEquals(4, range.getIntIndices().length);
    assertEquals(true, range.isInRange(0));
    assertEquals(true, range.isInRange(1));
    assertEquals(false, range.isInRange(2));
    assertEquals(false, range.isInRange(3));
    assertEquals(false, range.isInRange(4));
    assertEquals(true, range.isInRange(5));
    assertEquals(false, range.isInRange(6));
    assertEquals(true, range.isInRange(7));
    assertEquals(false, range.isInRange(8));
    assertEquals(false, range.isInRange(9));
    assertEqualsArrays(new int[]{7,1,5,0}, range.getIntIndices());

    range = newRange();
    range.setRange("#1,#2,#6,#8");
    range.setMax(10);
    assertEquals(4, range.getIntIndices().length);
    assertEquals(true, range.isInRange(0));
    assertEquals(true, range.isInRange(1));
    assertEquals(false, range.isInRange(2));
    assertEquals(false, range.isInRange(3));
    assertEquals(false, range.isInRange(4));
    assertEquals(true, range.isInRange(5));
    assertEquals(false, range.isInRange(6));
    assertEquals(true, range.isInRange(7));
    assertEquals(false, range.isInRange(8));
    assertEquals(false, range.isInRange(9));
    assertEqualsArrays(new int[]{0,1,5,7}, range.getIntIndices());
  }

  /**
   * Tests a single index that is out of range of max.
   */
  public void testSingleIndexOutOfRange() {
    UnorderedRange	range;

    range = newRange();
    range.setRange("12");
    range.setMax(10);
    assertEquals(0, range.getIntIndices().length);
    assertEquals(false, range.isInRange(11));

    range = newRange();
    range.setRange("#12");
    range.setMax(10);
    assertEquals(0, range.getIntIndices().length);
    assertEquals(false, range.isInRange(11));
  }

  /**
   * Tests robustness if some indices of the range are outside the max.
   */
  public void testIndexOutOfRange2() {
    UnorderedRange	range;

    range = newRange();
    range.setRange("5,12");
    range.setMax(10);
    assertEquals(1, range.getIntIndices().length);
    assertEquals(true, range.isInRange(4));
    assertEquals(false, range.isInRange(11));

    range = newRange();
    range.setRange("#5,#12");
    range.setMax(10);
    assertEquals(1, range.getIntIndices().length);
    assertEquals(true, range.isInRange(4));
    assertEquals(false, range.isInRange(11));
  }

  /**
   * Tests if range is out of range.
   */
  public void testRangeOutOfRange() {
    UnorderedRange	range;

    range = newRange();
    range.setRange("11-20");
    range.setMax(10);
    assertEquals(0, range.getIntIndices().length);

    range = newRange();
    range.setRange("#11-#20");
    range.setMax(10);
    assertEquals(0, range.getIntIndices().length);
  }

  /**
   * Tests if range is out of range.
   */
  public void testRangeOutOfRange2() {
    UnorderedRange	range;

    range = newRange();
    range.setRange("5-9,11-20");
    range.setMax(10);
    assertEquals(5, range.getIntIndices().length);
    assertEquals(true, range.isInRange(4));
    assertEquals(false, range.isInRange(11));

    range = newRange();
    range.setRange("#5-#9,#11-#20");
    range.setMax(10);
    assertEquals(5, range.getIntIndices().length);
    assertEquals(true, range.isInRange(4));
    assertEquals(false, range.isInRange(11));
  }

  /**
   * Tests if range is out of range, checks partial range as well.
   */
  public void testRangeOutOfRange3() {
    UnorderedRange	range;

    range = newRange();
    range.setRange("5-12,15-20");
    range.setMax(10);
    assertEquals(6, range.getIntIndices().length);
    assertEquals(true, range.isInRange(4));
    assertEquals(false, range.isInRange(11));

    range = newRange();
    range.setRange("#5-#12,#15-#20");
    range.setMax(10);
    assertEquals(6, range.getIntIndices().length);
    assertEquals(true, range.isInRange(4));
    assertEquals(false, range.isInRange(11));
  }

  /**
   * Tests a single sub-range.
   */
  public void testSingleSubRange() {
    UnorderedRange	range;

    range = newRange();
    range.setRange("1-4");
    range.setMax(10);
    assertEquals(4, range.getIntIndices().length);
    assertEquals(true, range.isInRange(0));
    assertEquals(true, range.isInRange(1));
    assertEquals(true, range.isInRange(2));
    assertEquals(true, range.isInRange(3));
    assertEquals(false, range.isInRange(4));
    assertEquals(false, range.isInRange(5));
    assertEquals(false, range.isInRange(6));
    assertEquals(false, range.isInRange(7));
    assertEquals(false, range.isInRange(8));
    assertEquals(false, range.isInRange(9));

    range = newRange();
    range.setRange("#1-#4");
    range.setMax(10);
    assertEquals(4, range.getIntIndices().length);
    assertEquals(true, range.isInRange(0));
    assertEquals(true, range.isInRange(1));
    assertEquals(true, range.isInRange(2));
    assertEquals(true, range.isInRange(3));
    assertEquals(false, range.isInRange(4));
    assertEquals(false, range.isInRange(5));
    assertEquals(false, range.isInRange(6));
    assertEquals(false, range.isInRange(7));
    assertEquals(false, range.isInRange(8));
    assertEquals(false, range.isInRange(9));
  }

  /**
   * Tests multiple sub-ranges.
   */
  public void testMultipleSubRange() {
    UnorderedRange	range;

    range = newRange();
    range.setRange("1-4,6-8");
    range.setMax(10);
    assertEquals(7, range.getIntIndices().length);
    assertEquals(true, range.isInRange(0));
    assertEquals(true, range.isInRange(1));
    assertEquals(true, range.isInRange(2));
    assertEquals(true, range.isInRange(3));
    assertEquals(false, range.isInRange(4));
    assertEquals(true, range.isInRange(5));
    assertEquals(true, range.isInRange(6));
    assertEquals(true, range.isInRange(7));
    assertEquals(false, range.isInRange(8));
    assertEquals(false, range.isInRange(9));

    range = newRange();
    range.setRange("6-8,1-4");
    range.setMax(10);
    assertEquals(7, range.getIntIndices().length);
    assertEquals(true, range.isInRange(0));
    assertEquals(true, range.isInRange(1));
    assertEquals(true, range.isInRange(2));
    assertEquals(true, range.isInRange(3));
    assertEquals(false, range.isInRange(4));
    assertEquals(true, range.isInRange(5));
    assertEquals(true, range.isInRange(6));
    assertEquals(true, range.isInRange(7));
    assertEquals(false, range.isInRange(8));
    assertEquals(false, range.isInRange(9));

    range = newRange();
    range.setRange("#1-#4,#6-#8");
    range.setMax(10);
    assertEquals(7, range.getIntIndices().length);
    assertEquals(true, range.isInRange(0));
    assertEquals(true, range.isInRange(1));
    assertEquals(true, range.isInRange(2));
    assertEquals(true, range.isInRange(3));
    assertEquals(false, range.isInRange(4));
    assertEquals(true, range.isInRange(5));
    assertEquals(true, range.isInRange(6));
    assertEquals(true, range.isInRange(7));
    assertEquals(false, range.isInRange(8));
    assertEquals(false, range.isInRange(9));
  }

  /**
   * Tests mixed single indices and sub-ranges.
   */
  public void testMixed() {
    UnorderedRange	range;

    range = newRange();
    range.setRange("1-3,4,6-7,8");
    range.setMax(10);
    assertEquals(7, range.getIntIndices().length);
    assertEquals(true, range.isInRange(0));
    assertEquals(true, range.isInRange(1));
    assertEquals(true, range.isInRange(2));
    assertEquals(true, range.isInRange(3));
    assertEquals(false, range.isInRange(4));
    assertEquals(true, range.isInRange(5));
    assertEquals(true, range.isInRange(6));
    assertEquals(true, range.isInRange(7));
    assertEquals(false, range.isInRange(8));
    assertEquals(false, range.isInRange(9));

    range = newRange();
    range.setRange("#1-#3,#4,#6-#7,#8");
    range.setMax(10);
    assertEquals(7, range.getIntIndices().length);
    assertEquals(true, range.isInRange(0));
    assertEquals(true, range.isInRange(1));
    assertEquals(true, range.isInRange(2));
    assertEquals(true, range.isInRange(3));
    assertEquals(false, range.isInRange(4));
    assertEquals(true, range.isInRange(5));
    assertEquals(true, range.isInRange(6));
    assertEquals(true, range.isInRange(7));
    assertEquals(false, range.isInRange(8));
    assertEquals(false, range.isInRange(9));
  }

  /**
   * Tests the manual setting of indices, using 0-based int arrays.
   */
  public void testSetIndices() {
    UnorderedRange	range;

    range = newRange();
    range.setMax(10);

    range.setIndices(new int[]{});
    assertEquals("", range.getRange());

    range.setIndices(new int[]{0,1,2,3,4,5,6,7,8,9});
    assertEquals("1,2,3,4,5,6,7,8,9,10", range.getRange());

    range.setIndices(new int[]{0,1,3,4,6,7,9});
    assertEquals("1,2,4,5,7,8,10", range.getRange());

    range.setIndices(new int[]{0,2,4,6,9});
    assertEquals("1,3,5,7,10", range.getRange());

    range.setIndices(new int[]{4,0,2,9,6});
    assertEquals("5,1,3,10,7", range.getRange());

    range.setIndices(new int[]{0});
    assertEquals("1", range.getRange());

    range.setIndices(new int[]{9});
    assertEquals("10", range.getRange());

    range.setIndices(new int[]{4});
    assertEquals("5", range.getRange());

    range.setIndices(new int[]{0,9});
    assertEquals("1,10", range.getRange());
  }

  /**
   * Tests the static toRange() method.
   */
  public void testToRange() {
    int[] indices = new int[]{0,1,2,3,4};
    assertEquals("Range differs", "1,2,3,4,5", UnorderedRange.toRange(indices).getRange());

    indices = new int[]{0,1,3,4};
    assertEquals("Range differs", "1,2,4,5", UnorderedRange.toRange(indices).getRange());

    indices = new int[]{0,3,6,8};
    assertEquals("Range differs", "1,4,7,9", UnorderedRange.toRange(indices).getRange());
  }

  /**
   * Tests the static toExplicitRange() method.
   */
  public void testToExplicitRange() {
    UnorderedRange 	range;
    
    range = newRange();
    range.setRange("first-last");
    range.setMax(5);
    assertEquals("Range string differs", "1,2,3,4,5", range.toExplicitRange());
    
    range = newRange();
    range.setRange("second-last_1");
    range.setMax(5);
    assertEquals("Range string differs", "2,3,4", range.toExplicitRange());
  }

  /**
   * Returns a test suite.
   *
   * @return		the test suite
   */
  public static Test suite() {
    return new TestSuite(UnorderedRangeTest.class);
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
