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
 * ExpandStorageTest.java
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
 * Test for ExpandStorage actor.
 *
 * @author fracpete
 * @author adams.core.option.FlowJUnitTestProducer (code generator)
 * @version $Revision$
 */
public class ExpandStorageTest
  extends AbstractFlowTest {

  /**
   * Initializes the test.
   *
   * @param name	the name of the test
   */
  public ExpandStorageTest(String name) {
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
    return new TestSuite(ExpandStorageTest.class);
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
      adams.flow.core.AbstractActor[] actors1 = new adams.flow.core.AbstractActor[4];

      // Flow.SetVariable
      adams.flow.standalone.SetVariable setvariable2 = new adams.flow.standalone.SetVariable();
      argOption = (AbstractArgumentOption) setvariable2.getOptionManager().findByProperty("variableName");
      setvariable2.setVariableName((adams.core.VariableName) argOption.valueOf("a"));
      argOption = (AbstractArgumentOption) setvariable2.getOptionManager().findByProperty("variableValue");
      setvariable2.setVariableValue((adams.core.base.BaseText) argOption.valueOf("123"));
      actors1[0] = setvariable2;

      // Flow.Start
      adams.flow.source.Start start5 = new adams.flow.source.Start();
      actors1[1] = start5;

      // Flow.Trigger-1
      adams.flow.control.Trigger trigger6 = new adams.flow.control.Trigger();
      argOption = (AbstractArgumentOption) trigger6.getOptionManager().findByProperty("name");
      trigger6.setName((java.lang.String) argOption.valueOf("Trigger-1"));
      argOption = (AbstractArgumentOption) trigger6.getOptionManager().findByProperty("actors");
      adams.flow.core.AbstractActor[] actors8 = new adams.flow.core.AbstractActor[3];

      // Flow.Trigger-1.StringConstants
      adams.flow.source.StringConstants stringconstants9 = new adams.flow.source.StringConstants();
      argOption = (AbstractArgumentOption) stringconstants9.getOptionManager().findByProperty("strings");
      adams.core.base.BaseString[] strings10 = new adams.core.base.BaseString[1];
      strings10[0] = (adams.core.base.BaseString) argOption.valueOf("456");
      stringconstants9.setStrings(strings10);
      actors8[0] = stringconstants9;

      // Flow.Trigger-1.Convert
      adams.flow.transformer.Convert convert11 = new adams.flow.transformer.Convert();
      argOption = (AbstractArgumentOption) convert11.getOptionManager().findByProperty("conversion");
      adams.data.conversion.StringToInt stringtoint13 = new adams.data.conversion.StringToInt();
      convert11.setConversion(stringtoint13);

      actors8[1] = convert11;

      // Flow.Trigger-1.SetStorageValue
      adams.flow.transformer.SetStorageValue setstoragevalue14 = new adams.flow.transformer.SetStorageValue();
      argOption = (AbstractArgumentOption) setstoragevalue14.getOptionManager().findByProperty("storageName");
      setstoragevalue14.setStorageName((adams.flow.control.StorageName) argOption.valueOf("b"));
      actors8[2] = setstoragevalue14;
      trigger6.setActors(actors8);

      actors1[2] = trigger6;

      // Flow.Trigger
      adams.flow.control.Trigger trigger16 = new adams.flow.control.Trigger();
      argOption = (AbstractArgumentOption) trigger16.getOptionManager().findByProperty("actors");
      adams.flow.core.AbstractActor[] actors17 = new adams.flow.core.AbstractActor[3];

      // Flow.Trigger.StringConstants
      adams.flow.source.StringConstants stringconstants18 = new adams.flow.source.StringConstants();
      argOption = (AbstractArgumentOption) stringconstants18.getOptionManager().findByProperty("strings");
      adams.core.base.BaseString[] strings19 = new adams.core.base.BaseString[1];
      strings19[0] = (adams.core.base.BaseString) argOption.valueOf("@{a}+%{b}=?");
      stringconstants18.setStrings(strings19);
      actors17[0] = stringconstants18;

      // Flow.Trigger.ExpandStorage
      adams.flow.transformer.ExpandStorage expandstorage20 = new adams.flow.transformer.ExpandStorage();
      actors17[1] = expandstorage20;

      // Flow.Trigger.DumpFile
      adams.flow.sink.DumpFile dumpfile21 = new adams.flow.sink.DumpFile();
      argOption = (AbstractArgumentOption) dumpfile21.getOptionManager().findByProperty("outputFile");
      dumpfile21.setOutputFile((adams.core.io.PlaceholderFile) argOption.valueOf("${TMP}/dumpfile.txt"));
      actors17[2] = dumpfile21;
      trigger16.setActors(actors17);

      actors1[3] = trigger16;
      flow.setActors(actors1);

      argOption = (AbstractArgumentOption) flow.getOptionManager().findByProperty("flowExecutionListener");
      adams.flow.execution.NullListener nulllistener24 = new adams.flow.execution.NullListener();
      flow.setFlowExecutionListener(nulllistener24);

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

