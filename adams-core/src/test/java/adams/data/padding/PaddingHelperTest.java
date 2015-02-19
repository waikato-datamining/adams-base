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
 * PaddingHelperTest.java
 * Copyright (C) 2015 University of Waikato, Hamilton, New Zealand
 */

package adams.data.padding;

import adams.env.Environment;
import adams.test.AdamsTestCase;
import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * Tests the adams.data.padding.PaddingHelper class.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision: 9527 $
 */
public class PaddingHelperTest
  extends AdamsTestCase {

  /**
   * Initializes the test.
   *
   * @param name	the name of the test
   */
  public PaddingHelperTest(String name) {
    super(name);
  }

  /**
   * Tests the nextPowerOf2 method.
   */
  public void testNextPowerOf2() {
    assertEquals(4, PaddingHelper.nextPowerOf2(1));
    assertEquals(4, PaddingHelper.nextPowerOf2(2));
    assertEquals(4, PaddingHelper.nextPowerOf2(3));
    assertEquals(1024, PaddingHelper.nextPowerOf2(1023));
    assertEquals(1024, PaddingHelper.nextPowerOf2(1024));
    assertEquals(1024, PaddingHelper.nextPowerOf2(513));
  }

  /**
   * Tests the padPow2 method.
   */
  public void testPadPow2() {
    float[] f = new float[]{1f, 2f, 3f, 4f, 5f};
    assertEqualsArrays(new float[]{1f, 2f, 3f, 4f, 5f, 0f, 0f, 0f}, PaddingHelper.padPow2(f, PaddingType.ZERO));
    assertEqualsArrays(new float[]{1f, 2f, 3f, 4f, 5f, 5f, 5f, 5f}, PaddingHelper.padPow2(f, PaddingType.LAST));
    assertEqualsArrays(new float[]{0f, 0f, 0f, 1f, 2f, 3f, 4f, 5f}, PaddingHelper.padPow2(f, PaddingType.ZERO, true));
    assertEqualsArrays(new float[]{1f, 1f, 1f, 1f, 2f, 3f, 4f, 5f}, PaddingHelper.padPow2(f, PaddingType.LAST, true));

    double[] d = new double[]{1.0, 2.0, 3.0, 4.0, 5.0};
    assertEqualsArrays(new double[]{1.0, 2.0, 3.0, 4.0, 5.0, 0.0, 0.0, 0.0}, PaddingHelper.padPow2(d, PaddingType.ZERO));
    assertEqualsArrays(new double[]{1.0, 2.0, 3.0, 4.0, 5.0, 5.0, 5.0, 5.0}, PaddingHelper.padPow2(d, PaddingType.LAST));
    assertEqualsArrays(new double[]{0.0, 0.0, 0.0, 1.0, 2.0, 3.0, 4.0, 5.0}, PaddingHelper.padPow2(d, PaddingType.ZERO, true));
    assertEqualsArrays(new double[]{1.0, 1.0, 1.0, 1.0, 2.0, 3.0, 4.0, 5.0}, PaddingHelper.padPow2(d, PaddingType.LAST, true));
  }

  /**
   * Tests the pad method.
   */
  public void testPad() {
    float[] f = new float[]{1f, 2f, 3f, 4f, 5f};
    assertEqualsArrays(new float[]{1f, 2f, 3f, 4f, 5f, 0f, 0f, 0f}, PaddingHelper.pad(f, 8, PaddingType.ZERO));
    assertEqualsArrays(new float[]{1f, 2f, 3f, 4f, 5f, 5f, 5f, 5f}, PaddingHelper.pad(f, 8, PaddingType.LAST));
    assertEqualsArrays(new float[]{0f, 0f, 0f, 1f, 2f, 3f, 4f, 5f}, PaddingHelper.pad(f, 8, PaddingType.ZERO, true));
    assertEqualsArrays(new float[]{1f, 1f, 1f, 1f, 2f, 3f, 4f, 5f}, PaddingHelper.pad(f, 8, PaddingType.LAST, true));

    double[] d = new double[]{1.0, 2.0, 3.0, 4.0, 5.0};
    assertEqualsArrays(new double[]{1.0, 2.0, 3.0, 4.0, 5.0, 0.0, 0.0, 0.0}, PaddingHelper.pad(d, 8, PaddingType.ZERO));
    assertEqualsArrays(new double[]{1.0, 2.0, 3.0, 4.0, 5.0, 5.0, 5.0, 5.0}, PaddingHelper.pad(d, 8, PaddingType.LAST));
    assertEqualsArrays(new double[]{0.0, 0.0, 0.0, 1.0, 2.0, 3.0, 4.0, 5.0}, PaddingHelper.pad(d, 8, PaddingType.ZERO, true));
    assertEqualsArrays(new double[]{1.0, 1.0, 1.0, 1.0, 2.0, 3.0, 4.0, 5.0}, PaddingHelper.pad(d, 8, PaddingType.LAST, true));
  }

  /**
   * Returns a test suite.
   *
   * @return		the test suite
   */
  public static Test suite() {
    return new TestSuite(PaddingHelperTest.class);
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
