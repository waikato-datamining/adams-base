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
 * QueueEventTest.java
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
 * Test for QueueEvent actor.
 *
 * @author fracpete
 * @author adams.core.option.FlowJUnitTestProducer (code generator)
 * @version $Revision$
 */
public class QueueEventTest
  extends AbstractFlowTest {

  /**
   * Initializes the test.
   *
   * @param name	the name of the test
   */
  public QueueEventTest(String name) {
    super(name);
  }

  /**
   * 
   * Returns a test suite.
   *
   * @return		the test suite
   */
  public static Test suite() {
    return new TestSuite(QueueEventTest.class);
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
      adams.flow.core.Actor[] actors1 = new adams.flow.core.Actor[5];

      // Flow.QueueInit
      adams.flow.standalone.QueueInit queueinit2 = new adams.flow.standalone.QueueInit();
      actors1[0] = queueinit2;

      // Flow.Events
      adams.flow.standalone.Events events3 = new adams.flow.standalone.Events();
      argOption = (AbstractArgumentOption) events3.getOptionManager().findByProperty("actors");
      adams.flow.core.Actor[] actors4 = new adams.flow.core.Actor[1];

      // Flow.Events.QueueEvent
      adams.flow.standalone.QueueEvent queueevent5 = new adams.flow.standalone.QueueEvent();
      argOption = (AbstractArgumentOption) queueevent5.getOptionManager().findByProperty("actors");
      adams.flow.core.Actor[] actors6 = new adams.flow.core.Actor[1];

      // .Sequence.Display
      adams.flow.sink.Display display7 = new adams.flow.sink.Display();
      argOption = (AbstractArgumentOption) display7.getOptionManager().findByProperty("writer");
      adams.data.io.output.NullWriter nullwriter9 = new adams.data.io.output.NullWriter();
      display7.setWriter(nullwriter9);

      actors6[0] = display7;
      queueevent5.setActors(actors6);

      argOption = (AbstractArgumentOption) queueevent5.getOptionManager().findByProperty("interval");
      queueevent5.setInterval((Integer) argOption.valueOf("100"));
      actors4[0] = queueevent5;
      events3.setActors(actors4);

      actors1[1] = events3;

      // Flow.StringConstants
      adams.flow.source.StringConstants stringconstants11 = new adams.flow.source.StringConstants();
      argOption = (AbstractArgumentOption) stringconstants11.getOptionManager().findByProperty("strings");
      adams.core.base.BaseString[] strings12 = new adams.core.base.BaseString[5];
      strings12[0] = (adams.core.base.BaseString) argOption.valueOf("1");
      strings12[1] = (adams.core.base.BaseString) argOption.valueOf("2");
      strings12[2] = (adams.core.base.BaseString) argOption.valueOf("3");
      strings12[3] = (adams.core.base.BaseString) argOption.valueOf("4");
      strings12[4] = (adams.core.base.BaseString) argOption.valueOf("5");
      stringconstants11.setStrings(strings12);
      actors1[2] = stringconstants11;

      // Flow.Sleep
      adams.flow.control.Sleep sleep13 = new adams.flow.control.Sleep();
      actors1[3] = sleep13;

      // Flow.EnQueue
      adams.flow.sink.EnQueue enqueue14 = new adams.flow.sink.EnQueue();
      actors1[4] = enqueue14;
      flow.setActors(actors1);

      argOption = (AbstractArgumentOption) flow.getOptionManager().findByProperty("flowExecutionListener");
      adams.flow.execution.NullListener nulllistener16 = new adams.flow.execution.NullListener();
      flow.setFlowExecutionListener(nulllistener16);

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

