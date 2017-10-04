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
 * CloseCallableDisplayTest.java
 * Copyright (C) 2017 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.control;

import adams.core.base.BaseString;
import adams.core.option.AbstractArgumentOption;
import adams.data.conversion.StringToString;
import adams.data.io.output.NullWriter;
import adams.env.Environment;
import adams.flow.AbstractFlowTest;
import adams.flow.core.AbstractActor;
import adams.flow.core.Actor;
import adams.flow.core.CallableActorReference;
import adams.flow.execution.NullListener;
import adams.flow.sink.CallableSink;
import adams.flow.sink.Display;
import adams.flow.source.Start;
import adams.flow.source.StringConstants;
import adams.flow.standalone.CallableActors;
import junit.framework.Test;
import junit.framework.TestSuite;

import java.util.ArrayList;
import java.util.List;

/**
 * Test for CloseCallableDisplay actor.
 *
 * @author fracpete
 * @author adams.core.option.FlowJUnitTestProducer (code generator)
 * @version $Revision$
 */
public class CloseCallableDisplayTest
  extends AbstractFlowTest {

  /**
   * Initializes the test.
   *
   * @param name	the name of the test
   */
  public CloseCallableDisplayTest(String name) {
    super(name);
  }

  /**
   * 
   * Returns a test suite.
   *
   * @return		the test suite
   */
  public static Test suite() {
    return new TestSuite(CloseCallableDisplayTest.class);
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
      NullWriter nullwriter = new NullWriter();
      display.setWriter(nullwriter);

      actors2.add(display);
      callableactors.setActors(actors2.toArray(new Actor[0]));

      actors.add(callableactors);

      // Flow.Start
      Start start = new Start();
      actors.add(start);

      // Flow.Trigger
      Trigger trigger = new Trigger();
      List<Actor> actors3 = new ArrayList<>();

      // Flow.Trigger.StringConstants
      StringConstants stringconstants = new StringConstants();
      argOption = (AbstractArgumentOption) stringconstants.getOptionManager().findByProperty("strings");
      List<BaseString> strings = new ArrayList<>();
      strings.add((BaseString) argOption.valueOf("1"));
      strings.add((BaseString) argOption.valueOf("2"));
      stringconstants.setStrings(strings.toArray(new BaseString[0]));
      StringToString stringtostring = new StringToString();
      stringconstants.setConversion(stringtostring);

      actors3.add(stringconstants);

      // Flow.Trigger.CallableSink
      CallableSink callablesink = new CallableSink();
      argOption = (AbstractArgumentOption) callablesink.getOptionManager().findByProperty("callableName");
      callablesink.setCallableName((CallableActorReference) argOption.valueOf("Display"));
      actors3.add(callablesink);
      trigger.setActors(actors3.toArray(new Actor[0]));

      actors.add(trigger);

      // Flow.CloseCallableDisplay
      CloseCallableDisplay closecallabledisplay = new CloseCallableDisplay();
      argOption = (AbstractArgumentOption) closecallabledisplay.getOptionManager().findByProperty("callableName");
      closecallabledisplay.setCallableName((CallableActorReference) argOption.valueOf("Display"));
      actors.add(closecallabledisplay);
      flow.setActors(actors.toArray(new Actor[0]));

      NullListener nulllistener = new NullListener();
      flow.setFlowExecutionListener(nulllistener);

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

