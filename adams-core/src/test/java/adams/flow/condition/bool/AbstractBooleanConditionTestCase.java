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
 * AbstractBooleanConditionTestCase.java
 * Copyright (C) 2013 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.condition.bool;

import adams.core.CleanUpHandler;
import adams.core.Destroyable;
import adams.core.io.FileUtils;
import adams.flow.core.AbstractActor;
import adams.flow.core.Token;
import adams.test.AbstractTestHelper;
import adams.test.AdamsTestCase;
import adams.test.TestHelper;
import adams.test.TmpFile;

/**
 * Ancestor for flow condition test cases.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractBooleanConditionTestCase
  extends AdamsTestCase {

  /**
   * Constructs the test case. Called by subclasses.
   *
   * @param name 	the name of the test
   */
  public AbstractBooleanConditionTestCase(String name) {
    super(name);
  }

  /**
   * Returns the test helper class to use.
   *
   * @return		the helper class instance
   */
  @Override
  protected AbstractTestHelper newTestHelper() {
    return new TestHelper(this, "adams/flow/condition/bool/data");
  }

  /**
   * Returns the owning actors to use in the regression test (one per regression setup).
   *
   * @return		the owners (not all conditions might need owners)
   */
  protected abstract AbstractActor[] getRegressionOwners();

  /**
   * Returns the input data to use in the regression test (one per regression setup).
   *
   * @return		the input data
   */
  protected abstract Object[] getRegressionInputs();

  /**
   * Returns the setups to use in the regression test.
   *
   * @return		the setups
   */
  protected abstract AbstractBooleanCondition[] getRegressionSetups();

  /**
   * Creates an output filename based on the setup.
   *
   * @param cond	the setup to create the filename for
   * @param no		the number of the test
   * @return		the generated output filename (no path)
   */
  protected String createOutputFilename(AbstractBooleanCondition cond, int no) {
    return cond.getClass().getName() + "-out" + no + ".txt";
  }

  /**
   * Saves the data in the tmp directory.
   *
   * @param data	the data to save
   * @param filename	the filename to save to (without path)
   * @return		true if successfully saved
   */
  protected boolean save(boolean data, String filename) {
    return FileUtils.writeToFile(new TmpFile(filename).getAbsolutePath(), data, false);
  }

  /**
   * Compares the processed data against previously saved output data.
   */
  public void testRegression() {
    boolean			ok;
    String			regression;
    int				i;
    AbstractActor[]		owners;
    Object[]			inputs;
    AbstractBooleanCondition[]	setups;
    String[]			outputs;
    TmpFile[]			outputFiles;
    boolean			processed;

    if (m_NoRegressionTest)
      return;

    setups  = getRegressionSetups();
    owners  = getRegressionOwners();
    inputs  = getRegressionInputs();
    outputs = new String[setups.length];
    assertEquals("Number of setups and inputs differ!", setups.length, inputs.length);
    assertEquals("Number of setups and owners differ!", setups.length, owners.length);

    // process data
    for (i = 0; i < outputs.length; i++) {
      setups[i].setUp(owners[i]);
      processed  = setups[i].evaluate(owners[i], new Token(inputs[i]));
      outputs[i] = createOutputFilename(setups[i], i);
      ok         = save(processed, outputs[i]);
      assertTrue("Failed to save regression data?", ok);
    }

    // test regression
    outputFiles = new TmpFile[outputs.length];
    for (i = 0; i < outputs.length; i++)
      outputFiles[i] = new TmpFile(outputs[i]);
    regression = m_Regression.compare(outputFiles);
    assertNull("Output differs:\n" + regression, regression);

    // remove output, clean up scheme
    for (i = 0; i < outputs.length; i++) {
      if (setups[i] instanceof Destroyable)
	((Destroyable) setups[i]).destroy();
      else if (setups[i] instanceof CleanUpHandler)
	((CleanUpHandler) setups[i]).cleanUp();
      m_TestHelper.deleteFileFromTmp(outputs[i]);
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
