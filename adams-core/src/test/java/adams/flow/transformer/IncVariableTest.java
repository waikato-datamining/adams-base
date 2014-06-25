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
 * IncVariableTest.java
 * Copyright (C) 2011-2014 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.transformer;

import java.io.File;

import junit.framework.Test;
import junit.framework.TestSuite;
import adams.core.base.BaseText;
import adams.core.option.AbstractArgumentOption;
import adams.env.Environment;
import adams.flow.AbstractFlowTest;
import adams.flow.control.Flow;
import adams.flow.core.AbstractActor;
import adams.test.Regression;
import adams.test.TmpFile;

/**
 * Test for IncVariable actor.
 *
 * @author fracpete
 * @author adams.core.option.FlowJUnitTestProducer (code generator)
 * @version $Revision$
 */
public class IncVariableTest
  extends AbstractFlowTest {

  /**
   * Initializes the test.
   *
   * @param name	the name of the test
   */
  public IncVariableTest(String name) {
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
    return new TestSuite(IncVariableTest.class);
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
      adams.flow.source.ForLoop tmp2 = new adams.flow.source.ForLoop();
      tmp1[0] = tmp2;
      adams.flow.transformer.IncVariable tmp3 = new adams.flow.transformer.IncVariable();
      argOption = (AbstractArgumentOption) tmp3.getOptionManager().findByProperty("variableName");
      tmp3.setVariableName((adams.core.VariableName) argOption.valueOf("inc"));

      argOption = (AbstractArgumentOption) tmp3.getOptionManager().findByProperty("integerIncrement");
      tmp3.setIntegerIncrement((Integer) argOption.valueOf("-2"));

      argOption = (AbstractArgumentOption) tmp3.getOptionManager().findByProperty("doubleIncrement");
      tmp3.setDoubleIncrement((Double) argOption.valueOf("2.3"));

      tmp1[1] = tmp3;
      adams.flow.control.Trigger tmp7 = new adams.flow.control.Trigger();
      argOption = (AbstractArgumentOption) tmp7.getOptionManager().findByProperty("actors");
      adams.flow.core.AbstractActor[] tmp8 = new adams.flow.core.AbstractActor[2];
      adams.flow.source.Variable tmp9 = new adams.flow.source.Variable();
      argOption = (AbstractArgumentOption) tmp9.getOptionManager().findByProperty("variableName");
      tmp9.setVariableName((adams.core.VariableName) argOption.valueOf("inc"));

      tmp8[0] = tmp9;
      adams.flow.sink.DumpFile tmp11 = new adams.flow.sink.DumpFile();
      argOption = (AbstractArgumentOption) tmp11.getOptionManager().findByProperty("outputFile");
      tmp11.setOutputFile((adams.core.io.PlaceholderFile) argOption.valueOf("${TMP}/dumpfile.txt"));

      tmp11.setAppend(true);

      tmp8[1] = tmp11;
      tmp7.setActors(tmp8);

      tmp1[2] = tmp7;
      flow.setActors(tmp1);

    }
    catch (Exception e) {
      fail("Failed to set up actor: " + e);
    }

    return flow;
  }

  /**
   * Used to create an instance of a specific actor.
   *
   * @return a suitably configured <code>AbstractActor</code> value
   */
  public AbstractActor getActorCallableActorAsVariable() {
    AbstractArgumentOption    argOption;
    
    Flow flow = new Flow();
    
    try {
      argOption = (AbstractArgumentOption) flow.getOptionManager().findByProperty("actors");
      adams.flow.core.AbstractActor[] tmp1 = new adams.flow.core.AbstractActor[6];
      adams.flow.standalone.SetVariable tmp2 = new adams.flow.standalone.SetVariable();
      argOption = (AbstractArgumentOption) tmp2.getOptionManager().findByProperty("name");
      tmp2.setName((java.lang.String) argOption.valueOf("SetVariable-1"));

      argOption = (AbstractArgumentOption) tmp2.getOptionManager().findByProperty("variableName");
      tmp2.setVariableName((adams.core.VariableName) argOption.valueOf("val"));

      argOption = (AbstractArgumentOption) tmp2.getOptionManager().findByProperty("variableValue");
      tmp2.setVariableValue((BaseText) argOption.valueOf("0"));

      tmp1[0] = tmp2;
      adams.flow.standalone.CallableActors tmp6 = new adams.flow.standalone.CallableActors();
      argOption = (AbstractArgumentOption) tmp6.getOptionManager().findByProperty("actors");
      adams.flow.core.AbstractActor[] tmp7 = new adams.flow.core.AbstractActor[1];
      adams.flow.source.SequenceSource tmp8 = new adams.flow.source.SequenceSource();
      argOption = (AbstractArgumentOption) tmp8.getOptionManager().findByProperty("name");
      tmp8.setName((java.lang.String) argOption.valueOf("increment"));

      argOption = (AbstractArgumentOption) tmp8.getOptionManager().findByProperty("actors");
      adams.flow.core.AbstractActor[] tmp10 = new adams.flow.core.AbstractActor[4];
      adams.flow.source.Variable tmp11 = new adams.flow.source.Variable();
      argOption = (AbstractArgumentOption) tmp11.getOptionManager().findByProperty("variableName");
      tmp11.setVariableName((adams.core.VariableName) argOption.valueOf("i"));

      tmp10[0] = tmp11;
      adams.flow.transformer.Convert tmp13 = new adams.flow.transformer.Convert();
      argOption = (AbstractArgumentOption) tmp13.getOptionManager().findByProperty("conversion");
      adams.data.conversion.StringToInt tmp15 = new adams.data.conversion.StringToInt();
      tmp13.setConversion(tmp15);

      tmp10[1] = tmp13;
      adams.flow.transformer.MathExpression tmp16 = new adams.flow.transformer.MathExpression();
      argOption = (AbstractArgumentOption) tmp16.getOptionManager().findByProperty("expression");
      tmp16.setExpression((adams.parser.MathematicalExpressionText) argOption.valueOf("X*2"));

      tmp10[2] = tmp16;
      adams.flow.transformer.Convert tmp18 = new adams.flow.transformer.Convert();
      argOption = (AbstractArgumentOption) tmp18.getOptionManager().findByProperty("name");
      tmp18.setName((java.lang.String) argOption.valueOf("Convert-1"));

      argOption = (AbstractArgumentOption) tmp18.getOptionManager().findByProperty("conversion");
      adams.data.conversion.DoubleToInt tmp21 = new adams.data.conversion.DoubleToInt();
      tmp18.setConversion(tmp21);

      tmp10[3] = tmp18;
      tmp8.setActors(tmp10);

      tmp7[0] = tmp8;
      tmp6.setActors(tmp7);

      tmp1[1] = tmp6;
      adams.flow.source.ForLoop tmp22 = new adams.flow.source.ForLoop();
      tmp1[2] = tmp22;
      adams.flow.transformer.SetVariable tmp23 = new adams.flow.transformer.SetVariable();
      argOption = (AbstractArgumentOption) tmp23.getOptionManager().findByProperty("variableName");
      tmp23.setVariableName((adams.core.VariableName) argOption.valueOf("i"));

      tmp1[3] = tmp23;
      adams.flow.transformer.IncVariable tmp25 = new adams.flow.transformer.IncVariable();
      argOption = (AbstractArgumentOption) tmp25.getOptionManager().findByProperty("variableName");
      tmp25.setVariableName((adams.core.VariableName) argOption.valueOf("val"));

      argOption = (AbstractArgumentOption) tmp25.getOptionManager().findByProperty("integerIncrement");
      argOption.setVariable("@{callable:increment}");

      tmp1[4] = tmp25;
      adams.flow.control.Trigger tmp27 = new adams.flow.control.Trigger();
      argOption = (AbstractArgumentOption) tmp27.getOptionManager().findByProperty("actors");
      adams.flow.core.AbstractActor[] tmp28 = new adams.flow.core.AbstractActor[2];
      adams.flow.source.Variable tmp29 = new adams.flow.source.Variable();
      argOption = (AbstractArgumentOption) tmp29.getOptionManager().findByProperty("variableName");
      tmp29.setVariableName((adams.core.VariableName) argOption.valueOf("val"));

      tmp28[0] = tmp29;
      adams.flow.sink.DumpFile tmp31 = new adams.flow.sink.DumpFile();
      argOption = (AbstractArgumentOption) tmp31.getOptionManager().findByProperty("outputFile");
      tmp31.setOutputFile((adams.core.io.PlaceholderFile) argOption.valueOf("${TMP}/dumpfile.txt"));

      tmp31.setAppend(true);

      tmp28[1] = tmp31;
      tmp27.setActors(tmp28);

      tmp1[5] = tmp27;
      flow.setActors(tmp1);

    }
    catch (Exception e) {
      fail("Failed to set up actor: " + e);
    }
    
    return flow;
  }

  /**
   * Test the @{callable:...} variable definition as part of the expression.
   */
  public void testCallableActorAsVariable() {
    Regression regr = new Regression(IncVariable.class);
    regr.setReferenceFile(Regression.createReferenceFile(IncVariable.class, "-callable"));
    performRegressionTest(getActorCallableActorAsVariable(), new File[]{new TmpFile("dumpfile.txt")}, regr, getRegressionIgnoredLineIndices());
  }

  /**
   * Used to create an instance of a specific actor.
   *
   * @return a suitably configured <code>AbstractActor</code> value
   */
  public AbstractActor getActorStorageAsVariable() {
    AbstractArgumentOption    argOption;
    
    Flow flow = new Flow();
    
    try {
      argOption = (AbstractArgumentOption) flow.getOptionManager().findByProperty("actors");
      adams.flow.core.AbstractActor[] tmp1 = new adams.flow.core.AbstractActor[5];
      adams.flow.standalone.SetVariable tmp2 = new adams.flow.standalone.SetVariable();
      argOption = (AbstractArgumentOption) tmp2.getOptionManager().findByProperty("variableName");
      tmp2.setVariableName((adams.core.VariableName) argOption.valueOf("val"));

      argOption = (AbstractArgumentOption) tmp2.getOptionManager().findByProperty("variableValue");
      tmp2.setVariableValue((BaseText) argOption.valueOf("0"));

      tmp1[0] = tmp2;
      adams.flow.source.ForLoop tmp5 = new adams.flow.source.ForLoop();
      tmp1[1] = tmp5;
      adams.flow.control.Tee tmp6 = new adams.flow.control.Tee();
      argOption = (AbstractArgumentOption) tmp6.getOptionManager().findByProperty("actors");
      adams.flow.core.AbstractActor[] tmp7 = new adams.flow.core.AbstractActor[3];
      adams.flow.transformer.MathExpression tmp8 = new adams.flow.transformer.MathExpression();
      argOption = (AbstractArgumentOption) tmp8.getOptionManager().findByProperty("expression");
      tmp8.setExpression((adams.parser.MathematicalExpressionText) argOption.valueOf("X*2"));

      tmp7[0] = tmp8;
      adams.flow.transformer.Convert tmp10 = new adams.flow.transformer.Convert();
      argOption = (AbstractArgumentOption) tmp10.getOptionManager().findByProperty("conversion");
      adams.data.conversion.DoubleToInt tmp12 = new adams.data.conversion.DoubleToInt();
      tmp10.setConversion(tmp12);

      tmp7[1] = tmp10;
      adams.flow.transformer.SetStorageValue tmp13 = new adams.flow.transformer.SetStorageValue();
      argOption = (AbstractArgumentOption) tmp13.getOptionManager().findByProperty("storageName");
      tmp13.setStorageName((adams.flow.control.StorageName) argOption.valueOf("i"));

      tmp7[2] = tmp13;
      tmp6.setActors(tmp7);

      tmp1[2] = tmp6;
      adams.flow.transformer.IncVariable tmp15 = new adams.flow.transformer.IncVariable();
      argOption = (AbstractArgumentOption) tmp15.getOptionManager().findByProperty("variableName");
      tmp15.setVariableName((adams.core.VariableName) argOption.valueOf("val"));

      argOption = (AbstractArgumentOption) tmp15.getOptionManager().findByProperty("integerIncrement");
      argOption.setVariable("@{storage:i}");

      tmp1[3] = tmp15;
      adams.flow.control.Trigger tmp17 = new adams.flow.control.Trigger();
      argOption = (AbstractArgumentOption) tmp17.getOptionManager().findByProperty("actors");
      adams.flow.core.AbstractActor[] tmp18 = new adams.flow.core.AbstractActor[2];
      adams.flow.source.Variable tmp19 = new adams.flow.source.Variable();
      argOption = (AbstractArgumentOption) tmp19.getOptionManager().findByProperty("variableName");
      tmp19.setVariableName((adams.core.VariableName) argOption.valueOf("val"));

      tmp18[0] = tmp19;
      adams.flow.sink.DumpFile tmp21 = new adams.flow.sink.DumpFile();
      argOption = (AbstractArgumentOption) tmp21.getOptionManager().findByProperty("outputFile");
      tmp21.setOutputFile((adams.core.io.PlaceholderFile) argOption.valueOf("${TMP}/dumpfile.txt"));

      tmp21.setAppend(true);

      tmp18[1] = tmp21;
      tmp17.setActors(tmp18);

      tmp1[4] = tmp17;
      flow.setActors(tmp1);

    }
    catch (Exception e) {
      fail("Failed to set up actor: " + e);
    }
    
    return flow;
  }

  /**
   * Test the @{storage:...} variable definition as part of the expression.
   */
  public void testStorageAsVariable() {
    Regression regr = new Regression(IncVariable.class);
    regr.setReferenceFile(Regression.createReferenceFile(IncVariable.class, "-storage"));
    performRegressionTest(getActorStorageAsVariable(), new File[]{new TmpFile("dumpfile.txt")}, regr, getRegressionIgnoredLineIndices());
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

