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
 * GetStorageValueTest.java
 * Copyright (C) 2014 University of Waikato, Hamilton, New Zealand
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
 * Test for GetStorageValue actor.
 *
 * @author fracpete
 * @author adams.core.option.FlowJUnitTestProducer (code generator)
 * @version $Revision$
 */
public class GetStorageValueTest
  extends AbstractFlowTest {

  /**
   * Initializes the test.
   *
   * @param name	the name of the test
   */
  public GetStorageValueTest(String name) {
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
    return new TestSuite(GetStorageValueTest.class);
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
      adams.flow.core.AbstractActor[] actors4 = new adams.flow.core.AbstractActor[4];

      // Flow.Trigger.StringConstants
      adams.flow.source.StringConstants stringconstants5 = new adams.flow.source.StringConstants();
      argOption = (AbstractArgumentOption) stringconstants5.getOptionManager().findByProperty("strings");
      adams.core.base.BaseString[] strings6 = new adams.core.base.BaseString[3];
      strings6[0] = (adams.core.base.BaseString) argOption.valueOf("1");
      strings6[1] = (adams.core.base.BaseString) argOption.valueOf("2");
      strings6[2] = (adams.core.base.BaseString) argOption.valueOf("3");
      stringconstants5.setStrings(strings6);
      actors4[0] = stringconstants5;

      // Flow.Trigger.SetVariable
      adams.flow.transformer.SetVariable setvariable7 = new adams.flow.transformer.SetVariable();
      argOption = (AbstractArgumentOption) setvariable7.getOptionManager().findByProperty("variableName");
      setvariable7.setVariableName((adams.core.VariableName) argOption.valueOf("name"));
      actors4[1] = setvariable7;

      // Flow.Trigger.StringReplace
      adams.flow.transformer.StringReplace stringreplace9 = new adams.flow.transformer.StringReplace();
      argOption = (AbstractArgumentOption) stringreplace9.getOptionManager().findByProperty("find");
      stringreplace9.setFind((adams.core.base.BaseRegExp) argOption.valueOf("$"));
      argOption = (AbstractArgumentOption) stringreplace9.getOptionManager().findByProperty("replace");
      stringreplace9.setReplace((java.lang.String) argOption.valueOf("-blah"));
      actors4[2] = stringreplace9;

      // Flow.Trigger.SetStorageValue
      adams.flow.transformer.SetStorageValue setstoragevalue12 = new adams.flow.transformer.SetStorageValue();
      argOption = (AbstractArgumentOption) setstoragevalue12.getOptionManager().findByProperty("storageName");
      argOption.setVariable("@{name}");
      actors4[3] = setstoragevalue12;
      trigger3.setActors(actors4);

      actors1[1] = trigger3;

      // Flow.Trigger-1
      adams.flow.control.Trigger trigger13 = new adams.flow.control.Trigger();
      argOption = (AbstractArgumentOption) trigger13.getOptionManager().findByProperty("name");
      trigger13.setName((java.lang.String) argOption.valueOf("Trigger-1"));
      argOption = (AbstractArgumentOption) trigger13.getOptionManager().findByProperty("actors");
      adams.flow.core.AbstractActor[] actors15 = new adams.flow.core.AbstractActor[3];

      // Flow.Trigger-1.ListStorageNames
      adams.flow.source.ListStorageNames liststoragenames16 = new adams.flow.source.ListStorageNames();
      actors15[0] = liststoragenames16;

      // Flow.Trigger-1.GetStorageValue
      adams.flow.transformer.GetStorageValue getstoragevalue17 = new adams.flow.transformer.GetStorageValue();
      actors15[1] = getstoragevalue17;

      // Flow.Trigger-1.DumpFile
      adams.flow.sink.DumpFile dumpfile18 = new adams.flow.sink.DumpFile();
      argOption = (AbstractArgumentOption) dumpfile18.getOptionManager().findByProperty("outputFile");
      dumpfile18.setOutputFile((adams.core.io.PlaceholderFile) argOption.valueOf("${TMP}/dumpfile.txt"));
      dumpfile18.setAppend(true);

      actors15[2] = dumpfile18;
      trigger13.setActors(actors15);

      actors1[2] = trigger13;
      flow.setActors(actors1);

      argOption = (AbstractArgumentOption) flow.getOptionManager().findByProperty("flowExecutionListener");
      adams.flow.execution.NullListener nulllistener21 = new adams.flow.execution.NullListener();
      flow.setFlowExecutionListener(nulllistener21);

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

