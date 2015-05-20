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
 * AbstractActorTemplateTestCase.java
 * Copyright (C) 2010-2013 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.template;

import adams.core.CleanUpHandler;
import adams.core.Destroyable;
import adams.core.io.FileUtils;
import adams.core.option.NestedProducer;
import adams.flow.core.AbstractActor;
import adams.test.AbstractDatabaseTestCase;
import adams.test.AbstractTestHelper;
import adams.test.TestHelper;
import adams.test.TmpFile;

/**
 * Ancestor for flow condition test cases.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractActorTemplateTestCase
  extends AbstractDatabaseTestCase {

  /**
   * Constructs the test case. Called by subclasses.
   *
   * @param name 	the name of the test
   */
  public AbstractActorTemplateTestCase(String name) {
    super(name);
  }

  /**
   * Returns the test helper class to use.
   *
   * @return		the helper class instance
   */
  @Override
  protected AbstractTestHelper newTestHelper() {
    return new TestHelper(this, "adams/flow/template/data");
  }

  /**
   * Returns the setups to use in the regression test.
   *
   * @return		the setups
   */
  protected abstract AbstractActorTemplate[] getRegressionSetups();

  /**
   * Returns the ignored line indices to use in the regression test.
   *
   * @return		the setups
   */
  protected int[] getRegressionIgnoredLineIndices() {
    return new int[0];
  }

  /**
   * Creates an output filename based on the setup.
   *
   * @param cond	the setup to create the filename for
   * @param no		the number of the test
   * @return		the generated output filename (no path)
   */
  protected String createOutputFilename(AbstractActorTemplate cond, int no) {
    return cond.getClass().getName() + "-out" + no + ".txt";
  }

  /**
   * Saves the actor in the tmp directory.
   *
   * @param actor	the actor to save
   * @param filename	the filename to save to (without path)
   * @return		true if successfully saved
   */
  protected boolean save(AbstractActor actor, String filename) {
    String		content;
    NestedProducer	producer;

    if (actor == null) {
      content = "<null>";
    }
    else {
      producer = new NestedProducer();
      producer.setOutputProlog(false);
      producer.produce(actor);
      content = producer.toString();
      producer.cleanUp();
    }

    return FileUtils.writeToFile(new TmpFile(filename).getAbsolutePath(), content, false);
  }

  /**
   * Compares the processed data against previously saved output data.
   */
  public void testRegression() {
    boolean			ok;
    String			regression;
    int				i;
    AbstractActorTemplate[]	setups;
    String[]			output;
    TmpFile[]			outputFiles;
    AbstractActor		processed;

    if (m_NoRegressionTest)
      return;

    setups  = getRegressionSetups();
    output  = new String[setups.length];
    assertEquals("Number of files and setups differ!", output.length, setups.length);

    // process data
    for (i = 0; i < output.length; i++) {
      processed = setups[i].generate();
      output[i] = createOutputFilename(setups[i], i);
      ok        = save(processed, output[i]);
      assertTrue("Failed to save regression data?", ok);
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
