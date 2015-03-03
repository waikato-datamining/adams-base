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
 * SinkResetTest.java
 * Copyright (C) 2014 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.control;

import junit.framework.Test;
import junit.framework.TestSuite;
import adams.core.option.AbstractArgumentOption;
import adams.env.Environment;
import adams.flow.AbstractFlowTest;
import adams.flow.core.AbstractActor;
import adams.test.TmpFile;

/**
 * Test for SinkReset actor.
 *
 * @author fracpete
 * @author adams.core.option.FlowJUnitTestProducer (code generator)
 * @version $Revision$
 */
public class SinkResetTest
  extends AbstractFlowTest {

  /**
   * Initializes the test.
   *
   * @param name	the name of the test
   */
  public SinkResetTest(String name) {
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
    return new TestSuite(SinkResetTest.class);
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

      // Flow.CallableActors
      adams.flow.standalone.CallableActors callableactors2 = new adams.flow.standalone.CallableActors();
      argOption = (AbstractArgumentOption) callableactors2.getOptionManager().findByProperty("actors");
      adams.flow.core.AbstractActor[] actors3 = new adams.flow.core.AbstractActor[1];

      // Flow.CallableActors.DumpFile
      adams.flow.sink.DumpFile dumpfile4 = new adams.flow.sink.DumpFile();
      argOption = (AbstractArgumentOption) dumpfile4.getOptionManager().findByProperty("outputFile");
      dumpfile4.setOutputFile((adams.core.io.PlaceholderFile) argOption.valueOf("${TMP}/dumpfile.txt"));
      dumpfile4.setAppend(true);
      actors3[0] = dumpfile4;
      callableactors2.setActors(actors3);

      actors1[0] = callableactors2;

      // Flow.ForLoop
      adams.flow.source.ForLoop forloop6 = new adams.flow.source.ForLoop();
      actors1[1] = forloop6;

      // Flow.ConditionalTee
      adams.flow.control.ConditionalTee conditionaltee7 = new adams.flow.control.ConditionalTee();
      argOption = (AbstractArgumentOption) conditionaltee7.getOptionManager().findByProperty("actors");
      adams.flow.core.AbstractActor[] actors8 = new adams.flow.core.AbstractActor[1];

      // Flow.ConditionalTee.SetVariable
      adams.flow.transformer.SetVariable setvariable9 = new adams.flow.transformer.SetVariable();
      argOption = (AbstractArgumentOption) setvariable9.getOptionManager().findByProperty("variableName");
      setvariable9.setVariableName((adams.core.VariableName) argOption.valueOf("reset"));
      actors8[0] = setvariable9;
      conditionaltee7.setActors(actors8);

      argOption = (AbstractArgumentOption) conditionaltee7.getOptionManager().findByProperty("condition");
      adams.flow.condition.bool.Counting counting12 = new adams.flow.condition.bool.Counting();
      argOption = (AbstractArgumentOption) counting12.getOptionManager().findByProperty("interval");
      counting12.setInterval((Integer) argOption.valueOf("5"));
      conditionaltee7.setCondition(counting12);

      actors1[2] = conditionaltee7;

      // Flow.SinkReset
      adams.flow.control.SinkReset sinkreset14 = new adams.flow.control.SinkReset();
      argOption = (AbstractArgumentOption) sinkreset14.getOptionManager().findByProperty("actors");
      adams.flow.core.AbstractActor[] actors15 = new adams.flow.core.AbstractActor[1];

      // Flow.SinkReset.Count
      adams.flow.control.Count count16 = new adams.flow.control.Count();
      argOption = (AbstractArgumentOption) count16.getOptionManager().findByProperty("actors");
      adams.flow.core.AbstractActor[] actors17 = new adams.flow.core.AbstractActor[1];

      // Flow.SinkReset.Count.CallableSink
      adams.flow.sink.CallableSink callablesink18 = new adams.flow.sink.CallableSink();
      argOption = (AbstractArgumentOption) callablesink18.getOptionManager().findByProperty("callableName");
      callablesink18.setCallableName((adams.flow.core.CallableActorReference) argOption.valueOf("DumpFile"));
      actors17[0] = callablesink18;
      count16.setActors(actors17);

      argOption = (AbstractArgumentOption) count16.getOptionManager().findByProperty("condition");
      adams.flow.condition.bool.Counting counting21 = new adams.flow.condition.bool.Counting();
      count16.setCondition(counting21);

      actors15[0] = count16;
      sinkreset14.setActors(actors15);

      argOption = (AbstractArgumentOption) sinkreset14.getOptionManager().findByProperty("variableName");
      sinkreset14.setVariableName((adams.core.VariableName) argOption.valueOf("reset"));
      actors1[3] = sinkreset14;
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

