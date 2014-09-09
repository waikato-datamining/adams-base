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
 * CropTest.java
 * Copyright (C) 2012-2014 University of Waikato, Hamilton, New Zealand
 */
package adams.data.adams.transformer;

import junit.framework.Test;
import junit.framework.TestSuite;
import adams.data.image.ImageAnchor;
import adams.env.Environment;

/**
 * Test class for the Crop transformer. Run from the command line with: <p/>
 * java adams.data.adams.transformer.CropTest
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class CropTest
  extends AbstractBufferedImageTransformerTestCase {

  /**
   * Constructs the test case. Called by subclasses.
   *
   * @param name 	the name of the test
   */
  public CropTest(String name) {
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
	"adams_logo.png",
	"adams_logo.png",
	"adams_logo.png",
	"adams_logo.png",
	"adams_logo.png"
    };
  }

  /**
   * Returns the setups to use in the regression test.
   *
   * @return		the setups
   */
  @Override
  protected AbstractBufferedImageTransformer[] getRegressionSetups() {
    Crop[]	result;

    result    = new Crop[5];
    result[0] = new Crop();
    result[1] = new Crop();
    result[1].setImageAnchor(ImageAnchor.TOP_RIGHT);
    result[2] = new Crop();
    result[2].setImageAnchor(ImageAnchor.BOTTOM_LEFT);
    result[3] = new Crop();
    result[3].setImageAnchor(ImageAnchor.BOTTOM_RIGHT);
    result[4] = new Crop();
    result[4].setImageAnchor(ImageAnchor.CENTER);

    return result;
  }

  /**
   * Returns the test suite.
   *
   * @return		the suite
   */
  public static Test suite() {
    return new TestSuite(CropTest.class);
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
