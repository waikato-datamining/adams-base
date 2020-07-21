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
 * HasVariableValueTest.java
 * Copyright (C) 2020 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.condition.bool;

import adams.core.VariableName;
import adams.env.Environment;
import adams.flow.control.Flow;
import adams.flow.core.AbstractActor;
import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * Tests the 'HasVariableValue' boolean condition.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public class HasVariableValueTest
  extends AbstractBooleanConditionTestCase {

  /**
   * Constructs the test case. Called by subclasses.
   *
   * @param name 	the name of the test
   */
  public HasVariableValueTest(String name) {
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
    Flow	flow3;

    flow1 = new Flow();
    flow2 = new Flow();
    flow2.getVariables().set("hello", "world");
    flow3 = new Flow();
    flow3.getVariables().set("hello", "you");
    return new AbstractActor[]{
      flow1,
      flow2,
      flow3,
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
    HasVariableValue[]	result;

    result    = new HasVariableValue[3];
    result[0] = new HasVariableValue();
    result[1] = new HasVariableValue();
    result[1].setVariableName(new VariableName("hello"));
    result[1].setValue("world");
    result[2] = new HasVariableValue();
    result[2].setVariableName(new VariableName("hello"));
    result[2].setValue("world");

    return result;
  }

  /**
   * Returns a test suite.
   *
   * @return		the test suite
   */
  public static Test suite() {
    return new TestSuite(HasVariableValueTest.class);
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
