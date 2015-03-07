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
 * CroppedLocatorTest.java
 * Copyright (C) 2014-2015 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.transformer.locateobjects;

import adams.data.image.transformer.crop.SimpleCrop;
import adams.env.Environment;
import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * Tests the CroppedLocator object locator.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision: 9648 $
 */
public class CroppedLocatorTest
  extends AbstractObjectLocatorTestCase {

  /**
   * Initializes the test.
   *
   * @param name	the name of the test
   */
  public CroppedLocatorTest(String name) {
    super(name);
  }

  /**
   * Returns the filenames (without path) of the input data files to use
   * in the regression test.
   *
   * @return		the filenames
   */
  @Override
  protected String[] getRegressionInputFiles() {
    return new String[]{
	"particles.jpg",
	"particles.jpg",
	"particles_cropping.jpg"
    };
  }

  /**
   * Returns the setups to use in the regression test.
   *
   * @return		the setups
   */
  @Override
  protected AbstractObjectLocator[] getRegressionSetups() {
    CroppedLocator[]	result;
    SimpleCrop		crop;

    result    = new CroppedLocator[3];
    result[0] = new CroppedLocator();
    result[1] = new CroppedLocator();
    result[1].setLocator(new CannyEdges());
    result[2] = new CroppedLocator();
    result[2].setLocator(new CannyEdges());
    crop = new SimpleCrop();
    crop.setLeft(20);
    crop.setTop(20);
    crop.setWidth(500);
    crop.setHeight(380);
    result[2].setCrop(crop);

    return result;
  }

  /**
   *
   * Returns a test suite.
   *
   * @return		the test suite
   */
  public static Test suite() {
    return new TestSuite(CroppedLocatorTest.class);
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
