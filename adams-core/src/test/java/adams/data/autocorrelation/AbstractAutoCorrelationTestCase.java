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
 * AbstractAutoCorrelationTestCase.java
 * Copyright (C) 2015 University of Waikato, Hamilton, New Zealand
 */
package adams.data.autocorrelation;

import adams.core.CleanUpHandler;
import adams.core.Destroyable;
import adams.core.Utils;
import adams.core.io.FileUtils;
import adams.core.option.OptionHandler;
import adams.core.option.OptionUtils;
import adams.test.AbstractDatabaseTestCase;
import adams.test.AbstractTestHelper;
import adams.test.TestHelper;
import adams.test.TmpFile;

/**
 * Ancestor for autocorrelation test cases.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractAutoCorrelationTestCase
  extends AbstractDatabaseTestCase {

  /**
   * Constructs the test case. Called by subclasses.
   *
   * @param name 	the name of the test
   */
  public AbstractAutoCorrelationTestCase(String name) {
    super(name);
  }

  /**
   * Returns the test helper class to use.
   *
   * @return		the helper class instance
   */
  @Override
  protected AbstractTestHelper newTestHelper() {
    return new TestHelper(this, "adams/data/autocorrelation/data");
  }

  /**
   * Processes the input data (header definition and row data) and returns 
   * the generate data (header/row).
   * First element is the header (null if none generated), everything 
   * afterwards are the rows (null if none generated).
   *
   * @param data	the data to work on
   * @return		the processed data (header/row)
   */
  protected double[] process(double[] data, AbstractAutoCorrelation scheme) {
    return scheme.correlate(data);
  }

  /**
   * Turns the data object into a useful string representation.
   *
   * @param data	the object to convert
   * @return		the string representation
   */
  protected String toString(double[] data) {
    return Utils.arrayToString(data);
  }

  /**
   * Saves the data in the tmp directory.
   *
   * @param data	the data to save
   * @param filename	the filename to save to (without path)
   * @return		true if successfully saved
   */
  protected boolean save(Object data, String filename) {
    return FileUtils.writeToFile(new TmpFile(filename).getAbsolutePath(), data.toString(), false);
  }

  /**
   * Returns the data to use in the regression test. An array per setup.
   *
   * @return		the data
   */
  protected abstract double[][] getRegressionData();

  /**
   * Returns the setups to use in the regression test.
   *
   * @return		the setups
   */
  protected abstract AbstractAutoCorrelation[] getRegressionSetups();

  /**
   * Returns the ignored line indices to use in the regression test.
   *
   * @return		the setups
   */
  protected int[] getRegressionIgnoredLineIndices() {
    return new int[0];
  }

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
    double[]			processed;
    boolean			ok;
    String			regression;
    int				n;
    int				i;
    double[][]			data;
    AbstractAutoCorrelation[]	setups;
    AbstractAutoCorrelation	current;
    String[]			output;
    TmpFile[]			outputFiles;
    int[]			ignored;

    if (m_NoRegressionTest)
      return;

    data    = getRegressionData();
    setups  = getRegressionSetups();
    output  = new String[setups.length];
    ignored = getRegressionIgnoredLineIndices();
    assertEquals("Number of data and setups differ!", data.length, setups.length);

    // process data
    for (n = 0; n < setups.length; n++) {
      output[n] = createOutputFilename(n);
      current   = (AbstractAutoCorrelation) OptionUtils.shallowCopy((OptionHandler) setups[n], false);
      assertNotNull("Failed to create copy of autocorrelation algorithm: " + OptionUtils.getCommandLine(setups[n]), current);

      processed = process(data[n], current);
      assertNotNull("Failed to process data: " + Utils.arrayToString(data[n]), processed);

      ok        = save(toString(processed), output[n]);
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
