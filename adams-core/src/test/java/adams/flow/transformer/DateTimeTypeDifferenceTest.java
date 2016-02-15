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
 * DateTimeTypeDifferenceTest.java
 * Copyright (C) 2013 University of Waikato, Hamilton, New Zealand
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
 * Test for DateTimeTypeDifference actor.
 *
 * @author fracpete
 * @author adams.core.option.FlowJUnitTestProducer (code generator)
 * @version $Revision$
 */
public class DateTimeTypeDifferenceTest
  extends AbstractFlowTest {

  /**
   * Initializes the test.
   *
   * @param name	the name of the test
   */
  public DateTimeTypeDifferenceTest(String name) {
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
    return new TestSuite(DateTimeTypeDifferenceTest.class);
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
      adams.flow.core.Actor[] actors1 = new adams.flow.core.Actor[6];

      // Flow.StringConstants
      adams.flow.source.StringConstants stringconstants2 = new adams.flow.source.StringConstants();
      stringconstants2.setOutputArray(true);

      argOption = (AbstractArgumentOption) stringconstants2.getOptionManager().findByProperty("strings");
      adams.core.base.BaseString[] strings3 = new adams.core.base.BaseString[2];
      strings3[0] = (adams.core.base.BaseString) argOption.valueOf("2013-05-03");
      strings3[1] = (adams.core.base.BaseString) argOption.valueOf("2011-01-17");
      stringconstants2.setStrings(strings3);
      actors1[0] = stringconstants2;

      // Flow.ArrayProcess
      adams.flow.control.ArrayProcess arrayprocess4 = new adams.flow.control.ArrayProcess();
      argOption = (AbstractArgumentOption) arrayprocess4.getOptionManager().findByProperty("actors");
      adams.flow.core.Actor[] actors5 = new adams.flow.core.Actor[1];

      // Flow.ArrayProcess.Convert
      adams.flow.transformer.Convert convert6 = new adams.flow.transformer.Convert();
      argOption = (AbstractArgumentOption) convert6.getOptionManager().findByProperty("conversion");
      adams.data.conversion.StringToDateTimeType stringtodatetimetype8 = new adams.data.conversion.StringToDateTimeType();
      convert6.setConversion(stringtodatetimetype8);

      actors5[0] = convert6;
      arrayprocess4.setActors(actors5);

      actors1[1] = arrayprocess4;

      // Flow.DateTimeTypeDifference
      adams.flow.transformer.DateTimeTypeDifference datetimetypedifference9 = new adams.flow.transformer.DateTimeTypeDifference();
      argOption = (AbstractArgumentOption) datetimetypedifference9.getOptionManager().findByProperty("outputDateTimeType");
      datetimetypedifference9.setOutputDateTimeType((adams.core.DateTimeType) argOption.valueOf("SECONDS"));
      actors1[2] = datetimetypedifference9;

      // Flow.MathExpression
      adams.flow.transformer.MathExpression mathexpression11 = new adams.flow.transformer.MathExpression();
      argOption = (AbstractArgumentOption) mathexpression11.getOptionManager().findByProperty("expression");
      mathexpression11.setExpression((adams.parser.MathematicalExpressionText) argOption.valueOf("X / 24 / 60 / 60"));
      actors1[3] = mathexpression11;

      // Flow.Convert
      adams.flow.transformer.Convert convert13 = new adams.flow.transformer.Convert();
      argOption = (AbstractArgumentOption) convert13.getOptionManager().findByProperty("conversion");
      adams.data.conversion.DoubleToInt doubletoint15 = new adams.data.conversion.DoubleToInt();
      convert13.setConversion(doubletoint15);

      actors1[4] = convert13;

      // Flow.DumpFile
      adams.flow.sink.DumpFile dumpfile16 = new adams.flow.sink.DumpFile();
      argOption = (AbstractArgumentOption) dumpfile16.getOptionManager().findByProperty("outputFile");
      dumpfile16.setOutputFile((adams.core.io.PlaceholderFile) argOption.valueOf("${TMP}/dumpfile.txt"));
      actors1[5] = dumpfile16;
      flow.setActors(actors1);

      argOption = (AbstractArgumentOption) flow.getOptionManager().findByProperty("flowExecutionListener");
      adams.flow.execution.NullListener nulllistener19 = new adams.flow.execution.NullListener();
      flow.setFlowExecutionListener(nulllistener19);

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

