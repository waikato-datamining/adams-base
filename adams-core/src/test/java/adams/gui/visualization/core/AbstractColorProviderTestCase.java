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
 * AbstractColorProviderTestCase.java
 * Copyright (C) 2011 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.visualization.core;

import java.awt.Color;
import java.util.Vector;

import adams.core.CleanUpHandler;
import adams.core.Destroyable;
import adams.core.io.FileUtils;
import adams.core.option.OptionHandler;
import adams.core.option.OptionUtils;
import adams.test.AbstractTestHelper;
import adams.test.AdamsTestCase;
import adams.test.TestHelper;
import adams.test.TmpFile;

/**
 * Ancestor for color provider test cases.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractColorProviderTestCase
  extends AdamsTestCase {

  /**
   * Constructs the test case. Called by subclasses.
   *
   * @param name 	the name of the test
   */
  public AbstractColorProviderTestCase(String name) {
    super(name);
  }

  /**
   * Returns the test helper class to use.
   *
   * @return		the helper class instance
   */
  @Override
  protected AbstractTestHelper newTestHelper() {
    return new TestHelper(this, "adams/gui/visualization/core/data");
  }

  /**
   * Generates an array of colors.
   *
   * @param scheme	the scheme to generate the colors with
   * @param numColors	the number of colors to generate
   * @return		the generated colors
   */
  protected Color[] generate(AbstractColorProvider scheme, int numColors) {
    Vector<Color>	result;
    int			i;

    result = new Vector<Color>();
    scheme.resetColors();
    for (i = 0; i < numColors; i++)
      result.add(scheme.next());

    return result.toArray(new Color[result.size()]);
  }

  /**
   * Saves the colors in the tmp directory.
   *
   * @param colors	the colors to save
   * @param filename	the filename to save to (without path)
   * @return		true if successfully saved
   */
  protected boolean save(Color[] colors, String filename) {
    int			i;
    StringBuilder	content;

    content = new StringBuilder();
    for (i = 0; i < colors.length; i++)
      content.append(colors[i].toString() + "\n");

    return FileUtils.writeToFile(new TmpFile(filename).getAbsolutePath(), content, false);
  }

  /**
   * Returns the number of colors to generate per regresion setup.
   *
   * @return		the number of colors to generate
   */
  protected abstract int[] getRegressionNumColors();

  /**
   * Returns the setups to use in the regression test.
   *
   * @return		the setups
   */
  protected abstract AbstractColorProvider[] getRegressionSetups();

  /**
   * Creates an output filename.
   *
   * @param no		the number of the test
   * @return		the generated output filename (no path)
   */
  protected String createOutputFilename(int no) {
    return "out-" + no + ".txt";
  }

  /**
   * Compares the processed data against previously saved output data.
   */
  public void testRegression() {
    Color[]			processed;
    boolean			ok;
    String			regression;
    int				i;
    AbstractColorProvider[]	setups;
    int[]			numColors;
    AbstractColorProvider	current;
    String[]			output;
    TmpFile[]			outputFiles;

    if (m_NoRegressionTest)
      return;

    numColors = getRegressionNumColors();
    setups    = getRegressionSetups();
    output    = new String[setups.length];

    // generate colors
    for (i = 0; i < setups.length; i++) {
      output[i] = createOutputFilename(i);
      current   = (AbstractColorProvider) OptionUtils.shallowCopy((OptionHandler) setups[i], false);
      assertNotNull("Failed to create copy of color provider: " + OptionUtils.getCommandLine(setups[i]), current);

      processed = generate(current, numColors[i]);
      assertNotNull("Failed to generate colors?", processed);
      assertFalse("Failed to generate colors?", (processed.length == 0));

      ok = save(processed, output[i]);
      assertTrue("Failed to save regression data?", ok);

      if (current instanceof Destroyable)
	((Destroyable) current).destroy();
    }

    // test regression
    outputFiles = new TmpFile[output.length];
    for (i = 0; i < output.length; i++)
      outputFiles[i] = new TmpFile(output[i]);
    regression = m_Regression.compare(outputFiles);
    assertNull("Output differs:\n" + regression, regression);

    // remove output, clean up scheme
    for (i = 0; i < output.length; i++) {
      if (setups[i] instanceof Destroyable)
	((Destroyable) setups[i]).destroy();
      else if (setups[i] instanceof CleanUpHandler)
	((CleanUpHandler) setups[i]).cleanUp();
      m_TestHelper.deleteFileFromTmp(output[i]);
    }
    cleanUpAfterRegression();
  }

  /**
   * For further cleaning up after the regression tests.
   * <br><br>
   * Default implementation does nothing.
   */
  protected void cleanUpAfterRegression() {
  }
}
