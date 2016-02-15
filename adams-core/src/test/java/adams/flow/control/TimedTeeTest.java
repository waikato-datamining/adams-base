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
 * TimedTeeTest.java
 * Copyright (C) 2014-2016 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.control;

import junit.framework.Test;
import junit.framework.TestSuite;
import adams.core.option.AbstractArgumentOption;
import adams.env.Environment;
import adams.flow.AbstractFlowTest;
import adams.flow.core.Actor;

/**
 * Test for TimedTee actor.
 *
 * @author fracpete
 * @author adams.core.option.FlowJUnitTestProducer (code generator)
 * @version $Revision$
 */
public class TimedTeeTest
  extends AbstractFlowTest {

  /**
   * Initializes the test.
   *
   * @param name	the name of the test
   */
  public TimedTeeTest(String name) {
    super(name);
  }

  /**
   * 
   * Returns a test suite.
   *
   * @return		the test suite
   */
  public static Test suite() {
    return new TestSuite(TimedTeeTest.class);
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
      adams.flow.core.Actor[] actors1 = new adams.flow.core.Actor[3];

      // Flow.CallableActors
      adams.flow.standalone.CallableActors callableactors2 = new adams.flow.standalone.CallableActors();
      argOption = (AbstractArgumentOption) callableactors2.getOptionManager().findByProperty("actors");
      adams.flow.core.Actor[] actors3 = new adams.flow.core.Actor[1];

      // Flow.CallableActors.Display
      adams.flow.sink.Display display4 = new adams.flow.sink.Display();
      argOption = (AbstractArgumentOption) display4.getOptionManager().findByProperty("writer");
      adams.data.io.output.NullWriter nullwriter6 = new adams.data.io.output.NullWriter();
      display4.setWriter(nullwriter6);

      actors3[0] = display4;
      callableactors2.setActors(actors3);

      actors1[0] = callableactors2;

      // Flow.Start
      adams.flow.source.Start start7 = new adams.flow.source.Start();
      actors1[1] = start7;

      // Flow.TimedTee
      adams.flow.control.TimedTee timedtee8 = new adams.flow.control.TimedTee();
      argOption = (AbstractArgumentOption) timedtee8.getOptionManager().findByProperty("actors");
      adams.flow.core.Actor[] actors9 = new adams.flow.core.Actor[1];

      // Flow.TimedTee.Sleep
      adams.flow.control.Sleep sleep10 = new adams.flow.control.Sleep();
      actors9[0] = sleep10;
      timedtee8.setActors(actors9);

      argOption = (AbstractArgumentOption) timedtee8.getOptionManager().findByProperty("callableName");
      timedtee8.setCallableName((adams.flow.core.CallableActorReference) argOption.valueOf("Display"));
      actors1[2] = timedtee8;
      flow.setActors(actors1);

      argOption = (AbstractArgumentOption) flow.getOptionManager().findByProperty("flowExecutionListener");
      adams.flow.execution.NullListener nulllistener13 = new adams.flow.execution.NullListener();
      flow.setFlowExecutionListener(nulllistener13);

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

