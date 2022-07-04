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
 * RegionsTest.java
 * Copyright (C) 2022 University of Waikato, Hamilton, New Zealand
 */
package adams.data.image.transformer.subimages;

import adams.core.base.BaseRectangle;
import adams.env.Environment;
import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * Test class for the Regions subimages generator. Run from the command line with: <br><br>
 * java adams.data.adams.transformer.subimages.RegionsTest
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public class RegionsTest
  extends AbstractSubImagesGeneratorTestCase {

  /**
   * Constructs the test case. Called by subclasses.
   *
   * @param name 	the name of the test
   */
  public RegionsTest(String name) {
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
    };
  }

  /**
   * Returns the setups to use in the regression test.
   *
   * @return		the setups
   */
  @Override
  protected AbstractSubImagesGenerator[] getRegressionSetups() {
    Regions[]	result;

    result    = new Regions[3];
    result[0] = new Regions();
    result[1] = new Regions();
    result[1].setOneBasedCoords(true);
    result[1].setRegions(new BaseRectangle[]{
        new BaseRectangle("100 10 50 50"),
        new BaseRectangle("50 100 150 150"),
    });
    result[2] = new Regions();
    result[2].setRegions(new BaseRectangle[]{
        new BaseRectangle("100 10 50 50"),
        new BaseRectangle("50 100 150 150"),
    });
    result[2].setOneBasedCoords(false);

    return result;
  }

  /**
   * Returns the test suite.
   *
   * @return		the suite
   */
  public static Test suite() {
    return new TestSuite(RegionsTest.class);
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
