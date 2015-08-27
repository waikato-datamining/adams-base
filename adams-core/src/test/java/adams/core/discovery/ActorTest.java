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

/**
 * ActorTest.java
 * Copyright (C) 2015 University of Waikato, Hamilton, NZ
 */

package adams.core.discovery;

import adams.env.Environment;
import adams.flow.control.Flow;
import adams.flow.control.Tee;
import adams.flow.sink.Display;
import adams.flow.sink.Null;
import adams.flow.source.Start;
import adams.flow.transformer.PassThrough;
import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * Tests the Actor discovery handler. Use the following to run from command-line:<br>
 * adams.core.discovery.ActorTest
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class ActorTest
  extends AbstractDiscoveryHandlerTestCase {

  /**
   * Constructs the test case. Called by subclasses.
   *
   * @param name the name of the test
   */
  public ActorTest(String name) {
    super(name);
  }

  /**
   * Returns the objects to use in the regression test.
   *
   * @return		the objects
   */
  @Override
  protected Object[] getRegressionObjects() {
    Flow flow = new Flow();
    flow.add(new Start());
    flow.add(new PassThrough());
    Tee tee = new Tee();
    flow.add(tee);
    {
      tee.add(new PassThrough());
      tee.add(new Null());
    }
    flow.add(new Display());

    return new Object[]{flow};
  }

  /**
   * Returns the setups to use in the regression test.
   *
   * @return		the setups
   */
  @Override
  protected AbstractDiscoveryHandler[] getRegressionSetups() {
    return new AbstractDiscoveryHandler[]{
      new Actor()
    };
  }

  /**
   * Returns the test suite.
   *
   * @return		the suite
   */
  public static Test suite() {
    return new TestSuite(ActorTest.class);
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
