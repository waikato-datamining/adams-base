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
 * RaiseErrorTest.java
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
 * Test for RaiseError actor.
 *
 * @author fracpete
 * @author adams.core.option.FlowJUnitTestProducer (code generator)
 * @version $Revision$
 */
public class RaiseErrorTest
  extends AbstractFlowTest {

  /**
   * Initializes the test.
   *
   * @param name	the name of the test
   */
  public RaiseErrorTest(String name) {
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
    return new TestSuite(RaiseErrorTest.class);
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
      adams.flow.core.Actor[] actors1 = new adams.flow.core.Actor[3];

      // Flow.StringConstants
      adams.flow.source.StringConstants stringconstants2 = new adams.flow.source.StringConstants();
      argOption = (AbstractArgumentOption) stringconstants2.getOptionManager().findByProperty("strings");
      adams.core.base.BaseString[] strings3 = new adams.core.base.BaseString[3];
      strings3[0] = (adams.core.base.BaseString) argOption.valueOf("a");
      strings3[1] = (adams.core.base.BaseString) argOption.valueOf("b");
      strings3[2] = (adams.core.base.BaseString) argOption.valueOf("c");
      stringconstants2.setStrings(strings3);
      actors1[0] = stringconstants2;

      // Flow.TryCatch
      adams.flow.control.TryCatch trycatch4 = new adams.flow.control.TryCatch();
      argOption = (AbstractArgumentOption) trycatch4.getOptionManager().findByProperty("try");

      // Flow.TryCatch.try
      adams.flow.control.SubProcess subprocess6 = new adams.flow.control.SubProcess();
      argOption = (AbstractArgumentOption) subprocess6.getOptionManager().findByProperty("name");
      subprocess6.setName((java.lang.String) argOption.valueOf("try"));
      argOption = (AbstractArgumentOption) subprocess6.getOptionManager().findByProperty("actors");
      adams.flow.core.Actor[] actors8 = new adams.flow.core.Actor[1];

      // Flow.TryCatch.try.RaiseError
      adams.flow.control.RaiseError raiseerror9 = new adams.flow.control.RaiseError();
      argOption = (AbstractArgumentOption) raiseerror9.getOptionManager().findByProperty("condition");
      adams.flow.condition.bool.Expression expression11 = new adams.flow.condition.bool.Expression();
      argOption = (AbstractArgumentOption) expression11.getOptionManager().findByProperty("expression");
      expression11.setExpression((adams.parser.BooleanExpressionText) argOption.valueOf("\\\"X\\\" = \\\"b\\\""));
      raiseerror9.setCondition(expression11);

      argOption = (AbstractArgumentOption) raiseerror9.getOptionManager().findByProperty("errorMessage");
      raiseerror9.setErrorMessage((java.lang.String) argOption.valueOf("They killed Kenny!"));
      actors8[0] = raiseerror9;
      subprocess6.setActors(actors8);

      trycatch4.setTry(subprocess6);

      argOption = (AbstractArgumentOption) trycatch4.getOptionManager().findByProperty("catch");

      // Flow.TryCatch.catch
      adams.flow.source.SequenceSource sequencesource15 = new adams.flow.source.SequenceSource();
      argOption = (AbstractArgumentOption) sequencesource15.getOptionManager().findByProperty("name");
      sequencesource15.setName((java.lang.String) argOption.valueOf("catch"));
      argOption = (AbstractArgumentOption) sequencesource15.getOptionManager().findByProperty("actors");
      adams.flow.core.Actor[] actors17 = new adams.flow.core.Actor[4];

      // Flow.TryCatch.catch.Variable
      adams.flow.source.Variable variable18 = new adams.flow.source.Variable();
      argOption = (AbstractArgumentOption) variable18.getOptionManager().findByProperty("variableName");
      variable18.setVariableName((adams.core.VariableName) argOption.valueOf("trycatch"));
      actors17[0] = variable18;

      // Flow.TryCatch.catch.StringSplit
      adams.flow.transformer.StringSplit stringsplit20 = new adams.flow.transformer.StringSplit();
      argOption = (AbstractArgumentOption) stringsplit20.getOptionManager().findByProperty("expression");
      stringsplit20.setExpression((java.lang.String) argOption.valueOf("\n"));
      actors17[1] = stringsplit20;

      // Flow.TryCatch.catch.ArraySubset
      adams.flow.transformer.ArraySubset arraysubset22 = new adams.flow.transformer.ArraySubset();
      argOption = (AbstractArgumentOption) arraysubset22.getOptionManager().findByProperty("elements");
      arraysubset22.setElements((adams.core.Range) argOption.valueOf("1,2"));
      actors17[2] = arraysubset22;

      // Flow.TryCatch.catch.StringJoin
      adams.flow.transformer.StringJoin stringjoin24 = new adams.flow.transformer.StringJoin();
      argOption = (AbstractArgumentOption) stringjoin24.getOptionManager().findByProperty("glue");
      stringjoin24.setGlue((java.lang.String) argOption.valueOf(" "));
      actors17[3] = stringjoin24;
      sequencesource15.setActors(actors17);

      trycatch4.setCatch(sequencesource15);

      trycatch4.setStoreError(true);

      actors1[1] = trycatch4;

      // Flow.DumpFile
      adams.flow.sink.DumpFile dumpfile26 = new adams.flow.sink.DumpFile();
      argOption = (AbstractArgumentOption) dumpfile26.getOptionManager().findByProperty("outputFile");
      dumpfile26.setOutputFile((adams.core.io.PlaceholderFile) argOption.valueOf("${TMP}/dumpfile.txt"));
      dumpfile26.setAppend(true);

      actors1[2] = dumpfile26;
      flow.setActors(actors1);

      argOption = (AbstractArgumentOption) flow.getOptionManager().findByProperty("flowExecutionListener");
      adams.flow.execution.NullListener nulllistener29 = new adams.flow.execution.NullListener();
      flow.setFlowExecutionListener(nulllistener29);

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

