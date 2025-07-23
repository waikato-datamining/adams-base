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
 * TimedSourceTest.java
 * Copyright (C) 2025 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.source;

import adams.core.option.AbstractArgumentOption;
import adams.data.io.output.NullWriter;
import adams.env.Environment;
import adams.flow.AbstractFlowTest;
import adams.flow.control.Flow;
import adams.flow.control.Sleep;
import adams.flow.control.flowrestart.NullManager;
import adams.flow.control.postflowexecution.Null;
import adams.flow.core.AbstractActor;
import adams.flow.core.Actor;
import adams.flow.core.CallableActorReference;
import adams.flow.core.displaytype.Default;
import adams.flow.execution.NullListener;
import adams.flow.sink.Display;
import adams.flow.standalone.CallableActors;
import junit.framework.Test;
import junit.framework.TestSuite;

import java.util.ArrayList;
import java.util.List;

/**
 * Test for TimedSource actor.
 *
 * @author fracpete
 * @author adams.core.option.FlowJUnitTestProducer (code generator)
 */
public class TimedSourceTest
  extends AbstractFlowTest {

  /**
   * Initializes the test.
   *
   * @param name	the name of the test
   */
  public TimedSourceTest(String name) {
    super(name);
  }

  /**
   * 
   * Returns a test suite.
   *
   * @return		the test suite
   */
  public static Test suite() {
    return new TestSuite(TimedSourceTest.class);
  }

  /**
   * Used to create an instance of a specific actor.
   *
   * @return a suitably configured <code>AbstractActor</code> value
   */
  public AbstractActor getActor() {
    AbstractArgumentOption    argOption;
    
    Flow flow = new Flow();
    
    try {
      List<Actor> actors = new ArrayList<>();

      // Flow.CallableActors
      CallableActors callableactors = new CallableActors();
      List<Actor> actors2 = new ArrayList<>();

      // Flow.CallableActors.Display
      Display display = new Display();
      Default default_ = new Default();
      display.setDisplayType(default_);

      NullWriter nullwriter = new NullWriter();
      display.setWriter(nullwriter);

      actors2.add(display);
      callableactors.setActors(actors2.toArray(new Actor[0]));

      actors.add(callableactors);

      // Flow.TimedSource
      TimedSource timedsource = new TimedSource();
      List<Actor> actors3 = new ArrayList<>();

      // Flow.TimedSource.Start
      Start start = new Start();
      actors3.add(start);

      // Flow.TimedSource.Sleep
      Sleep sleep = new Sleep();
      actors3.add(sleep);
      timedsource.setActors(actors3.toArray(new Actor[0]));

      argOption = (AbstractArgumentOption) timedsource.getOptionManager().findByProperty("callableName");
      timedsource.setCallableName((CallableActorReference) argOption.valueOf("Display"));
      actors.add(timedsource);
      flow.setActors(actors.toArray(new Actor[0]));

      Null null_ = new Null();
      flow.setExecuteOnError(null_);

      Null null_2 = new Null();
      flow.setExecuteOnFinish(null_2);

      NullListener nulllistener = new NullListener();
      flow.setFlowExecutionListener(nulllistener);

      NullManager nullmanager = new NullManager();
      flow.setFlowRestartManager(nullmanager);

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

