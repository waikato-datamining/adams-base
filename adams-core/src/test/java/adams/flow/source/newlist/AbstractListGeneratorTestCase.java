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
 * AbstractConversionTestCase.java
 * Copyright (C) 2014 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.source.newlist;

import java.util.List;

import adams.core.CleanUpHandler;
import adams.core.Destroyable;
import adams.core.Utils;
import adams.core.io.FileUtils;
import adams.core.option.OptionUtils;
import adams.test.AdamsTestCase;
import adams.test.TmpFile;

/**
 * Ancestor for conversion test cases.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractListGeneratorTestCase
  extends AdamsTestCase {

  /**
   * Constructs the test case. Called by subclasses.
   *
   * @param name 	the name of the test
   */
  public AbstractListGeneratorTestCase(String name) {
    super(name);
  }

  /**
   * Generates the list.
   * First element is the list (null if none generated), second is the
   * generator exception (null if none generated).
   *
   * @param scheme	the scheme to generate data with
   * @return		the generated data
   */
  protected Object[] generate(AbstractListGenerator scheme) {
    Object[]	result;

    result = new Object[2];
    try {
      result[0] = scheme.generate();
    }
    catch (Exception e) {
      result[1] = e.toString();
    }

    return result;
  }

  /**
   * Saves the list in the tmp directory.
   *
   * @param list	the list to save
   * @param filename	the filename to save to (without path)
   * @param append	whether to append the data or not
   * @return		true if successfully saved
   */
  protected boolean save(List<String> list, String filename, boolean append) {
    return FileUtils.writeToFile(new TmpFile(filename).getAbsolutePath(), Utils.flatten(list, "\n"), append);
  }

  /**
   * Returns the setups to use in the regression test.
   *
   * @return		the setups
   */
  protected abstract AbstractListGenerator[] getRegressionSetups();

  /**
   * Returns the ignored line indices to use in the regression test.
   *
   * @return		the setups
   */
  protected abstract int[] getRegressionIgnoredLineIndices();

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
    Object[]			generated;
    boolean			ok;
    String			regression;
    int				n;
    int				i;
    boolean			append;
    AbstractListGenerator[]	setups;
    AbstractListGenerator		current;
    String[]			output;
    TmpFile[]			outputFiles;
    int[]			ignored;

    if (m_NoRegressionTest)
      return;

    setups  = getRegressionSetups();
    output  = new String[setups.length];
    ignored = getRegressionIgnoredLineIndices();

    // process data
    for (n = 0; n < setups.length; n++) {
      append    = false;
      output[n] = createOutputFilename(n);
      current   = setups[n].shallowCopy(true);
      assertNotNull("Failed to create copy of generator algorithm: " + OptionUtils.getCommandLine(setups[n]), current);

      generated = generate(current);
      assertNull("Failed to generate data: " + generated[1], generated[1]);

      // any output generated?
      if (generated[0] == null)
	continue;

      ok = save((List<String>) generated[0], output[n], append);
      assertTrue("Failed to save regression data?", ok);

      if (current instanceof Destroyable)
	((Destroyable) current).destroy();
    }

    // test regression
    outputFiles = new TmpFile[output.length];
    for (i = 0; i < output.length; i++)
      outputFiles[i] = new TmpFile(output[i]);
    regression = m_Regression.compare(outputFiles, ignored);
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
   * <p/>
   * Default implementation does nothing.
   */
  protected void cleanUpAfterRegression() {
  }
}
