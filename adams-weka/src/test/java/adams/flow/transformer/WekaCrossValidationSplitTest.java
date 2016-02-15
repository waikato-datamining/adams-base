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
 * WekaCrossValidationSplitTest.java
 * Copyright (C) 2010 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.transformer;

import junit.framework.Test;
import junit.framework.TestSuite;
import weka.core.Instances;
import weka.core.converters.ConverterUtils.DataSource;
import adams.env.Environment;
import adams.flow.AbstractFlowTest;
import adams.flow.container.WekaTrainTestSetContainer;
import adams.flow.control.Flow;
import adams.flow.core.Actor;
import adams.flow.core.Token;
import adams.flow.sink.DumpFile;
import adams.flow.source.FileSupplier;
import adams.test.TmpFile;

/**
 * Tests the WekaCrossValidationSplit actor.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class WekaCrossValidationSplitTest
  extends AbstractFlowTest {

  /**
   * Initializes the test.
   *
   * @param name	the name of the test
   */
  public WekaCrossValidationSplitTest(String name) {
    super(name);
  }

  /**
   * Called by JUnit before each test method.
   *
   * @throws Exception if an error occurs
   */
  @Override
  protected void setUp() throws Exception {
    super.setUp();

    m_TestHelper.copyResourceToTmp("vote.arff");
    m_TestHelper.deleteFileFromTmp("dumpfile.txt");
  }

  /**
   * Called by JUnit after each test method.
   *
   * @throws Exception	if tear-down fails
   */
  @Override
  protected void tearDown() throws Exception {
    m_TestHelper.deleteFileFromTmp("vote.arff");
    m_TestHelper.deleteFileFromTmp("dumpfile.txt");

    super.tearDown();
  }

  /**
   * Used to create an instance of a specific actor.
   *
   * @return a suitably configured <code>Actor</code> value
   */
  @Override
  public Actor getActor() {
    FileSupplier sfs = new FileSupplier();
    sfs.setFiles(new adams.core.io.PlaceholderFile[]{new TmpFile("vote.arff")});

    WekaFileReader fr = new WekaFileReader();

    WekaClassSelector cs = new WekaClassSelector();

    WekaCrossValidationSplit cvs = new WekaCrossValidationSplit();
    cvs.setRelationName("@-$T-$N");
    cvs.setFolds(3);
    cvs.setSeed(2);

    DumpFile df = new DumpFile();
    df.setAppend(true);
    df.setOutputFile(new TmpFile("dumpfile.txt"));

    Flow flow = new Flow();
    flow.setActors(new Actor[]{sfs, fr, cs, cvs, df});

    return flow;
  }

  /**
   * Tests leave-one-out cross-validation.
   */
  public void testLOOCV() {
    Instances 			data;
    WekaCrossValidationSplit 	actor;
    String			result;
    int				count;
    Token			output;
    WekaTrainTestSetContainer	cont;

    try {
      data = DataSource.read(new TmpFile("vote.arff").getAbsolutePath());
      data.setClassIndex(data.numAttributes() - 1);
    }
    catch (Exception e) {
      data = null;
      e.printStackTrace();
      fail("Failed to load test data 'vote.arff'!");
    }
    assertNotNull("No data loaded?", data);

    actor = new WekaCrossValidationSplit();
    actor.setFolds(-1);

    result = actor.setUp();
    assertNull("setUp() of actor failed: " + result, result);

    actor.input(new Token(data));

    result = actor.execute();
    assertNull("execute() of actor failed: " + result, result);

    count = 0;
    while (actor.hasPendingOutput()) {
      count++;
      output = actor.output();
      assertNotNull("No token produced? count=" + count, output);

      cont = (WekaTrainTestSetContainer) output.getPayload();
      assertNotNull("No payload? count=" + count, cont);
      assertEquals("Test must have only 1 instance!", 1, ((Instances) cont.getValue("Test")).numInstances());
      assertEquals("Mismatch of overall instance count", data.numInstances(), ((Instances) cont.getValue("Train")).numInstances() + ((Instances) cont.getValue("Test")).numInstances());
    }

    assertEquals("Number of data pairs differs", data.numInstances(), count);
  }

  /**
   * Tests the actor if no class attribute is set.
   */
  public void testNoClassAttributeSet() {
    Instances 			data;
    WekaCrossValidationSplit 	actor;
    String			result;

    try {
      data = DataSource.read(new TmpFile("vote.arff").getAbsolutePath());
    }
    catch (Exception e) {
      data = null;
      e.printStackTrace();
      fail("Failed to load test data 'vote.arff'!");
    }
    assertNotNull("No data loaded?", data);

    actor = new WekaCrossValidationSplit();

    result = actor.setUp();
    assertNull("setUp() of actor failed: " + result, result);

    actor.input(new Token(data));

    result = actor.execute();
    assertNotNull("execute() of actor must fail due to class attribute not set", result);
  }

  /**
   * Performs a regression test, comparing against previously generated output.
   */
  public void testRegression() {
    performRegressionTest(
	new TmpFile("dumpfile.txt"));
  }

  /**
   * Returns a test suite.
   *
   * @return		the test suite
   */
  public static Test suite() {
    return new TestSuite(WekaCrossValidationSplitTest.class);
  }

  /**
   * Runs the test from commandline.
   *
   * @param args	ignored
   */
  public static void main(String[] args) {
    Environment.setEnvironmentClass(Environment.class);
    runTest(suite());
  }
}
