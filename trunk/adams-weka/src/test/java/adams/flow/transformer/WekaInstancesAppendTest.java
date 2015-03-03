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
 * WekaInstancesAppendTest.java
 * Copyright (C) 2012-2014 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.transformer;

import junit.framework.Test;
import junit.framework.TestSuite;
import adams.core.option.AbstractArgumentOption;
import adams.core.option.OptionUtils;
import adams.env.Environment;
import adams.flow.AbstractFlowTest;
import adams.flow.control.Flow;
import adams.flow.core.AbstractActor;
import adams.test.TmpFile;

/**
 * Test for WekaInstancesAppend actor.
 *
 * @author fracpete
 * @author adams.core.option.FlowJUnitTestProducer (code generator)
 * @version $Revision$
 */
public class WekaInstancesAppendTest
  extends AbstractFlowTest {

  /**
   * Initializes the test.
   *
   * @param name	the name of the test
   */
  public WekaInstancesAppendTest(String name) {
    super(name);
  }

  /**
   * Called by JUnit before each test method.
   *
   * @throws Exception 	if an error occurs.
   */
  @Override
  protected void setUp() throws Exception {
    super.setUp();
    
    m_TestHelper.copyResourceToTmp("anneal_train.arff");
    m_TestHelper.copyResourceToTmp("anneal_test.arff");
    m_TestHelper.deleteFileFromTmp("dumpfile.arff");
  }

  /**
   * Called by JUnit after each test method.
   *
   * @throws Exception	if tear-down fails
   */
  @Override
  protected void tearDown() throws Exception {
    m_TestHelper.deleteFileFromTmp("anneal_train.arff");
    m_TestHelper.deleteFileFromTmp("anneal_test.arff");
    m_TestHelper.deleteFileFromTmp("dumpfile.arff");
    
    super.tearDown();
  }

  /**
   * Performs a regression test, comparing against previously generated output.
   */
  public void testRegression() {
    performRegressionTest(
        new TmpFile[]{
          new TmpFile("dumpfile.arff")
        });
  }

  /**
   * 
   * Returns a test suite.
   *
   * @return		the test suite
   */
  public static Test suite() {
    return new TestSuite(WekaInstancesAppendTest.class);
  }

  /**
   * Used to create an instance of a specific actor.
   *
   * @return a suitably configured <code>AbstractActor</code> value
   */
  @Override
  public AbstractActor getActor() {
    AbstractArgumentOption    argOption;
    
    Flow flow = new Flow();
    
    try {
      argOption = (AbstractArgumentOption) flow.getOptionManager().findByProperty("actors");
      adams.flow.core.AbstractActor[] tmp1 = new adams.flow.core.AbstractActor[3];
      // Flow.FileSupplier
      adams.flow.source.FileSupplier tmp2 = new adams.flow.source.FileSupplier();
      tmp2.setOutputArray(true);

      argOption = (AbstractArgumentOption) tmp2.getOptionManager().findByProperty("files");
      adams.core.io.PlaceholderFile[] tmp3 = new adams.core.io.PlaceholderFile[2];
      tmp3[0] = (adams.core.io.PlaceholderFile) argOption.valueOf("${TMP}/anneal_train.arff");
      tmp3[1] = (adams.core.io.PlaceholderFile) argOption.valueOf("${TMP}/anneal_test.arff");
      tmp2.setFiles(tmp3);

      tmp1[0] = tmp2;
      // Flow.WekaInstancesAppend
      adams.flow.transformer.WekaInstancesAppend tmp4 = new adams.flow.transformer.WekaInstancesAppend();
      tmp1[1] = tmp4;
      // Flow.WekaFileWriter
      adams.flow.sink.WekaFileWriter tmp5 = new adams.flow.sink.WekaFileWriter();
      argOption = (AbstractArgumentOption) tmp5.getOptionManager().findByProperty("outputFile");
      tmp5.setOutputFile((adams.core.io.PlaceholderFile) argOption.valueOf("${TMP}/dumpfile.arff"));

      argOption = (AbstractArgumentOption) tmp5.getOptionManager().findByProperty("customSaver");
      weka.core.converters.ArffSaver tmp8 = new weka.core.converters.ArffSaver();
      tmp8.setOptions(OptionUtils.splitOptions("-decimal 6"));
      tmp5.setCustomSaver(tmp8);

      tmp1[2] = tmp5;
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

