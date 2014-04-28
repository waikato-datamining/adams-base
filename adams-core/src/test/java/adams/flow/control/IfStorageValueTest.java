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
 * IfStorageValueTest.java
 * Copyright (C) 2011-2014 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.control;

import junit.framework.Test;
import junit.framework.TestSuite;
import adams.core.option.AbstractArgumentOption;
import adams.env.Environment;
import adams.flow.AbstractFlowTest;
import adams.flow.condition.bool.Expression;
import adams.flow.core.AbstractActor;
import adams.parser.BooleanExpressionText;
import adams.test.TmpFile;

/**
 * Test for IfStorageValue actor.
 *
 * @author fracpete
 * @author adams.core.option.FlowJUnitTestProducer (code generator)
 * @version $Revision$
 */
public class IfStorageValueTest
  extends AbstractFlowTest {

  /**
   * Initializes the test.
   *
   * @param name	the name of the test
   */
  public IfStorageValueTest(String name) {
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
    return new TestSuite(IfStorageValueTest.class);
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
      adams.flow.standalone.InitStorageCache tmp6 = new adams.flow.standalone.InitStorageCache();
      argOption = (AbstractArgumentOption) tmp6.getOptionManager().findByProperty("cache");
      tmp6.setCache((java.lang.String) argOption.valueOf("random_numbers"));

      argOption = (AbstractArgumentOption) tmp6.getOptionManager().findByProperty("size");
      tmp6.setSize((Integer) argOption.valueOf("25"));

      tmp1[1] = tmp6;
      adams.flow.source.RandomNumberGenerator tmp9 = new adams.flow.source.RandomNumberGenerator();
      argOption = (AbstractArgumentOption) tmp9.getOptionManager().findByProperty("generator");
      adams.data.random.JavaRandomInt tmp11 = new adams.data.random.JavaRandomInt();
      argOption = (AbstractArgumentOption) tmp11.getOptionManager().findByProperty("maxValue");
      tmp11.setMaxValue((Integer) argOption.valueOf("50"));

      tmp9.setGenerator(tmp11);

      argOption = (AbstractArgumentOption) tmp9.getOptionManager().findByProperty("maxNum");
      tmp9.setMaxNum((Integer) argOption.valueOf("100"));

      tmp1[2] = tmp9;
      adams.flow.transformer.SetVariable tmp14 = new adams.flow.transformer.SetVariable();
      argOption = (AbstractArgumentOption) tmp14.getOptionManager().findByProperty("variableName");
      tmp14.setVariableName((adams.core.VariableName) argOption.valueOf("rand"));

      tmp1[3] = tmp14;
      adams.flow.control.Tee tmp16 = new adams.flow.control.Tee();
      argOption = (AbstractArgumentOption) tmp16.getOptionManager().findByProperty("actors");
      adams.flow.core.AbstractActor[] tmp17 = new adams.flow.core.AbstractActor[1];
      adams.flow.control.IfThenElse tmp18 = new adams.flow.control.IfThenElse();
      argOption = (AbstractArgumentOption) tmp18.getOptionManager().findByProperty("thenActor");
      adams.flow.transformer.SetStorageValue tmp20 = new adams.flow.transformer.SetStorageValue();
      argOption = (AbstractArgumentOption) tmp20.getOptionManager().findByProperty("cache");
      tmp20.setCache((java.lang.String) argOption.valueOf("random_numbers"));

      argOption = (AbstractArgumentOption) tmp20.getOptionManager().findByProperty("storageName");
      argOption.setVariable("@{rand}");

      tmp18.setThenActor(tmp20);

      argOption = (AbstractArgumentOption) tmp18.getOptionManager().findByProperty("elseActor");
      adams.flow.sink.Null tmp23 = new adams.flow.sink.Null();
      argOption = (AbstractArgumentOption) tmp23.getOptionManager().findByProperty("name");
      tmp23.setName((java.lang.String) argOption.valueOf("else"));

      tmp18.setElseActor(tmp23);

      Expression cond = new Expression();
      cond.setExpression(new BooleanExpressionText("X < 25"));
      tmp18.setCondition(cond);

      tmp17[0] = tmp18;
      tmp16.setActors(tmp17);

      tmp1[4] = tmp16;
      adams.flow.control.Trigger tmp26 = new adams.flow.control.Trigger();
      argOption = (AbstractArgumentOption) tmp26.getOptionManager().findByProperty("actors");
      adams.flow.core.AbstractActor[] tmp27 = new adams.flow.core.AbstractActor[1];
      adams.flow.control.IfStorageValue tmp28 = new adams.flow.control.IfStorageValue();
      argOption = (AbstractArgumentOption) tmp28.getOptionManager().findByProperty("cache");
      tmp28.setCache((java.lang.String) argOption.valueOf("random_numbers"));

      argOption = (AbstractArgumentOption) tmp28.getOptionManager().findByProperty("storageName");
      argOption.setVariable("@{rand}");

      argOption = (AbstractArgumentOption) tmp28.getOptionManager().findByProperty("thenActor");
      adams.flow.control.Sequence tmp31 = new adams.flow.control.Sequence();
      argOption = (AbstractArgumentOption) tmp31.getOptionManager().findByProperty("name");
      tmp31.setName((java.lang.String) argOption.valueOf("then"));

      argOption = (AbstractArgumentOption) tmp31.getOptionManager().findByProperty("actors");
      adams.flow.core.AbstractActor[] tmp33 = new adams.flow.core.AbstractActor[1];
      adams.flow.sink.CallableSink tmp34 = new adams.flow.sink.CallableSink();
      argOption = (AbstractArgumentOption) tmp34.getOptionManager().findByProperty("callableName");
      tmp34.setCallableName((adams.flow.core.CallableActorReference) argOption.valueOf("DumpFile"));

      tmp33[0] = tmp34;
      tmp31.setActors(tmp33);

      tmp28.setThenActor(tmp31);

      argOption = (AbstractArgumentOption) tmp28.getOptionManager().findByProperty("elseActor");
      adams.flow.control.Sequence tmp37 = new adams.flow.control.Sequence();
      argOption = (AbstractArgumentOption) tmp37.getOptionManager().findByProperty("name");
      tmp37.setName((java.lang.String) argOption.valueOf("else"));

      argOption = (AbstractArgumentOption) tmp37.getOptionManager().findByProperty("actors");
      adams.flow.core.AbstractActor[] tmp39 = new adams.flow.core.AbstractActor[3];
      adams.flow.source.StringConstants tmp40 = new adams.flow.source.StringConstants();
      argOption = (AbstractArgumentOption) tmp40.getOptionManager().findByProperty("strings");
      adams.core.base.BaseString[] tmp41 = new adams.core.base.BaseString[1];
      tmp41[0] = (adams.core.base.BaseString) argOption.valueOf("not cached: ");
      tmp40.setStrings(tmp41);

      tmp39[0] = tmp40;
      adams.flow.transformer.StringReplace tmp42 = new adams.flow.transformer.StringReplace();
      argOption = (AbstractArgumentOption) tmp42.getOptionManager().findByProperty("find");
      tmp42.setFind((adams.core.base.BaseRegExp) argOption.valueOf("$"));

      argOption = (AbstractArgumentOption) tmp42.getOptionManager().findByProperty("replace");
      argOption.setVariable("@{rand}");

      tmp42.setReplaceContainsVariable(true);

      tmp39[1] = tmp42;
      adams.flow.sink.CallableSink tmp44 = new adams.flow.sink.CallableSink();
      argOption = (AbstractArgumentOption) tmp44.getOptionManager().findByProperty("callableName");
      tmp44.setCallableName((adams.flow.core.CallableActorReference) argOption.valueOf("DumpFile"));

      tmp39[2] = tmp44;
      tmp37.setActors(tmp39);

      tmp28.setElseActor(tmp37);

      tmp27[0] = tmp28;
      tmp26.setActors(tmp27);

      tmp1[5] = tmp26;
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

