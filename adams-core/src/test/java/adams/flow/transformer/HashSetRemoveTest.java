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
 * HashSetRemoveTest.java
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
 * Test for HashSetRemove actor.
 *
 * @author fracpete
 * @author adams.core.option.FlowJUnitTestProducer (code generator)
 * @version $Revision$
 */
public class HashSetRemoveTest
  extends AbstractFlowTest {

  /**
   * Initializes the test.
   *
   * @param name	the name of the test
   */
  public HashSetRemoveTest(String name) {
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
    
    m_TestHelper.deleteFileFromTmp("contained.txt");
    m_TestHelper.deleteFileFromTmp("notcontained.txt");
  }

  /**
   * Called by JUnit after each test method.
   *
   * @throws Exception	if tear-down fails
   */
  @Override
  protected void tearDown() throws Exception {
    m_TestHelper.deleteFileFromTmp("contained.txt");
    m_TestHelper.deleteFileFromTmp("notcontained.txt");
    
    super.tearDown();
  }

  /**
   * Performs a regression test, comparing against previously generated output.
   */
  public void testRegression() {
    performRegressionTest(
        new TmpFile[]{
          new TmpFile("contained.txt"),
          new TmpFile("notcontained.txt")
        });
  }

  /**
   * 
   * Returns a test suite.
   *
   * @return		the test suite
   */
  public static Test suite() {
    return new TestSuite(HashSetRemoveTest.class);
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
      adams.flow.core.AbstractActor[] actors1 = new adams.flow.core.AbstractActor[5];

      // Flow.HashSetInit
      adams.flow.standalone.HashSetInit hashsetinit2 = new adams.flow.standalone.HashSetInit();
      actors1[0] = hashsetinit2;

      // Flow.Start
      adams.flow.source.Start start3 = new adams.flow.source.Start();
      actors1[1] = start3;

      // Flow.Trigger
      adams.flow.control.Trigger trigger4 = new adams.flow.control.Trigger();
      argOption = (AbstractArgumentOption) trigger4.getOptionManager().findByProperty("actors");
      adams.flow.core.AbstractActor[] actors5 = new adams.flow.core.AbstractActor[2];

      // Flow.Trigger.ForLoop
      adams.flow.source.ForLoop forloop6 = new adams.flow.source.ForLoop();
      actors5[0] = forloop6;

      // Flow.Trigger.HashSetAdd
      adams.flow.transformer.HashSetAdd hashsetadd7 = new adams.flow.transformer.HashSetAdd();
      actors5[1] = hashsetadd7;
      trigger4.setActors(actors5);

      actors1[2] = trigger4;

      // Flow.Trigger-2
      adams.flow.control.Trigger trigger8 = new adams.flow.control.Trigger();
      argOption = (AbstractArgumentOption) trigger8.getOptionManager().findByProperty("name");
      trigger8.setName((java.lang.String) argOption.valueOf("Trigger-2"));
      argOption = (AbstractArgumentOption) trigger8.getOptionManager().findByProperty("actors");
      adams.flow.core.AbstractActor[] actors10 = new adams.flow.core.AbstractActor[2];

      // Flow.Trigger-2.ForLoop
      adams.flow.source.ForLoop forloop11 = new adams.flow.source.ForLoop();
      argOption = (AbstractArgumentOption) forloop11.getOptionManager().findByProperty("loopLower");
      forloop11.setLoopLower((Integer) argOption.valueOf("3"));
      argOption = (AbstractArgumentOption) forloop11.getOptionManager().findByProperty("loopUpper");
      forloop11.setLoopUpper((Integer) argOption.valueOf("5"));
      actors10[0] = forloop11;

      // Flow.Trigger-2.HashSetRemove
      adams.flow.transformer.HashSetRemove hashsetremove14 = new adams.flow.transformer.HashSetRemove();
      actors10[1] = hashsetremove14;
      trigger8.setActors(actors10);

      actors1[3] = trigger8;

      // Flow.Trigger-1
      adams.flow.control.Trigger trigger15 = new adams.flow.control.Trigger();
      argOption = (AbstractArgumentOption) trigger15.getOptionManager().findByProperty("name");
      trigger15.setName((java.lang.String) argOption.valueOf("Trigger-1"));
      argOption = (AbstractArgumentOption) trigger15.getOptionManager().findByProperty("actors");
      adams.flow.core.AbstractActor[] actors17 = new adams.flow.core.AbstractActor[3];

      // Flow.Trigger-1.ForLoop
      adams.flow.source.ForLoop forloop18 = new adams.flow.source.ForLoop();
      argOption = (AbstractArgumentOption) forloop18.getOptionManager().findByProperty("loopUpper");
      forloop18.setLoopUpper((Integer) argOption.valueOf("15"));
      actors17[0] = forloop18;

      // Flow.Trigger-1.ConditionalTee
      adams.flow.control.ConditionalTee conditionaltee20 = new adams.flow.control.ConditionalTee();
      argOption = (AbstractArgumentOption) conditionaltee20.getOptionManager().findByProperty("actors");
      adams.flow.core.AbstractActor[] actors21 = new adams.flow.core.AbstractActor[1];

      // Flow.Trigger-1.ConditionalTee.DumpFile
      adams.flow.sink.DumpFile dumpfile22 = new adams.flow.sink.DumpFile();
      argOption = (AbstractArgumentOption) dumpfile22.getOptionManager().findByProperty("outputFile");
      dumpfile22.setOutputFile((adams.core.io.PlaceholderFile) argOption.valueOf("${TMP}/contained.txt"));
      dumpfile22.setAppend(true);

      actors21[0] = dumpfile22;
      conditionaltee20.setActors(actors21);

      argOption = (AbstractArgumentOption) conditionaltee20.getOptionManager().findByProperty("condition");
      adams.flow.condition.bool.HashSet hashset25 = new adams.flow.condition.bool.HashSet();
      conditionaltee20.setCondition(hashset25);

      actors17[1] = conditionaltee20;

      // Flow.Trigger-1.ConditionalTee-1
      adams.flow.control.ConditionalTee conditionaltee26 = new adams.flow.control.ConditionalTee();
      argOption = (AbstractArgumentOption) conditionaltee26.getOptionManager().findByProperty("name");
      conditionaltee26.setName((java.lang.String) argOption.valueOf("ConditionalTee-1"));
      argOption = (AbstractArgumentOption) conditionaltee26.getOptionManager().findByProperty("actors");
      adams.flow.core.AbstractActor[] actors28 = new adams.flow.core.AbstractActor[1];

      // Flow.Trigger-1.ConditionalTee-1.DumpFile
      adams.flow.sink.DumpFile dumpfile29 = new adams.flow.sink.DumpFile();
      argOption = (AbstractArgumentOption) dumpfile29.getOptionManager().findByProperty("outputFile");
      dumpfile29.setOutputFile((adams.core.io.PlaceholderFile) argOption.valueOf("${TMP}/notcontained.txt"));
      dumpfile29.setAppend(true);

      actors28[0] = dumpfile29;
      conditionaltee26.setActors(actors28);

      argOption = (AbstractArgumentOption) conditionaltee26.getOptionManager().findByProperty("condition");
      adams.flow.condition.bool.Not not32 = new adams.flow.condition.bool.Not();
      argOption = (AbstractArgumentOption) not32.getOptionManager().findByProperty("condition");
      adams.flow.condition.bool.HashSet hashset34 = new adams.flow.condition.bool.HashSet();
      not32.setCondition(hashset34);

      conditionaltee26.setCondition(not32);

      actors17[2] = conditionaltee26;
      trigger15.setActors(actors17);

      actors1[4] = trigger15;
      flow.setActors(actors1);

      argOption = (AbstractArgumentOption) flow.getOptionManager().findByProperty("flowExecutionListener");
      adams.flow.execution.NullListener nulllistener36 = new adams.flow.execution.NullListener();
      flow.setFlowExecutionListener(nulllistener36);

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

