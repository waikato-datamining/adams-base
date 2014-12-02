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
 * RandomNumberExpressionTest.java
 * Copyright (C) 2013 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.condition.bool;

import junit.framework.Test;
import junit.framework.TestSuite;
import adams.env.Environment;
import adams.flow.control.Flow;
import adams.flow.core.AbstractActor;
import adams.parser.BooleanExpressionText;

/**
 * Tests the 'RandomNumberExpression' boolean condition.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class RandomNumberExpressionTest
  extends AbstractBooleanConditionTestCase {

  /**
   * Constructs the test case. Called by subclasses.
   *
   * @param name 	the name of the test
   */
  public RandomNumberExpressionTest(String name) {
    super(name);
  }
  
  /**
   * Returns the owning actors to use in the regression test (one per regression setup).
   *
   * @return		the owners (not all conditions might need owners)
   */
  @Override
  protected AbstractActor[] getRegressionOwners() {
    AbstractActor[]	result;
    
    result    = new AbstractActor[4];
    result[0] = new Flow();
    result[1] = new Flow();
    result[2] = new Flow();
    result[3] = new Flow();
    
    return result;
  }

  /**
   * Returns the input data to use in the regression test (one per regression setup).
   *
   * @return		the input data
   */
  @Override
  protected Object[] getRegressionInputs() {
    return new Object[]{
	"blah",
	"blah",
	"blah",
	"blah"
    };
  }

  /**
   * Returns the setups to use in the regression test.
   *
   * @return		the setups
   */
  @Override
  protected AbstractBooleanCondition[] getRegressionSetups() {
    RandomNumberExpression[]	result;
    
    result    = new RandomNumberExpression[4];
    result[0] = new RandomNumberExpression();
    result[1] = new RandomNumberExpression();
    result[1].setExpression(new BooleanExpressionText("true"));
    result[2] = new RandomNumberExpression();
    result[2].setExpression(new BooleanExpressionText("1 = 2"));
    result[3] = new RandomNumberExpression();
    result[3].setExpression(new BooleanExpressionText("X < 1000"));
    
    return result;
  }

  /**
   * Returns a test suite.
   *
   * @return		the test suite
   */
  public static Test suite() {
    return new TestSuite(RandomNumberExpressionTest.class);
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
