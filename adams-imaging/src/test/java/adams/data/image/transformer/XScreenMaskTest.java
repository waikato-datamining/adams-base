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
 * XScreenMaskTest.java
 * Copyright (C) 2015-2016 University of Waikato, Hamilton, New Zealand
 */
package adams.data.image.transformer;

import adams.data.image.XScreenMaskHelper;
import adams.env.Environment;
import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * Test class for the XScreenMask transformer. Run from the command line with: <br><br>
 * java adams.data.adams.transformer.XScreenMaskTest
 *
 * @author lx51 (lx51 at students dot waikato dot ac dot nz)
 * @version $Revision$
 */
public class XScreenMaskTest
  extends AbstractBufferedImageTransformerTestCase {

  /**
   * Constructs the test case. Called by subclasses.
   *
   * @param name the name of the test
   */
  public XScreenMaskTest(String name) {
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
    return new String[]{
      "ColorChecker100423.jpg",
      "ColorChecker100423.jpg"
    };
  }

  /**
   * Returns the setups to use in the regression test.
   *
   * @return the setups
   */
  @Override
  protected AbstractBufferedImageTransformer[] getRegressionSetups() {
    XScreenMask[] result;

    result = new XScreenMask[2];
    result[0] = new XScreenMask();
    result[1] = new XScreenMask();
    result[1].setColor(XScreenMaskHelper.Color.RED);
    result[1].setDown(true);
    result[1].setThreshold(-1);

    return result;
  }

  /**
   * Returns the test suite.
   *
   * @return the suite
   */
  public static Test suite() {
    return new TestSuite(XScreenMaskTest.class);
  }

  /**
   * Runs the test from commandline.
   *
   * @param args ignored
   */
  public static void main(String[] args) {
    Environment.setEnvironmentClass(Environment.class);
    runTest(suite());
  }
}
