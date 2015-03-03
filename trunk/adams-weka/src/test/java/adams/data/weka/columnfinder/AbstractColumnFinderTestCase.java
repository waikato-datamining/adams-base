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
 * AbstractColumnFinderTestCase.java
 * Copyright (C) 2012-2013 University of Waikato, Hamilton, New Zealand
 */
package adams.data.weka.columnfinder;

import java.util.Vector;

import weka.core.Instances;
import weka.core.converters.ConverterUtils.DataSource;
import adams.core.CleanUpHandler;
import adams.core.Destroyable;
import adams.core.ShallowCopySupporter;
import adams.core.Utils;
import adams.core.io.FileUtils;
import adams.core.option.OptionUtils;
import adams.test.AbstractTestHelper;
import adams.test.AdamsTestCase;
import adams.test.TestHelper;
import adams.test.TmpFile;

/**
 * Ancestor for column finder test cases.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractColumnFinderTestCase
  extends AdamsTestCase {

  /**
   * Constructs the test case. Called by subclasses.
   *
   * @param name 	the name of the test
   */
  public AbstractColumnFinderTestCase(String name) {
    super(name);
  }

  /**
   * Returns the test helper class to use.
   *
   * @return		the helper class instance
   */
  @Override
  protected AbstractTestHelper newTestHelper() {
    return new TestHelper(this, "adams/data/weka/columnfinder/data");
  }

  /**
   * Loads the data to process.
   *
   * @param filename		the filename to load (without path)
   * @return			the data, null if it could not be loaded
   * @see			#getDataDirectory()
   */
  protected Instances load(String filename) {
    Instances	result;
    
    try {
      m_TestHelper.copyResourceToTmp(filename);
      result = DataSource.read(m_TestHelper.getTmpDirectory() + "/" + filename);
      m_TestHelper.deleteFileFromTmp(filename);
    }
    catch (Exception e) {
      fail("Failed to load '" + filename + "': " + filename);
      result = null;
    }
    
    return result;
  }

  /**
   * Returns the filenames (without path) of the input data files to use
   * in the regression test.
   *
   * @return		the filenames
   */
  protected abstract String[] getRegressionInputFiles();

  /**
   * Returns the setups to use in the regression test.
   *
   * @return		the setups
   */
  protected abstract ColumnFinder[] getRegressionSetups();

  /**
   * Processes the input data and returns the processed data.
   *
   * @param data	the data to work on
   * @param scheme	the scheme to process the data with
   * @return		the processed data
   */
  protected int[] process(Instances data, ColumnFinder scheme) {
    return scheme.findColumns(data);
  }

  /**
   * Creates an output filename based on the input filename.
   *
   * @param input	the input filename (no path)
   * @param no		the number of the test
   * @return		the generated output filename (no path)
   */
  protected String createOutputFilename(String input, int no) {
    String	result;
    int		index;
    String	ext;

    ext = "-out" + no;

    index = input.lastIndexOf('.');
    if (index == -1) {
      result = input + ext;
    }
    else {
      result  = input.substring(0, index);
      result += ext;
      result += input.substring(index);
    }

    return result;
  }

  /**
   * Saves the data in the tmp directory.
   *
   * @param data	the data to save
   * @param filename	the filename to save to (without path)
   * @return		true if successfully saved
   */
  protected boolean save(int[] data, String filename) {
    Vector<String>	list;
    int			i;

    list = new Vector<String>();
    for (i = 0; i < data.length; i++)
      list.add(Integer.toString(data[i]));

    return FileUtils.saveToFile(list, new TmpFile(filename));
  }

  /**
   * Compares the processed data against previously saved output data.
   */
  public void testRegression() {
    Instances			data;
    int[]			processed;
    boolean			ok;
    String			regression;
    int				i;
    String[]			input;
    ColumnFinder[]			setups;
    ColumnFinder			current;
    String[]			output;
    TmpFile[]			outputFiles;

    if (m_NoRegressionTest)
      return;

    input   = getRegressionInputFiles();
    output  = new String[input.length];
    setups  = getRegressionSetups();
    assertEquals("Number of files and setups differ!", input.length, setups.length);

    // process data
    for (i = 0; i < input.length; i++) {
      data = load(input[i]);
      assertNotNull("Could not load data for regression test from " + input[i], data);

      if (setups[i] instanceof ShallowCopySupporter)
	current = (ColumnFinder) ((ShallowCopySupporter) setups[i]).shallowCopy();
      else
	current = (ColumnFinder) Utils.deepCopy(setups[i]);
      assertNotNull("Failed to create copy of algorithm: " + OptionUtils.getCommandLine(setups[i]), current);

      processed = process(data, current);
      assertNotNull("Failed to process data?", processed);

      output[i] = createOutputFilename(input[i], i);
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
