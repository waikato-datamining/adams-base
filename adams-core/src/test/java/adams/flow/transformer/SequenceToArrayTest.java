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
 * SequenceToArrayTest.java
 * Copyright (C) 2009 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.transformer;

import junit.framework.Test;
import junit.framework.TestSuite;
import adams.core.base.BaseString;
import adams.data.conversion.StringToDouble;
import adams.env.Environment;
import adams.flow.AbstractFlowTest;
import adams.flow.control.Flow;
import adams.flow.core.Actor;
import adams.flow.core.Token;
import adams.flow.sink.Null;
import adams.flow.source.StringConstants;

/**
 * Tests the SequenceToArray actor.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class SequenceToArrayTest
  extends AbstractFlowTest {

  /**
   * Initializes the test.
   *
   * @param name	the name of the test
   */
  public SequenceToArrayTest(String name) {
    super(name);
  }

  /**
   * Used to create an instance of a specific actor.
   *
   * @return a suitably configured <code>Flow</code>
   */
  @Override
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

    Null nul = new Null();

    Flow flow = new Flow();
    flow.setActors(new Actor[]{sc, con, s2a, nul});

    return flow;
  }

  /**
   * performs a test.
   *
   * @param objIn	the "sequence" to turn into an array
   */
  protected void performTest(Object[] objIn) {
    SequenceToArray actor = new SequenceToArray();
    actor.setArrayLength(objIn.length);
    assertNull("problem with setUp()", actor.setUp());

    for (int i = 0; i < objIn.length; i++) {
      actor.input(new Token(objIn[i]));
      assertNull("problem with execute()", actor.execute());
    }

    Token out = actor.output();
    assertNotNull("problem with output()", out);
    Object[] array = (Object[]) out.getPayload();

    for (int i = 0; i < objIn.length; i++)
      assertEquals("values at #" + i + " differ", objIn[i], array[i]);

    actor.wrapUp();
    actor.cleanUp();
  }

  /**
   * Tests the actor by itself. Using doubles.
   */
  public void testActorDouble() {
    Double[] in = new Double[]{123.0, 1.0, -3.0, 1001.0};
    for (int i = 0; i < in.length; i++)
      performTest(in);
  }

  /**
   * Tests the actor by itself. Using integers.
   */
  public void testActorInteger() {
    Integer[] in = new Integer[]{123, 1, -3, 1001};
    for (int i = 0; i < in.length; i++)
      performTest(in);
  }

  /**
   * Tests the actor by itself. Using strings.
   */
  public void testActorString() {
    String[] in = new String[]{"123", "1", "-3", "1001"};
    for (int i = 0; i < in.length; i++)
      performTest(in);
  }

  /**
   * Returns a test suite.
   *
   * @return		the test suite
   */
  public static Test suite() {
    return new TestSuite(SequenceToArrayTest.class);
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
