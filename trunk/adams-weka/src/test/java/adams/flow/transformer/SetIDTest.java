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
 * SetIDTest.java
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
 * Test for SetID actor.
 *
 * @author fracpete
 * @author adams.core.option.FlowJUnitTestProducer (code generator)
 * @version $Revision$
 */
public class SetIDTest
  extends AbstractFlowTest {

  /**
   * Initializes the test.
   *
   * @param name	the name of the test
   */
  public SetIDTest(String name) {
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
    return new TestSuite(SetIDTest.class);
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
      adams.flow.core.AbstractActor[] tmp1 = new adams.flow.core.AbstractActor[7];
      adams.flow.source.FileSupplier tmp2 = new adams.flow.source.FileSupplier();
      argOption = (AbstractArgumentOption) tmp2.getOptionManager().findByProperty("files");
      tmp2.setFiles(new adams.core.io.PlaceholderFile[]{new TmpFile("bolts.arff")});

      tmp1[0] = tmp2;
      adams.flow.transformer.WekaFileReader tmp4 = new adams.flow.transformer.WekaFileReader();
      argOption = (AbstractArgumentOption) tmp4.getOptionManager().findByProperty("customLoader");
      weka.core.converters.ArffLoader tmp6 = new weka.core.converters.ArffLoader();
      tmp4.setCustomLoader(tmp6);

      argOption = (AbstractArgumentOption) tmp4.getOptionManager().findByProperty("outputType");
      tmp4.setOutputType((adams.flow.transformer.WekaFileReader.OutputType) argOption.valueOf("INCREMENTAL"));

      tmp1[1] = tmp4;
      adams.flow.transformer.Convert tmp8 = new adams.flow.transformer.Convert();
      argOption = (AbstractArgumentOption) tmp8.getOptionManager().findByProperty("conversion");
      adams.data.conversion.WekaInstanceToAdamsInstance tmp10 = new adams.data.conversion.WekaInstanceToAdamsInstance();
      tmp8.setConversion(tmp10);

      tmp1[2] = tmp8;
      adams.flow.control.Count tmp11 = new adams.flow.control.Count();
      argOption = (AbstractArgumentOption) tmp11.getOptionManager().findByProperty("actors");
      adams.flow.core.AbstractActor[] tmp12 = new adams.flow.core.AbstractActor[1];
      adams.flow.transformer.SetVariable tmp13 = new adams.flow.transformer.SetVariable();
      argOption = (AbstractArgumentOption) tmp13.getOptionManager().findByProperty("variableName");
      tmp13.setVariableName((adams.core.VariableName) argOption.valueOf("id"));

      tmp12[0] = tmp13;
      tmp11.setActors(tmp12);

      argOption = (AbstractArgumentOption) tmp11.getOptionManager().findByProperty("condition");
      adams.flow.condition.bool.Counting tmp16 = new adams.flow.condition.bool.Counting();
      tmp11.setCondition(tmp16);

      tmp1[3] = tmp11;
      adams.flow.transformer.SetID tmp17 = new adams.flow.transformer.SetID();
      argOption = (AbstractArgumentOption) tmp17.getOptionManager().findByProperty("ID");
      argOption.setVariable("@{id}");

      tmp1[4] = tmp17;
      adams.flow.transformer.GetID tmp18 = new adams.flow.transformer.GetID();
      tmp1[5] = tmp18;
      adams.flow.sink.DumpFile tmp19 = new adams.flow.sink.DumpFile();
      argOption = (AbstractArgumentOption) tmp19.getOptionManager().findByProperty("outputFile");
      tmp19.setOutputFile((adams.core.io.PlaceholderFile) argOption.valueOf("${TMP}/dumpfile.txt"));

      tmp19.setAppend(true);

      tmp1[6] = tmp19;
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

