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
 * CombineStorageTest.java
 * Copyright (C) 2014 University of Waikato, Hamilton, New Zealand
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
 * Test for CombineStorage actor.
 *
 * @author fracpete
 * @author adams.core.option.FlowJUnitTestProducer (code generator)
 * @version $Revision$
 */
public class CombineStorageTest
  extends AbstractFlowTest {

  /**
   * Initializes the test.
   *
   * @param name	the name of the test
   */
  public CombineStorageTest(String name) {
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
    return new TestSuite(CombineStorageTest.class);
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
      adams.flow.core.AbstractActor[] actors1 = new adams.flow.core.AbstractActor[3];

      // Flow.Start
      adams.flow.source.Start start2 = new adams.flow.source.Start();
      actors1[0] = start2;

      // Flow.Trigger
      adams.flow.control.Trigger trigger3 = new adams.flow.control.Trigger();
      argOption = (AbstractArgumentOption) trigger3.getOptionManager().findByProperty("actors");
      adams.flow.core.AbstractActor[] actors4 = new adams.flow.core.AbstractActor[3];

      // Flow.Trigger.StringConstants
      adams.flow.source.StringConstants stringconstants5 = new adams.flow.source.StringConstants();
      argOption = (AbstractArgumentOption) stringconstants5.getOptionManager().findByProperty("strings");
      adams.core.base.BaseString[] strings6 = new adams.core.base.BaseString[3];
      strings6[0] = (adams.core.base.BaseString) argOption.valueOf("a");
      strings6[1] = (adams.core.base.BaseString) argOption.valueOf("b");
      strings6[2] = (adams.core.base.BaseString) argOption.valueOf("c");
      stringconstants5.setStrings(strings6);
      actors4[0] = stringconstants5;

      // Flow.Trigger.SetVariable
      adams.flow.transformer.SetVariable setvariable7 = new adams.flow.transformer.SetVariable();
      argOption = (AbstractArgumentOption) setvariable7.getOptionManager().findByProperty("variableName");
      setvariable7.setVariableName((adams.core.VariableName) argOption.valueOf("name"));
      actors4[1] = setvariable7;

      // Flow.Trigger.SetStorageValue
      adams.flow.transformer.SetStorageValue setstoragevalue9 = new adams.flow.transformer.SetStorageValue();
      argOption = (AbstractArgumentOption) setstoragevalue9.getOptionManager().findByProperty("storageName");
      argOption.setVariable("@{name}");
      actors4[2] = setstoragevalue9;
      trigger3.setActors(actors4);

      actors1[1] = trigger3;

      // Flow.Trigger-1
      adams.flow.control.Trigger trigger10 = new adams.flow.control.Trigger();
      argOption = (AbstractArgumentOption) trigger10.getOptionManager().findByProperty("name");
      trigger10.setName((java.lang.String) argOption.valueOf("Trigger-1"));
      argOption = (AbstractArgumentOption) trigger10.getOptionManager().findByProperty("actors");
      adams.flow.core.AbstractActor[] actors12 = new adams.flow.core.AbstractActor[2];

      // Flow.Trigger-1.CombineStorage
      adams.flow.source.CombineStorage combinestorage13 = new adams.flow.source.CombineStorage();
      argOption = (AbstractArgumentOption) combinestorage13.getOptionManager().findByProperty("expression");
      combinestorage13.setExpression((adams.core.base.BaseText) argOption.valueOf("%{a}+%{b}-%{c}"));
      actors12[0] = combinestorage13;

      // Flow.Trigger-1.DumpFile
      adams.flow.sink.DumpFile dumpfile15 = new adams.flow.sink.DumpFile();
      argOption = (AbstractArgumentOption) dumpfile15.getOptionManager().findByProperty("outputFile");
      dumpfile15.setOutputFile((adams.core.io.PlaceholderFile) argOption.valueOf("${TMP}/dumpfile.txt"));
      actors12[1] = dumpfile15;
      trigger10.setActors(actors12);

      actors1[2] = trigger10;
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

