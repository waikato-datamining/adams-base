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
 * SubStringCountTest.java
 * Copyright (C) 2015 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.transformer;

import adams.core.option.AbstractArgumentOption;
import adams.env.Environment;
import adams.flow.AbstractFlowTest;
import adams.flow.control.Flow;
import adams.flow.core.Actor;
import adams.test.TmpFile;
import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * Test for SubStringCount actor.
 *
 * @author fracpete
 * @author adams.core.option.FlowJUnitTestProducer (code generator)
 * @version $Revision$
 */
public class SubStringCountTest
  extends AbstractFlowTest {

  /**
   * Initializes the test.
   *
   * @param name	the name of the test
   */
  public SubStringCountTest(String name) {
    super(name);
  }

  /**
   * Called by JUnit before each test method.
   *
   * @throws Exception 	if an error occurs.
   */
  protected void setUp() throws Exception {
    super.setUp();
    
    m_TestHelper.deleteFileFromTmp("dumpfile.txt");
  }

  /**
   * Called by JUnit after each test method.
   *
   * @throws Exception	if tear-down fails
   */
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
    return new TestSuite(SubStringCountTest.class);
  }

  /**
   * Used to create an instance of a specific actor.
   *
   * @return a suitably configured <code>Actor</code> value
   */
  public Actor getActor() {
    AbstractArgumentOption    argOption;
    
    Flow flow = new Flow();
    
    try {
      argOption = (AbstractArgumentOption) flow.getOptionManager().findByProperty("actors");
      adams.flow.core.Actor[] actors1 = new adams.flow.core.Actor[5];

      // Flow.StringConstants
      adams.flow.source.StringConstants stringconstants2 = new adams.flow.source.StringConstants();
      argOption = (AbstractArgumentOption) stringconstants2.getOptionManager().findByProperty("strings");
      adams.core.base.BaseString[] strings3 = new adams.core.base.BaseString[7];
      strings3[0] = (adams.core.base.BaseString) argOption.valueOf("  RODERIGO. Tush, never tell me! I take it much unkindly");
      strings3[1] = (adams.core.base.BaseString) argOption.valueOf("    That thou, Iago, who hast had my purse");
      strings3[2] = (adams.core.base.BaseString) argOption.valueOf("    As if the strings were thine, shouldst know of this.");
      strings3[3] = (adams.core.base.BaseString) argOption.valueOf("  IAGO. \'Sblood, but you will not hear me.");
      strings3[4] = (adams.core.base.BaseString) argOption.valueOf("    If ever I did dream of such a matter,");
      strings3[5] = (adams.core.base.BaseString) argOption.valueOf("    Abhor me.");
      strings3[6] = (adams.core.base.BaseString) argOption.valueOf("  RODERIGO. Thou told\'st me thou didst hold him in thy hate.");
      stringconstants2.setStrings(strings3);
      actors1[0] = stringconstants2;

      // Flow.SetVariable
      adams.flow.transformer.SetVariable setvariable4 = new adams.flow.transformer.SetVariable();
      argOption = (AbstractArgumentOption) setvariable4.getOptionManager().findByProperty("variableName");
      setvariable4.setVariableName((adams.core.VariableName) argOption.valueOf("text"));
      actors1[1] = setvariable4;

      // Flow.SubStringCount
      adams.flow.transformer.SubStringCount substringcount6 = new adams.flow.transformer.SubStringCount();
      argOption = (AbstractArgumentOption) substringcount6.getOptionManager().findByProperty("find");
      substringcount6.setFind((java.lang.String) argOption.valueOf("i"));
      actors1[2] = substringcount6;

      // Flow.SetVariable-1
      adams.flow.transformer.SetVariable setvariable8 = new adams.flow.transformer.SetVariable();
      argOption = (AbstractArgumentOption) setvariable8.getOptionManager().findByProperty("name");
      setvariable8.setName((java.lang.String) argOption.valueOf("SetVariable-1"));
      argOption = (AbstractArgumentOption) setvariable8.getOptionManager().findByProperty("variableName");
      setvariable8.setVariableName((adams.core.VariableName) argOption.valueOf("count"));
      actors1[3] = setvariable8;

      // Flow.Trigger
      adams.flow.control.Trigger trigger11 = new adams.flow.control.Trigger();
      argOption = (AbstractArgumentOption) trigger11.getOptionManager().findByProperty("actors");
      adams.flow.core.Actor[] actors12 = new adams.flow.core.Actor[2];

      // Flow.Trigger.CombineVariables
      adams.flow.source.CombineVariables combinevariables13 = new adams.flow.source.CombineVariables();
      argOption = (AbstractArgumentOption) combinevariables13.getOptionManager().findByProperty("expression");
      combinevariables13.setExpression((adams.core.base.BaseText) argOption.valueOf("@{text}: @{count}"));
      actors12[0] = combinevariables13;

      // Flow.Trigger.DumpFile
      adams.flow.sink.DumpFile dumpfile15 = new adams.flow.sink.DumpFile();
      argOption = (AbstractArgumentOption) dumpfile15.getOptionManager().findByProperty("outputFile");
      dumpfile15.setOutputFile((adams.core.io.PlaceholderFile) argOption.valueOf("${TMP}/dumpfile.txt"));
      dumpfile15.setAppend(true);

      actors12[1] = dumpfile15;
      trigger11.setActors(actors12);

      actors1[4] = trigger11;
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

