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
 * Copyright (C) 2016-2025 University of Waikato, Hamilton, New Zealand
 */
package weka.classifiers;

import adams.core.CleanUpHandler;
import adams.core.Destroyable;
import adams.core.Utils;
import adams.core.io.FileUtils;
import adams.core.logging.LoggingHelper;
import adams.flow.container.TrainTestSetContainer;
import adams.flow.container.WekaTrainTestSetContainer;
import adams.test.AbstractTestHelper;
import adams.test.AdamsTestCase;
import adams.test.TestHelper;
import adams.test.TmpFile;
import weka.core.Instances;
import weka.core.converters.ConverterUtils.DataSource;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Ancestor for test cases tailored for split generators.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision: 10087 $
 */
public abstract class AbstractSplitGeneratorTestCase
  extends AdamsTestCase {

  /**
   * Constructs the test case. Called by subclasses.
   *
   * @param name 	the name of the test
   */
  public AbstractSplitGeneratorTestCase(String name) {
    super(name);
  }

  /**
   * Returns the test helper class to use.
   *
   * @return		the helper class instance
   */
  @Override
  protected AbstractTestHelper newTestHelper() {
    return new TestHelper(this, "weka/classifiers/data");
  }

  /**
   * Loads the data to process.
   *
   * @param filename	the filename to load (without path)
   * @return		always null
   */
  protected Instances load(String filename) {
    return load(filename, -1);
  }

  /**
   * Loads the data to process.
   *
   * @param filename	the filename to load (without path)
   * @param maxRows 	the maximum number of rows to read, -1 for all
   * @return		always null
   */
  protected Instances load(String filename, int maxRows) {
    Instances	result;

    m_TestHelper.copyResourceToTmp(filename);
    try {
      result = DataSource.read(new TmpFile(filename).getAbsolutePath());
      result.setClassIndex(result.numAttributes() - 1);
      if (maxRows != -1)
	result = new Instances(result, 0, maxRows);
    }
    catch (Exception e) {
      result = null;
    }
    m_TestHelper.deleteFileFromTmp(filename);

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
   * Saves the data in the tmp directory.
   *
   * @param data	the data to save
   * @param filename	the filename to save to (without path)
   * @return		true if successfully saved
   */
  protected boolean save(WekaTrainTestSetContainer data, String filename) {
    String			output;

    output = m_TestHelper.getTmpDirectory() + File.separator + filename;

    return
      FileUtils.writeToFile(output, "\nTrain:\n", false)
	&& FileUtils.writeToFile(output, toString(data.getValue(TrainTestSetContainer.VALUE_TRAIN)), true)
	&& FileUtils.writeToFile(output, "\nTest:\n", true)
	&& FileUtils.writeToFile(output, toString(data.getValue(TrainTestSetContainer.VALUE_TEST)), true)
	&& FileUtils.writeToFile(output, "\nTrain original indices:\n", true)
	&& FileUtils.writeToFile(output, toString(data.getValue(TrainTestSetContainer.VALUE_TRAIN_ORIGINALINDICES)), true)
	&& FileUtils.writeToFile(output, "\nTest original indices:\n", true)
	&& FileUtils.writeToFile(output, toString(data.getValue(TrainTestSetContainer.VALUE_TEST_ORIGINALINDICES)), true);
  }

  /**
   * Returns the setups to use in the regression test.
   *
   * @return		the setups
   */
  protected abstract AbstractSplitGenerator[] getRegressionSetups();

  /**
   * Creates an output filename based on the input filename.
   *
   * @param no		the number of the test
   * @param sub		the sub-test
   * @return		the generated output filename (no path)
   */
  protected String createOutputFilename(int no, int sub) {
    return "out-" + no + "-" + sub + ".txt";
  }

  /**
   * Compares the processed data against previously saved output data.
   */
  public void testRegression() {
    boolean			ok;
    String			regression;
    int				i;
    AbstractSplitGenerator[]	setups;
    String	 		output;
    List<TmpFile>		outputFiles;
    WekaTrainTestSetContainer	cont;
    int				count;

    if (m_NoRegressionTest)
      return;

    setups      = getRegressionSetups();
    outputFiles = new ArrayList<>();

    // process data
    for (i = 0; i < setups.length; i++) {
      count = 0;
      while (setups[i].hasNext()) {
	try {
	  cont = setups[i].next();
	  assertNotNull("Failed to generate data (" + (i + 1) + "/" + (count + 1) + ")?", cont);
	}
	catch (Exception e) {
	  fail("Failed to generate data (" + (i + 1) + "/" + (count + 1) + "): " + LoggingHelper.throwableToString(e));
	  continue;
	}

	output = createOutputFilename(i, count);
	outputFiles.add(new TmpFile(output));
	ok     = save(cont, output);
	assertTrue("Failed to save regression data?", ok);

	count++;
      }
    }

    // test regression
    regression = m_Regression.compare(outputFiles.toArray(new TmpFile[0]));
    assertNull("Output differs:\n" + regression, regression);

    // remove output, clean up scheme
    for (i = 0; i < setups.length; i++) {
      if (setups[i] != null)
	((Destroyable) setups[i]).destroy();
      else if (setups[i] instanceof CleanUpHandler)
	((CleanUpHandler) setups[i]).cleanUp();
    }
    for (i = 0; i < outputFiles.size(); i++)
      outputFiles.get(i).delete();
  }
}
