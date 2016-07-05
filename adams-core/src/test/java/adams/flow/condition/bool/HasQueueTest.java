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
 * HasQueueTest.java
 * Copyright (C) 2016 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.condition.bool;

import adams.env.Environment;
import adams.flow.control.Flow;
import adams.flow.control.StorageName;
import adams.flow.control.StorageQueueHandler;
import adams.flow.core.AbstractActor;
import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * Tests the 'HasQueue' boolean condition.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision: 7337 $
 */
public class HasQueueTest
  extends AbstractBooleanConditionTestCase {

  /**
   * Constructs the test case. Called by subclasses.
   *
   * @param name 	the name of the test
   */
  public HasQueueTest(String name) {
    super(name);
  }

  /**
   * Returns the owning actors to use in the regression test (one per regression setup).
   *
   * @return		the owners (not all conditions might need owners)
   */
  @Override
  protected AbstractActor[] getRegressionOwners() {
    Flow	flow1;
    Flow	flow2;

    flow1 = new Flow();
    flow2 = new Flow();
    flow2.getStorageHandler().getStorage().put(new StorageName("queue1"), new StorageQueueHandler("queue1"));
    return new AbstractActor[]{
      flow1,
      flow2,
      flow2
    };
  }

  /**
   * Returns the input data to use in the regression test (one per regression setup).
   *
   * @return		the input data
   */
  @Override
  protected Object[] getRegressionInputs() {
    return new Object[]{
      "1",
      "2",
      "3",
    };
  }

  /**
   * Returns the setups to use in the regression test.
   *
   * @return		the setups
   */
  @Override
  protected AbstractBooleanCondition[] getRegressionSetups() {
    HasQueue[]	result;

    result    = new HasQueue[3];
    result[0] = new HasQueue();
    result[1] = new HasQueue();
    result[1].setStorageName(new StorageName("queue1"));
    result[2] = new HasQueue();
    result[2].setStorageName(new StorageName("queue2"));

    return result;
  }

  /**
   * Returns a test suite.
   *
   * @return		the test suite
   */
  public static Test suite() {
    return new TestSuite(HasQueueTest.class);
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
