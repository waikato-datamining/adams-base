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
 * MaxTest.java
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
import adams.data.conversion.StringToDouble;

/**
 * Tests the Max actor.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class MaxTest
  extends AbstractFlowTest {

  /**
   * Initializes the test.
   *
   * @param name	the name of the test
   */
  public MaxTest(String name) {
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
	new BaseString("10.1"),
	new BaseString("1.3"),
	new BaseString("-10.9"),
	new BaseString("987.001")
    });

    StringToDouble s2d = new StringToDouble();
    Convert con = new Convert();
    con.setConversion(s2d);

    SequenceToArray s2a = new SequenceToArray();
    s2a.setArrayLength(4);

    Max actor = new Max();

    Null nul = new Null();

    Flow flow = new Flow();
    flow.setActors(new Actor[]{sc, con, s2a, actor, nul});

    return flow;
  }

  /**
   * performs a test.
   *
   * @param dblIn	the double to convert
   * @param intOut	the expected integer (if retrieving index, null otherwise)
   * @param dblOut	the expected double (if retrieving value, null otherwise)
   */
  protected void performTest(Double[] dblIn, Integer intOut, Double dblOut) {
    Max actor = new Max();
    actor.setReturnIndex(intOut != null);
    assertNull("problem with setUp()", actor.setUp());

    actor.input(new Token(dblIn));
    assertNull("problem with execute()", actor.execute());

    Token out = actor.output();
    assertNotNull("problem with output()", out);

    if (intOut != null) {
      Integer intOutActor = (Integer) out.getPayload();
      assertEquals("values differ", intOut, intOutActor);
    }
    else {
      Double dblOutActor = (Double) out.getPayload();
      assertEquals("values differ", dblOut, dblOutActor);
    }

    actor.wrapUp();
    actor.cleanUp();
  }

  /**
   * Tests the actor by itself. Retrieves the index.
   */
  public void testActorIndex() {
    Double[] dblIn = new Double[]{123.0, 1.0, -3.0, 1001.0};
    for (int i = 0; i < dblIn.length; i++)
      performTest(dblIn, 3, null);
  }

  /**
   * Tests the actor by itself. Retrieves the value.
   */
  public void testActorValue() {
    Double[] dblIn = new Double[]{123.0, 1.0, -3.0, 1001.0};
    for (int i = 0; i < dblIn.length; i++)
      performTest(dblIn, null, 1001.0);
  }

  /**
   * Returns a test suite.
   *
   * @return		the test suite
   */
  public static Test suite() {
    return new TestSuite(MaxTest.class);
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
