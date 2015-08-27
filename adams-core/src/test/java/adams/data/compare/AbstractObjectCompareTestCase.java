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
 * AbstractObjectCompareTestCase.java
 * Copyright (C) 2015 University of Waikato, Hamilton, New Zealand
 */
package adams.data.compare;

import adams.core.Utils;
import adams.core.io.FileUtils;
import adams.core.option.OptionHandler;
import adams.core.option.OptionUtils;
import adams.test.AbstractDatabaseTestCase;
import adams.test.AbstractTestHelper;
import adams.test.TestHelper;
import adams.test.TmpFile;

/**
 * Ancestor for object comparison test cases.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractObjectCompareTestCase
  extends AbstractDatabaseTestCase {

  /**
   * Constructs the test case. Called by subclasses.
   *
   * @param name 	the name of the test
   */
  public AbstractObjectCompareTestCase(String name) {
    super(name);
  }

  /**
   * Returns the test helper class to use.
   *
   * @return		the helper class instance
   */
  @Override
  protected AbstractTestHelper newTestHelper() {
    return new TestHelper(this, "adams/data/compare/data");
  }

  /**
   * Processes the input data and returns the generate comparison result.
   * First element is the header (null if none generated), everything 
   * afterwards are the rows (null if none generated).
   *
   * @param array	the data to work on
   * @param scheme	the scheme to process the data with
   * @return		the processed data (header/row)
   */
  protected Object process(Object[] array, AbstractObjectCompare scheme) {
    return scheme.compareObjects(array[0], array[1]);
  }

  /**
   * Turns the data object into a useful string representation.
   * <br><br>
   * The default implementation merely performs a "toString()" on the object.
   *
   * @param data	the object to convert
   * @return		the string representation
   */
  protected String toString(Object data) {
    return "" + data;
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
   * Returns the object arrays to process in regression.
   *
   * @return		the arrays
   */
  protected abstract Object[][] getRegressionArrays();

  /**
   * Returns the setups to use in the regression test.
   *
   * @return		the setups
   */
  protected abstract AbstractObjectCompare[] getRegressionSetups();

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
    Object			processed;
    boolean			ok;
    String			regression;
    int				n;
    int				i;
    boolean			append;
    Object[][] 			arrays;
    AbstractObjectCompare[]	setups;
    AbstractObjectCompare	current;
    String[]			output;
    TmpFile[]			outputFiles;
    int[]			ignored;

    if (m_NoRegressionTest)
      return;

    arrays  = getRegressionArrays();
    setups  = getRegressionSetups();
    output  = new String[setups.length];
    ignored = getRegressionIgnoredLineIndices();

    // process data
    for (n = 0; n < setups.length; n++) {
      append    = false;
      output[n] = createOutputFilename(n);
      current   = (AbstractObjectCompare) OptionUtils.shallowCopy((OptionHandler) setups[n], false);
      assertNotNull("Failed to create copy of feature converter algorithm: " + OptionUtils.getCommandLine(setups[n]), current);

      for (i = 0; i < arrays.length; i++) {
	processed = process(arrays[i], current);
	assertNotNull("Failed to process data: " + Utils.arrayToString(arrays[i]), processed);

	// any output generated?
	if (processed == null)
	  continue;

	ok        = save(toString(processed), output[n], append);
	assertTrue("Failed to save regression data?", ok);

	append = true;
      }

      current.destroy();
    }

    // test regression
    outputFiles = new TmpFile[output.length];
    for (i = 0; i < output.length; i++)
      outputFiles[i] = new TmpFile(output[i]);
    regression = m_Regression.compare(outputFiles, ignored);
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
