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
 * ProgressBarTest.java
 * Copyright (C) 2013-2014 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.sink;

import junit.framework.Test;
import junit.framework.TestSuite;
import adams.core.option.AbstractArgumentOption;
import adams.env.Environment;
import adams.flow.AbstractFlowTest;
import adams.flow.control.Flow;
import adams.flow.core.AbstractActor;

/**
 * Test for ProgressBar actor.
 *
 * @author fracpete
 * @author adams.core.option.FlowJUnitTestProducer (code generator)
 * @version $Revision$
 */
public class ProgressBarTest
  extends AbstractFlowTest {

  /**
   * Initializes the test.
   *
   * @param name	the name of the test
   */
  public ProgressBarTest(String name) {
    super(name);
  }

  /**
   * 
   * Returns a test suite.
   *
   * @return		the test suite
   */
  public static Test suite() {
    return new TestSuite(ProgressBarTest.class);
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
      adams.flow.standalone.CallableActors globalactors2 = new adams.flow.standalone.CallableActors();
      argOption = (AbstractArgumentOption) globalactors2.getOptionManager().findByProperty("actors");
      adams.flow.core.AbstractActor[] actors3 = new adams.flow.core.AbstractActor[1];

      // Flow.CallableActors.ProgressBar
      adams.flow.sink.ProgressBar progressbar4 = new adams.flow.sink.ProgressBar();
      argOption = (AbstractArgumentOption) progressbar4.getOptionManager().findByProperty("writer");
      adams.gui.print.NullWriter nullwriter6 = new adams.gui.print.NullWriter();
      progressbar4.setWriter(nullwriter6);

      argOption = (AbstractArgumentOption) progressbar4.getOptionManager().findByProperty("maximum");
      progressbar4.setMaximum((Double) argOption.valueOf("20.0"));
      actors3[0] = progressbar4;
      globalactors2.setActors(actors3);

      actors1[0] = globalactors2;

      // Flow.ForLoop
      adams.flow.source.ForLoop forloop8 = new adams.flow.source.ForLoop();
      argOption = (AbstractArgumentOption) forloop8.getOptionManager().findByProperty("loopUpper");
      forloop8.setLoopUpper((Integer) argOption.valueOf("20"));
      actors1[1] = forloop8;

      // Flow.Sleep
      adams.flow.control.Sleep sleep10 = new adams.flow.control.Sleep();
      argOption = (AbstractArgumentOption) sleep10.getOptionManager().findByProperty("interval");
      sleep10.setInterval((Integer) argOption.valueOf("100"));
      actors1[2] = sleep10;

      // Flow.GlobalSink
      adams.flow.sink.CallableSink globalsink12 = new adams.flow.sink.CallableSink();
      argOption = (AbstractArgumentOption) globalsink12.getOptionManager().findByProperty("callableName");
      globalsink12.setCallableName((adams.flow.core.CallableActorReference) argOption.valueOf("ProgressBar"));
      actors1[3] = globalsink12;
      flow.setActors(actors1);

      argOption = (AbstractArgumentOption) flow.getOptionManager().findByProperty("flowExecutionListener");
      adams.flow.execution.NullListener nulllistener15 = new adams.flow.execution.NullListener();
      flow.setFlowExecutionListener(nulllistener15);

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

