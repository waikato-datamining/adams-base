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
 * AbstractArrayStatisticTestCase.java
 * Copyright (C) 2010-2013 University of Waikato, Hamilton, New Zealand
 */
package adams.data.statistics;

import java.io.File;
import java.io.Serializable;

import adams.core.CleanUpHandler;
import adams.core.Destroyable;
import adams.core.io.FileUtils;
import adams.data.statistics.AbstractArrayStatistic.StatisticContainer;
import adams.test.AbstractTestHelper;
import adams.test.AdamsTestCase;
import adams.test.TestHelper;
import adams.test.TmpFile;

/**
 * Ancestor for array statistic test cases.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 * @param <A> the type of algorithm to use
 * @param <D> the type of data to process
 */
public abstract class AbstractArrayStatisticTestCase<A extends AbstractArrayStatistic, D extends Serializable>
  extends AdamsTestCase {

  /**
   * Constructs the test case. Called by subclasses.
   *
   * @param name 	the name of the test
   */
  public AbstractArrayStatisticTestCase(String name) {
    super(name);
  }

  /**
   * Returns the test helper class to use.
   *
   * @return		the helper class instance
   */
  @Override
  protected AbstractTestHelper newTestHelper() {
    return new TestHelper(this, "adams/data/statistic/data");
  }

  /**
   * Generates the statistics from the input data.
   *
   * @param data	the data to work on
   * @param scheme	the scheme to process the data with
   * @return		the generated statistics
   */
  protected StatisticContainer process(D[][] data, A scheme) {
    int		i;

    scheme.clear();
    for (i = 0; i < data.length; i++)
      scheme.add(data[i]);

    return scheme.calculate();
  }

  /**
   * Returns the data used in the regression test.
   *
   * @return		the data
   */
  protected abstract D[][][] getRegressionInputData();

  /**
   * Returns the setups to use in the regression test.
   *
   * @return		the setups
   */
  protected abstract A[] getRegressionSetups();

  /**
   * Saves the generated statistics output as file.
   *
   * @param data	the generated output data
   * @param filename	the file to save the data to (in the temp directory)
   * @return		true if successfully saved
   */
  protected boolean save(String data, String filename) {
    return FileUtils.writeToFile(m_TestHelper.getTmpDirectory() + File.separator + filename, data, false);
  }

  /**
   * Creates an output filename based on the number of the test.
   *
   * @param no		the number of the test
   * @return		the generated output filename (no path)
   */
  protected String createOutputFilename(int no) {
    return "out-" + no;
  }

  /**
   * Compares the processed data against previously saved output data.
   */
  public void testRegression() {
    D[][][]		data;
    String		processed;
    boolean		ok;
    String		regression;
    int			i;
    A[]			setups;
    String[]		output;
    TmpFile[]		outputFiles;

    if (m_NoRegressionTest)
      return;

    data    = getRegressionInputData();
    output  = new String[data.length];
    setups  = getRegressionSetups();
    assertEquals("Number of data arrays and setups differ!", data.length, setups.length);

    // process data
    for (i = 0; i < data.length; i++) {
      processed = process(data[i], setups[i]).toString();
      assertNotNull("Failed to process data?", processed);

      output[i] = createOutputFilename(i);
      ok        = save(processed, output[i]);
      assertTrue("Failed to save regression data?", ok);
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
   * <p/>
   * Default implementation does nothing.
   */
  protected void cleanUpAfterRegression() {
  }
}
