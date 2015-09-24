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
 * PlugInFilterTest.java
 * Copyright (C) 2010 University of Waikato, Hamilton, New Zealand
 */
package adams.data.imagej.transformer;

import java.util.Arrays;
import java.util.HashSet;

import junit.framework.Test;
import junit.framework.TestSuite;
import adams.env.Environment;
import adams.test.Platform;

/**
 * Test class for the PlugInFilter flattener. Run from the command line with: <br><br>
 * java adams.data.imagej.flattener.PlugInFilterTest
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class PlugInFilterTest
  extends AbstractImageJTransformerTestCase {

  /**
   * Constructs the test case. Called by subclasses.
   *
   * @param name 	the name of the test
   */
  public PlugInFilterTest(String name) {
    super(name);
  }

  /**
   * Returns the platform this test class is for.
   * 
   * @return		the platform.
   */
  protected HashSet<Platform> getPlatforms() {
    return new HashSet<Platform>(Arrays.asList(new Platform[]{Platform.LINUX}));
  }

  /**
   * Returns the filenames (without path) of the input data files to use
   * in the regression test.
   *
   * @return		the filenames
   */
  protected String[] getRegressionInputFiles() {
    return new String[]{
	"adams_logo.png"
    };
  }

  /**
   * Returns the setups to use in the regression test.
   *
   * @return		the setups
   */
  protected AbstractImageJTransformer[] getRegressionSetups() {
    PlugInFilter[]	result;

    result = new PlugInFilter[1];
    result[0] = new PlugInFilter();
    result[0].setSuppressPlugInDialog(true);
    result[0].setPlugInOptions("filter_large=40 filter_small=3 suppress=None tolerance=5 autoscale saturate");

    return result;
  }
  
  /**
   * Returns whether the test can be executed in a headless environment. If not
   * then the test gets skipped.
   * 
   * @return		true if OK to run in headless mode
   */
  protected boolean canHandleHeadless() {
    return false;
  }

  /**
   * Returns the test suite.
   *
   * @return		the suite
   */
  public static Test suite() {
    return new TestSuite(PlugInFilterTest.class);
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
