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
 * AbstractDataContainerWriterTestCase.java
 * Copyright (C) 2010-2013 University of Waikato, Hamilton, New Zealand
 */
package adams.data.io.output;

import adams.core.CleanUpHandler;
import adams.core.Destroyable;
import adams.core.option.OptionHandler;
import adams.core.option.OptionUtils;
import adams.data.container.DataContainer;
import adams.test.AbstractDatabaseTestCase;
import adams.test.AbstractTestHelper;
import adams.test.TestHelper;
import adams.test.TmpFile;

/**
 * Ancestor for data container reader test cases.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 * @param <A> the type of reader to use
 * @param <D> the type of data to read
 */
public abstract class AbstractDataContainerWriterTestCase<A extends AbstractDataContainerWriter, D extends DataContainer>
  extends AbstractDatabaseTestCase {

  /**
   * Constructs the test case. Called by subclasses.
   *
   * @param name 	the name of the test
   */
  public AbstractDataContainerWriterTestCase(String name) {
    super(name);
  }

  /**
   * Returns the test helper class to use.
   *
   * @return		the helper class instance
   */
  @Override
  protected AbstractTestHelper newTestHelper() {
    return new TestHelper(this, "adams/data/io/output/data");
  }

  /**
   * Loads the data to process.
   *
   * @param filename	the filename to load (without path)
   * @return		the data, null if it could not be loaded
   */
  protected D load(String filename) {
    return (D) m_TestHelper.load(filename);
  }

  /**
   * Saves the data in the tmp directory.
   *
   * @param data	the data to save
   * @param scheme	the scheme to use for writing the data
   * @param filename	the filename to save to (without path)
   * @return		true if successfully saved
   */
  protected boolean save(D data, A scheme, String filename) {
    TmpFile	file;

    file = new TmpFile(filename);
    scheme.setOutput(file);
    scheme.write(data);

    return file.exists();
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
   * Compares the processed data against previously saved output data.
   */
  public void testRegression() {
    D		data;
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

    if (m_NoRegressionTest)
      return;

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

      current = (A) OptionUtils.shallowCopy((OptionHandler) setups[i], false);
      assertNotNull("Failed to create copy of algorithm: " + OptionUtils.getCommandLine(setups[i]), current);

      data = load(input[i]);
      assertNotNull("Failed to read data?", data);

      output[i] = createOutputFilename(input[i], i);
      ok        = save(data, current, output[i]);
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
   * For further cleaning up after the regression tests.
   * <br><br>
   * Default implementation does nothing.
   */
  protected void cleanUpAfterRegression() {
  }
}
