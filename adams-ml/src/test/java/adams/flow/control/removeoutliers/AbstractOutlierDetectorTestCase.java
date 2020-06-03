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
 * AbstractOutlierDetectorTestCase.java
 * Copyright (C) 2020 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.control.removeoutliers;

import adams.core.io.FileUtils;
import adams.core.option.OptionUtils;
import adams.data.io.input.CsvSpreadSheetReader;
import adams.data.spreadsheet.SpreadSheet;
import adams.data.spreadsheet.SpreadSheetColumnIndex;
import adams.test.AbstractDatabaseTestCase;
import adams.test.AbstractTestHelper;
import adams.test.TestHelper;
import adams.test.TmpFile;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * Ancestor for outlier detector test cases.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public abstract class AbstractOutlierDetectorTestCase
  extends AbstractDatabaseTestCase {

  /**
   * Constructs the test case. Called by subclasses.
   *
   * @param name 	the name of the test
   */
  public AbstractOutlierDetectorTestCase(String name) {
    super(name);
  }

  /**
   * Returns the test helper class to use.
   *
   * @return		the helper class instance
   */
  @Override
  protected AbstractTestHelper newTestHelper() {
    return new TestHelper(this, "adams/flow/control/removeoutliers/data");
  }

  /**
   * Processes the input data (header definition and row data) and returns 
   * the generate data (header/row).
   * First element is the header (null if none generated), everything 
   * afterwards are the rows (null if none generated).
   *
   * @param sheet	the spreadsheet to work on
   * @param actual 	the column with the ground truth
   * @param predicted 	the column with the predictions
   * @param scheme	the scheme to process the data with
   * @return		the list of outlier indices
   */
  protected List<Integer> process(SpreadSheet sheet, SpreadSheetColumnIndex actual, SpreadSheetColumnIndex predicted, AbstractOutlierDetector scheme) {
    List<Integer>	result;
    Set<Integer> 	outliers;

    outliers = scheme.detect(sheet, actual, predicted);
    result = new ArrayList<>(outliers);
    Collections.sort(result);

    return result;
  }

  /**
   * Turns the collection of integets into a useful string representation.
   *
   * @param data	the object to convert
   * @return		the string representation
   */
  protected String toString(Collection<Integer> data) {
    StringBuilder	result;
    Iterator<Integer> 	iter;

    result = new StringBuilder();
    iter   = data.iterator();
    while (iter.hasNext())
      result.append(iter.next()).append("\n");

    return result.toString();
  }

  /**
   * Saves the data in the tmp directory.
   *
   * @param data	the data to save
   * @param filename	the filename to save to (without path)
   * @return		true if successfully saved
   */
  protected boolean save(Collection<Integer> data, String filename) {
    return FileUtils.writeToFile(new TmpFile(filename).getAbsolutePath(), toString(data), false);
  }

  /**
   * Loads the CSV file.
   *
   * @param filename	the name (no path)
   * @return		the sheet, null if failed to read
   */
  protected SpreadSheet load(String filename) {
    CsvSpreadSheetReader	reader;
    TmpFile			file;

    if (!m_TestHelper.copyResourceToTmp(filename))
      throw new IllegalStateException("Failed to copy '" + filename + "' to tmp dir!");
    file   = new TmpFile(filename);
    reader = new CsvSpreadSheetReader();
    return reader.read(file);
  }

  /**
   * Returns the spreadsheets to use in the regression test.
   *
   * @return		the sheets
   */
  protected abstract SpreadSheet[] getRegressionSpreadSheets();

  /**
   * Returns the "actual" columns to use in the regression test.
   *
   * @return		the columns
   */
  protected abstract SpreadSheetColumnIndex[] getRegressionActualCols();

  /**
   * Returns the "predicted" columns to use in the regression test.
   *
   * @return		the columns
   */
  protected abstract SpreadSheetColumnIndex[] getRegressionPredictedCols();

  /**
   * Returns the setups to use in the regression test.
   *
   * @return		the setups
   */
  protected abstract AbstractOutlierDetector[] getRegressionSetups();

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
    List<Integer>		processed;
    boolean			ok;
    String			regression;
    int				n;
    int				i;
    SpreadSheet[]		sheets;
    SpreadSheetColumnIndex[]	actCols;
    SpreadSheetColumnIndex[]	predCols;
    AbstractOutlierDetector[]	setups;
    AbstractOutlierDetector 	current;
    String[]			output;
    TmpFile[]			outputFiles;
    int[]			ignored;

    if (m_NoRegressionTest)
      return;

    setups   = getRegressionSetups();
    sheets   = getRegressionSpreadSheets();
    actCols  = getRegressionActualCols();
    predCols = getRegressionPredictedCols();
    output   = new String[setups.length];
    ignored  = getRegressionIgnoredLineIndices();
    assertEquals("Number of sheets and setups differ!", sheets.length, setups.length);
    assertEquals("Number of sheets and actual columns differ!", sheets.length, actCols.length);
    assertEquals("Number of sheets and predicted columns differ!", sheets.length, predCols.length);

    // process data
    for (n = 0; n < setups.length; n++) {
      output[n] = createOutputFilename(n);
      current   = (AbstractOutlierDetector) OptionUtils.shallowCopy(setups[n], false);
      assertNotNull("Failed to create copy of outlier detector: " + OptionUtils.getCommandLine(setups[n]), current);

      processed = process(sheets[n], actCols[n], predCols[n], current);
      assertNotNull("Failed to process sheet: " + actCols[n].getIndex() + "/" + predCols[n].getIndex(), processed);

      ok        = save(processed, output[n]);
      assertTrue("Failed to save regression data?", ok);

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
