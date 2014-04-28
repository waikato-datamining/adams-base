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
 * SetPropertyValueTest.java
 * Copyright (C) 2013 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.transformer;

import junit.framework.Test;
import junit.framework.TestSuite;
import adams.core.option.AbstractArgumentOption;
import adams.env.Environment;
import adams.flow.AbstractFlowTest;
import adams.flow.control.Flow;
import adams.flow.core.AbstractActor;
import adams.test.TmpFile;

/**
 * Test for SetPropertyValue actor.
 *
 * @author fracpete
 * @author adams.core.option.FlowJUnitTestProducer (code generator)
 * @version $Revision$
 */
public class SetPropertyValueTest
  extends AbstractFlowTest {

  /**
   * Initializes the test.
   *
   * @param name	the name of the test
   */
  public SetPropertyValueTest(String name) {
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
    return new TestSuite(SetPropertyValueTest.class);
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
      adams.flow.core.AbstractActor[] actors1 = new adams.flow.core.AbstractActor[7];

      // Flow.NewProperties
      adams.flow.source.NewProperties newproperties2 = new adams.flow.source.NewProperties();
      actors1[0] = newproperties2;

      // Flow.SetPropertyValue
      adams.flow.transformer.SetPropertyValue setpropertyvalue3 = new adams.flow.transformer.SetPropertyValue();
      argOption = (AbstractArgumentOption) setpropertyvalue3.getOptionManager().findByProperty("key");
      setpropertyvalue3.setKey((java.lang.String) argOption.valueOf("hello"));
      argOption = (AbstractArgumentOption) setpropertyvalue3.getOptionManager().findByProperty("value");
      setpropertyvalue3.setValue((java.lang.String) argOption.valueOf("world"));
      actors1[1] = setpropertyvalue3;

      // Flow.Convert
      adams.flow.transformer.Convert convert6 = new adams.flow.transformer.Convert();
      argOption = (AbstractArgumentOption) convert6.getOptionManager().findByProperty("conversion");
      adams.data.conversion.AnyToString anytostring8 = new adams.data.conversion.AnyToString();
      convert6.setConversion(anytostring8);

      actors1[2] = convert6;

      // Flow.StringSplit
      adams.flow.transformer.StringSplit stringsplit9 = new adams.flow.transformer.StringSplit();
      argOption = (AbstractArgumentOption) stringsplit9.getOptionManager().findByProperty("expression");
      stringsplit9.setExpression((java.lang.String) argOption.valueOf("\n"));
      actors1[3] = stringsplit9;

      // Flow.StringMatcher
      adams.flow.transformer.StringMatcher stringmatcher11 = new adams.flow.transformer.StringMatcher();
      argOption = (AbstractArgumentOption) stringmatcher11.getOptionManager().findByProperty("regExp");
      stringmatcher11.setRegExp((adams.core.base.BaseRegExp) argOption.valueOf("#.*"));
      stringmatcher11.setInvert(true);

      actors1[4] = stringmatcher11;

      // Flow.StringJoin
      adams.flow.transformer.StringJoin stringjoin13 = new adams.flow.transformer.StringJoin();
      argOption = (AbstractArgumentOption) stringjoin13.getOptionManager().findByProperty("glue");
      stringjoin13.setGlue((java.lang.String) argOption.valueOf("\n"));
      actors1[5] = stringjoin13;

      // Flow.DumpFile
      adams.flow.sink.DumpFile dumpfile15 = new adams.flow.sink.DumpFile();
      argOption = (AbstractArgumentOption) dumpfile15.getOptionManager().findByProperty("outputFile");
      dumpfile15.setOutputFile((adams.core.io.PlaceholderFile) argOption.valueOf("${TMP}/dumpfile.txt"));
      actors1[6] = dumpfile15;
      flow.setActors(actors1);

      argOption = (AbstractArgumentOption) flow.getOptionManager().findByProperty("flowExecutionListener");
      adams.flow.execution.NullListener nulllistener18 = new adams.flow.execution.NullListener();
      flow.setFlowExecutionListener(nulllistener18);

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

