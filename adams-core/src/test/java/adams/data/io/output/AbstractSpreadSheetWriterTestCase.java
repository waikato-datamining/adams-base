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
 * AbstractSpreadSheetWriterTestCase.java
 * Copyright (C) 2010-2013 University of Waikato, Hamilton, New Zealand
 */
package adams.data.io.output;

import adams.core.CleanUpHandler;
import adams.core.Destroyable;
import adams.data.io.input.CsvSpreadSheetReader;
import adams.data.spreadsheet.SpreadSheet;
import adams.test.AbstractTestHelper;
import adams.test.AdamsTestCase;
import adams.test.TestHelper;
import adams.test.TmpFile;

/**
 * Ancestor for spreadsheet writer test cases.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractSpreadSheetWriterTestCase
  extends AdamsTestCase {

  /**
   * Constructs the test case. Called by subclasses.
   *
   * @param name 	the name of the test
   */
  public AbstractSpreadSheetWriterTestCase(String name) {
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
   * Reads the data to use.
   *
   * @param filename	the file to read (no path)
   * @return		the generated content
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
   * Saves the spreadsheet with the specified writer.
   *
   * @param data	the output data
   * @param filename	the file to save the data to (in the temp directory)
   * @param writer	the writer to use for saving the data
   * @return		true if successfully saved
   */
  protected boolean save(SpreadSheet data, String filename, SpreadSheetWriter writer) {
    return writer.write(data, new TmpFile(filename).getAbsolutePath());
  }

  /**
   * Returns the filenames (without path) of the input data files to use
   * in the setup tests.
   *
   * @return		the filenames
   */
  protected abstract String[] getInputFiles();

  /**
   * Returns the filenames (without path) of the output data files to use
   * in the setup tests.
   *
   * @return		the filenames
   */
  protected abstract String[] getOutputFiles();

  /**
   * Returns the setups to use in the setup tests.
   *
   * @return		the setups
   */
  protected abstract SpreadSheetWriter[] getSetups();

  /**
   * Tests the setups.
   */
  public void testSetups() {
    SpreadSheet		data;
    boolean		ok;
    int			i;
    String[]		input;
    String[]		output;
    SpreadSheetWriter[]	setups;

    input   = getInputFiles();
    output  = getOutputFiles();
    setups  = getSetups();
    assertEquals("Number of input and output files differ!", input.length, output.length);
    assertEquals("Number of files and setups differ!", input.length, setups.length);

    // process data
    for (i = 0; i < input.length; i++) {
      data = load(input[i]);
      assertNotNull("Failed to load data?", data);

      ok = save(data, output[i], setups[i]);
      assertTrue("Failed to save data?", ok);
    }

    // remove output, clean up scheme
    for (i = 0; i < output.length; i++) {
      if (setups[i] instanceof Destroyable)
	((Destroyable) setups[i]).destroy();
      else if (setups[i] instanceof CleanUpHandler)
	((CleanUpHandler) setups[i]).cleanUp();
      m_TestHelper.deleteFileFromTmp(output[i]);
    }
  }

  /**
   * Returns whether a regression can be run.
   * <p/>
   * Default implementation returns false.
   *
   * @return		true if the regression can be run (e.g., for non-binary output)
   */
  protected boolean hasRegressionTest() {
    return false;
  }

  /**
   * Returns the ignored line indices to use in the regression test.
   *
   * @return		the line indices
   */
  protected int[] getRegressionIgnoredLineIndices() {
    return new int[0];
  }

  /**
   * Regression test.
   * <p/>
   * Only gets executed if "hasRegressionTest" returns true.
   *
   * @see
   */
  public void testRegression() {
    SpreadSheet		data;
    boolean		ok;
    String		regression;
    int			i;
    String[]		input;
    String[]		output;
    TmpFile[]		outputFiles;
    SpreadSheetWriter[]	setups;

    if (m_NoRegressionTest)
      return;

    if (!hasRegressionTest())
      return;

    input   = getInputFiles();
    output  = getOutputFiles();
    setups  = getSetups();
    assertEquals("Number of input and output files differ!", input.length, output.length);
    assertEquals("Number of files and setups differ!", input.length, setups.length);

    // process data
    for (i = 0; i < input.length; i++) {
      data = load(input[i]);
      assertNotNull("Failed to load data?", data);

      ok = save(data, output[i], setups[i]);
      assertTrue("Failed to save data?", ok);
    }

    // test regression
    outputFiles = new TmpFile[output.length];
    for (i = 0; i < output.length; i++)
      outputFiles[i] = new TmpFile(output[i]);
    regression = m_Regression.compare(outputFiles, getRegressionIgnoredLineIndices());
    assertNull("Output differs:\n" + regression, regression);

    // remove output, clean up scheme
    for (i = 0; i < output.length; i++) {
      if (setups[i] instanceof Destroyable)
	((Destroyable) setups[i]).destroy();
      else if (setups[i] instanceof CleanUpHandler)
	((CleanUpHandler) setups[i]).cleanUp();
      m_TestHelper.deleteFileFromTmp(output[i]);
    }
  }
}
