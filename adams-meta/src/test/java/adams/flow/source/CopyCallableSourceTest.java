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
 * CopyCallableSourceTest.java
 * Copyright (C) 2012-2014 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.source;

import junit.framework.Test;
import junit.framework.TestSuite;
import adams.core.option.AbstractArgumentOption;
import adams.env.Environment;
import adams.flow.AbstractFlowTest;
import adams.flow.control.Flow;
import adams.flow.core.AbstractActor;
import adams.test.TmpFile;

/**
 * Test for CopyCallableSource actor.
 *
 * @author fracpete
 * @author adams.core.option.FlowJUnitTestProducer (code generator)
 * @version $Revision$
 */
public class CopyCallableSourceTest
  extends AbstractFlowTest {

  /**
   * Initializes the test.
   *
   * @param name	the name of the test
   */
  public CopyCallableSourceTest(String name) {
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
    return new TestSuite(CopyCallableSourceTest.class);
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
      adams.flow.standalone.DeleteFile tmp2 = new adams.flow.standalone.DeleteFile();
      argOption = (AbstractArgumentOption) tmp2.getOptionManager().findByProperty("directory");
      tmp2.setDirectory((adams.core.io.PlaceholderDirectory) argOption.valueOf("${TMP}"));

      argOption = (AbstractArgumentOption) tmp2.getOptionManager().findByProperty("regExp");
      tmp2.setRegExp((adams.core.base.BaseRegExp) argOption.valueOf("dumpfile.txt"));

      tmp1[0] = tmp2;
      adams.flow.standalone.CallableActors tmp5 = new adams.flow.standalone.CallableActors();
      argOption = (AbstractArgumentOption) tmp5.getOptionManager().findByProperty("actors");
      adams.flow.core.AbstractActor[] tmp6 = new adams.flow.core.AbstractActor[1];
      adams.flow.source.StringConstants tmp7 = new adams.flow.source.StringConstants();
      argOption = (AbstractArgumentOption) tmp7.getOptionManager().findByProperty("strings");
      adams.core.base.BaseString[] tmp8 = new adams.core.base.BaseString[5];
      tmp8[0] = (adams.core.base.BaseString) argOption.valueOf("1");
      tmp8[1] = (adams.core.base.BaseString) argOption.valueOf("2");
      tmp8[2] = (adams.core.base.BaseString) argOption.valueOf("3");
      tmp8[3] = (adams.core.base.BaseString) argOption.valueOf("4");
      tmp8[4] = (adams.core.base.BaseString) argOption.valueOf("5");
      tmp7.setStrings(tmp8);

      tmp6[0] = tmp7;
      tmp5.setActors(tmp6);

      tmp1[1] = tmp5;
      adams.flow.source.Start tmp9 = new adams.flow.source.Start();
      tmp1[2] = tmp9;
      adams.flow.control.Trigger tmp10 = new adams.flow.control.Trigger();
      argOption = (AbstractArgumentOption) tmp10.getOptionManager().findByProperty("actors");
      adams.flow.core.AbstractActor[] tmp11 = new adams.flow.core.AbstractActor[4];
      adams.flow.source.CopyCallableSource tmp12 = new adams.flow.source.CopyCallableSource();
      argOption = (AbstractArgumentOption) tmp12.getOptionManager().findByProperty("callableName");
      tmp12.setCallableName((adams.flow.core.CallableActorReference) argOption.valueOf("StringConstants"));

      tmp11[0] = tmp12;
      adams.flow.transformer.Convert tmp14 = new adams.flow.transformer.Convert();
      argOption = (AbstractArgumentOption) tmp14.getOptionManager().findByProperty("conversion");
      adams.data.conversion.StringToInt tmp16 = new adams.data.conversion.StringToInt();
      tmp14.setConversion(tmp16);

      tmp11[1] = tmp14;
      adams.flow.transformer.MathExpression tmp17 = new adams.flow.transformer.MathExpression();
      argOption = (AbstractArgumentOption) tmp17.getOptionManager().findByProperty("expression");
      tmp17.setExpression((adams.parser.MathematicalExpressionText) argOption.valueOf("X/10"));

      tmp11[2] = tmp17;
      adams.flow.sink.DumpFile tmp19 = new adams.flow.sink.DumpFile();
      argOption = (AbstractArgumentOption) tmp19.getOptionManager().findByProperty("outputFile");
      tmp19.setOutputFile((adams.core.io.PlaceholderFile) argOption.valueOf("${TMP}/dumpfile.txt"));

      tmp19.setAppend(true);

      tmp11[3] = tmp19;
      tmp10.setActors(tmp11);

      tmp1[3] = tmp10;
      adams.flow.control.Trigger tmp21 = new adams.flow.control.Trigger();
      argOption = (AbstractArgumentOption) tmp21.getOptionManager().findByProperty("name");
      tmp21.setName((java.lang.String) argOption.valueOf("Trigger-1"));

      argOption = (AbstractArgumentOption) tmp21.getOptionManager().findByProperty("actors");
      adams.flow.core.AbstractActor[] tmp23 = new adams.flow.core.AbstractActor[4];
      adams.flow.source.CopyCallableSource tmp24 = new adams.flow.source.CopyCallableSource();
      argOption = (AbstractArgumentOption) tmp24.getOptionManager().findByProperty("callableName");
      tmp24.setCallableName((adams.flow.core.CallableActorReference) argOption.valueOf("StringConstants"));

      tmp23[0] = tmp24;
      adams.flow.transformer.Convert tmp26 = new adams.flow.transformer.Convert();
      argOption = (AbstractArgumentOption) tmp26.getOptionManager().findByProperty("conversion");
      adams.data.conversion.StringToInt tmp28 = new adams.data.conversion.StringToInt();
      tmp26.setConversion(tmp28);

      tmp23[1] = tmp26;
      adams.flow.transformer.MathExpression tmp29 = new adams.flow.transformer.MathExpression();
      argOption = (AbstractArgumentOption) tmp29.getOptionManager().findByProperty("expression");
      tmp29.setExpression((adams.parser.MathematicalExpressionText) argOption.valueOf("X*10"));

      tmp23[2] = tmp29;
      adams.flow.sink.DumpFile tmp31 = new adams.flow.sink.DumpFile();
      argOption = (AbstractArgumentOption) tmp31.getOptionManager().findByProperty("outputFile");
      tmp31.setOutputFile((adams.core.io.PlaceholderFile) argOption.valueOf("${TMP}/dumpfile.txt"));

      tmp31.setAppend(true);

      tmp23[3] = tmp31;
      tmp21.setActors(tmp23);

      tmp1[4] = tmp21;
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

