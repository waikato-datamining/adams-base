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
 * AbstractTimeseriesFeatureGeneratorTestCase.java
 * Copyright (C) 2011-2014 University of Waikato, Hamilton, New Zealand
 */
package adams.data.timeseries;

import java.io.File;
import java.util.List;

import adams.core.CleanUpHandler;
import adams.core.Destroyable;
import adams.core.Utils;
import adams.core.io.FileUtils;
import adams.data.io.input.AbstractTimeseriesReader;
import adams.data.spreadsheet.Cell;
import adams.data.spreadsheet.Row;
import adams.test.AbstractTestHelper;
import adams.test.AdamsTestCase;
import adams.test.TestHelper;
import adams.test.TmpFile;

/**
 * Ancestor for test cases tailored for timeseries feature generators.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision: 7706 $
 */
public abstract class AbstractTimeseriesFeatureGeneratorTestCase
  extends AdamsTestCase {

  /**
   * Constructs the test case. Called by subclasses.
   *
   * @param name 	the name of the test
   */
  public AbstractTimeseriesFeatureGeneratorTestCase(String name) {
    super(name);
  }

  /**
   * Returns the test helper class to use.
   *
   * @return		the helper class instance
   */
  @Override
  protected AbstractTestHelper newTestHelper() {
    return new TestHelper(this, "adams/data/timeseries/data");
  }

  /**
   * Loads the image to process.
   *
   * @param filename	the filename to load (without path)
   * @return		the image, null in case of an error
   */
  protected Timeseries[] load(String filename, AbstractTimeseriesReader reader) {
    List<Timeseries>	result;
    
    result = null;

    m_TestHelper.copyResourceToTmp(filename);
    reader.setInput(new TmpFile(filename));
    result = reader.read();
    m_TestHelper.deleteFileFromTmp(filename);

    return result.toArray(new Timeseries[result.size()]);
  }

  /**
   * Processes the input data and returns the processed data.
   *
   * @param timeseries	the timeseries to work on
   * @param scheme	the scheme to process the data with
   * @return		the generated data
   */
  protected Object[][] process(Timeseries[] timeseries, AbstractTimeseriesFeatureGenerator scheme) {
    Object[][]	result;
    int		i;
    
    result = new Object[timeseries.length][];
    for (i = 0; i < timeseries.length; i++)
      result[i] = scheme.generate(timeseries[i]);
    
    return result;
  }
  
  /**
   * Converts the object to a string representation.
   * 
   * @param obj		the object to convert
   * @return		the generated string
   */
  protected String toString(Object obj) {
    StringBuilder	result;
    Row			row;
    int			i;
    Cell		cell;
    
    if (obj instanceof Row) {
      row    = (Row) obj;
      result = new StringBuilder();
      for (i = 0; i < row.getCellCount(); i++) {
	if (i > 0)
	  result.append(",");
	cell = row.getCell(i);
	if (cell.isNumeric())
	  result.append(Utils.doubleToString(cell.toDouble(), 6));
	else
	  result.append(cell.getContent());
      }
      return result.toString();
    }
    else {
      return obj.toString();
    }
  }

  /**
   * Saves the data in the tmp directory.
   *
   * @param data	the data to save
   * @param filename	the filename to save to (without path)
   * @return		true if successfully saved
   */
  protected boolean save(Object[][] data, String filename) {
    boolean	result;
    boolean	first;
    
    result = true;
    
    first = true;
    for (Object[] objects: data) {
      for (Object obj: objects) {
	result = FileUtils.writeToFile(
	    m_TestHelper.getTmpDirectory() + File.separator + filename,
	    toString(obj) + "\n",
	    first);
	if (!result)
	  break;
	first = false;
      }
    }
    
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
   * Returns the timeseries filereaders to use in the regression test for 
   * loading the timeseries files.
   *
   * @return		the readers
   */
  protected abstract AbstractTimeseriesReader[] getRegressionInputReaders();

  /**
   * Returns the setups to use in the regression test.
   *
   * @return		the setups
   */
  protected abstract AbstractTimeseriesFeatureGenerator[] getRegressionSetups();

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
    }
    result += ".txt";

    return result;
  }

  /**
   * Compares the processed data against previously saved output data.
   */
  public void testRegression() {
    Timeseries[]		data;
    Object[][]			processed;
    boolean			ok;
    String			regression;
    int				i;
    String[]			input;
    AbstractTimeseriesReader[]	readers;
    AbstractTimeseriesFeatureGenerator[]	setups;
    String[]			output;
    TmpFile[]			outputFiles;

    if (m_NoRegressionTest)
      return;

    input   = getRegressionInputFiles();
    readers = getRegressionInputReaders();
    output  = new String[input.length];
    setups  = getRegressionSetups();
    assertEquals("Number of files and readers differ!", input.length, readers.length);
    assertEquals("Number of files and setups differ!", input.length, setups.length);

    // process data
    for (i = 0; i < input.length; i++) {
      data = load(input[i], readers[i]);
      assertNotNull("Could not load data for regression test from " + input[i], data);

      processed = process(data, setups[i]);
      assertNotNull("Failed to process data?", processed);

      output[i] = createOutputFilename(input[i], i);
      ok        = save(processed, output[i]);
      assertTrue("Failed to save regression data?", ok);
    }

    // test regression
    outputFiles = new TmpFile[output.length];
    for (i = 0; i < output.length; i++)
      outputFiles[i] = new TmpFile(output[i]);
    regression = m_Regression.compare(outputFiles);
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
