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
 * RoundTest.java
 * Copyright (C) 2009 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.transformer;

import junit.framework.Test;
import junit.framework.TestSuite;
import adams.core.base.BaseString;
import adams.env.Environment;
import adams.flow.AbstractFlowTest;
import adams.flow.control.Flow;
import adams.flow.core.Actor;
import adams.flow.core.Token;
import adams.flow.sink.Null;
import adams.flow.source.StringConstants;
import adams.flow.transformer.Convert;
import adams.data.RoundingType;
import adams.data.conversion.StringToDouble;

/**
 * Tests the Round actor.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class RoundTest
  extends AbstractFlowTest {

  /**
   * Initializes the test.
   *
   * @param name	the name of the test
   */
  public RoundTest(String name) {
    super(name);
  }

  /**
   * Used to create an instance of a specific actor.
   *
   * @return a suitably configured <code>Flow</code>
   */
  public Actor getActor() {
    StringConstants sc = new StringConstants();
    sc.setStrings(new BaseString[]{
	new BaseString("1.0"),
	new BaseString("1"),
	new BaseString("-10.3"),
	new BaseString("0.987")
    });

    StringToDouble s2d = new StringToDouble();
    Convert con = new Convert();
    con.setConversion(s2d);
    Round actor = new Round();

    Null nul = new Null();

    Flow flow = new Flow();
    flow.setActors(new Actor[]{sc, con, actor, nul});

    return flow;
  }

  /**
   * performs a test.
   *
   * @param dblIn	the double to convert
   * @param intOut	the expected int
   * @param action	the type of action to perform
   */
  protected void performTest(Double dblIn, Integer intOut, RoundingType action) {
    Round actor = new Round();
    actor.setAction(action);
    assertNull("problem with setUp()", actor.setUp());

    actor.input(new Token(dblIn));
    assertNull("problem with execute()", actor.execute());

    Token out = actor.output();
    assertNotNull("problem with output()", out);

    Integer intOutActor = (Integer) out.getPayload();
    assertEquals("values differ", intOut, intOutActor);

    actor.wrapUp();
    actor.cleanUp();
  }

  /**
   * Tests the actor by itself.
   */
  public void testActorTypical() {
    Double[] dblIn = new Double[]{123D, 1.01, -3.8, 1001.00304};
    for (RoundingType action: RoundingType.values()) {
      Integer[] intOut = new Integer[dblIn.length];
      for (int i = 0; i < intOut.length; i++) {
	switch (action) {
	  case ROUND:
	    intOut[i] = (int) Math.round(dblIn[i]);
	    break;
	  case FLOOR:
	    intOut[i] = (int) Math.floor(dblIn[i]);
	    break;
	  case CEILING:
	    intOut[i] = (int) Math.ceil(dblIn[i]);
	    break;
	  default:
	    throw new IllegalStateException("Unhandled action: " + action);
	}
      }
      for (int i = 0; i < dblIn.length; i++)
	performTest(dblIn[i], intOut[i], action);
    }
  }

  /**
   * Returns a test suite.
   *
   * @return		the test suite
   */
  public static Test suite() {
    return new TestSuite(RoundTest.class);
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
