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
 * AbstractFilterTestCase.java
 * Copyright (C) 2018 University of Waikato, Hamilton, NZ
 */

package adams.ml.preprocessing;

import adams.core.io.FileUtils;
import adams.core.logging.LoggingHelper;
import adams.core.option.OptionUtils;
import adams.data.io.input.ChunkedSpreadSheetReader;
import adams.data.io.input.SpreadSheetReader;
import adams.data.spreadsheet.Row;
import adams.data.spreadsheet.SpreadSheet;
import adams.data.spreadsheet.SpreadSheetHelper;
import adams.ml.data.Dataset;
import adams.ml.data.DefaultDataset;
import adams.test.AbstractTestHelper;
import adams.test.AdamsTestCase;
import adams.test.TestHelper;
import adams.test.TmpFile;

/**
 * Ancestor for filter tests.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public abstract class AbstractFilterTestCase<T extends BatchFilter>
  extends AdamsTestCase {

  /**
   * Constructs the test case. Called by subclasses.
   *
   * @param name the name of the test
   */
  public AbstractFilterTestCase(String name) {
    super(name);
  }

  /**
   * Returns the test helper class to use.
   *
   * @return the helper class instance
   */
  @Override
  protected AbstractTestHelper newTestHelper() {
    return new TestHelper(this, "adams/ml/preprocessing/data");
  }

  /**
   * Returns a typical setup.
   *
   * @return		the setup
   */
  protected abstract T getTypicalSetup();

  /**
   * Returns a typical dataset.
   *
   * @return		the dataset
   */
  protected abstract Dataset getTypicalDataset();

  /**
   * Tests whether the algorithm changes the input data.
   */
  public void testDoesntChangeInput() {
    Dataset	data;
    Dataset	dataCopy;
    T		algorithm;

    data      = getTypicalDataset();
    dataCopy  = data.getClone();
    algorithm = getTypicalSetup();
    try {
      algorithm.filter(data);
    }
    catch (Exception e) {
      fail("Failed to build model: " + LoggingHelper.throwableToString(e));
      return;
    }

    assertNull("Changed input data", SpreadSheetHelper.compare(dataCopy, data));
  }

  /**
   * Compares two datasets, fails at the first row with differences.
   *
   * @param data1	the first dataset
   * @param data2	the second dataset
   * @return		null if equal, otherwise error message with details
   */
  protected String compareDatasets(Dataset data1, Dataset data2) {
    String	result;
    Row		row1;
    Row		row2;
    int		i;

    result = data1.equalsHeader(data2);

    if (result == null) {
      if (data1.getRowCount() != data2.getRowCount())
        result = "# rows differ: " + data1.getRowCount() + " != " + data2.getRowCount();
    }

    if (result == null) {
      for (i = 0; i < data1.getRowCount(); i++) {
        row1 = data1.getRow(i);
        row2 = data2.getRow(i);
        if (!row1.toString().equals(row2.toString())) {
	  result = "Row #" + (i+1) + " differs:\nRow1: " + row1 + "\nRow2: " + row2;
	  break;
	}
      }
    }

    return result;
  }

  /**
   * Tests whether the filter produces the same data in subsequent filter runs.
   */
  public void testSubsequentFiltering() {
    Dataset	data;
    T 		filter;
    Dataset 	filtered1;
    Dataset 	filtered2;

    data      = getTypicalDataset();
    filter = getTypicalSetup();
    try {
      filter.filter(data);
      filtered1 = filter(filter, data);
    }
    catch (Exception e) {
      fail("Failed to filter (1): " + LoggingHelper.throwableToString(e));
      return;
    }
    try {
      filter.filter(data);
      filtered2 = filter(filter, data);
    }
    catch (Exception e) {
      fail("Failed to filter (1): " + LoggingHelper.throwableToString(e));
      return;
    }

    assertNull("filtered datasets differ", compareDatasets(filtered1, filtered2));
  }

  /**
   * Returns the filenames (without path) of the input data files to use
   * in the regression test.
   *
   * @return		the filenames
   */
  protected abstract String[] getRegressionInputFiles();

  /**
   * Returns the readers for the input data files to use
   * in the regression test.
   *
   * @return		the readers
   */
  protected abstract SpreadSheetReader[] getRegressionInputReaders();

  /**
   * Returns the class attributes names for the input data files to use
   * in the regression test.
   *
   * @return		the attribute names
   */
  protected abstract String[] getRegressionInputClasses();

  /**
   * Returns the setups to use in the regression test.
   *
   * @return		the setups
   */
  protected abstract T[] getRegressionSetups();

  /**
   * Returns the ignored line indices to use in the regression test.
   *
   * @return		the line indices
   */
  protected int[] getRegressionIgnoredLineIndices() {
    return new int[0];
  }

  /**
   * Reads the data using the reader.
   *
   * @param filename	the file to read (no path)
   * @param reader	the reader for loading the data
   * @param cls		the class attribute name
   * @return		the generated content
   */
  protected Dataset load(String filename, SpreadSheetReader reader, String cls) {
    DefaultDataset	result;
    SpreadSheet 	full;
    SpreadSheet		chunk;

    m_TestHelper.copyResourceToTmp(filename);
    full = reader.read(new TmpFile(filename));
    if (reader instanceof ChunkedSpreadSheetReader) {
      while (((ChunkedSpreadSheetReader) reader).hasMoreChunks()) {
	chunk = ((ChunkedSpreadSheetReader) reader).nextChunk();
	for (Row row : chunk.rows())
	  full.addRow().assign(row);
      }
    }
    m_TestHelper.deleteFileFromTmp(filename);

    result = new DefaultDataset(full);
    result.setClassAttributeByName(cls, true);

    return result;
  }

  /**
   * Initializes the filter and filters the data.
   *
   * @param filter	the filter to use
   * @param data	the data
   * @return		the filtered data
   */
  protected Dataset filter(T filter, Dataset data) {
    Dataset	result;

    try {
      result = filter.filter(data);
    }
    catch (Exception e) {
      fail(
	"Failed to filter data!\n"
	  + "Filter: " + OptionUtils.getCommandLine(filter) + "\n"
	  + "Data:\n" + data);
      return null;
    }

    return result;
  }

  /**
   * Saves the filtered data as file.
   *
   * @param filtered	the filtered data
   * @param filename	the file to save the data to (in the temp directory)
   * @return		true if successfully saved
   */
  protected boolean save(Dataset filtered, String filename) {
    return FileUtils.writeToFile(new TmpFile(filename).getAbsolutePath(), filtered.toString(), false);
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
    Dataset		data;
    boolean		ok;
    String		regression;
    int			i;
    String[]		input;
    SpreadSheetReader[]	readers;
    String[]		classes;
    T[]			setups;
    String[]		output;
    Dataset 		filtered;
    TmpFile[]		outputFiles;

    if (m_NoRegressionTest)
      return;

    input     = getRegressionInputFiles();
    readers   = getRegressionInputReaders();
    classes   = getRegressionInputClasses();
    output    = new String[input.length];
    setups    = getRegressionSetups();
    assertEquals("Number of files and readers differ!", input.length, readers.length);
    assertEquals("Number of files and classes differ!", input.length, classes.length);
    assertEquals("Number of files and setups differ!", input.length, setups.length);

    // process data
    for (i = 0; i < input.length; i++) {
      data = load(input[i], readers[i], classes[i]);
      assertNotNull("Failed to load data?", data);

      filtered = filter(setups[i], data);
      assertNotNull("Failed to filter?", filtered);

      output[i] = createOutputFilename(input[i], i);
      ok        = save(filtered, output[i]);
      assertTrue("Failed to save regression data?", ok);
    }

    // test regression
    outputFiles = new TmpFile[output.length];
    for (i = 0; i < output.length; i++)
      outputFiles[i] = new TmpFile(output[i]);
    regression = m_Regression.compare(outputFiles, getRegressionIgnoredLineIndices());
    assertNull("Output differs:\n" + regression, regression);

    // remove output, clean up scheme
    for (i = 0; i < output.length; i++)
      m_TestHelper.deleteFileFromTmp(output[i]);
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
