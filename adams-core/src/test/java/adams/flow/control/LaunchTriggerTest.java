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
 * LaunchTriggerTest.java
 * Copyright (C) 2024 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.control;

import adams.core.VariableName;
import adams.core.option.AbstractArgumentOption;
import adams.data.io.output.NullWriter;
import adams.env.Environment;
import adams.flow.AbstractFlowTest;
import adams.flow.control.ScopeHandler.ScopeHandling;
import adams.flow.control.flowrestart.NullManager;
import adams.flow.core.AbstractActor;
import adams.flow.core.Actor;
import adams.flow.core.CallableActorReference;
import adams.flow.core.displaytype.Default;
import adams.flow.execution.NullListener;
import adams.flow.sink.CallableSink;
import adams.flow.sink.Display;
import adams.flow.source.ForLoop;
import adams.flow.source.MathExpression;
import adams.flow.standalone.CallableActors;
import adams.flow.transformer.SetVariable;
import adams.parser.MathematicalExpressionText;
import junit.framework.Test;
import junit.framework.TestSuite;

import java.util.ArrayList;
import java.util.List;

/**
 * Test for LaunchTrigger actor.
 *
 * @author fracpete
 * @author adams.core.option.FlowJUnitTestProducer (code generator)
 */
public class LaunchTriggerTest
  extends AbstractFlowTest {

  /**
   * Initializes the test.
   *
   * @param name	the name of the test
   */
  public LaunchTriggerTest(String name) {
    super(name);
  }

  /**
   * 
   * Returns a test suite.
   *
   * @return		the test suite
   */
  public static Test suite() {
    return new TestSuite(LaunchTriggerTest.class);
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

      // Flow.ForLoop
      ForLoop forloop = new ForLoop();
      actors.add(forloop);

      // Flow.SetVariable
      SetVariable setvariable = new SetVariable();
      argOption = (AbstractArgumentOption) setvariable.getOptionManager().findByProperty("variableName");
      setvariable.setVariableName((VariableName) argOption.valueOf("i"));
      actors.add(setvariable);

      // Flow.LaunchTrigger
      LaunchTrigger launchtrigger = new LaunchTrigger();
      List<Actor> actors3 = new ArrayList<>();

      // Flow.LaunchTrigger.MathExpression
      MathExpression mathexpression = new MathExpression();
      argOption = (AbstractArgumentOption) mathexpression.getOptionManager().findByProperty("expression");
      mathexpression.setExpression((MathematicalExpressionText) argOption.valueOf("@{i} * rand(@{i})"));
      actors3.add(mathexpression);

      // Flow.LaunchTrigger.CallableSink
      CallableSink callablesink = new CallableSink();
      argOption = (AbstractArgumentOption) callablesink.getOptionManager().findByProperty("callableName");
      callablesink.setCallableName((CallableActorReference) argOption.valueOf("Display"));
      actors3.add(callablesink);
      launchtrigger.setActors(actors3.toArray(new Actor[0]));

      argOption = (AbstractArgumentOption) launchtrigger.getOptionManager().findByProperty("scopeHandlingVariables");
      launchtrigger.setScopeHandlingVariables((ScopeHandling) argOption.valueOf("COPY"));
      actors.add(launchtrigger);
      flow.setActors(actors.toArray(new Actor[0]));

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

