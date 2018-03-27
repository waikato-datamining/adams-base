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
 * InitPublishSubscribeTest.java
 * Copyright (C) 2018 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.standalone;

import adams.env.Environment;
import adams.flow.AbstractFlowTest;
import adams.flow.control.Flow;
import adams.flow.control.StorageName;
import adams.flow.core.Actor;
import adams.flow.sink.Publish;
import adams.flow.source.ForLoop;
import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * Tests the InitPublishSubscribe standalone.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public class InitPublishSubscribeTest
  extends AbstractFlowTest {

  /**
   * Initializes the test.
   *
   * @param name	the name of the test
   */
  public InitPublishSubscribeTest(String name) {
    super(name);
  }

  /**
   * Used to create an instance of a specific actor.
   *
   * @return a suitably configured <code>Actor</code> value
   */
  public Actor getActor() {
    Flow flow = new Flow();

    InitPublishSubscribe init = new InitPublishSubscribe();
    init.setStorageNames(new StorageName[]{new StorageName("pubsub1")});
    flow.add(init);

    ForLoop loop = new ForLoop();
    flow.add(loop);

    Publish pub = new Publish();
    pub.setStorageName(new StorageName("pubsub1"));
    flow.add(pub);

    return flow;
  }

  /**
   * Returns a test suite.
   *
   * @return		the test suite
   */
  public static Test suite() {
    return new TestSuite(InitPublishSubscribeTest.class);
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
