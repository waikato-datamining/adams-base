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
 * AbstractTimeseriesSplitterTestCase.java
 * Copyright (C) 2014 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.transformer.timeseriessplit;

import java.util.Arrays;
import java.util.Iterator;

import adams.core.CleanUpHandler;
import adams.core.DateFormat;
import adams.core.DateUtils;
import adams.core.Destroyable;
import adams.core.Utils;
import adams.core.io.FileUtils;
import adams.core.option.OptionUtils;
import adams.data.io.input.AbstractTimeseriesReader;
import adams.data.report.Report;
import adams.data.timeseries.Timeseries;
import adams.data.timeseries.TimeseriesPoint;
import adams.test.AbstractTestHelper;
import adams.test.AdamsTestCase;
import adams.test.TestHelper;
import adams.test.TmpFile;

/**
 * Ancestor for row score test cases.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision: 9765 $
 */
public abstract class AbstractTimeseriesSplitterTestCase
  extends AdamsTestCase {

  /**
   * Constructs the test case. Called by subclasses.
   *
   * @param name 	the name of the test
   */
  public AbstractTimeseriesSplitterTestCase(String name) {
    super(name);
  }

  /**
   * Returns the test helper class to use.
   *
   * @return		the helper class instance
   */
  @Override
  protected AbstractTestHelper newTestHelper() {
    return new TestHelper(this, "adams/flow/transformer/timeseriessplit/data");
  }

  /**
   * Loads the data to process.
   *
   * @param filename		the filename to load (without path)
   * @param reader		the reader to use for reading
   * @return			the data, null if it could not be loaded
   */
  protected Timeseries load(String filename, AbstractTimeseriesReader reader) {
    Timeseries			result;

    m_TestHelper.copyResourceToTmp(filename);
    reader.setInput(new TmpFile(filename));
    result = reader.read().get(0);
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
   * Returns the reader setups to read the input data files to use
   * in the regression test.
   *
   * @return		the readers
   */
  protected abstract AbstractTimeseriesReader[] getRegressionInputReaders();

  /**
   * Returns the setups to use in the regression test.
   *
   * @return		the setups
   */
  protected abstract AbstractTimeseriesSplitter[] getRegressionSetups();

  /**
   * Processes the input data and returns the processed data.
   *
   * @param data	the data to work on
   * @param scheme	the scheme to process the data with
   * @return		the processed data
   */
  protected Timeseries[] process(Timeseries data, AbstractTimeseriesSplitter scheme) {
    return scheme.split(data);
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
   * Saves the data in the tmp directory.
   *
   * @param data	the data to save
   * @param filename	the filename to save to (without path)
   * @return		true if successfully saved
   */
  protected boolean save(Timeseries[] data, String filename) {
    StringBuilder		content;
    int				i;
    Iterator<TimeseriesPoint>	iter;
    TimeseriesPoint		point;
    Report			report;
    DateFormat			dformat;
    String[]			lines;
    int				index;
    
    content = new StringBuilder();
    dformat = DateUtils.getTimestampFormatterMsecs();
    for (i = 0; i < data.length; i++) {
      content.append((i+1) + "/" + data.length + "\n");
      
      // report
      if (data[i].hasReport()) {
	report = data[i].getReport().getClone();
	report.setStringValue("ID", data[i].getID());
	lines    = report.toProperties().toComment().split("\n");
	lines[0] = "";  // remove comment
	Arrays.sort(lines);
	content.append(Utils.flatten(lines, "\n").trim());
	content.append("\n");
      }
      
      // header
      content.append("Timestamp,Value");
      content.append("\n");

      // data points
      iter  = data[i].iterator();
      index = 0;
      while (iter.hasNext()) {
	index++;
  	point = iter.next();
  	content.append("" + index);
  	content.append(",");
  	content.append(Utils.doubleQuote(dformat.format(point.getTimestamp())));
  	content.append(",");
  	content.append(Utils.doubleToString(point.getValue(), 6));
  	content.append("\n");
      }

      content.append("\n");
    }
    
    return FileUtils.writeToFile(new TmpFile(filename).getAbsolutePath(), content.toString(), false);
  }

  /**
   * Compares the processed data against previously saved output data.
   */
  public void testRegression() {
    Timeseries				data;
    Timeseries[]			processed;
    boolean				ok;
    String				regression;
    int					i;
    String[]				input;
    AbstractTimeseriesReader[]		readers;
    AbstractTimeseriesSplitter[]	setups;
    AbstractTimeseriesSplitter		current;
    String[]				output;
    TmpFile[]				outputFiles;

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

      current = (AbstractTimeseriesSplitter) OptionUtils.shallowCopy(setups[i]);
      assertNotNull("Failed to create copy of algorithm: " + OptionUtils.getCommandLine(setups[i]), current);

      processed = process(data, current);
      assertNotNull("Failed to process data?", processed);

      output[i] = createOutputFilename(input[i], i);
      ok        = save(processed, output[i]);
      assertTrue("Failed to save regression data?", ok);

      if (current instanceof Destroyable)
	((Destroyable) current).destroy();
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
    cleanUpAfterRegression();
  }

  /**
   * For further cleaning up after the regression tests.
   * <p/>
   * Default implementation does nothing.
   */
  protected void cleanUpAfterRegression() {
  }
}
