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
 * StopwatchTest.java
 * Copyright (C) 2011 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.standalone;

import adams.env.Environment;
import adams.core.option.AbstractArgumentOption;
import adams.core.option.OptionUtils;
import junit.framework.Test;
import junit.framework.TestSuite;
import adams.flow.core.Actor;
import adams.flow.control.Flow;
import adams.flow.source.Start;
import adams.flow.AbstractFlowTest;

/**
 * Test for Stopwatch actor.
 *
 * @author fracpete
 * @author adams.core.option.FlowJUnitTestProducer (code generator)
 * @version $Revision$
 */
public class StopwatchTest
  extends AbstractFlowTest {

  /**
   * Initializes the test.
   *
   * @param name	the name of the test
   */
  public StopwatchTest(String name) {
    super(name);
  }

  /**
   * 
   * Returns a test suite.
   *
   * @return		the test suite
   */
  public static Test suite() {
    return new TestSuite(StopwatchTest.class);
  }

  /**
   * Used to create an instance of a specific actor.
   *
   * @return a suitably configured <code>Actor</code> value
   */
  public Actor getActor() {
    AbstractArgumentOption    argOption;
    
    Flow flow = new Flow();
    
    try {
      argOption = (AbstractArgumentOption) flow.getOptionManager().findByProperty("actors");
      adams.flow.core.Actor[] tmp1 = new adams.flow.core.Actor[3];
      adams.flow.standalone.Stopwatch tmp2 = new adams.flow.standalone.Stopwatch();
      argOption = (AbstractArgumentOption) tmp2.getOptionManager().findByProperty("updateInterval");
      tmp2.setUpdateInterval((Integer) argOption.valueOf("300"));

      tmp2.setShowSeconds(true);

      tmp1[0] = tmp2;
      adams.flow.control.Sleep tmp4 = new adams.flow.control.Sleep();
      argOption = (AbstractArgumentOption) tmp4.getOptionManager().findByProperty("interval");
      tmp4.setInterval((Integer) argOption.valueOf("3000"));

      tmp1[1] = new Start();
      
      tmp1[2] = tmp4;
      flow.setActors(tmp1);

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

