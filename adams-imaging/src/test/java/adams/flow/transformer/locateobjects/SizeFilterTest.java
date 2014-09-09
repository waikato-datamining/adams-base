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
 * SizeFilterTest.java
 * Copyright (C) 2014 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.transformer.locateobjects;

/**
 * Tests the SizeFilter object locator.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class SizeFilterTest
  extends AbstractObjectLocatorTestCase {

  /**
   * Initializes the test.
   *
   * @param name	the name of the test
   */
  public SizeFilterTest(String name) {
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
	"particles.jpg"
    };
  }

  /**
   * Returns the setups to use in the regression test.
   *
   * @return		the setups
   */
  @Override
  protected AbstractObjectLocator[] getRegressionSetups() {
    SizeFilter[]	result;

    result    = new SizeFilter[3];
    result[0] = new SizeFilter();
    result[1] = new SizeFilter();
    result[1].setLocator(new CannyEdges());
    result[2] = new SizeFilter();
    result[2].setLocator(new CannyEdges());
    result[2].setMinWidth(10);
    result[2].setMinHeight(10);

    return result;
  }
}
