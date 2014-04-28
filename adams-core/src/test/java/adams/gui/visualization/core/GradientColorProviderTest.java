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
 * GradientColorProviderTest.java
 * Copyright (C) 2013 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.visualization.core;

/**
 * Tests the GradientColorProvider color provider.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class GradientColorProviderTest
  extends AbstractColorProviderTestCase {

  /**
   * Constructs the test case. Called by subclasses.
   *
   * @param name 	the name of the test
   */
  public GradientColorProviderTest(String name) {
    super(name);
  }

  /**
   * Returns the number of colors to generate per regresion setup.
   *
   * @return		the number of colors to generate
   */
  @Override
  protected int[] getRegressionNumColors() {
    return new int[]{
	25, 50
    };
  }

  /**
   * Returns the setups to use in the regression test.
   *
   * @return		the setups
   */
  @Override
  protected AbstractColorProvider[] getRegressionSetups() {
    GradientColorProvider[]	result;

    result    = new GradientColorProvider[2];

    result[0] = new GradientColorProvider();

    result[1] = new GradientColorProvider();
    result[1].setGenerator(new MultiColorGenerator());

    return result;
  }
}
