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
 * StatUtilsTest.java
 * Copyright (C) 2015-2025 University of Waikato, Hamilton, New Zealand
 */

package adams.data.statistics;

import adams.env.Environment;
import adams.test.AdamsTestCase;
import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * Tests the adams.data.statistics.StatUtils class. Run from commandline with: <br><br>
 * java adams.data.statistics.StatUtilsTest
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public class StatUtilsTest
  extends AdamsTestCase {

  /**
   * Initializes the test.
   *
   * @param name	the name of the test
   */
  public StatUtilsTest(String name) {
    super(name);
  }

  /**
   * Returns a test suite.
   *
   * @return		the test suite
   */
  public static Test suite() {
    return new TestSuite(StatUtilsTest.class);
  }

  /**
   * Tests the min methods.
   */
  public void testMin() {
    assertEquals(1, StatUtils.min(new int[]{1, 3, 2}));
    assertEquals(1, StatUtils.min(new Integer[]{1, 3, 2}));
    assertEquals(1.1, StatUtils.min(new double[]{1.1, 3.3, 2.2}));
    assertEquals(1.1, StatUtils.min(new Double[]{1.1, 3.3, 2.2}));
  }

  /**
   * Tests the max methods.
   */
  public void testMax() {
    assertEquals(3, StatUtils.max(new int[]{1, 3, 2}));
    assertEquals(3, StatUtils.max(new Integer[]{1, 3, 2}));
    assertEquals(3.3, StatUtils.max(new double[]{1.1, 3.3, 2.2}));
    assertEquals(3.3, StatUtils.max(new Double[]{1.1, 3.3, 2.2}));
  }

  /**
   * Tests the toNumberArray methods.
   */
  public void testToNumberArray() {
    assertEqualsArrays(new Byte[]{1, 2, 3}, StatUtils.toNumberArray(new byte[]{1, 2, 3}));
    assertEqualsArrays(new Short[]{1, 2, 3}, StatUtils.toNumberArray(new short[]{1, 2, 3}));
    assertEqualsArrays(new Integer[]{1, 2, 3}, StatUtils.toNumberArray(new int[]{1, 2, 3}));
    assertEqualsArrays(new Long[]{1L, 2L, 3L}, StatUtils.toNumberArray(new long[]{1L, 2L, 3L}));
    assertEqualsArrays(new Float[]{1.1f, 2.2f, 3.3f}, StatUtils.toNumberArray(new float[]{1.1f, 2.2f, 3.3f}));
    assertEqualsArrays(new Double[]{1.1, 2.2, 3.3}, StatUtils.toNumberArray(new double[]{1.1, 2.2, 3.3}));
  }

  /**
   * Tests the flatten methods.
   */
  public void testFlatten() {
    assertEqualsArrays(new byte[]{1, 2, 3, 4, 5, 6}, StatUtils.flatten(new byte[][]{{1, 2, 3},{4, 5, 6}}));
    assertEqualsArrays(new short[]{1, 2, 3, 4, 5, 6}, StatUtils.flatten(new short[][]{{1, 2, 3}, {4, 5, 6}}));
    assertEqualsArrays(new int[]{1, 2, 3, 4, 5, 6}, StatUtils.flatten(new int[][]{{1, 2, 3},{4, 5, 6}}));
    assertEqualsArrays(new long[]{1, 2, 3, 4, 5, 6}, StatUtils.flatten(new long[][]{{1, 2, 3}, {4, 5, 6}}));
    assertEqualsArrays(new float[]{1.1f, 2.2f, 3.3f, 4.4f, 5.5f, 6.6f}, StatUtils.flatten(new float[][]{{1.1f, 2.2f, 3.3f}, {4.4f, 5.5f, 6.6f}}));
    assertEqualsArrays(new double[]{1.1, 2.2, 3.3, 4.4, 5.5, 6.6}, StatUtils.flatten(new double[][]{{1.1, 2.2, 3.3}, {4.4, 5.5, 6.6}}));
    assertEqualsArrays(new Double[]{1.1, 2.2, 3.3, 4.4, 5.5, 6.6}, StatUtils.flatten(new Double[][]{{1.1, 2.2, 3.3}, {4.4, 5.5, 6.6}}));
  }

  /**
   * Tests the normalizeRange method.
   */
  public void testNormalizeRange() {
    assertNull(StatUtils.normalizeRange(new double[]{}, 0.0, 1.0));
    assertNull(StatUtils.normalizeRange(new double[]{1.0, 1.0}, 0.0, 1.0));
    assertEqualsArrays(new double[]{0.0, 1.0}, StatUtils.normalizeRange(new double[]{0.0, 1.0}, 0.0, 1.0));
    assertEqualsArrays(new double[]{0.0, 5.0}, StatUtils.normalizeRange(new double[]{0.0, 1.0}, 0.0, 5.0));
    assertEqualsArrays(new double[]{0.0, 0.5, 1.0}, StatUtils.normalizeRange(new double[]{0.0, 1.0, 2.0}, 0.0, 1.0));
    assertEqualsArrays(new double[]{0.0, 1.0}, StatUtils.normalizeRange(new double[]{-1.0, 1.0}, 0.0, 1.0));
  }

  /**
   * Tests the countNonZero method.
   */
  public void testCountNonZero() {
    assertEquals("non-zero count differs", 0, StatUtils.countNonZero(new byte[0]));
    assertEquals("non-zero count differs", 0, StatUtils.countNonZero(new byte[]{0, 0, 0, 0, 0, 0}));
    assertEquals("non-zero count differs", 6, StatUtils.countNonZero(new byte[]{-1, 1, 2, 3, -2, -3}));
    assertEquals("non-zero count differs", 3, StatUtils.countNonZero(new byte[]{0, 1, 2, 3, 0, 0}));
    assertEquals("non-zero count differs", 3, StatUtils.countNonZero(new int[]{0, 1, 2, 3, 0, 0}));
    assertEquals("non-zero count differs", 3, StatUtils.countNonZero(new long[]{0L, 1L, 2L, 3L, 0L, 0L}));
    assertEquals("non-zero count differs", 0, StatUtils.countNonZero(new double[0]));
    assertEquals("non-zero count differs", 0, StatUtils.countNonZero(new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 0.0}));
    assertEquals("non-zero count differs", 6, StatUtils.countNonZero(new double[]{-1.0, 1.0, 2.0, 3.0, -2.0, -3.0}));
    assertEquals("non-zero count differs", 3, StatUtils.countNonZero(new double[]{-0.0, 1.0, 2.0, 3.0, -0.0, +0.0}));
    assertEquals("non-zero count differs", 3, StatUtils.countNonZero(new double[]{0.0, 1.0, 2.0, 3.0, 0.0, 0.0}));
    assertEquals("non-zero count differs", 3, StatUtils.countNonZero(new float[]{0.0f, 1.0f, 2.0f, 3.0f, 0.0f, 0.0f}));
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
