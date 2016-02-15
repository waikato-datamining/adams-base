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
 * ArrayGenerateTest.java
 * Copyright (C) 2014-2016 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.control;

import junit.framework.Test;
import junit.framework.TestSuite;
import adams.core.option.AbstractArgumentOption;
import adams.env.Environment;
import adams.flow.AbstractFlowTest;
import adams.flow.core.Actor;
import adams.test.TmpFile;

/**
 * Test for ArrayGenerate actor.
 *
 * @author fracpete
 * @author adams.core.option.FlowJUnitTestProducer (code generator)
 * @version $Revision$
 */
public class ArrayGenerateTest
  extends AbstractFlowTest {

  /**
   * Initializes the test.
   *
   * @param name	the name of the test
   */
  public ArrayGenerateTest(String name) {
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
    return new TestSuite(ArrayGenerateTest.class);
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
      adams.flow.core.Actor[] actors1 = new adams.flow.core.Actor[5];

      // Flow.RandomNumberGenerator
      adams.flow.source.RandomNumberGenerator randomnumbergenerator2 = new adams.flow.source.RandomNumberGenerator();
      argOption = (AbstractArgumentOption) randomnumbergenerator2.getOptionManager().findByProperty("generator");
      adams.data.random.JavaRandomInt javarandomint4 = new adams.data.random.JavaRandomInt();
      randomnumbergenerator2.setGenerator(javarandomint4);

      argOption = (AbstractArgumentOption) randomnumbergenerator2.getOptionManager().findByProperty("maxNum");
      randomnumbergenerator2.setMaxNum((Integer) argOption.valueOf("5"));
      actors1[0] = randomnumbergenerator2;

      // Flow.ArrayGenerate
      adams.flow.control.ArrayGenerate arraygenerate6 = new adams.flow.control.ArrayGenerate();
      argOption = (AbstractArgumentOption) arraygenerate6.getOptionManager().findByProperty("branches");
      adams.flow.core.Actor[] branches7 = new adams.flow.core.Actor[5];

      // Flow.ArrayGenerate.PassThrough
      adams.flow.transformer.PassThrough passthrough8 = new adams.flow.transformer.PassThrough();
      branches7[0] = passthrough8;

      // Flow.ArrayGenerate.MathExpression
      adams.flow.transformer.MathExpression mathexpression9 = new adams.flow.transformer.MathExpression();
      argOption = (AbstractArgumentOption) mathexpression9.getOptionManager().findByProperty("expression");
      mathexpression9.setExpression((adams.parser.MathematicalExpressionText) argOption.valueOf("X / 1000"));
      branches7[1] = mathexpression9;

      // Flow.ArrayGenerate.MathExpression-1
      adams.flow.transformer.MathExpression mathexpression11 = new adams.flow.transformer.MathExpression();
      argOption = (AbstractArgumentOption) mathexpression11.getOptionManager().findByProperty("name");
      mathexpression11.setName((java.lang.String) argOption.valueOf("MathExpression-1"));
      argOption = (AbstractArgumentOption) mathexpression11.getOptionManager().findByProperty("expression");
      mathexpression11.setExpression((adams.parser.MathematicalExpressionText) argOption.valueOf("X * 1000"));
      branches7[2] = mathexpression11;

      // Flow.ArrayGenerate.MathExpression-2
      adams.flow.transformer.MathExpression mathexpression14 = new adams.flow.transformer.MathExpression();
      argOption = (AbstractArgumentOption) mathexpression14.getOptionManager().findByProperty("name");
      mathexpression14.setName((java.lang.String) argOption.valueOf("MathExpression-2"));
      argOption = (AbstractArgumentOption) mathexpression14.getOptionManager().findByProperty("expression");
      mathexpression14.setExpression((adams.parser.MathematicalExpressionText) argOption.valueOf("X * PI"));
      branches7[3] = mathexpression14;

      // Flow.ArrayGenerate.MathExpression-3
      adams.flow.transformer.MathExpression mathexpression17 = new adams.flow.transformer.MathExpression();
      argOption = (AbstractArgumentOption) mathexpression17.getOptionManager().findByProperty("name");
      mathexpression17.setName((java.lang.String) argOption.valueOf("MathExpression-3"));
      argOption = (AbstractArgumentOption) mathexpression17.getOptionManager().findByProperty("expression");
      mathexpression17.setExpression((adams.parser.MathematicalExpressionText) argOption.valueOf("X / E"));
      branches7[4] = mathexpression17;
      arraygenerate6.setBranches(branches7);

      actors1[1] = arraygenerate6;

      // Flow.ArrayProcess
      adams.flow.control.ArrayProcess arrayprocess20 = new adams.flow.control.ArrayProcess();
      argOption = (AbstractArgumentOption) arrayprocess20.getOptionManager().findByProperty("actors");
      adams.flow.core.Actor[] actors21 = new adams.flow.core.Actor[1];

      // Flow.ArrayProcess.Convert
      adams.flow.transformer.Convert convert22 = new adams.flow.transformer.Convert();
      argOption = (AbstractArgumentOption) convert22.getOptionManager().findByProperty("conversion");
      adams.data.conversion.DoubleToString doubletostring24 = new adams.data.conversion.DoubleToString();
      convert22.setConversion(doubletostring24);

      actors21[0] = convert22;
      arrayprocess20.setActors(actors21);

      actors1[2] = arrayprocess20;

      // Flow.StringJoin
      adams.flow.transformer.StringJoin stringjoin25 = new adams.flow.transformer.StringJoin();
      argOption = (AbstractArgumentOption) stringjoin25.getOptionManager().findByProperty("glue");
      stringjoin25.setGlue((java.lang.String) argOption.valueOf(","));
      actors1[3] = stringjoin25;

      // Flow.DumpFile
      adams.flow.sink.DumpFile dumpfile27 = new adams.flow.sink.DumpFile();
      argOption = (AbstractArgumentOption) dumpfile27.getOptionManager().findByProperty("outputFile");
      dumpfile27.setOutputFile((adams.core.io.PlaceholderFile) argOption.valueOf("${TMP}/dumpfile.txt"));
      dumpfile27.setAppend(true);

      actors1[4] = dumpfile27;
      flow.setActors(actors1);

      argOption = (AbstractArgumentOption) flow.getOptionManager().findByProperty("flowExecutionListener");
      adams.flow.execution.NullListener nulllistener30 = new adams.flow.execution.NullListener();
      flow.setFlowExecutionListener(nulllistener30);

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

