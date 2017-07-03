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
 * InterpolationUtilsTest.java
 * Copyright (C) 2017 University of Waikato, Hamilton, NZ
 */

package adams.data;

import adams.env.Environment;
import adams.test.AdamsTestCase;
import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * Tests the {@link InterpolationUtils} class.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class InterpolationUtilsTest
  extends AdamsTestCase  {

  /**
   * Constructs the test case. Called by subclasses.
   *
   * @param name the name of the test
   */
  public InterpolationUtilsTest(String name) {
    super(name);
  }

  /**
   * Tests the {@link InterpolationUtils#weights(double, double, double)} method.
   */
  public void testWeights() {
    double x;
    double xL;
    double xR;

    x  = 0.0;
    xL = 0.0;
    xR = 1.0;
    assertEqualsArrays(new double[]{1.0, 0.0}, InterpolationUtils.weights(x, xL, xR));

    x  = 1.0;
    xL = 0.0;
    xR = 1.0;
    assertEqualsArrays(new double[]{0.0, 1.0}, InterpolationUtils.weights(x, xL, xR));

    x  = 0.5;
    xL = 0.0;
    xR = 1.0;
    assertEqualsArrays(new double[]{0.5, 0.5}, InterpolationUtils.weights(x, xL, xR));
  }

  /**
   * Tests the {@link InterpolationUtils#interpolate(double, double, double, double, double)} method.
   */
  public void testInterpolate() {
    double x;
    double xL;
    double yL;
    double xR;
    double yR;

    x  = 0.0;
    xL = 0.0;
    yL = 0.0;
    xR = 1.0;
    yR = 1.0;
    assertEquals(0.0, InterpolationUtils.interpolate(x, xL, yL, xR, yR));

    x  = 1.0;
    xL = 0.0;
    yL = 0.0;
    xR = 1.0;
    yR = 1.0;
    assertEquals(1.0, InterpolationUtils.interpolate(x, xL, yL, xR, yR));

    x  = 0.5;
    xL = 0.0;
    yL = 0.0;
    xR = 1.0;
    yR = 1.0;
    assertEquals(0.5, InterpolationUtils.interpolate(x, xL, yL, xR, yR));
  }

  /**
   * Returns a test suite.
   *
   * @return		the test suite
   */
  public static Test suite() {
    return new TestSuite(InterpolationUtilsTest.class);
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
