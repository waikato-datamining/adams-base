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
 * AbstractFeatureConverterTestCase.java
 * Copyright (C) 2014 University of Waikato, Hamilton, New Zealand
 */
package adams.data.featureconverter;

import java.lang.reflect.Array;
import java.util.List;

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
public abstract class AbstractFeatureConverterTestCase
  extends AbstractDatabaseTestCase {

  /**
   * Constructs the test case. Called by subclasses.
   *
   * @param name 	the name of the test
   */
  public AbstractFeatureConverterTestCase(String name) {
    super(name);
  }

  /**
   * Returns the test helper class to use.
   *
   * @return		the helper class instance
   */
  @Override
  protected AbstractTestHelper newTestHelper() {
    return new TestHelper(this, "adams/data/featureconverter/data");
  }

  /**
   * Processes the input data (header definition and row data) and returns 
   * the generate data (header/row).
   * First element is the header (null if none generated), everything 
   * afterwards are the rows (null if none generated).
   *
   * @param data	the data to work on
   * @param scheme	the scheme to process the data with
   * @return		the processed data (header/row)
   */
  protected Object[] process(HeaderDefinition header, List[] rows, AbstractFeatureConverter scheme) {
    Object[]	result;
    int		i;

    result = new Object[rows.length + 1];
    result[0] = scheme.generateHeader(header);
    for (i = 0; i < rows.length; i++)
      result[i + 1] = scheme.generateRow(rows[i]);

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
    StringBuilder	result;
    List		list;
    int			i;
    
    if (data instanceof HeaderDefinition) {
      return data.toString();
    }
    else if (data instanceof List) {
      list = (List) data;
      result = new StringBuilder();
      for (i = 0; i < list.size(); i++) {
	if (i > 0)
	  result.append(",");
	result.append(i + ":");
	if (list.get(i) instanceof Number)
	  result.append(Utils.doubleToString(((Number) list.get(i)).doubleValue(), 8));
	else
	  result.append(list.get(i).toString());
      }
      return result.toString();
    }
    else if (data.getClass().isArray()) {
      result = new StringBuilder();
      for (i = 0; i < Array.getLength(data); i++) {
	if (i > 0)
	  result.append("\n");
	result.append(toString(Array.get(data, i)));
      }
      return result.toString();
    }
    else {
      return data.toString();
    }
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
   * Returns the header definitions to use in the regression test.
   *
   * @return		the header definitions
   */
  protected abstract HeaderDefinition[] getRegressionHeaderDefinitions();

  /**
   * Returns the data rows to use in the regression test. An array of rows per
   * header definition.
   *
   * @return		the data rows
   */
  protected abstract List[][] getRegressionRows();

  /**
   * Returns the setups to use in the regression test.
   *
   * @return		the setups
   */
  protected abstract AbstractFeatureConverter[] getRegressionSetups();

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
    Object[]			processed;
    boolean			ok;
    String			regression;
    int				n;
    int				i;
    boolean			append;
    HeaderDefinition[]		headers;
    List[][]			rows;
    AbstractFeatureConverter[]	setups;
    AbstractFeatureConverter	current;
    String[]			output;
    TmpFile[]			outputFiles;
    int[]			ignored;

    if (m_NoRegressionTest)
      return;

    headers = getRegressionHeaderDefinitions();
    rows    = getRegressionRows();
    setups  = getRegressionSetups();
    output  = new String[setups.length];
    ignored = getRegressionIgnoredLineIndices();
    assertEquals("Number of headers and setups differ!", headers.length, setups.length);
    assertEquals("Number of headers and rows differ!", headers.length, rows.length);

    // process data
    for (n = 0; n < setups.length; n++) {
      append    = false;
      output[n] = createOutputFilename(n);
      current   = (AbstractFeatureConverter) OptionUtils.shallowCopy((OptionHandler) setups[n], false);
      assertNotNull("Failed to create copy of feature converter algorithm: " + OptionUtils.getCommandLine(setups[n]), current);

      for (i = 0; i < headers.length; i++) {
	processed = process(headers[i], rows[i], current);
	assertNotNull("Failed to process data: " + Utils.arrayToString(rows[i]), processed);

	// any output generated?
	if (processed[0] == null)
	  continue;

	ok        = save(toString(processed), output[n], append);
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
