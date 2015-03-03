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
 * CustomColorProviderTest.java
 * Copyright (C) 2011 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.visualization.core;

import adams.core.base.BaseText;
import adams.gui.visualization.core.AbstractColorProvider;
import adams.test.TmpFile;

import java.awt.Color;
import java.io.File;

/**
 * Tests the CustomColorProvider color provider.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class CustomColorProviderTest
  extends AbstractColorProviderTestCase {

  /**
   * Constructs the test case. Called by subclasses.
   *
   * @param name 	the name of the test
   */
  public CustomColorProviderTest(String name) {
    super(name);
  }

  /**
   * Returns the number of colors to generate per regresion setup.
   *
   * @return		the number of colors to generate
   */
  protected int[] getRegressionNumColors() {
    return new int[]{
	10, 50, 10, 50
    };
  }

  /**
   * Returns the setups to use in the regression test.
   *
   * @return		the setups
   */
  protected AbstractColorProvider[] getRegressionSetups() {
    CustomColorProvider[]	result;

    result    = new CustomColorProvider[4];

    result[0] = new CustomColorProvider();
    result[0].setColors(new Color[]{Color.RED, Color.GREEN, Color.BLUE});

    result[1] = new CustomColorProvider();
    result[1].setColors(new Color[]{Color.RED, Color.GREEN, Color.BLUE});
    result[1].setAllowDarkening(true);

    result[2] = new CustomColorProvider();
    result[2].setColors(new Color[]{Color.RED, Color.GREEN, Color.BLUE});

    result[3] = new CustomColorProvider();
    result[3].setColors(new Color[]{Color.RED, Color.GREEN, Color.BLUE, Color.CYAN, Color.MAGENTA, Color.PINK});
    result[3].setAllowDarkening(true);

    return result;
  }
}
