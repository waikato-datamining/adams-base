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
 * AbstractConversionTestCase.java
 * Copyright (C) 2010-2013 University of Waikato, Hamilton, New Zealand
 */
package adams.data.conversion;

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
 * Ancestor for conversion test cases.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractConversionTestCase
  extends AbstractDatabaseTestCase {

  /**
   * Constructs the test case. Called by subclasses.
   *
   * @param name 	the name of the test
   */
  public AbstractConversionTestCase(String name) {
    super(name);
  }

  /**
   * Returns the test helper class to use.
   *
   * @return		the helper class instance
   */
  @Override
  protected AbstractTestHelper newTestHelper() {
    return new TestHelper(this, "adams/data/conversion/data");
  }

  /**
   * Processes the input data and returns the processed data.
   * First element is the result (null if none generated), second is the
   * conversion error message (null if none generated).
   *
   * @param data	the data to work on
   * @param scheme	the scheme to process the data with
   * @return		the processed data
   */
  protected Object[] process(Object data, Conversion scheme) {
    Object[]	result;
    String	msg;

    result = new Object[2];
    scheme.setInput(data);
    msg    = scheme.convert();
    if (msg == null)
      result[0] = scheme.getOutput();
    else
      result[1] = msg;

    return result;
  }

  /**
   * Turns the data object into a useful string representation.
   * <br><br>
   * The default implementation merely performs a "toString()" on the object, 
   * or, in case of arrays, a {@link Utils#arrayToString(Object)}.
   *
   * @param data	the object to convert
   * @return		the string representation
   */
  protected String toString(Object data) {
    if (data.getClass().isArray())
      return Utils.arrayToString(data);
    else
      return data.toString();
  }

  /**
   * Saves the data in the tmp directory.
   *
   * @param data	the data to save
   * @param filename	the filename to save to (without path)
   * @param append	whether to append the data or not
   * @return		true if successfully saved
   */
  protected boolean save(Object data, String filename, boolean append) {
    return FileUtils.writeToFile(new TmpFile(filename).getAbsolutePath(), data.toString(), append);
  }

  /**
   * Returns the input data to use in the regression test.
   *
   * @return		the objects
   */
  protected abstract Object[] getRegressionInput();

  /**
   * Returns the setups to use in the regression test.
   *
   * @return		the setups
   */
  protected abstract Conversion[] getRegressionSetups();

  /**
   * Returns the ignored line indices to use in the regression test.
   *
   * @return		the setups
   */
  protected abstract int[] getRegressionIgnoredLineIndices();

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
    Object[]		processed;
    boolean		ok;
    String		regression;
    int			n;
    int			i;
    boolean		append;
    Object[]		input;
    Conversion[]	setups;
    Conversion		current;
    String[]		output;
    TmpFile[]		outputFiles;
    int[]		ignored;

    if (m_NoRegressionTest)
      return;

    input   = getRegressionInput();
    setups  = getRegressionSetups();
    output  = new String[setups.length];
    ignored = getRegressionIgnoredLineIndices();

    // process data
    for (n = 0; n < setups.length; n++) {
      append    = false;
      output[n] = createOutputFilename(n);
      if (setups[n] instanceof OptionHandler)
	current = (Conversion) OptionUtils.shallowCopy((OptionHandler) setups[n], false);
      else
	current = (Conversion) Utils.deepCopy(setups[n]);
      assertNotNull("Failed to create copy of conversion algorithm: " + OptionUtils.getCommandLine(setups[n]), current);

      for (i = 0; i < input.length; i++) {
	processed = process(input[i], current);
	assertNull("Failed to process data: " + processed[1], processed[1]);

	// any output generated?
	if (processed[0] == null)
	  continue;

	ok        = save(toString(processed[0]), output[n], append);
	assertTrue("Failed to save regression data?", ok);

	append = true;
      }

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
   * <br><br>
   * Default implementation does nothing.
   */
  protected void cleanUpAfterRegression() {
  }
}
