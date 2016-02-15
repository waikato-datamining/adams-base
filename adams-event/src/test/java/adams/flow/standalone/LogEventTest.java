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
 * LogEventTest.java
 * Copyright (C) 2013 University of Waikato, Hamilton, New Zealand
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
 * Test for LogEvent actor.
 *
 * @author fracpete
 * @author adams.core.option.FlowJUnitTestProducer (code generator)
 * @version $Revision$
 */
public class LogEventTest
  extends AbstractFlowTest {

  /**
   * Initializes the test.
   *
   * @param name	the name of the test
   */
  public LogEventTest(String name) {
    super(name);
  }

  /**
   * 
   * Returns a test suite.
   *
   * @return		the test suite
   */
  public static Test suite() {
    return new TestSuite(LogEventTest.class);
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

      // Flow.Events
      adams.flow.standalone.Events events2 = new adams.flow.standalone.Events();
      argOption = (AbstractArgumentOption) events2.getOptionManager().findByProperty("actors");
      adams.flow.core.Actor[] actors3 = new adams.flow.core.Actor[1];

      // Flow.Events.LogEvent
      adams.flow.standalone.LogEvent logevent4 = new adams.flow.standalone.LogEvent();
      argOption = (AbstractArgumentOption) logevent4.getOptionManager().findByProperty("filter");
      adams.flow.standalone.logevent.AcceptAllFilter acceptallfilter6 = new adams.flow.standalone.logevent.AcceptAllFilter();
      logevent4.setFilter(acceptallfilter6);

      argOption = (AbstractArgumentOption) logevent4.getOptionManager().findByProperty("processor");
      adams.flow.standalone.logevent.SimpleProcessor simpleprocessor8 = new adams.flow.standalone.logevent.SimpleProcessor();
      logevent4.setProcessor(simpleprocessor8);

      argOption = (AbstractArgumentOption) logevent4.getOptionManager().findByProperty("actors");
      adams.flow.core.Actor[] actors9 = new adams.flow.core.Actor[1];

      // .Sequence.Display
      adams.flow.sink.Display display10 = new adams.flow.sink.Display();
      actors9[0] = display10;
      logevent4.setActors(actors9);

      actors3[0] = logevent4;
      events2.setActors(actors3);

      actors1[0] = events2;

      // Flow.ForLoop
      adams.flow.source.ForLoop forloop11 = new adams.flow.source.ForLoop();
      actors1[1] = forloop11;

      // Flow.SetVariable
      adams.flow.transformer.SetVariable setvariable12 = new adams.flow.transformer.SetVariable();
      argOption = (AbstractArgumentOption) setvariable12.getOptionManager().findByProperty("loggingLevel");
      setvariable12.setLoggingLevel((adams.core.logging.LoggingLevel) argOption.valueOf("FINE"));
      actors1[2] = setvariable12;

      // Flow.Sleep
      adams.flow.control.Sleep sleep14 = new adams.flow.control.Sleep();
      argOption = (AbstractArgumentOption) sleep14.getOptionManager().findByProperty("interval");
      sleep14.setInterval((Integer) argOption.valueOf("100"));
      actors1[3] = sleep14;

      // Flow.SetVariable-1
      adams.flow.transformer.SetVariable setvariable16 = new adams.flow.transformer.SetVariable();
      argOption = (AbstractArgumentOption) setvariable16.getOptionManager().findByProperty("loggingLevel");
      setvariable16.setLoggingLevel((adams.core.logging.LoggingLevel) argOption.valueOf("FINE"));
      argOption = (AbstractArgumentOption) setvariable16.getOptionManager().findByProperty("name");
      setvariable16.setName((java.lang.String) argOption.valueOf("SetVariable-1"));
      argOption = (AbstractArgumentOption) setvariable16.getOptionManager().findByProperty("variableName");
      setvariable16.setVariableName((adams.core.VariableName) argOption.valueOf("variable2"));
      actors1[4] = setvariable16;

      // Flow.Sleep-1
      adams.flow.control.Sleep sleep20 = new adams.flow.control.Sleep();
      argOption = (AbstractArgumentOption) sleep20.getOptionManager().findByProperty("name");
      sleep20.setName((java.lang.String) argOption.valueOf("Sleep-1"));
      argOption = (AbstractArgumentOption) sleep20.getOptionManager().findByProperty("interval");
      sleep20.setInterval((Integer) argOption.valueOf("100"));
      actors1[5] = sleep20;
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

