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
 * AbstractDataProcessorTestCase.java
 * Copyright (C) 2010-2013 University of Waikato, Hamilton, New Zealand
 */
package adams.data;

import adams.core.CleanUpHandler;
import adams.core.Destroyable;
import adams.core.option.OptionHandler;
import adams.core.option.OptionUtils;
import adams.data.container.DataContainer;
import adams.db.DatabaseConnectionHandler;
import adams.test.AbstractDatabaseTestCase;
import adams.test.AbstractTestHelper;
import adams.test.TestHelper;
import adams.test.TmpFile;

/**
 * Ancestor for test cases tailored for filters, etc.
 * <br><br>
 * The regression test can be skipped as follows: <br>
 *   <code>-Dadams.test.data.noregression=true</code>
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 * @param <A> the type of algorithm to use
 * @param <I> the type of data to process (input)
 * @param <O> the type of data to process (output)
 */
public abstract class AbstractDataProcessorTestCase<A, I extends DataContainer, O>
  extends AbstractDatabaseTestCase {

  /** property indicating whether regression tests should not be executed. */
  public final static String PROPERTY_NODATAREGRESSION = "adams.test.data.noregression";

  /** whether to execute the data regression test. */
  protected boolean m_NoDataRegressionTest;

  /**
   * Constructs the test case. Called by subclasses.
   *
   * @param name 	the name of the test
   */
  public AbstractDataProcessorTestCase(String name) {
    super(name);
  }

  /**
   * Called by JUnit before each test method.
   *
   * @throws Exception if an error occurs
   */
  @Override
  protected void setUp() throws Exception {
    super.setUp();

    m_NoDataRegressionTest = Boolean.getBoolean(PROPERTY_NODATAREGRESSION);
  }
  
  /**
   * Returns the test helper class to use.
   *
   * @return		the helper class instance
   */
  @Override
  protected AbstractTestHelper<I, O> newTestHelper() {
    return new TestHelper<I, O>(this, "");
  }

  /**
   * Loads the data to process.
   *
   * @param filename	the filename to load (without path)
   * @return		the data, null if it could not be loaded
   * @see		#getDataDirectory()
   */
  protected I load(String filename) {
    return (I) m_TestHelper.load(filename);
  }

  /**
   * Processes the input data and returns the processed data.
   *
   * @param data	the data to work on
   * @param scheme	the scheme to process the data with
   * @return		the processed data
   */
  protected abstract O process(I data, A scheme);

  /**
   * Saves the data in the tmp directory.
   *
   * @param data	the data to save
   * @param filename	the filename to save to (without path)
   * @return		true if successfully saved
   */
  protected boolean save(O data, String filename) {
    return m_TestHelper.save(data, filename);
  }

  /**
   * Returns the database connection props files.
   * <br><br>
   * The default returns null.
   *
   * @return		the props files, null if to use the the default one
   * @see		#getDatabasePropertiesFile()
   */
  protected String[] getRegressionConnections() {
    return null;
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
   * Returns the ignored line indices to use in the regression test.
   *
   * @return		the setups
   */
  protected abstract int[] getRegressionIgnoredLineIndices();

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
   * Tests whether the input data gets changed when processing it. Uses the
   * regression setups.
   */
  public void testDoesntChangeInputData() {
    I		data;
    I		backup;
    O		processed;
    int		i;
    String[]	input;
    A[]		setups;
    A		current;
    String[]	props;

    input   = getRegressionInputFiles();
    setups  = getRegressionSetups();
    props   = getRegressionConnections();
    assertEquals("Number of files and setups differ!", input.length, setups.length);
    if (props != null) {
      assertEquals("Number of files and connection setups differ!", input.length, props.length);
    }
    else {
      props = new String[input.length];
      for (i = 0; i < props.length; i++)
	props[i] = getDatabasePropertiesFile();
    }

    // process data
    for (i = 0; i < input.length; i++) {
      // connect to correct database
      reconnect(props[i]);

      data = load(input[i]);
      assertNotNull("Could not load data for regression test from " + input[i], data);
      backup = (I) data.getClone();

      current = (A) OptionUtils.shallowCopy((OptionHandler) setups[i], false);
      assertNotNull("Failed to create copy of algorithm: " + OptionUtils.getCommandLine(setups[i]), current);

      if (current instanceof DatabaseConnectionHandler)
	((DatabaseConnectionHandler) current).setDatabaseConnection(getDatabaseConnection());

      processed = process(data, current);
      assertNotNull("Failed to process data?", processed);

      assertTrue("Input got changed", data.equals(backup));

      if (current instanceof Destroyable)
	((Destroyable) current).destroy();
    }

    // remove output, clean up scheme
    for (i = 0; i < setups.length; i++) {
      if (setups[i] instanceof Destroyable)
	((Destroyable) setups[i]).destroy();
      else if (setups[i] instanceof CleanUpHandler)
	((CleanUpHandler) setups[i]).cleanUp();
    }

    // connect to default database
    m_Properties = null;
    getDatabaseProperties();
  }

  /**
   * Compares the processed data against previously saved output data.
   */
  public void testRegression() {
    I		data;
    O		processed;
    boolean	ok;
    String	regression;
    int		i;
    String[]	input;
    A[]		setups;
    A		current;
    String[]	output;
    TmpFile[]	outputFiles;
    int[]	ignored;
    String[]	props;

    if (m_NoRegressionTest || m_NoDataRegressionTest)
      return;
    
    setUpBeforeRegression();
    
    input   = getRegressionInputFiles();
    output  = new String[input.length];
    setups  = getRegressionSetups();
    ignored = getRegressionIgnoredLineIndices();
    props   = getRegressionConnections();
    assertEquals("Number of files and setups differ!", input.length, setups.length);
    if (props != null) {
      assertEquals("Number of files and connection setups differ!", input.length, props.length);
    }
    else {
      props = new String[input.length];
      for (i = 0; i < props.length; i++)
	props[i] = getDatabasePropertiesFile();
    }

    // process data
    for (i = 0; i < input.length; i++) {
      // connect to correct database
      reconnect(props[i]);

      data = load(input[i]);
      assertNotNull("Could not load data for regression test from " + input[i], data);

      current = (A) OptionUtils.shallowCopy((OptionHandler) setups[i], false);
      assertNotNull("Failed to create copy of algorithm: " + OptionUtils.getCommandLine(setups[i]), current);

      if (current instanceof DatabaseConnectionHandler)
	((DatabaseConnectionHandler) current).setDatabaseConnection(getDatabaseConnection());

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

    // connect to default database
    m_Properties = null;
    getDatabaseProperties();
  }
  
  /**
   * For further setting up before the regression tests.
   * <br><br>
   * Default implementation does nothing.
   */
  protected void setUpBeforeRegression() {
  }

  /**
   * For further cleaning up after the regression tests.
   * <br><br>
   * Default implementation does nothing.
   */
  protected void cleanUpAfterRegression() {
  }
}
