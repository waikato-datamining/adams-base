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
 * AbstractSplitGeneratorTestCase.java
 * Copyright (C) 2025 University of Waikato, Hamilton, NZ
 */

package adams.ml.evaluation;

import adams.core.Utils;
import adams.core.io.FileUtils;
import adams.core.logging.LoggingHelper;
import adams.core.option.OptionUtils;
import adams.data.io.input.ChunkedSpreadSheetReader;
import adams.data.io.input.SpreadSheetReader;
import adams.data.spreadsheet.Row;
import adams.data.spreadsheet.SpreadSheet;
import adams.data.spreadsheet.SpreadSheetHelper;
import adams.flow.container.TrainTestSetContainer;
import adams.ml.data.Dataset;
import adams.ml.data.DefaultDataset;
import adams.test.AbstractTestHelper;
import adams.test.AdamsTestCase;
import adams.test.TestHelper;
import adams.test.TmpFile;

import java.util.ArrayList;
import java.util.List;

/**
 * Ancestor for split generator tests.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public abstract class AbstractSplitGeneratorTestCase<T extends AbstractSplitGenerator>
  extends AdamsTestCase {

  /**
   * Constructs the test case. Called by subclasses.
   *
   * @param name the name of the test
   */
  public AbstractSplitGeneratorTestCase(String name) {
    super(name);
  }

  /**
   * Returns the test helper class to use.
   *
   * @return the helper class instance
   */
  @Override
  protected AbstractTestHelper newTestHelper() {
    return new TestHelper(this, "adams/ml/evaluation/data");
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
    T generator;

    data      = getTypicalDataset();
    dataCopy  = data.getClone();
    generator = getTypicalSetup();
    try {
      generator.setData(data);
      generator.initializeIterator();
      while (generator.hasNext())
	generator.next();
    }
    catch (Exception e) {
      fail("Failed to generate splits: " + LoggingHelper.throwableToString(e));
      return;
    }

    assertNull("Changed input data", SpreadSheetHelper.compare(dataCopy, data));
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
   * Initializes the generator and returns the containers from the splits.
   *
   * @param generator	the generator to use
   * @param data	the data
   * @return		the generated containers
   */
  protected List<TrainTestSetContainer> generateSplits(T generator, Dataset data) {
    List<TrainTestSetContainer>	result;

    result = new ArrayList<>();
    try {
      generator.setData(data);
      generator.initializeIterator();
      while (generator.hasNext())
	result.add(generator.next());
    }
    catch (Exception e) {
      fail(
	"Failed to generate data!\n"
	  + "Generator: " + OptionUtils.getCommandLine(generator) + "\n"
	  + "Data:\n" + data);
      return null;
    }

    return result;
  }

  /**
   * Turns the object into a string.
   *
   * @param obj		the object to convert, can be null
   * @return		the generated string
   */
  protected String toString(Object obj) {
    if (obj == null)
      return "" + obj;
    if (obj.getClass().isArray())
      return Utils.arrayToString(obj);
    return obj.toString();
  }

  /**
   * Saves the container data as a file.
   *
   * @param conts	the containers to save
   * @param filename	the file to save the data to (in the temp directory)
   * @return		true if successfully saved
   */
  protected boolean save(List<TrainTestSetContainer> conts, String filename) {
    boolean			result;
    String			output;
    int				i;
    TrainTestSetContainer	cont;

    result = true;
    output = new TmpFile(filename).getAbsolutePath();

    for (i = 0; i < conts.size(); i++) {
      cont = conts.get(i);
      result =
	FileUtils.writeToFile(output, "\nTrain " + i + ":\n", (i > 0))
	  && FileUtils.writeToFile(output, toString(cont.getValue(TrainTestSetContainer.VALUE_TRAIN)), true)
	  && FileUtils.writeToFile(output, "Test " + i + ":\n", true)
	  && FileUtils.writeToFile(output, toString(cont.getValue(TrainTestSetContainer.VALUE_TEST)), true)
	  && FileUtils.writeToFile(output, "Train original indices " + i + ":\n", true)
	  && FileUtils.writeToFile(output, toString(cont.getValue(TrainTestSetContainer.VALUE_TRAIN_ORIGINALINDICES)), true)
	  && FileUtils.writeToFile(output, "\nTest original indices " + i + ":\n", true)
	  && FileUtils.writeToFile(output, toString(cont.getValue(TrainTestSetContainer.VALUE_TEST_ORIGINALINDICES)), true);
      if (!result)
	break;
    }

    return result;
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
    Dataset			data;
    boolean			ok;
    String			regression;
    int				i;
    String[]			input;
    SpreadSheetReader[]		readers;
    String[]			classes;
    T[]				setups;
    String[]			output;
    List<TrainTestSetContainer> splits;
    TmpFile[]			outputFiles;

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

      splits = generateSplits(setups[i], data);
      assertNotNull("Failed to generate splits?", splits);

      output[i] = createOutputFilename(input[i], i);
      ok        = save(splits, output[i]);
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
