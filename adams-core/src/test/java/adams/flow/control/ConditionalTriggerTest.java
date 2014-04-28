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
 * ConditionalTriggerTest.java
 * Copyright (C) 2012-2014 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.control;

import junit.framework.Test;
import junit.framework.TestSuite;
import adams.core.option.AbstractArgumentOption;
import adams.env.Environment;
import adams.flow.AbstractFlowTest;
import adams.flow.core.AbstractActor;
import adams.test.TmpFile;

/**
 * Test for ConditionalTrigger actor.
 *
 * @author fracpete
 * @author adams.core.option.FlowJUnitTestProducer (code generator)
 * @version $Revision$
 */
public class ConditionalTriggerTest
  extends AbstractFlowTest {

  /**
   * Initializes the test.
   *
   * @param name	the name of the test
   */
  public ConditionalTriggerTest(String name) {
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
    return new TestSuite(ConditionalTriggerTest.class);
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
      adams.flow.standalone.CallableActors tmp2 = new adams.flow.standalone.CallableActors();
      argOption = (AbstractArgumentOption) tmp2.getOptionManager().findByProperty("actors");
      adams.flow.core.AbstractActor[] tmp3 = new adams.flow.core.AbstractActor[1];
      adams.flow.sink.DumpFile tmp4 = new adams.flow.sink.DumpFile();
      argOption = (AbstractArgumentOption) tmp4.getOptionManager().findByProperty("outputFile");
      tmp4.setOutputFile((adams.core.io.PlaceholderFile) argOption.valueOf("${TMP}/dumpfile.txt"));

      tmp4.setAppend(true);

      tmp3[0] = tmp4;
      tmp2.setActors(tmp3);

      tmp1[0] = tmp2;
      adams.flow.source.RandomNumberGenerator tmp6 = new adams.flow.source.RandomNumberGenerator();
      argOption = (AbstractArgumentOption) tmp6.getOptionManager().findByProperty("generator");
      adams.data.random.JavaRandomInt tmp8 = new adams.data.random.JavaRandomInt();
      argOption = (AbstractArgumentOption) tmp8.getOptionManager().findByProperty("maxValue");
      tmp8.setMaxValue((Integer) argOption.valueOf("10"));

      tmp6.setGenerator(tmp8);

      argOption = (AbstractArgumentOption) tmp6.getOptionManager().findByProperty("maxNum");
      tmp6.setMaxNum((Integer) argOption.valueOf("20"));

      tmp1[1] = tmp6;
      adams.flow.control.Tee tmp11 = new adams.flow.control.Tee();
      argOption = (AbstractArgumentOption) tmp11.getOptionManager().findByProperty("actors");
      adams.flow.core.AbstractActor[] tmp12 = new adams.flow.core.AbstractActor[1];
      adams.flow.sink.CallableSink tmp13 = new adams.flow.sink.CallableSink();
      argOption = (AbstractArgumentOption) tmp13.getOptionManager().findByProperty("callableName");
      tmp13.setCallableName((adams.flow.core.CallableActorReference) argOption.valueOf("DumpFile"));

      tmp12[0] = tmp13;
      tmp11.setActors(tmp12);

      tmp1[2] = tmp11;
      adams.flow.control.ConditionalTrigger tmp15 = new adams.flow.control.ConditionalTrigger();
      argOption = (AbstractArgumentOption) tmp15.getOptionManager().findByProperty("name");
      tmp15.setName((java.lang.String) argOption.valueOf("ConditionalTrigger1"));

      argOption = (AbstractArgumentOption) tmp15.getOptionManager().findByProperty("actors");
      adams.flow.core.AbstractActor[] tmp17 = new adams.flow.core.AbstractActor[2];
      adams.flow.source.StringConstants tmp18 = new adams.flow.source.StringConstants();
      argOption = (AbstractArgumentOption) tmp18.getOptionManager().findByProperty("strings");
      adams.core.base.BaseString[] tmp19 = new adams.core.base.BaseString[1];
      tmp19[0] = (adams.core.base.BaseString) argOption.valueOf("Less than 5!");
      tmp18.setStrings(tmp19);

      tmp17[0] = tmp18;
      adams.flow.sink.CallableSink tmp20 = new adams.flow.sink.CallableSink();
      argOption = (AbstractArgumentOption) tmp20.getOptionManager().findByProperty("callableName");
      tmp20.setCallableName((adams.flow.core.CallableActorReference) argOption.valueOf("DumpFile"));

      tmp17[1] = tmp20;
      tmp15.setActors(tmp17);

      argOption = (AbstractArgumentOption) tmp15.getOptionManager().findByProperty("condition");
      adams.flow.condition.bool.Expression tmp23 = new adams.flow.condition.bool.Expression();
      argOption = (AbstractArgumentOption) tmp23.getOptionManager().findByProperty("expression");
      tmp23.setExpression((adams.parser.BooleanExpressionText) argOption.valueOf("X < 5"));

      tmp15.setCondition(tmp23);

      tmp1[3] = tmp15;
      adams.flow.control.ConditionalTrigger tmp25 = new adams.flow.control.ConditionalTrigger();
      argOption = (AbstractArgumentOption) tmp25.getOptionManager().findByProperty("name");
      tmp25.setName((java.lang.String) argOption.valueOf("ConditionalTrigger2"));

      argOption = (AbstractArgumentOption) tmp25.getOptionManager().findByProperty("actors");
      adams.flow.core.AbstractActor[] tmp27 = new adams.flow.core.AbstractActor[2];
      adams.flow.source.StringConstants tmp28 = new adams.flow.source.StringConstants();
      argOption = (AbstractArgumentOption) tmp28.getOptionManager().findByProperty("strings");
      adams.core.base.BaseString[] tmp29 = new adams.core.base.BaseString[1];
      tmp29[0] = (adams.core.base.BaseString) argOption.valueOf("Greater than 5!");
      tmp28.setStrings(tmp29);

      tmp27[0] = tmp28;
      adams.flow.sink.CallableSink tmp30 = new adams.flow.sink.CallableSink();
      argOption = (AbstractArgumentOption) tmp30.getOptionManager().findByProperty("callableName");
      tmp30.setCallableName((adams.flow.core.CallableActorReference) argOption.valueOf("DumpFile"));

      tmp27[1] = tmp30;
      tmp25.setActors(tmp27);

      argOption = (AbstractArgumentOption) tmp25.getOptionManager().findByProperty("condition");
      adams.flow.condition.bool.Expression tmp33 = new adams.flow.condition.bool.Expression();
      argOption = (AbstractArgumentOption) tmp33.getOptionManager().findByProperty("expression");
      tmp33.setExpression((adams.parser.BooleanExpressionText) argOption.valueOf("X > 5"));

      tmp25.setCondition(tmp33);

      tmp1[4] = tmp25;
      adams.flow.control.ConditionalTrigger tmp35 = new adams.flow.control.ConditionalTrigger();
      argOption = (AbstractArgumentOption) tmp35.getOptionManager().findByProperty("name");
      tmp35.setName((java.lang.String) argOption.valueOf("ConditionalTrigger3"));

      argOption = (AbstractArgumentOption) tmp35.getOptionManager().findByProperty("actors");
      adams.flow.core.AbstractActor[] tmp37 = new adams.flow.core.AbstractActor[2];
      adams.flow.source.StringConstants tmp38 = new adams.flow.source.StringConstants();
      argOption = (AbstractArgumentOption) tmp38.getOptionManager().findByProperty("strings");
      adams.core.base.BaseString[] tmp39 = new adams.core.base.BaseString[1];
      tmp39[0] = (adams.core.base.BaseString) argOption.valueOf("Exactly 5!");
      tmp38.setStrings(tmp39);

      tmp37[0] = tmp38;
      adams.flow.sink.CallableSink tmp40 = new adams.flow.sink.CallableSink();
      argOption = (AbstractArgumentOption) tmp40.getOptionManager().findByProperty("callableName");
      tmp40.setCallableName((adams.flow.core.CallableActorReference) argOption.valueOf("DumpFile"));

      tmp37[1] = tmp40;
      tmp35.setActors(tmp37);

      argOption = (AbstractArgumentOption) tmp35.getOptionManager().findByProperty("condition");
      adams.flow.condition.bool.Expression tmp43 = new adams.flow.condition.bool.Expression();
      argOption = (AbstractArgumentOption) tmp43.getOptionManager().findByProperty("expression");
      tmp43.setExpression((adams.parser.BooleanExpressionText) argOption.valueOf("X = 5"));

      tmp35.setCondition(tmp43);

      tmp1[5] = tmp35;
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

