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
 * HasVariableTest.java
 * Copyright (C) 2014 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.condition.bool;

import junit.framework.Test;
import junit.framework.TestSuite;
import adams.core.VariableName;
import adams.env.Environment;
import adams.flow.control.Flow;
import adams.flow.core.AbstractActor;

/**
 * Tests the 'HasVariable' boolean condition.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision: 7337 $
 */
public class HasVariableTest
  extends AbstractBooleanConditionTestCase {

  /**
   * Constructs the test case. Called by subclasses.
   *
   * @param name 	the name of the test
   */
  public HasVariableTest(String name) {
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
    flow2.getVariables().set("hello", "world");
    return new AbstractActor[]{
	flow1,
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
    };
  }

  /**
   * Returns the setups to use in the regression test.
   *
   * @return		the setups
   */
  @Override
  protected AbstractBooleanCondition[] getRegressionSetups() {
    HasVariable[]	result;
    
    result    = new HasVariable[2];
    result[0] = new HasVariable();
    result[1] = new HasVariable();
    result[1].setVariableName(new VariableName("hello"));
    
    return result;
  }

  /**
   * Returns a test suite.
   *
   * @return		the test suite
   */
  public static Test suite() {
    return new TestSuite(HasVariableTest.class);
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
