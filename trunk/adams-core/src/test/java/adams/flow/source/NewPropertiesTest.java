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
 * NewPropertiesTest.java
 * Copyright (C) 2013 University of Waikato, Hamilton, New Zealand
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
 * Test for NewProperties actor.
 *
 * @author fracpete
 * @author adams.core.option.FlowJUnitTestProducer (code generator)
 * @version $Revision$
 */
public class NewPropertiesTest
  extends AbstractFlowTest {

  /**
   * Initializes the test.
   *
   * @param name	the name of the test
   */
  public NewPropertiesTest(String name) {
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
    return new TestSuite(NewPropertiesTest.class);
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
      adams.flow.core.AbstractActor[] actors1 = new adams.flow.core.AbstractActor[6];

      // Flow.NewProperties
      adams.flow.source.NewProperties newproperties2 = new adams.flow.source.NewProperties();
      actors1[0] = newproperties2;

      // Flow.Convert
      adams.flow.transformer.Convert convert3 = new adams.flow.transformer.Convert();
      argOption = (AbstractArgumentOption) convert3.getOptionManager().findByProperty("conversion");
      adams.data.conversion.AnyToString anytostring5 = new adams.data.conversion.AnyToString();
      convert3.setConversion(anytostring5);

      actors1[1] = convert3;

      // Flow.StringSplit
      adams.flow.transformer.StringSplit stringsplit6 = new adams.flow.transformer.StringSplit();
      argOption = (AbstractArgumentOption) stringsplit6.getOptionManager().findByProperty("expression");
      stringsplit6.setExpression((java.lang.String) argOption.valueOf("\n"));
      actors1[2] = stringsplit6;

      // Flow.StringMatcher
      adams.flow.transformer.StringMatcher stringmatcher8 = new adams.flow.transformer.StringMatcher();
      argOption = (AbstractArgumentOption) stringmatcher8.getOptionManager().findByProperty("regExp");
      stringmatcher8.setRegExp((adams.core.base.BaseRegExp) argOption.valueOf("#.*"));
      stringmatcher8.setInvert(true);

      actors1[3] = stringmatcher8;

      // Flow.StringJoin
      adams.flow.transformer.StringJoin stringjoin10 = new adams.flow.transformer.StringJoin();
      argOption = (AbstractArgumentOption) stringjoin10.getOptionManager().findByProperty("glue");
      stringjoin10.setGlue((java.lang.String) argOption.valueOf("\n"));
      actors1[4] = stringjoin10;

      // Flow.DumpFile
      adams.flow.sink.DumpFile dumpfile12 = new adams.flow.sink.DumpFile();
      argOption = (AbstractArgumentOption) dumpfile12.getOptionManager().findByProperty("outputFile");
      dumpfile12.setOutputFile((adams.core.io.PlaceholderFile) argOption.valueOf("${TMP}/dumpfile.txt"));
      actors1[5] = dumpfile12;
      flow.setActors(actors1);

      argOption = (AbstractArgumentOption) flow.getOptionManager().findByProperty("flowExecutionListener");
      adams.flow.execution.NullListener nulllistener15 = new adams.flow.execution.NullListener();
      flow.setFlowExecutionListener(nulllistener15);

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

