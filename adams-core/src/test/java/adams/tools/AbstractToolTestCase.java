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
 * AbstractToolTestCase.java
 * Copyright (C) 2011-2013 University of Waikato, Hamilton, New Zealand
 */

package adams.tools;

import adams.core.CleanUpHandler;
import adams.core.Destroyable;
import adams.core.option.OptionHandler;
import adams.core.option.OptionUtils;
import adams.db.DatabaseConnectionHandler;
import adams.test.AbstractDatabaseTestCase;
import adams.test.AbstractTestHelper;
import adams.test.TestHelper;
import adams.test.TmpFile;

/**
 * Ancestor for test cases for tools.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 * @param <A> the type of tool to test
 */
public abstract class AbstractToolTestCase<A extends AbstractTool>
  extends AbstractDatabaseTestCase {

  /**
   * Constructs the test case. Called by subclasses.
   *
   * @param name 	the name of the test
   */
  public AbstractToolTestCase(String name) {
    super(name);
  }

  /**
   * Returns the test helper class to use.
   *
   * @return		the helper class instance
   */
  @Override
  protected AbstractTestHelper newTestHelper() {
    return new TestHelper(this, "adams/tools/data");
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
   * The files to use as input in the regression tests, in case of tool
   * implementing the InputFileHandler interface.
   *
   * @return		the files, zero-length if not an InputFileHandler
   */
  protected abstract String[] getRegressionInputFiles();

  /**
   * The files to use as output in the regression tests, in case of tool
   * implementing the OutputFileGenerator interface.
   * <br><br>
   * NB: these names must be all different!
   *
   * @return		the files, zero-length if not an OutputFileGenerator
   */
  protected abstract String[] getRegressionOutputFiles();

  /**
   * Returns the setups to test in the regression tests.
   *
   * @return		the setups to test
   */
  protected abstract A[] getRegressionSetups();

  /**
   * Returns the ignored line indices to use in the regression test.
   *
   * @return		the setups
   */
  protected abstract int[] getRegressionIgnoredLineIndices();

  /**
   * Compares the processed data against previously saved output data.
   */
  public void testRegression() {
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
    output  = getRegressionOutputFiles();
    setups  = getRegressionSetups();
    ignored = getRegressionIgnoredLineIndices();
    props   = getRegressionConnections();
    if (setups.length == 0)
      return;

    if (setups[0] instanceof InputFileHandler)
      assertEquals("Number of input files and setups differ!", input.length, setups.length);
    if (setups[0] instanceof OutputFileGenerator)
      assertEquals("Number of output files and setups differ!", output.length, setups.length);
    if (props != null) {
      assertEquals("Number of connection props and setups differ!", input.length, setups.length);
    }
    else {
      props = new String[setups.length];
      for (i = 0; i < props.length; i++)
	props[i] = getDatabasePropertiesFile();
    }

    // process data
    for (i = 0; i < setups.length; i++) {
      // connect to correct database
      reconnect(props[i]);

      current = (A) OptionUtils.shallowCopy((OptionHandler) setups[i], false);
      assertNotNull("Failed to create copy of algorithm: " + OptionUtils.getCommandLine(setups[i]), current);

      if (current instanceof DatabaseConnectionHandler)
	((DatabaseConnectionHandler) current).setDatabaseConnection(getDatabaseConnection());
      if (current instanceof InputFileHandler)
	((InputFileHandler) current).setInputFile(new TmpFile(input[i]));
      if (current instanceof OutputFileGenerator)
	((OutputFileGenerator) current).setOutputFile(new TmpFile(output[i]));

      current.run();

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
