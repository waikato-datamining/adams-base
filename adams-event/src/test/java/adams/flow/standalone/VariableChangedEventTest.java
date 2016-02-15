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
 * VariableChangedEventTest.java
 * Copyright (C) 2014 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.standalone;

import junit.framework.Test;
import junit.framework.TestSuite;
import adams.core.option.AbstractArgumentOption;
import adams.env.Environment;
import adams.flow.AbstractFlowTest;
import adams.flow.control.Flow;
import adams.flow.core.Actor;

/**
 * Test for VariableChangedEvent actor.
 *
 * @author fracpete
 * @author adams.core.option.FlowJUnitTestProducer (code generator)
 * @version $Revision$
 */
public class VariableChangedEventTest
  extends AbstractFlowTest {

  /**
   * Initializes the test.
   *
   * @param name	the name of the test
   */
  public VariableChangedEventTest(String name) {
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
    
    m_TestHelper.copyResourceToTmp("variable1.txt");
    m_TestHelper.copyResourceToTmp("variable2.txt");
  }

  /**
   * Called by JUnit after each test method.
   *
   * @throws Exception	if tear-down fails
   */
  @Override
  protected void tearDown() throws Exception {
    m_TestHelper.deleteFileFromTmp("variable1.txt");
    m_TestHelper.deleteFileFromTmp("variable2.txt");
    
    super.tearDown();
  }

  /**
   * 
   * Returns a test suite.
   *
   * @return		the test suite
   */
  public static Test suite() {
    return new TestSuite(VariableChangedEventTest.class);
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

      // Flow.CallableActors
      adams.flow.standalone.CallableActors callableactors2 = new adams.flow.standalone.CallableActors();
      argOption = (AbstractArgumentOption) callableactors2.getOptionManager().findByProperty("actors");
      adams.flow.core.Actor[] actors3 = new adams.flow.core.Actor[1];

      // Flow.CallableActors.HistoryDisplay
      adams.flow.sink.HistoryDisplay historydisplay4 = new adams.flow.sink.HistoryDisplay();
      actors3[0] = historydisplay4;
      callableactors2.setActors(actors3);

      actors1[0] = callableactors2;

      // Flow.Events
      adams.flow.standalone.Events events5 = new adams.flow.standalone.Events();
      argOption = (AbstractArgumentOption) events5.getOptionManager().findByProperty("actors");
      adams.flow.core.Actor[] actors6 = new adams.flow.core.Actor[1];

      // Flow.Events.VariableChangedEvent
      adams.flow.standalone.VariableChangedEvent variablechangedevent7 = new adams.flow.standalone.VariableChangedEvent();
      argOption = (AbstractArgumentOption) variablechangedevent7.getOptionManager().findByProperty("variable");
      variablechangedevent7.setVariable((adams.core.VariableNameNoUpdate) argOption.valueOf("filename"));
      variablechangedevent7.setNoDiscard(true);

      argOption = (AbstractArgumentOption) variablechangedevent7.getOptionManager().findByProperty("actors");
      adams.flow.core.Actor[] actors9 = new adams.flow.core.Actor[2];

      // .Sequence.Variable
      adams.flow.source.Variable variable10 = new adams.flow.source.Variable();
      argOption = (AbstractArgumentOption) variable10.getOptionManager().findByProperty("variableName");
      variable10.setVariableName((adams.core.VariableName) argOption.valueOf("filename"));
      actors9[0] = variable10;

      // .Sequence.CallableSink
      adams.flow.sink.CallableSink callablesink12 = new adams.flow.sink.CallableSink();
      argOption = (AbstractArgumentOption) callablesink12.getOptionManager().findByProperty("callableName");
      callablesink12.setCallableName((adams.flow.core.CallableActorReference) argOption.valueOf("HistoryDisplay"));
      actors9[1] = callablesink12;
      variablechangedevent7.setActors(actors9);

      actors6[0] = variablechangedevent7;
      events5.setActors(actors6);

      actors1[1] = events5;

      // Flow.ForLoop
      adams.flow.source.ForLoop forloop14 = new adams.flow.source.ForLoop();
      argOption = (AbstractArgumentOption) forloop14.getOptionManager().findByProperty("loopUpper");
      forloop14.setLoopUpper((Integer) argOption.valueOf("2"));
      actors1[2] = forloop14;

      // Flow.Tee (set filename variable)
      adams.flow.control.Tee tee16 = new adams.flow.control.Tee();
      argOption = (AbstractArgumentOption) tee16.getOptionManager().findByProperty("name");
      tee16.setName((java.lang.String) argOption.valueOf("Tee (set filename variable)"));
      argOption = (AbstractArgumentOption) tee16.getOptionManager().findByProperty("actors");
      adams.flow.core.Actor[] actors18 = new adams.flow.core.Actor[4];

      // Flow.Tee (set filename variable).Convert
      adams.flow.transformer.Convert convert19 = new adams.flow.transformer.Convert();
      argOption = (AbstractArgumentOption) convert19.getOptionManager().findByProperty("conversion");
      adams.data.conversion.AnyToString anytostring21 = new adams.data.conversion.AnyToString();
      convert19.setConversion(anytostring21);

      actors18[0] = convert19;

      // Flow.Tee (set filename variable).StringReplace (path)
      adams.flow.transformer.StringReplace stringreplace22 = new adams.flow.transformer.StringReplace();
      argOption = (AbstractArgumentOption) stringreplace22.getOptionManager().findByProperty("name");
      stringreplace22.setName((java.lang.String) argOption.valueOf("StringReplace (path)"));
      argOption = (AbstractArgumentOption) stringreplace22.getOptionManager().findByProperty("find");
      stringreplace22.setFind((adams.core.base.BaseRegExp) argOption.valueOf("^"));
      argOption = (AbstractArgumentOption) stringreplace22.getOptionManager().findByProperty("replace");
      stringreplace22.setReplace((java.lang.String) argOption.valueOf("${TMP}/variable"));
      stringreplace22.setReplaceContainsPlaceholder(true);

      actors18[1] = stringreplace22;

      // Flow.Tee (set filename variable).StringReplace (extension)
      adams.flow.transformer.StringReplace stringreplace26 = new adams.flow.transformer.StringReplace();
      argOption = (AbstractArgumentOption) stringreplace26.getOptionManager().findByProperty("name");
      stringreplace26.setName((java.lang.String) argOption.valueOf("StringReplace (extension)"));
      argOption = (AbstractArgumentOption) stringreplace26.getOptionManager().findByProperty("find");
      stringreplace26.setFind((adams.core.base.BaseRegExp) argOption.valueOf("$"));
      argOption = (AbstractArgumentOption) stringreplace26.getOptionManager().findByProperty("replace");
      stringreplace26.setReplace((java.lang.String) argOption.valueOf(".txt"));
      actors18[2] = stringreplace26;

      // Flow.Tee (set filename variable).SetVariable
      adams.flow.transformer.SetVariable setvariable30 = new adams.flow.transformer.SetVariable();
      argOption = (AbstractArgumentOption) setvariable30.getOptionManager().findByProperty("variableName");
      setvariable30.setVariableName((adams.core.VariableName) argOption.valueOf("filename"));
      actors18[3] = setvariable30;
      tee16.setActors(actors18);

      actors1[3] = tee16;

      // Flow.Sleep
      adams.flow.control.Sleep sleep32 = new adams.flow.control.Sleep();
      argOption = (AbstractArgumentOption) sleep32.getOptionManager().findByProperty("interval");
      sleep32.setInterval((Integer) argOption.valueOf("100"));
      actors1[4] = sleep32;

      // Flow.Trigger (load and display file)
      adams.flow.control.Trigger trigger34 = new adams.flow.control.Trigger();
      argOption = (AbstractArgumentOption) trigger34.getOptionManager().findByProperty("name");
      trigger34.setName((java.lang.String) argOption.valueOf("Trigger (load and display file)"));
      argOption = (AbstractArgumentOption) trigger34.getOptionManager().findByProperty("actors");
      adams.flow.core.Actor[] actors36 = new adams.flow.core.Actor[3];

      // Flow.Trigger (load and display file).FileSupplier
      adams.flow.source.FileSupplier filesupplier37 = new adams.flow.source.FileSupplier();
      argOption = (AbstractArgumentOption) filesupplier37.getOptionManager().findByProperty("files");
      argOption.setVariable("@{filename}");
      actors36[0] = filesupplier37;

      // Flow.Trigger (load and display file).TextFileReader
      adams.flow.transformer.TextFileReader textfilereader38 = new adams.flow.transformer.TextFileReader();
      argOption = (AbstractArgumentOption) textfilereader38.getOptionManager().findByProperty("reader");
      adams.data.io.input.SingleStringTextReader singlestringtextreader40 = new adams.data.io.input.SingleStringTextReader();
      textfilereader38.setReader(singlestringtextreader40);

      actors36[1] = textfilereader38;

      // Flow.Trigger (load and display file).CallableSink
      adams.flow.sink.CallableSink callablesink41 = new adams.flow.sink.CallableSink();
      argOption = (AbstractArgumentOption) callablesink41.getOptionManager().findByProperty("callableName");
      callablesink41.setCallableName((adams.flow.core.CallableActorReference) argOption.valueOf("HistoryDisplay"));
      actors36[2] = callablesink41;
      trigger34.setActors(actors36);

      actors1[5] = trigger34;
      flow.setActors(actors1);

      argOption = (AbstractArgumentOption) flow.getOptionManager().findByProperty("flowExecutionListener");
      adams.flow.execution.NullListener nulllistener44 = new adams.flow.execution.NullListener();
      flow.setFlowExecutionListener(nulllistener44);

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

