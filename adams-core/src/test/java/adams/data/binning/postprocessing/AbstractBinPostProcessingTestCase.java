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
 * AbstractBinPostProcessingTestCase.java
 * Copyright (C) 2019-2020 University of Waikato, Hamilton, New Zealand
 */
package adams.data.binning.postprocessing;

import adams.core.MessageCollection;
import adams.core.classmanager.ClassManager;
import adams.core.io.FileUtils;
import adams.core.option.OptionUtils;
import adams.data.binning.Bin;
import adams.test.AdamsTestCase;
import adams.test.TmpFile;

import java.io.File;
import java.util.List;

/**
 * Ancestor for test cases tailored for post-processing schemes for bins.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @param <T> the type of data to process
 */
public abstract class AbstractBinPostProcessingTestCase<T>
  extends AdamsTestCase {

  /**
   * Constructs the test case. Called by subclasses.
   *
   * @param name 	the name of the test
   */
  public AbstractBinPostProcessingTestCase(String name) {
    super(name);
  }

  /**
   * Processes the input data and returns the processed data.
   *
   * @param objects	the data to work on
   * @param scheme	the scheme to process the data with
   * @param errors 	for collecting errors
   * @return		the processed data
   */
  protected List<Bin<T>> process(List<Bin<T>> objects, BinPostProcessing scheme, MessageCollection errors) {
    try {
      return scheme.postProcessBins(objects);
    }
    catch (Exception e) {
      errors.add("Failed to process: " + e.getMessage());
      return null;
    }
  }

  /**
   * Saves the data in the tmp directory.
   *
   * @param data	the data to save
   * @param errors 	associated errors
   * @param filename	the filename to save to (without path)
   * @return		true if successfully saved
   */
  protected boolean save(List<Bin<T>> data, MessageCollection errors, String filename) {
    int			i;
    StringBuilder	str;
    
    str = new StringBuilder();
    if (data != null) {
      for (i = 0; i < data.size(); i++) {
	if (i > 0)
	  str.append("\n");
	str.append(data.get(i).toString());
      }
    }
    else {
      str.append("No data generated!");
    }
    str.append(" - ").append(errors.isEmpty() ? "no errors" : errors.toString());

    return FileUtils.writeToFile(m_TestHelper.getTmpDirectory() + File.separator + filename, str, false);
  }

  /**
   * Returns the data to use in the regression test.
   *
   * @return		the data
   */
  protected abstract List<Bin<T>>[] getRegressionInputData();

  /**
   * Returns the setups to use in the regression test.
   *
   * @return		the setups
   */
  protected abstract BinPostProcessing[] getRegressionSetups();

  /**
   * Creates an output filename based on the input filename.
   *
   * @param setup	the number of the setup
   * @param data 	the number dataset
   * @return		the generated output filename (no path)
   */
  protected String createOutputFilename(int setup, int data) {
    return "out-setup=" + setup + "-data=" + data + ".txt";
  }

  /**
   * Compares the processed data against previously saved output data.
   */
  public void testRegression() {
    List<Bin<T>>[]	input;
    List<Bin<T>>	processed;
    boolean		ok;
    String		regression;
    int			i;
    int			n;
    BinPostProcessing[]	setups;
    BinPostProcessing	current;
    String[]		output;
    TmpFile[]		outputFiles;
    MessageCollection	errors;

    if (m_NoRegressionTest)
      return;

    input   = getRegressionInputData();
    setups  = getRegressionSetups();
    output  = new String[setups.length * input.length];

    // process data
    for (i = 0; i < setups.length; i++) {
      for (n = 0; n < input.length; n++) {
	current = (BinPostProcessing) ClassManager.getSingleton().deepCopy(setups[i]);
	assertNotNull("Failed to create copy of algorithm: " + OptionUtils.getCommandLine(setups[i]), current);

	errors = new MessageCollection();
	processed = process(input[n], current, errors);

	output[i * input.length + n] = createOutputFilename(i, n);
	ok = save(processed, errors, output[i * input.length + n]);
	assertTrue("Failed to save regression data?", ok);
	current.destroy();
      }
    }

    // test regression
    outputFiles = new TmpFile[output.length];
    for (i = 0; i < output.length; i++)
      outputFiles[i] = new TmpFile(output[i]);
    regression = m_Regression.compare(outputFiles);
    assertNull("Output differs:\n" + regression, regression);

    // remove output, clean up scheme
    for (i = 0; i < setups.length; i++)
      setups[i].destroy();
    for (i = 0; i < output.length; i++)
      m_TestHelper.deleteFileFromTmp(output[i]);
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
