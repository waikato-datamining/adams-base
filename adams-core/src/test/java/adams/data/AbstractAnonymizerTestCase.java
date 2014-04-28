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
 * AbstractAnonymizerTestCase.java
 * Copyright (C) 2010 University of Waikato, Hamilton, New Zealand
 */
package adams.data;

import java.io.File;
import java.lang.reflect.Array;

import adams.core.CleanUpHandler;
import adams.core.Destroyable;
import adams.core.Utils;
import adams.core.io.FileUtils;
import adams.core.option.OptionUtils;
import adams.test.AdamsTestCase;
import adams.test.TmpFile;

/**
 * Ancestor for test cases tailored for anonymizers.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 * @param <T> the type of data to process
 */
public abstract class AbstractAnonymizerTestCase<T>
  extends AdamsTestCase {

  /**
   * Constructs the test case. Called by subclasses.
   *
   * @param name 	the name of the test
   */
  public AbstractAnonymizerTestCase(String name) {
    super(name);
  }

  /**
   * Processes the input data and returns the processed data.
   *
   * @param data	the data to work on
   * @param scheme	the scheme to process the data with
   * @return		the processed data
   */
  protected T[] process(T[] data, AbstractAnonymizer<T> scheme) {
    T[]		result;
    int		i;
    
    result = (T[]) Array.newInstance(data.getClass().getComponentType(), data.length);
    
    for (i = 0; i < data.length; i++)
      result[i] = scheme.anonymize(data[i]);
    
    return result;
  }

  /**
   * Saves the data in the tmp directory.
   *
   * @param data	the data to save
   * @param filename	the filename to save to (without path)
   * @return		true if successfully saved
   */
  protected boolean save(T[] data, String filename) {
    boolean	result;
    int		i;
    
    result = true;
    
    for (i = 0; i < data.length; i++) {
      result = FileUtils.writeToFile(m_TestHelper.getTmpDirectory() + File.separator + filename, data[i], (i > 0));
      if (!result)
	break;
    }
    
    return result;
  }

  /**
   * Returns the data to use in the regression test.
   *
   * @return		the data
   */
  protected abstract T[] getRegressionInputData();

  /**
   * Returns the setups to use in the regression test.
   *
   * @return		the setups
   */
  protected abstract AbstractAnonymizer<T>[] getRegressionSetups();

  /**
   * Creates an output filename based on the input filename.
   *
   * @param no		the number of the test
   * @return		the generated output filename (no path)
   */
  protected String createOutputFilename(int no) {
    return "out" + no + ".txt";
  }

  /**
   * Compares the processed data against previously saved output data.
   */
  public void testRegression() {
    T[]				input;
    T[]				processed;
    boolean			ok;
    String			regression;
    int				i;
    AbstractAnonymizer<T>[]	setups;
    AbstractAnonymizer<T>	current;
    String[]			output;
    TmpFile[]			outputFiles;

    if (m_NoRegressionTest)
      return;

    input   = getRegressionInputData();
    setups  = getRegressionSetups();
    output  = new String[setups.length];

    // process data
    for (i = 0; i < setups.length; i++) {
      current = (AbstractAnonymizer<T>) Utils.deepCopy(setups[i]);
      assertNotNull("Failed to create copy of algorithm: " + OptionUtils.getCommandLine(setups[i]), current);

      processed = process(input, current);
      assertNotNull("Failed to process data?", processed);

      output[i] = createOutputFilename(i);
      ok        = save(processed, output[i]);
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
   * <p/>
   * Default implementation does nothing.
   */
  protected void cleanUpAfterRegression() {
  }
}
