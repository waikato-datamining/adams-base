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
 * ProgrammaticTokenListenerTest.java
 * Copyright (C) 2019 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.transformer;

import adams.env.Environment;
import adams.flow.AbstractFlowTest;
import adams.flow.control.Flow;
import adams.flow.core.Actor;
import adams.flow.core.Token;
import adams.flow.source.ForLoop;
import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * Test for ProgrammaticTokenListener actor.
 *
 * @author fracpete
 */
public class ProgrammaticTokenListenerTest
  extends AbstractFlowTest {

  /**
   * Initializes the test.
   *
   * @param name	the name of the test
   */
  public ProgrammaticTokenListenerTest(String name) {
    super(name);
  }

  /**
   * 
   * Returns a test suite.
   *
   * @return		the test suite
   */
  public static Test suite() {
    return new TestSuite(ProgrammaticTokenListenerTest.class);
  }

  /**
   * Used to create an instance of a specific actor.
   *
   * @return a suitably configured <code>Actor</code> value
   */
  public Actor getActor() {
    Flow flow = new Flow();

    ForLoop loop = new ForLoop();
    flow.add(loop);

    ProgrammaticTokenListener listener = new ProgrammaticTokenListener();
    listener.addTokenListener((Token token) -> System.out.println("[WIRE] " + token.getPayload()));
    flow.add(listener);
    
    return flow;
  }

  /**
   * Runs the test from commandline.
   *
   * @param args	ignored
   */
  public static void main(String[] args) {
    Environment.setEnvironmentClass(Environment.class);
    runTest(suite());
  }
}

