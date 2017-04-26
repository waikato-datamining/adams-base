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
 * HasClassTest.java
 * Copyright (C) 2017 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.condition.bool;

import adams.env.Environment;
import adams.flow.core.AbstractActor;
import adams.flow.core.Actor;
import adams.flow.sink.Null;
import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * Tests the 'HasClass' boolean condition.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class HasClassTest
  extends AbstractBooleanConditionTestCase {

  /**
   * Constructs the test case. Called by subclasses.
   *
   * @param name 	the name of the test
   */
  public HasClassTest(String name) {
    super(name);
  }

  /**
   * Returns the owning actors to use in the regression test (one per regression setup).
   *
   * @return		the owners (not all conditions might need owners)
   */
  @Override
  protected AbstractActor[] getRegressionOwners() {
    return new AbstractActor[]{
	null,
	null,
	null
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
	new Null(),
	new Null(),
	new Null(),
    };
  }

  /**
   * Returns the setups to use in the regression test.
   *
   * @return		the setups
   */
  @Override
  protected AbstractBooleanCondition[] getRegressionSetups() {
    HasClass[]	result;
    
    result    = new HasClass[3];
    result[0] = new HasClass();
    result[1] = new HasClass();
    result[1].setClassName(Actor.class.getName());
    result[2] = new HasClass();
    result[2].setClassName("weka.classifier.Weirdo");
    
    return result;
  }

  /**
   * Returns a test suite.
   *
   * @return		the test suite
   */
  public static Test suite() {
    return new TestSuite(HasClassTest.class);
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
