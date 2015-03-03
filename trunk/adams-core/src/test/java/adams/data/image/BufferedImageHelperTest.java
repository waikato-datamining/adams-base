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
 * BufferedImageHelperTest.java
 * Copyright (C) 2014 University of Waikato, Hamilton, New Zealand
 */
package adams.data.image;

import java.awt.Color;

import junit.framework.Test;
import junit.framework.TestSuite;
import adams.env.Environment;
import adams.test.AdamsTestCase;

/**
 * Tests the {@link BufferedImageHelper} class.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class BufferedImageHelperTest
  extends AdamsTestCase {

  /**
   * Constructs the test case. Called by subclasses.
   *
   * @param name 	the name of the test
   */
  public BufferedImageHelperTest(String name) {
    super(name);
  }

  /**
   * Tests the {@link BufferedImageHelper#split(int)} method.
   */
  public void testSplit() {
    assertEqualsArrays("split failed", new int[]{0, 0, 0, 0}, BufferedImageHelper.split(0));
    assertEqualsArrays("split failed", new int[]{10, 20, 30, 255}, BufferedImageHelper.split(new Color(10, 20, 30).getRGB()));
  }

  /**
   * Tests the {@link BufferedImageHelper#combine(int, int, int, int)} method.
   */
  public void testCombine() {
    assertEquals("combine failed", 0, BufferedImageHelper.combine(0, 0, 0, 0));
    assertEquals("combine failed", new Color(10, 20, 30).getRGB(), BufferedImageHelper.combine(10, 20, 30, 255));
  }
  
  /**
   * Returns a test suite.
   *
   * @return		the test suite
   */
  public static Test suite() {
    return new TestSuite(BufferedImageHelperTest.class);
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
