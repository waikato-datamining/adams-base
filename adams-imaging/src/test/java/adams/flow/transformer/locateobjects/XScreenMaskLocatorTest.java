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
 * XScreenMaskLocatorTest.java
 * Copyright (C) 2014 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.transformer.locateobjects;

import adams.data.image.XScreenMaskHelper.Color;
import adams.env.Environment;
import junit.framework.Test;
import junit.framework.TestSuite;

import java.util.Arrays;

/**
 * Tests the XScreenMaskLocator object locator.
 *
 * @author lx51 (lx51 at students dot waikato dot ac dot nz)
 * @version $Revision$
 */
public class XScreenMaskLocatorTest extends AbstractObjectLocatorTestCase {

  /**
   * Initializes the test.
   *
   * @param name the name of the test
   */
  public XScreenMaskLocatorTest(String name) {
    super(name);
  }

  /**
   * Returns the filenames (without path) of the input data files to use
   * in the regression test.
   *
   * @return the filenames
   */
  @Override
  protected String[] getRegressionInputFiles() {
    String[] files = new String[Color.values().length];
    Arrays.fill(files, "24bit_rgb_palette.png");
    return files;
  }

  /**
   * Returns the setups to use in the regression test.
   *
   * @return the setups
   */
  @Override
  protected AbstractObjectLocator[] getRegressionSetups() {
    Color[] colors = Color.values();

    XScreenMaskLocator[] result = new XScreenMaskLocator[colors.length];
    result[0] = new XScreenMaskLocator();

    for (int i = 0; i < colors.length; i++) {
      result[i] = new XScreenMaskLocator();
      result[i].setColor(colors[i]);
    }

    return result;
  }

  /**
   *
   * Returns a test suite.
   *
   * @return		the test suite
   */
  public static Test suite() {
    return new TestSuite(XScreenMaskLocatorTest.class);
  }

  /**
   * Runs the test from commandline.
   *
   * @param args	ignored
   */
  public static void main(String[] args) {
    Environment.setEnvironmentClass(adams.env.Environment.class);
    runTest(suite());
  }
}
