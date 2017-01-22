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
 * EnterManyValuesTest.java
 * Copyright (C) 2013 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.source;

import adams.flow.source.valuedefinition.DefaultValueDefinition;
import junit.framework.Test;
import junit.framework.TestSuite;
import adams.core.option.AbstractArgumentOption;
import adams.env.Environment;
import adams.flow.AbstractFlowTest;
import adams.flow.control.Flow;
import adams.flow.core.Actor;
import adams.test.TmpFile;

/**
 * Test for EnterManyValues actor.
 *
 * @author fracpete
 * @author adams.core.option.FlowJUnitTestProducer (code generator)
 * @version $Revision$
 */
public class EnterManyValuesTest
  extends AbstractFlowTest {

  /**
   * Initializes the test.
   *
   * @param name	the name of the test
   */
  public EnterManyValuesTest(String name) {
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
    
    m_TestHelper.deleteFileFromTmp("dumpfile.csv");
  }

  /**
   * Called by JUnit after each test method.
   *
   * @throws Exception	if tear-down fails
   */
  @Override
  protected void tearDown() throws Exception {
    m_TestHelper.deleteFileFromTmp("dumpfile.csv");
    
    super.tearDown();
  }

  /**
   * Performs a regression test, comparing against previously generated output.
   */
  public void testRegression() {
    performRegressionTest(
        new TmpFile[]{
          new TmpFile("dumpfile.csv")
        });
  }

  /**
   * 
   * Returns a test suite.
   *
   * @return		the test suite
   */
  public static Test suite() {
    return new TestSuite(EnterManyValuesTest.class);
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
      adams.flow.core.Actor[] actors1 = new adams.flow.core.Actor[2];

      // Flow.EnterManyValues
      adams.flow.source.EnterManyValues entermanyvalues2 = new adams.flow.source.EnterManyValues();
      argOption = (AbstractArgumentOption) entermanyvalues2.getOptionManager().findByProperty("values");
      DefaultValueDefinition[] values3 = new DefaultValueDefinition[4];
      DefaultValueDefinition valuedefinition4 = new DefaultValueDefinition();
      argOption = (AbstractArgumentOption) valuedefinition4.getOptionManager().findByProperty("name");
      valuedefinition4.setName((java.lang.String) argOption.valueOf("blah"));
      argOption = (AbstractArgumentOption) valuedefinition4.getOptionManager().findByProperty("defaultValue");
      valuedefinition4.setDefaultValue((java.lang.String) argOption.valueOf("hehe"));
      values3[0] = valuedefinition4;
      DefaultValueDefinition valuedefinition7 = new DefaultValueDefinition();
      argOption = (AbstractArgumentOption) valuedefinition7.getOptionManager().findByProperty("name");
      valuedefinition7.setName((java.lang.String) argOption.valueOf("bloerk"));
      argOption = (AbstractArgumentOption) valuedefinition7.getOptionManager().findByProperty("type");
      valuedefinition7.setType((adams.gui.core.PropertiesParameterPanel.PropertyType) argOption.valueOf("DOUBLE"));
      argOption = (AbstractArgumentOption) valuedefinition7.getOptionManager().findByProperty("defaultValue");
      valuedefinition7.setDefaultValue((java.lang.String) argOption.valueOf("1.0"));
      values3[1] = valuedefinition7;
      DefaultValueDefinition valuedefinition11 = new DefaultValueDefinition();
      argOption = (AbstractArgumentOption) valuedefinition11.getOptionManager().findByProperty("name");
      valuedefinition11.setName((java.lang.String) argOption.valueOf("color"));
      argOption = (AbstractArgumentOption) valuedefinition11.getOptionManager().findByProperty("type");
      valuedefinition11.setType((adams.gui.core.PropertiesParameterPanel.PropertyType) argOption.valueOf("COLOR"));
      argOption = (AbstractArgumentOption) valuedefinition11.getOptionManager().findByProperty("defaultValue");
      valuedefinition11.setDefaultValue((java.lang.String) argOption.valueOf("#000000"));
      values3[2] = valuedefinition11;
      DefaultValueDefinition valuedefinition15 = new DefaultValueDefinition();
      argOption = (AbstractArgumentOption) valuedefinition15.getOptionManager().findByProperty("name");
      valuedefinition15.setName((java.lang.String) argOption.valueOf("trueorfalse"));
      argOption = (AbstractArgumentOption) valuedefinition15.getOptionManager().findByProperty("type");
      valuedefinition15.setType((adams.gui.core.PropertiesParameterPanel.PropertyType) argOption.valueOf("BOOLEAN"));
      argOption = (AbstractArgumentOption) valuedefinition15.getOptionManager().findByProperty("defaultValue");
      valuedefinition15.setDefaultValue((java.lang.String) argOption.valueOf("true"));
      values3[3] = valuedefinition15;
      entermanyvalues2.setValues(values3);

      entermanyvalues2.setNonInteractive(true);

      actors1[0] = entermanyvalues2;

      // Flow.DumpFile
      adams.flow.sink.DumpFile dumpfile19 = new adams.flow.sink.DumpFile();
      argOption = (AbstractArgumentOption) dumpfile19.getOptionManager().findByProperty("outputFile");
      dumpfile19.setOutputFile((adams.core.io.PlaceholderFile) argOption.valueOf("${TMP}/dumpfile.csv"));
      actors1[1] = dumpfile19;
      flow.setActors(actors1);

      argOption = (AbstractArgumentOption) flow.getOptionManager().findByProperty("flowExecutionListener");
      adams.flow.execution.NullListener nulllistener22 = new adams.flow.execution.NullListener();
      flow.setFlowExecutionListener(nulllistener22);

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

