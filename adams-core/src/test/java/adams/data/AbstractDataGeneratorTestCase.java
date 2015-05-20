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
 * AbstractDataGeneratorTestCase.java
 * Copyright (C) 2010-2013 University of Waikato, Hamilton, New Zealand
 */
package adams.data;

import adams.core.CleanUpHandler;
import adams.core.Destroyable;
import adams.data.container.DataContainer;
import adams.test.AdamsTestCase;
import adams.test.TmpFile;

/**
 * Ancestor for test cases tailored for schemes that generate data.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 * @param <A> the type of algorithm to use
 * @param <D> the type of data to generate
 */
public abstract class AbstractDataGeneratorTestCase<A, D extends DataContainer>
  extends AdamsTestCase {

  /**
   * Constructs the test case. Called by subclasses.
   *
   * @param name 	the name of the test
   */
  public AbstractDataGeneratorTestCase(String name) {
    super(name);
  }

  /**
   * Returns the generated data.
   *
   * @param scheme	the scheme to generate the data with
   * @return		the generated data
   */
  protected abstract D generate(A scheme);

  /**
   * Saves the data in the tmp directory.
   *
   * @param data	the data to save
   * @param filename	the filename to save to (without path)
   * @return		true if successfully saved
   */
  protected boolean save(D data, String filename) {
    return m_TestHelper.save(data, filename);
  }

  /**
   * Returns the setups to use in the regression test.
   *
   * @return		the setups
   */
  protected abstract A[] getRegressionSetups();

  /**
   * Returns the ignored line indices to use in the regression test.
   *
   * @return		the setups
   */
  protected abstract int[] getRegressionIgnoredLineIndices();

  /**
   * Creates an output filename based on the input filename.
   *
   * @param no		the number of the test
   * @return		the generated output filename (no path)
   */
  protected String createOutputFilename(int no) {
    return "datagenerator-out" + no;
  }

  /**
   * Compares the generated data against previously saved output data.
   */
  public void testRegression() {
    D		data;
    boolean	ok;
    String	regression;
    int		i;
    A[]		setups;
    String[]	output;
    TmpFile[]	outputFiles;
    int[]	ignored;

    if (m_NoRegressionTest)
      return;

    setups  = getRegressionSetups();
    ignored = getRegressionIgnoredLineIndices();
    output  = new String[setups.length];

    // generate data
    for (i = 0; i < setups.length; i++) {
      data = generate(setups[i]);
      assertNotNull("Failed to generate data?", data);

      output[i] = createOutputFilename(i);
      ok        = save(data, output[i]);
      assertTrue("Failed to save regression data?", ok);
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
   * <br><br>
   * Default implementation does nothing.
   */
  protected void cleanUpAfterRegression() {
  }
}
