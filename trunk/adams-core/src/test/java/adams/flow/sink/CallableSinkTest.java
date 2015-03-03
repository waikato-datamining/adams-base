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
 * CallableSinkTest.java
 * Copyright (C) 2012-2014 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.sink;

import junit.framework.Test;
import junit.framework.TestSuite;
import adams.core.option.AbstractArgumentOption;
import adams.env.Environment;
import adams.flow.AbstractFlowTest;
import adams.flow.control.Flow;
import adams.flow.core.AbstractActor;
import adams.test.TmpFile;

/**
 * Test for CallableSink actor.
 *
 * @author fracpete
 * @author adams.core.option.FlowJUnitTestProducer (code generator)
 * @version $Revision$
 */
public class CallableSinkTest
  extends AbstractFlowTest {

  /**
   * Initializes the test.
   *
   * @param name	the name of the test
   */
  public CallableSinkTest(String name) {
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
    
    m_TestHelper.deleteFileFromTmp("dumpfile.txt");
  }

  /**
   * Called by JUnit after each test method.
   *
   * @throws Exception	if tear-down fails
   */
  @Override
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
    return new TestSuite(CallableSinkTest.class);
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
      adams.flow.core.AbstractActor[] tmp1 = new adams.flow.core.AbstractActor[6];
      adams.flow.standalone.DeleteFile tmp2 = new adams.flow.standalone.DeleteFile();
      argOption = (AbstractArgumentOption) tmp2.getOptionManager().findByProperty("directory");
      tmp2.setDirectory((adams.core.io.PlaceholderDirectory) argOption.valueOf("${TMP}"));

      argOption = (AbstractArgumentOption) tmp2.getOptionManager().findByProperty("regExp");
      tmp2.setRegExp((adams.core.base.BaseRegExp) argOption.valueOf("dumpfile.txt"));

      tmp1[0] = tmp2;
      adams.flow.standalone.CallableActors tmp5 = new adams.flow.standalone.CallableActors();
      argOption = (AbstractArgumentOption) tmp5.getOptionManager().findByProperty("actors");
      adams.flow.core.AbstractActor[] tmp6 = new adams.flow.core.AbstractActor[1];
      adams.flow.sink.DumpFile tmp7 = new adams.flow.sink.DumpFile();
      argOption = (AbstractArgumentOption) tmp7.getOptionManager().findByProperty("outputFile");
      tmp7.setOutputFile((adams.core.io.PlaceholderFile) argOption.valueOf("${TMP}/dumpfile.txt"));

      tmp7.setAppend(true);

      tmp6[0] = tmp7;
      tmp5.setActors(tmp6);

      tmp1[1] = tmp5;
      adams.flow.source.StringConstants tmp9 = new adams.flow.source.StringConstants();
      argOption = (AbstractArgumentOption) tmp9.getOptionManager().findByProperty("strings");
      adams.core.base.BaseString[] tmp10 = new adams.core.base.BaseString[5];
      tmp10[0] = (adams.core.base.BaseString) argOption.valueOf("1");
      tmp10[1] = (adams.core.base.BaseString) argOption.valueOf("2");
      tmp10[2] = (adams.core.base.BaseString) argOption.valueOf("3");
      tmp10[3] = (adams.core.base.BaseString) argOption.valueOf("4");
      tmp10[4] = (adams.core.base.BaseString) argOption.valueOf("5");
      tmp9.setStrings(tmp10);

      tmp1[2] = tmp9;
      adams.flow.transformer.Convert tmp11 = new adams.flow.transformer.Convert();
      argOption = (AbstractArgumentOption) tmp11.getOptionManager().findByProperty("conversion");
      adams.data.conversion.StringToInt tmp13 = new adams.data.conversion.StringToInt();
      tmp11.setConversion(tmp13);

      tmp1[3] = tmp11;
      adams.flow.control.Tee tmp14 = new adams.flow.control.Tee();
      argOption = (AbstractArgumentOption) tmp14.getOptionManager().findByProperty("actors");
      adams.flow.core.AbstractActor[] tmp15 = new adams.flow.core.AbstractActor[2];
      adams.flow.transformer.MathExpression tmp16 = new adams.flow.transformer.MathExpression();
      argOption = (AbstractArgumentOption) tmp16.getOptionManager().findByProperty("expression");
      tmp16.setExpression((adams.parser.MathematicalExpressionText) argOption.valueOf("X/10"));

      tmp15[0] = tmp16;
      adams.flow.sink.CallableSink tmp18 = new adams.flow.sink.CallableSink();
      argOption = (AbstractArgumentOption) tmp18.getOptionManager().findByProperty("callableName");
      tmp18.setCallableName((adams.flow.core.CallableActorReference) argOption.valueOf("DumpFile"));

      tmp15[1] = tmp18;
      tmp14.setActors(tmp15);

      tmp1[4] = tmp14;
      adams.flow.control.Tee tmp20 = new adams.flow.control.Tee();
      argOption = (AbstractArgumentOption) tmp20.getOptionManager().findByProperty("name");
      tmp20.setName((java.lang.String) argOption.valueOf("Tee-1"));

      argOption = (AbstractArgumentOption) tmp20.getOptionManager().findByProperty("actors");
      adams.flow.core.AbstractActor[] tmp22 = new adams.flow.core.AbstractActor[2];
      adams.flow.transformer.MathExpression tmp23 = new adams.flow.transformer.MathExpression();
      argOption = (AbstractArgumentOption) tmp23.getOptionManager().findByProperty("expression");
      tmp23.setExpression((adams.parser.MathematicalExpressionText) argOption.valueOf("X*10"));

      tmp22[0] = tmp23;
      adams.flow.sink.CallableSink tmp25 = new adams.flow.sink.CallableSink();
      argOption = (AbstractArgumentOption) tmp25.getOptionManager().findByProperty("callableName");
      tmp25.setCallableName((adams.flow.core.CallableActorReference) argOption.valueOf("DumpFile"));

      tmp22[1] = tmp25;
      tmp20.setActors(tmp22);

      tmp1[5] = tmp20;
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

