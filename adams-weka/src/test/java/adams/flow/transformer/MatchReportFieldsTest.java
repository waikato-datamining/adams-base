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
 * MatchReportFieldsTest.java
 * Copyright (C) 2012 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.transformer;

import junit.framework.Test;
import junit.framework.TestSuite;
import adams.core.option.AbstractArgumentOption;
import adams.env.Environment;
import adams.flow.AbstractFlowTest;
import adams.flow.control.Flow;
import adams.flow.core.AbstractActor;
import adams.test.TmpFile;

/**
 * Test for MatchReportFields actor.
 *
 * @author fracpete
 * @author adams.core.option.FlowJUnitTestProducer (code generator)
 * @version $Revision$
 */
public class MatchReportFieldsTest
  extends AbstractFlowTest {

  /**
   * Initializes the test.
   *
   * @param name	the name of the test
   */
  public MatchReportFieldsTest(String name) {
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
    
    m_TestHelper.copyResourceToTmp("bolts.arff");
    m_TestHelper.deleteFileFromTmp("dumpfile.txt");
  }

  /**
   * Called by JUnit after each test method.
   *
   * @throws Exception	if tear-down fails
   */
  @Override
  protected void tearDown() throws Exception {
    m_TestHelper.deleteFileFromTmp("bolts.arff");
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
    return new TestSuite(MatchReportFieldsTest.class);
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
      adams.flow.core.AbstractActor[] tmp1 = new adams.flow.core.AbstractActor[5];
      adams.flow.source.FileSupplier tmp2 = new adams.flow.source.FileSupplier();
      argOption = (AbstractArgumentOption) tmp2.getOptionManager().findByProperty("files");
      tmp2.setFiles(new adams.core.io.PlaceholderFile[]{new TmpFile("bolts.arff")});

      tmp1[0] = tmp2;
      adams.flow.transformer.WekaFileReader tmp4 = new adams.flow.transformer.WekaFileReader();
      argOption = (AbstractArgumentOption) tmp4.getOptionManager().findByProperty("customLoader");
      weka.core.converters.ArffLoader tmp6 = new weka.core.converters.ArffLoader();
      tmp4.setCustomLoader(tmp6);

      tmp1[1] = tmp4;
      adams.flow.transformer.WekaClassSelector tmp7 = new adams.flow.transformer.WekaClassSelector();
      tmp1[2] = tmp7;
      adams.flow.transformer.WekaInstanceBuffer tmp8 = new adams.flow.transformer.WekaInstanceBuffer();
      argOption = (AbstractArgumentOption) tmp8.getOptionManager().findByProperty("operation");
      tmp8.setOperation((adams.flow.transformer.WekaInstanceBuffer.Operation) argOption.valueOf("INSTANCES_TO_INSTANCE"));

      tmp1[3] = tmp8;
      adams.flow.control.ConditionalTee tmp10 = new adams.flow.control.ConditionalTee();
      argOption = (AbstractArgumentOption) tmp10.getOptionManager().findByProperty("actors");
      adams.flow.core.AbstractActor[] tmp11 = new adams.flow.core.AbstractActor[3];
      adams.flow.transformer.Convert tmp12 = new adams.flow.transformer.Convert();
      argOption = (AbstractArgumentOption) tmp12.getOptionManager().findByProperty("conversion");
      adams.data.conversion.WekaInstanceToAdamsInstance tmp14 = new adams.data.conversion.WekaInstanceToAdamsInstance();
      tmp12.setConversion(tmp14);

      tmp11[0] = tmp12;
      adams.flow.transformer.MatchReportFields tmp15 = new adams.flow.transformer.MatchReportFields();
      argOption = (AbstractArgumentOption) tmp15.getOptionManager().findByProperty("regExp");
      tmp15.setRegExp((adams.core.base.BaseRegExp) argOption.valueOf("D.*"));

      tmp11[1] = tmp15;
      adams.flow.sink.DumpFile tmp17 = new adams.flow.sink.DumpFile();
      tmp17.setOutputFile(new TmpFile("dumpfile.txt"));
      tmp17.setAppend(true);
      tmp11[2] = tmp17;
      tmp10.setActors(tmp11);

      argOption = (AbstractArgumentOption) tmp10.getOptionManager().findByProperty("condition");
      adams.flow.condition.bool.Counting tmp19 = new adams.flow.condition.bool.Counting();
      argOption = (AbstractArgumentOption) tmp19.getOptionManager().findByProperty("maximum");
      tmp19.setMaximum((Integer) argOption.valueOf("1"));

      tmp10.setCondition(tmp19);

      tmp1[4] = tmp10;
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
