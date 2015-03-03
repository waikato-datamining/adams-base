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
 * IncStorageValueTest.java
 * Copyright (C) 2011 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.transformer;

import adams.env.Environment;
import adams.core.option.AbstractArgumentOption;
import adams.core.option.OptionUtils;
import junit.framework.Test;
import junit.framework.TestSuite;
import adams.flow.core.AbstractActor;
import adams.flow.control.Flow;
import adams.flow.AbstractFlowTest;
import adams.test.TmpFile;

/**
 * Test for IncStorageValue actor.
 *
 * @author fracpete
 * @author adams.core.option.FlowJUnitTestProducer (code generator)
 * @version $Revision$
 */
public class IncStorageValueTest
  extends AbstractFlowTest {

  /**
   * Initializes the test.
   *
   * @param name	the name of the test
   */
  public IncStorageValueTest(String name) {
    super(name);
  }

  /**
   * Called by JUnit before each test method.
   *
   * @throws Exception 	if an error occurs.
   */
  protected void setUp() throws Exception {
    super.setUp();

    m_TestHelper.deleteFileFromTmp("dumpfile.txt");
  }

  /**
   * Called by JUnit after each test method.
   *
   * @throws Exception	if tear-down fails
   */
  protected void tearDown() throws Exception {
    m_TestHelper.deleteFileFromTmp("dumpfile.txt");

    super.tearDown();
  }

  /**
   * Performs a regression test, comparing against previously generated output.
   */
  public void testRegression() {
    performRegressionTest(
        new TmpFile[]{
          new TmpFile("dumpfile.txt")
        });
  }

  /**
   *
   * Returns a test suite.
   *
   * @return		the test suite
   */
  public static Test suite() {
    return new TestSuite(IncStorageValueTest.class);
  }

  /**
   * Used to create an instance of a specific actor.
   *
   * @return a suitably configured <code>AbstractActor</code> value
   */
  public AbstractActor getActor() {
    AbstractArgumentOption    argOption;

    Flow flow = new Flow();

    try {
      argOption = (AbstractArgumentOption) flow.getOptionManager().findByProperty("actors");
      adams.flow.core.AbstractActor[] tmp1 = new adams.flow.core.AbstractActor[3];
      adams.flow.source.ForLoop tmp2 = new adams.flow.source.ForLoop();
      tmp1[0] = tmp2;
      adams.flow.transformer.IncStorageValue tmp3 = new adams.flow.transformer.IncStorageValue();
      argOption = (AbstractArgumentOption) tmp3.getOptionManager().findByProperty("incrementType");
      tmp3.setIncrementType((adams.flow.transformer.IncStorageValue.IncrementType) argOption.valueOf("DOUBLE"));

      argOption = (AbstractArgumentOption) tmp3.getOptionManager().findByProperty("doubleIncrement");
      tmp3.setDoubleIncrement((Double) argOption.valueOf("2.1"));

      tmp1[1] = tmp3;
      adams.flow.control.Trigger tmp6 = new adams.flow.control.Trigger();
      argOption = (AbstractArgumentOption) tmp6.getOptionManager().findByProperty("actors");
      adams.flow.core.AbstractActor[] tmp7 = new adams.flow.core.AbstractActor[2];
      adams.flow.source.StorageValue tmp8 = new adams.flow.source.StorageValue();
      tmp7[0] = tmp8;
      adams.flow.sink.DumpFile tmp9 = new adams.flow.sink.DumpFile();
      argOption = (AbstractArgumentOption) tmp9.getOptionManager().findByProperty("outputFile");
      tmp9.setOutputFile((adams.core.io.PlaceholderFile) argOption.valueOf("${TMP}/dumpfile.txt"));

      tmp9.setAppend(true);

      tmp7[1] = tmp9;
      tmp6.setActors(tmp7);

      tmp1[2] = tmp6;
      flow.setActors(tmp1);

    }
    catch (Exception e) {
      fail("Failed to set up actor: " + e);
    }

    return flow;
  }

  /**
   * Runs the test from commandline.
   *
   * @param args	ignored
   */
  public static void main(String[] args) {
    Environment.setEnvironmentClass(adams.env.Environment.class);
    runTest(suite());
  }
}

