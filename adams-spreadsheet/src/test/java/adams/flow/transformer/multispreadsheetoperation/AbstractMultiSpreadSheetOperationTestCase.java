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
 * AbstractMultiSpreadSheetOperationTestCase.java
 * Copyright (C) 2021 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.transformer.multispreadsheetoperation;

import adams.core.MessageCollection;
import adams.core.Utils;
import adams.core.io.FileUtils;
import adams.core.option.OptionUtils;
import adams.data.io.input.CsvSpreadSheetReader;
import adams.data.spreadsheet.SpreadSheet;
import adams.test.AbstractTestHelper;
import adams.test.AdamsTestCase;
import adams.test.TestHelper;
import adams.test.TmpFile;

import java.lang.reflect.Array;

/**
 * Ancestor for multi-spreadsheet operation test cases.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public abstract class AbstractMultiSpreadSheetOperationTestCase
  extends AdamsTestCase {

  /**
   * Constructs the test case. Called by subclasses.
   *
   * @param name 	the name of the test
   */
  public AbstractMultiSpreadSheetOperationTestCase(String name) {
    super(name);
  }

  /**
   * Returns the test helper class to use.
   *
   * @return		the helper class instance
   */
  @Override
  protected AbstractTestHelper newTestHelper() {
    return new TestHelper(this, "adams/flow/transformer/multispreadsheetoperation/data");
  }

  /**
   * Loads the data to process.
   *
   * @param filename		the filename to load (without path)
   * @return			the data, null if it could not be loaded
   */
  protected SpreadSheet load(String filename) {
    SpreadSheet			result;
    CsvSpreadSheetReader	reader;

    m_TestHelper.copyResourceToTmp(filename);
    reader = new CsvSpreadSheetReader();
    result = reader.read(new TmpFile(filename).getAbsolutePath());
    m_TestHelper.deleteFileFromTmp(filename);

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
  protected abstract AbstractMultiSpreadSheetOperation[] getRegressionSetups();

  /**
   * Processes the input data and returns the processed data.
   *
   * @param data	the data to work on
   * @param scheme	the scheme to process the data with
   * @param errors 	for collecting errors
   * @return		the processed data
   */
  protected Object process(SpreadSheet[] data, AbstractMultiSpreadSheetOperation scheme, MessageCollection errors) {
    return scheme.process(data, errors);
  }

  /**
   * Creates an output filename based on the input filename.
   *
   * @param input	the input filename (no path)
   * @param no		the number of the test
   * @return		the generated output filename (no path)
   */
  protected String createOutputFilename(String[] input, int no) {
    String	result;
    String[]	noExt;
    int		i;
    String	ext;

    ext = "#out" + no;

    noExt = new String[input.length];
    for (i = 0; i < input.length; i++)
      noExt[i] = FileUtils.replaceExtension(input[i], "");
    result = Utils.flatten(noExt, "#") + ext;

    return result;
  }

  /**
   * Saves the data in the tmp directory.
   *
   * @param data	the data to save
   * @param filename	the filename to save to (without path)
   * @return		true if successfully saved
   */
  protected boolean save(Object data, String filename) {
    String	dataStr;
    Object[]	array;
    int		i;

    if (data.getClass().isArray()) {
      array = new Object[Array.getLength(data)];
      for (i = 0; i < array.length; i++)
        array[i] = Array.get(data, i);
      dataStr = Utils.flatten(array, "\n\n");
    }
    else {
      dataStr = data.toString();
    }
    return FileUtils.writeToFile(new TmpFile(filename).getAbsolutePath(), dataStr);
  }

  /**
   * Compares the processed data against previously saved output data.
   */
  public void testRegression() {
    SpreadSheet[]			data;
    Object				processed;
    boolean				ok;
    String				regression;
    int					i;
    String[]				input;
    AbstractMultiSpreadSheetOperation[]	setups;
    AbstractMultiSpreadSheetOperation	current;
    String[]				output;
    TmpFile[]				outputFiles;
    MessageCollection			errors;

    if (m_NoRegressionTest)
      return;

    input   = getRegressionInputFiles();
    setups  = getRegressionSetups();
    output  = new String[setups.length];

    // process data
    data = new SpreadSheet[input.length];
    for (i = 0; i < input.length; i++) {
      data[i] = load(input[i]);
      assertNotNull("Could not load data for regression test from " + input[i], data);
    }

    for (i = 0; i < setups.length; i++) {
      current = (AbstractMultiSpreadSheetOperation) OptionUtils.shallowCopy(setups[i]);
      assertNotNull("Failed to create copy of algorithm: " + OptionUtils.getCommandLine(setups[i]), current);

      errors = new MessageCollection();
      processed = process(data, current, errors);
      assertNotNull("Failed to process data?", processed);
      assertEquals("Should not have generated errors!", "", errors.toString());

      output[i] = createOutputFilename(input, i);
      ok = save(processed, output[i]);
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
    for (i = 0; i < output.length; i++)
      m_TestHelper.deleteFileFromTmp(output[i]);
    for (i = 0; i < setups.length; i++)
      setups[i].destroy();
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
