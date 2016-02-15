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
 * CronTest.java
 * Copyright (C) 2012 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.standalone;

import junit.framework.Test;
import junit.framework.TestSuite;
import adams.core.base.BaseText;
import adams.core.option.AbstractArgumentOption;
import adams.env.Environment;
import adams.flow.AbstractFlowTest;
import adams.flow.control.Flow;
import adams.flow.core.Actor;
import adams.flow.source.Start;
import adams.parser.BooleanExpressionText;
import adams.test.TmpFile;

/**
 * Test for Cron actor.
 *
 * @author fracpete
 * @author adams.core.option.FlowJUnitTestProducer (code generator)
 * @version $Revision$
 */
public class CronTest
  extends AbstractFlowTest {

  /**
   * Initializes the test.
   *
   * @param name	the name of the test
   */
  public CronTest(String name) {
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
    return new TestSuite(CronTest.class);
  }

  /**
   * Used to create an instance of a specific actor.
   *
   * @return a suitably configured <code>Actor</code> value
   */
  @Override
  public Actor getActor() {
    AbstractArgumentOption    argOption;

    Flow flow = new Flow();

    try {
      argOption = (AbstractArgumentOption) flow.getOptionManager().findByProperty("actors");
      adams.flow.core.Actor[] tmp1 = new adams.flow.core.Actor[5];
      adams.flow.standalone.DeleteFile tmp2 = new adams.flow.standalone.DeleteFile();
      argOption = (AbstractArgumentOption) tmp2.getOptionManager().findByProperty("directory");
      tmp2.setDirectory((adams.core.io.PlaceholderDirectory) argOption.valueOf("${TMP}"));

      argOption = (AbstractArgumentOption) tmp2.getOptionManager().findByProperty("regExp");
      tmp2.setRegExp((adams.core.base.BaseRegExp) argOption.valueOf("dumpfile.txt"));

      tmp1[0] = tmp2;
      adams.flow.standalone.SetVariable tmp5 = new adams.flow.standalone.SetVariable();
      argOption = (AbstractArgumentOption) tmp5.getOptionManager().findByProperty("variableName");
      tmp5.setVariableName((adams.core.VariableName) argOption.valueOf("counter"));

      argOption = (AbstractArgumentOption) tmp5.getOptionManager().findByProperty("variableValue");
      tmp5.setVariableValue((BaseText) argOption.valueOf("0"));

      tmp1[1] = tmp5;
      adams.flow.standalone.Events tmp8 = new adams.flow.standalone.Events();
      argOption = (AbstractArgumentOption) tmp8.getOptionManager().findByProperty("actors");
      adams.flow.core.Actor[] tmp9 = new adams.flow.core.Actor[1];
      adams.flow.standalone.Cron tmp10 = new adams.flow.standalone.Cron();
      argOption = (AbstractArgumentOption) tmp10.getOptionManager().findByProperty("cronActors");
      adams.flow.core.Actor[] tmp11 = new adams.flow.core.Actor[4];
      adams.flow.source.Variable tmp12 = new adams.flow.source.Variable();
      argOption = (AbstractArgumentOption) tmp12.getOptionManager().findByProperty("variableName");
      tmp12.setVariableName((adams.core.VariableName) argOption.valueOf("counter"));

      tmp11[0] = tmp12;
      adams.flow.transformer.IncVariable tmp14 = new adams.flow.transformer.IncVariable();
      argOption = (AbstractArgumentOption) tmp14.getOptionManager().findByProperty("variableName");
      tmp14.setVariableName((adams.core.VariableName) argOption.valueOf("counter"));

      tmp11[1] = tmp14;
      adams.flow.control.Tee tmp16 = new adams.flow.control.Tee();
      argOption = (AbstractArgumentOption) tmp16.getOptionManager().findByProperty("actors");
      adams.flow.core.Actor[] tmp17 = new adams.flow.core.Actor[1];
      adams.flow.sink.DumpFile tmp18 = new adams.flow.sink.DumpFile();
      argOption = (AbstractArgumentOption) tmp18.getOptionManager().findByProperty("outputFile");
      tmp18.setOutputFile(new TmpFile("dumpfile.txt"));

      tmp18.setAppend(true);

      tmp17[0] = tmp18;
      tmp16.setActors(tmp17);

      tmp11[2] = tmp16;
      adams.flow.control.IfThenElse tmp20 = new adams.flow.control.IfThenElse();
      argOption = (AbstractArgumentOption) tmp20.getOptionManager().findByProperty("condition");
      adams.flow.condition.bool.Expression tmp22 = new adams.flow.condition.bool.Expression();
      argOption = (AbstractArgumentOption) tmp22.getOptionManager().findByProperty("expression");
      tmp22.setExpression(new BooleanExpressionText("@{counter} = 3"));

      tmp20.setCondition(tmp22);

      argOption = (AbstractArgumentOption) tmp20.getOptionManager().findByProperty("thenActor");
      adams.flow.control.Sequence tmp25 = new adams.flow.control.Sequence();
      argOption = (AbstractArgumentOption) tmp25.getOptionManager().findByProperty("name");
      tmp25.setName((java.lang.String) argOption.valueOf("then"));

      argOption = (AbstractArgumentOption) tmp25.getOptionManager().findByProperty("actors");
      adams.flow.core.Actor[] tmp27 = new adams.flow.core.Actor[1];
      adams.flow.control.Stop tmp28 = new adams.flow.control.Stop();
      tmp27[0] = tmp28;
      tmp25.setActors(tmp27);

      tmp20.setThenActor(tmp25);

      argOption = (AbstractArgumentOption) tmp20.getOptionManager().findByProperty("elseActor");
      adams.flow.control.Sequence tmp30 = new adams.flow.control.Sequence();
      argOption = (AbstractArgumentOption) tmp30.getOptionManager().findByProperty("name");
      tmp30.setName((java.lang.String) argOption.valueOf("else"));

      tmp20.setElseActor(tmp30);

      tmp11[3] = tmp20;
      tmp10.setCronActors(tmp11);

      argOption = (AbstractArgumentOption) tmp10.getOptionManager().findByProperty("schedule");
      tmp10.setSchedule((adams.core.base.CronSchedule) argOption.valueOf("0/3 * * * * ?"));

      tmp9[0] = tmp10;
      tmp8.setActors(tmp9);

      tmp1[2] = tmp8;
      adams.flow.source.Start tmp33 = new adams.flow.source.Start();
      tmp1[3] = tmp33;
      adams.flow.control.WhileLoop tmp34 = new adams.flow.control.WhileLoop();
      argOption = (AbstractArgumentOption) tmp34.getOptionManager().findByProperty("actors");
      adams.flow.core.Actor[] tmp35 = new adams.flow.core.Actor[2];
      adams.flow.control.Sleep tmp36 = new adams.flow.control.Sleep();
      tmp35[0] = new Start();
      tmp35[1] = tmp36;
      tmp34.setActors(tmp35);

      tmp1[4] = tmp34;
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

