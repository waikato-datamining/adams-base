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
 * BinningAlgorithmTestCase.java
 * Copyright (C) 2019-2020 University of Waikato, Hamilton, New Zealand
 */
package adams.data.binning.algorithm;

import adams.core.classmanager.ClassManager;
import adams.core.io.FileUtils;
import adams.core.option.OptionUtils;
import adams.data.binning.Bin;
import adams.data.binning.Binnable;
import adams.test.AdamsTestCase;
import adams.test.TmpFile;

import java.io.File;
import java.util.List;

/**
 * Ancestor for test cases tailored for binning algorithms.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @param <T> the type of data to process
 */
public abstract class AbstractBinningAlgorithmTestCase<T>
  extends AdamsTestCase {

  /**
   * Constructs the test case. Called by subclasses.
   *
   * @param name 	the name of the test
   */
  public AbstractBinningAlgorithmTestCase(String name) {
    super(name);
  }

  /**
   * Processes the input data and returns the processed data.
   *
   * @param objects	the data to work on
   * @param scheme	the scheme to process the data with
   * @return		the processed data
   */
  protected List<Bin<T>> process(List<Binnable<T>> objects, BinningAlgorithm scheme) {
    return scheme.generateBins(objects);
  }

  /**
   * Saves the data in the tmp directory.
   *
   * @param data	the data to save
   * @param filename	the filename to save to (without path)
   * @return		true if successfully saved
   */
  protected boolean save(List<Bin<T>> data, String filename) {
    int			i;
    StringBuilder	str;
    
    str = new StringBuilder();
    for (i = 0; i < data.size(); i++) {
      if (i > 0)
        str.append("\n");
      str.append(data.get(i).toString());
    }

    return FileUtils.writeToFile(m_TestHelper.getTmpDirectory() + File.separator + filename, str, false);
  }

  /**
   * Returns the data to use in the regression test.
   *
   * @return		the data
   */
  protected abstract List<Binnable<T>> getRegressionInputData();

  /**
   * Returns the setups to use in the regression test.
   *
   * @return		the setups
   */
  protected abstract BinningAlgorithm[] getRegressionSetups();

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
    List<Binnable<T>>		input;
    List<Bin<T>>		processed;
    boolean			ok;
    String			regression;
    int				i;
    BinningAlgorithm[]	setups;
    BinningAlgorithm	current;
    String[]			output;
    TmpFile[]			outputFiles;

    if (m_NoRegressionTest)
      return;

    input   = getRegressionInputData();
    setups  = getRegressionSetups();
    output  = new String[setups.length];

    // process data
    for (i = 0; i < setups.length; i++) {
      current = (BinningAlgorithm) ClassManager.getSingleton().deepCopy(setups[i]);
      assertNotNull("Failed to create copy of algorithm: " + OptionUtils.getCommandLine(setups[i]), current);

      processed = process(input, current);
      assertNotNull("Failed to process data?", processed);

      output[i] = createOutputFilename(i);
      ok        = save(processed, output[i]);
      assertTrue("Failed to save regression data?", ok);
      current.destroy();
    }

    // test regression
    outputFiles = new TmpFile[output.length];
    for (i = 0; i < output.length; i++)
      outputFiles[i] = new TmpFile(output[i]);
    regression = m_Regression.compare(outputFiles);
    assertNull("Output differs:\n" + regression, regression);

    // remove output, clean up scheme
    for (i = 0; i < output.length; i++) {
      setups[i].destroy();
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
