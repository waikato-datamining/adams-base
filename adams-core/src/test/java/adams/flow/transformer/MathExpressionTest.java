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
 * MathExpressionTest.java
 * Copyright (C) 2010-2014 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.transformer;

import java.io.File;

import junit.framework.Test;
import junit.framework.TestSuite;
import adams.core.base.BaseString;
import adams.core.option.AbstractArgumentOption;
import adams.data.conversion.DoubleToString;
import adams.data.conversion.StringToDouble;
import adams.env.Environment;
import adams.flow.AbstractFlowTest;
import adams.flow.control.Flow;
import adams.flow.core.Actor;
import adams.flow.sink.DumpFile;
import adams.flow.source.StringConstants;
import adams.parser.MathematicalExpressionText;
import adams.test.Regression;
import adams.test.TmpFile;

/**
 * Tests the MathExpression actor.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class MathExpressionTest
  extends AbstractFlowTest {

  /**
   * Initializes the test.
   *
   * @param name	the name of the test
   */
  public MathExpressionTest(String name) {
    super(name);
  }

  /**
   * Called by JUnit before each test method.
   *
   * @throws Exception if an error occurs
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
   * Used to create an instance of a specific actor.
   *
   * @return a suitably configured <code>Flow</code>
   */
  @Override
  public Actor getActor() {
    StringConstants ids = new StringConstants();
    ids.setStrings(new BaseString[]{
	new BaseString("1"),
	new BaseString("2"),
	new BaseString("3"),
	new BaseString("4"),
	new BaseString("5"),
	new BaseString("6"),
	new BaseString("7"),
	new BaseString("8"),
	new BaseString("9"),
	new BaseString("10"),
	new BaseString("1.1"),
	new BaseString("-12"),
	new BaseString("1300"),
	new BaseString("-11224")
    });

    StringToDouble s2d = new StringToDouble();
    Convert conD = new Convert();
    conD.setConversion(s2d);

    MathExpression me = new MathExpression();
    me.setExpression(new MathematicalExpressionText("X^2 + X^3 + 0.1"));

    DoubleToString d2s = new DoubleToString();
    Convert conS = new Convert();
    conS.setConversion(d2s);

    DumpFile df = new DumpFile();
    df.setAppend(true);
    df.setOutputFile(new TmpFile("dumpfile.txt"));

    Flow flow = new Flow();
    flow.setActors(new Actor[]{ids, conD, me, conS, df});

    return flow;
  }

  /**
   * Used to create an instance of a specific actor.
   *
   * @return a suitably configured <code>Actor</code> value
   */
  public Actor getActorCallableActorAsVariable() {
    AbstractArgumentOption    argOption;
    
    Flow flow = new Flow();
    
    try {
      argOption = (AbstractArgumentOption) flow.getOptionManager().findByProperty("actors");
      adams.flow.core.Actor[] tmp1 = new adams.flow.core.Actor[4];
      adams.flow.standalone.CallableActors tmp2 = new adams.flow.standalone.CallableActors();
      argOption = (AbstractArgumentOption) tmp2.getOptionManager().findByProperty("actors");
      adams.flow.core.Actor[] tmp3 = new adams.flow.core.Actor[1];
      adams.flow.source.SequenceSource tmp4 = new adams.flow.source.SequenceSource();
      argOption = (AbstractArgumentOption) tmp4.getOptionManager().findByProperty("name");
      tmp4.setName((java.lang.String) argOption.valueOf("val"));

      argOption = (AbstractArgumentOption) tmp4.getOptionManager().findByProperty("actors");
      adams.flow.core.Actor[] tmp6 = new adams.flow.core.Actor[4];
      adams.flow.source.Variable tmp7 = new adams.flow.source.Variable();
      argOption = (AbstractArgumentOption) tmp7.getOptionManager().findByProperty("variableName");
      tmp7.setVariableName((adams.core.VariableName) argOption.valueOf("val"));

      tmp6[0] = tmp7;
      adams.flow.transformer.Convert tmp9 = new adams.flow.transformer.Convert();
      argOption = (AbstractArgumentOption) tmp9.getOptionManager().findByProperty("conversion");
      adams.data.conversion.StringToDouble tmp11 = new adams.data.conversion.StringToDouble();
      tmp9.setConversion(tmp11);

      tmp6[1] = tmp9;
      adams.flow.transformer.MathExpression tmp12 = new adams.flow.transformer.MathExpression();
      argOption = (AbstractArgumentOption) tmp12.getOptionManager().findByProperty("expression");
      tmp12.setExpression((adams.parser.MathematicalExpressionText) argOption.valueOf("X*1000"));

      tmp6[2] = tmp12;
      adams.flow.transformer.Convert tmp14 = new adams.flow.transformer.Convert();
      argOption = (AbstractArgumentOption) tmp14.getOptionManager().findByProperty("name");
      tmp14.setName((java.lang.String) argOption.valueOf("Convert-1"));

      argOption = (AbstractArgumentOption) tmp14.getOptionManager().findByProperty("conversion");
      adams.data.conversion.DoubleToInt tmp17 = new adams.data.conversion.DoubleToInt();
      tmp14.setConversion(tmp17);

      tmp6[3] = tmp14;
      tmp4.setActors(tmp6);

      tmp3[0] = tmp4;
      tmp2.setActors(tmp3);

      tmp1[0] = tmp2;
      adams.flow.source.ForLoop tmp18 = new adams.flow.source.ForLoop();
      tmp1[1] = tmp18;
      adams.flow.transformer.SetVariable tmp19 = new adams.flow.transformer.SetVariable();
      argOption = (AbstractArgumentOption) tmp19.getOptionManager().findByProperty("variableName");
      tmp19.setVariableName((adams.core.VariableName) argOption.valueOf("val"));

      tmp1[2] = tmp19;
      adams.flow.control.Trigger tmp21 = new adams.flow.control.Trigger();
      argOption = (AbstractArgumentOption) tmp21.getOptionManager().findByProperty("actors");
      adams.flow.core.Actor[] tmp22 = new adams.flow.core.Actor[3];
      adams.flow.source.ForLoop tmp23 = new adams.flow.source.ForLoop();
      argOption = (AbstractArgumentOption) tmp23.getOptionManager().findByProperty("loopLower");
      tmp23.setLoopLower((Integer) argOption.valueOf("10"));

      argOption = (AbstractArgumentOption) tmp23.getOptionManager().findByProperty("loopUpper");
      tmp23.setLoopUpper((Integer) argOption.valueOf("12"));

      tmp22[0] = tmp23;
      adams.flow.transformer.MathExpression tmp26 = new adams.flow.transformer.MathExpression();
      argOption = (AbstractArgumentOption) tmp26.getOptionManager().findByProperty("expression");
      tmp26.setExpression((adams.parser.MathematicalExpressionText) argOption.valueOf("X+@{callable:val}"));

      tmp22[1] = tmp26;
      adams.flow.sink.DumpFile tmp28 = new adams.flow.sink.DumpFile();
      argOption = (AbstractArgumentOption) tmp28.getOptionManager().findByProperty("outputFile");
      tmp28.setOutputFile((adams.core.io.PlaceholderFile) argOption.valueOf("${TMP}/dumpfile.txt"));

      tmp28.setAppend(true);

      tmp22[2] = tmp28;
      tmp21.setActors(tmp22);

      tmp1[3] = tmp21;
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
    Regression regr = new Regression(MathExpression.class);
    regr.setReferenceFile(Regression.createReferenceFile(MathExpression.class, "-callable"));
    performRegressionTest(getActorCallableActorAsVariable(), new File[]{new TmpFile("dumpfile.txt")}, regr, getRegressionIgnoredLineIndices());
  }

  /**
   * Performs a regression test, comparing against previously generated output.
   */
  public void testRegression() {
    performRegressionTest(
	new TmpFile("dumpfile.txt"));
  }

  /**
   * Returns a test suite.
   *
   * @return		the test suite
   */
  public static Test suite() {
    return new TestSuite(MathExpressionTest.class);
  }

  /**
   * Runs the test from commandline.
   *
   * @param args	ignored
   */
  public static void main(String[] args) {
    Environment.setEnvironmentClass(Environment.class);
    runTest(suite());
  }
}
