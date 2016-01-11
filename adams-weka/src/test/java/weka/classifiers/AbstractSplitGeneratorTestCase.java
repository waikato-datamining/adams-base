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
 * AbstractSplitGeneratorTestCase.java
 * Copyright (C) 2016 University of Waikato, Hamilton, New Zealand
 */
package weka.classifiers;

import adams.core.CleanUpHandler;
import adams.core.Destroyable;
import adams.core.Utils;
import adams.core.io.FileUtils;
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
   * Does nothing, since classes don't have default constructor.
   */
  public void testSerializable() {
  }

  /**
   * Loads the data to process.
   *
   * @param filename	the filename to load (without path)
   * @return		always null
   */
  protected Instances load(String filename) {
    Instances	result;

    m_TestHelper.copyResourceToTmp(filename);
    try {
      result = DataSource.read(new TmpFile(filename).getAbsolutePath());
      result.setClassIndex(result.numAttributes() - 1);
    }
    catch (Exception e) {
      result = null;
    }
    m_TestHelper.deleteFileFromTmp(filename);

    return result;
  }

  /**
   * Saves the data in the tmp directory.
   *
   * @param data	the data to save
   * @param filename	the filename to save to (without path)
   * @return		true if successfully saved
   */
  protected boolean save(WekaTrainTestSetContainer data, String filename) {
    return FileUtils.writeToFile(m_TestHelper.getTmpDirectory() + File.separator + filename, data, false);
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
	  fail("Failed to generate data (" + (i + 1) + "/" + (count + 1) + "): " + Utils.throwableToString(e));
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
    regression = m_Regression.compare(outputFiles.toArray(new TmpFile[outputFiles.size()]));
    assertNull("Output differs:\n" + regression, regression);

    // remove output, clean up scheme
    for (i = 0; i < setups.length; i++) {
      if (setups[i] instanceof Destroyable)
	((Destroyable) setups[i]).destroy();
      else if (setups[i] instanceof CleanUpHandler)
	((CleanUpHandler) setups[i]).cleanUp();
    }
    for (i = 0; i < outputFiles.size(); i++) {
      m_TestHelper.deleteFileFromTmp(outputFiles.get(i).getAbsolutePath());
    }
  }
}
