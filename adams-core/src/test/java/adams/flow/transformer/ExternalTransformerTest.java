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
 * ExternalTransformerTest.java
 * Copyright (C) 2012 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.transformer;

import junit.framework.Test;
import junit.framework.TestSuite;
import adams.core.option.AbstractArgumentOption;
import adams.env.Environment;
import adams.flow.AbstractFlowTest;
import adams.flow.control.Flow;
import adams.flow.core.Actor;
import adams.test.TmpFile;

/**
 * Test for ExternalTransformer actor.
 *
 * @author fracpete
 * @author adams.core.option.FlowJUnitTestProducer (code generator)
 * @version $Revision$
 */
public class ExternalTransformerTest
  extends AbstractFlowTest {

  /**
   * Initializes the test.
   *
   * @param name	the name of the test
   */
  public ExternalTransformerTest(String name) {
    super(name);
  }

  /**
   * Called by JUnit before each test method.
   *
   * @throws Exception 	if an error occurs.
   */
  protected void setUp() throws Exception {
    super.setUp();
    
    m_TestHelper.copyResourceToTmp("variable1.txt");
    m_TestHelper.copyResourceToTmp("variable2.txt");
    m_TestHelper.deleteFileFromTmp("dumpfile.txt");
  }

  /**
   * Called by JUnit after each test method.
   *
   * @throws Exception	if tear-down fails
   */
  protected void tearDown() throws Exception {
    m_TestHelper.deleteFileFromTmp("variable1.txt");
    m_TestHelper.deleteFileFromTmp("variable2.txt");
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
    return new TestSuite(ExternalTransformerTest.class);
  }

  /**
   * Used to create an instance of a specific actor.
   *
   * @return a suitably configured <code>Actor</code> value
   */
  public Actor getActor() {
    AbstractArgumentOption    argOption;
    
    Flow flow = new Flow();
    
    try {
      argOption = (AbstractArgumentOption) flow.getOptionManager().findByProperty("actors");
      adams.flow.core.Actor[] tmp1 = new adams.flow.core.Actor[3];
      adams.flow.source.ForLoop tmp2 = new adams.flow.source.ForLoop();
      argOption = (AbstractArgumentOption) tmp2.getOptionManager().findByProperty("loopUpper");
      tmp2.setLoopUpper((Integer) argOption.valueOf("3"));

      tmp1[0] = tmp2;
      adams.flow.control.Tee tmp4 = new adams.flow.control.Tee();
      argOption = (AbstractArgumentOption) tmp4.getOptionManager().findByProperty("name");
      tmp4.setName((java.lang.String) argOption.valueOf("assemble filename of external flow"));

      argOption = (AbstractArgumentOption) tmp4.getOptionManager().findByProperty("actors");
      adams.flow.core.Actor[] tmp6 = new adams.flow.core.Actor[3];
      adams.flow.transformer.Convert tmp7 = new adams.flow.transformer.Convert();
      argOption = (AbstractArgumentOption) tmp7.getOptionManager().findByProperty("conversion");
      adams.data.conversion.IntToString tmp9 = new adams.data.conversion.IntToString();
      tmp7.setConversion(tmp9);

      tmp6[0] = tmp7;
      adams.flow.transformer.StringReplace tmp10 = new adams.flow.transformer.StringReplace();
      argOption = (AbstractArgumentOption) tmp10.getOptionManager().findByProperty("find");
      tmp10.setFind((adams.core.base.BaseRegExp) argOption.valueOf(".*"));

      argOption = (AbstractArgumentOption) tmp10.getOptionManager().findByProperty("replace");
      tmp10.setReplace((java.lang.String) argOption.valueOf("${EXAMPLE_FLOWS}/adams-core-external_flow_variable-$0.flow"));

      tmp10.setReplaceContainsPlaceholder(true);

      tmp6[1] = tmp10;
      adams.flow.transformer.SetVariable tmp13 = new adams.flow.transformer.SetVariable();
      argOption = (AbstractArgumentOption) tmp13.getOptionManager().findByProperty("variableName");
      tmp13.setVariableName((adams.core.VariableName) argOption.valueOf("flow_name"));

      tmp6[2] = tmp13;
      tmp4.setActors(tmp6);

      tmp1[1] = tmp4;
      adams.flow.control.Trigger tmp15 = new adams.flow.control.Trigger();
      argOption = (AbstractArgumentOption) tmp15.getOptionManager().findByProperty("name");
      tmp15.setName((java.lang.String) argOption.valueOf("execute external flow"));

      argOption = (AbstractArgumentOption) tmp15.getOptionManager().findByProperty("actors");
      adams.flow.core.Actor[] tmp17 = new adams.flow.core.Actor[3];
      adams.flow.source.ForLoop tmp18 = new adams.flow.source.ForLoop();
      tmp17[0] = tmp18;
      adams.flow.transformer.ExternalTransformer tmp19 = new adams.flow.transformer.ExternalTransformer();
      argOption = (AbstractArgumentOption) tmp19.getOptionManager().findByProperty("actorFile");
      argOption.setVariable("@{flow_name}");

      tmp17[1] = tmp19;
      adams.flow.sink.DumpFile tmp20 = new adams.flow.sink.DumpFile();
      argOption = (AbstractArgumentOption) tmp20.getOptionManager().findByProperty("outputFile");
      tmp20.setOutputFile((adams.core.io.PlaceholderFile) argOption.valueOf("${TMP}/dumpfile.txt"));

      tmp20.setAppend(true);

      tmp17[2] = tmp20;
      tmp15.setActors(tmp17);

      tmp1[2] = tmp15;
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

