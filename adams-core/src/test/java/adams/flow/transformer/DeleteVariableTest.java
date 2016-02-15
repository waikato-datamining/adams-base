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
 * DeleteVariableTest.java
 * Copyright (C) 2010 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.transformer;

import junit.framework.Test;
import junit.framework.TestSuite;
import adams.core.VariableName;
import adams.core.Variables;
import adams.core.base.BaseString;
import adams.env.Environment;
import adams.flow.AbstractFlowTest;
import adams.flow.control.Flow;
import adams.flow.core.Actor;
import adams.flow.source.StringConstants;
import adams.flow.transformer.SetVariable;

/**
 * Tests the DeleteVariable actor.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class DeleteVariableTest
  extends AbstractFlowTest {

  /** the name of the variable used in the tests. */
  public final static String VARIABLE_NAME = "blah";

  /**
   * Initializes the test.
   *
   * @param name	the name of the test
   */
  public DeleteVariableTest(String name) {
    super(name);
  }

  /**
   * Dummy only.
   *
   * @return 		empty flow
   */
  public Actor getActor() {
    return new Flow();
  }

  /**
   * Tests the removal of a variable.
   */
  public void testRemoval() {
    StringConstants sc = new StringConstants();
    sc.setStrings(new BaseString[]{
	new BaseString("dummy")
    });

    SetVariable sv = new SetVariable();
    sv.setVariableName(new VariableName(VARIABLE_NAME));

    DeleteVariable dv = new DeleteVariable();
    dv.setVariableName(new VariableName(VARIABLE_NAME));

    Flow flow = new Flow();
    flow.setActors(new Actor[]{
	sc,
	sv,
	dv
    });

    String result = flow.setUp();
    assertNull("Failed to setUp flow", result);

    result = flow.execute();
    assertNull("Failed to execute flow", result);

    assertNull("Failed to delete variable", flow.getVariables().get(VARIABLE_NAME));

    flow.destroy();
  }

  /**
   * Returns a test suite.
   *
   * @return		the test suite
   */
  public static Test suite() {
    return new TestSuite(DeleteVariableTest.class);
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
