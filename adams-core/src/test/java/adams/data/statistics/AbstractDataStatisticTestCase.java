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
 * AbstractDataStatisticTestCase.java
 * Copyright (C) 2010-2013 University of Waikato, Hamilton, New Zealand
 */
package adams.data.statistics;

import java.io.File;
import java.util.Collections;
import java.util.Iterator;
import java.util.Vector;

import adams.core.CleanUpHandler;
import adams.core.Destroyable;
import adams.core.io.FileUtils;
import adams.data.container.DataContainer;
import adams.test.AbstractTestHelper;
import adams.test.AdamsTestCase;
import adams.test.TestHelper;
import adams.test.TmpFile;

/**
 * Ancestor for data statistic test cases.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 * @param <A> the type of algorithm to use
 * @param <D> the type of data to process
 */
public abstract class AbstractDataStatisticTestCase<A extends AbstractDataStatistic, D extends DataContainer>
  extends AdamsTestCase {

  /**
   * Constructs the test case. Called by subclasses.
   *
   * @param name 	the name of the test
   */
  public AbstractDataStatisticTestCase(String name) {
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
  protected A process(D data, A scheme) {
    scheme.setData((D) data);
    return scheme;
  }

  /**
   * Turns the data stored in a data statistic into a string representation.
   *
   * @param scheme	the data statistic to process
   * @return		the generated output
   */
  protected Vector<String> toString(A scheme) {
    Vector<String>	result;
    Iterator<String>	names;
    Vector<String>	namesSorted;

    result      = new Vector<String>();
    namesSorted = new Vector<String>();
    names       = scheme.statisticNames();
    while (names.hasNext())
      namesSorted.add(names.next());
    Collections.sort(namesSorted);
    for (String name: namesSorted)
      result.add(name + ": " + scheme.getStatistic(name));

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
  protected abstract A[] getRegressionSetups();

  /**
   * Loads the specified data container from disk.
   *
   * @param filename	the file to load (without the path)
   * @return		the data container or null in case of an error
   */
  protected abstract D load(String filename);

  /**
   * Saves the generated statistics output as file.
   *
   * @param data	the generated output data
   * @param filename	the file to save the data to (in the temp directory)
   * @return		true if successfully saved
   */
  protected boolean save(Vector<String> data, String filename) {
    return FileUtils.saveToFile(data, new File(m_TestHelper.getTmpDirectory() + File.separator + filename));
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
   * Compares the processed data against previously saved output data.
   */
  public void testRegression() {
    D			data;
    Vector<String>	processed;
    boolean		ok;
    String		regression;
    int			i;
    String[]		input;
    A[]			setups;
    String[]		output;
    TmpFile[]		outputFiles;

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

      processed = toString(process(data, setups[i]));
      assertNotNull("Failed to process data?", processed);

      output[i] = createOutputFilename(input[i], i);
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
