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
 * AbstractTestConditionTestCase.java
 * Copyright (C) 2010-2013 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.condition.test;

import adams.core.CleanUpHandler;
import adams.core.Destroyable;
import adams.core.io.FileUtils;
import adams.test.AbstractTestHelper;
import adams.test.AdamsTestCase;
import adams.test.TestHelper;
import adams.test.TmpFile;

/**
 * Ancestor for test condition test cases.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractTestConditionTestCase
  extends AdamsTestCase {

  /**
   * Constructs the test case. Called by subclasses.
   *
   * @param name 	the name of the test
   */
  public AbstractTestConditionTestCase(String name) {
    super(name);
  }

  /**
   * Returns the test helper class to use.
   *
   * @return		the helper class instance
   */
  @Override
  protected AbstractTestHelper newTestHelper() {
    return new TestHelper(this, "adams/flow/condition/test/data");
  }

  /**
   * Returns the setups to use in the regression test.
   *
   * @return		the setups
   */
  protected abstract AbstractTestCondition[] getRegressionSetups();

  /**
   * Creates an output filename based on the setup.
   *
   * @param cond	the setup to create the filename for
   * @param no		the number of the test
   * @return		the generated output filename (no path)
   */
  protected String createOutputFilename(AbstractTestCondition cond, int no) {
    return cond.getClass().getName() + "-out" + no + ".txt";
  }

  /**
   * Saves the data in the tmp directory.
   *
   * @param data	the data to save
   * @param filename	the filename to save to (without path)
   * @return		true if successfully saved
   */
  protected boolean save(String data, String filename) {
    return FileUtils.writeToFile(new TmpFile(filename).getAbsolutePath(), data, false);
  }

  /**
   * Compares the processed data against previously saved output data.
   */
  public void testRegression() {
    boolean		ok;
    String		regression;
    int			i;
    AbstractTestCondition[]	setups;
    String[]		output;
    TmpFile[]		outputFiles;
    String		processed;

    if (m_NoRegressionTest)
      return;

    setups  = getRegressionSetups();
    output  = new String[setups.length];
    assertEquals("Number of files and setups differ!", output.length, setups.length);

    // process data
    for (i = 0; i < output.length; i++) {
      processed = setups[i].performTest();
      output[i] = createOutputFilename(setups[i], i);
      ok        = save(((processed == null) ? "passed" : processed), output[i]);
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
