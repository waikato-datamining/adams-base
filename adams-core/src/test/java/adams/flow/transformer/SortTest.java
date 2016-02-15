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
 * SortTest.java
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

/**
 * Tests the Sort actor.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class SortTest
  extends AbstractFlowTest {

  /**
   * Initializes the test.
   *
   * @param name	the name of the test
   */
  public SortTest(String name) {
    super(name);
  }

  /**
   * Used to create an instance of a specific actor.
   *
   * @return a suitably configured <code>Flow</code>
   */
  public Actor getActor() {
    StringConstants con = new StringConstants();
    con.setStrings(new BaseString[]{
	new BaseString("ABCde"),
	new BaseString("abcde"),
	new BaseString("ABCDE"),
	new BaseString("abcDE")
    });

    SequenceToArray s2a = new SequenceToArray();
    s2a.setArrayLength(4);

    Sort actor = new Sort();

    Null nul = new Null();

    Flow flow = new Flow();
    flow.setActors(new Actor[]{con, s2a, actor, nul});

    return flow;
  }

  /**
   * Performs a test on the actor.
   *
   * @param strIn	the input strings
   * @param strOut	the expected output strings
   * @param reverse	whether to reverse the sorting
   */
  protected void performTest(String[] strIn, String[] strOut, boolean reverse) {
    Sort actor = new Sort();
    actor.setReverse(reverse);
    assertNull("problem with setUp()", actor.setUp());

    actor.input(new Token(strIn));
    assertNull("problem with execute()", actor.execute());

    Token out = actor.output();
    assertNotNull("problem with output()", out);

    String[] strOutActor = (String[]) out.getPayload();
    assertEquals("array length differs", strOut.length, strOutActor.length);

    for (int i = 0; i < strOut.length; i++)
      assertEquals("values differ", strOut[i], strOutActor[i]);

    actor.wrapUp();
    actor.cleanUp();
  }

  /**
   * Tests default sorting.
   */
  public void testActorDefault() {
    String[] strIn = new String[]{"abc", "ACB", "ABC", "adef"};
    String[] strOut = new String[]{"ABC", "ACB", "abc", "adef"};
    performTest(strIn, strOut, false);
  }

  /**
   * Tests reverse sorting.
   */
  public void testActorReverse() {
    String[] strIn = new String[]{"abc", "ACB", "ABC", "adef"};
    String[] strOut = new String[]{"adef", "abc", "ACB", "ABC"};
    performTest(strIn, strOut, true);
  }

  /**
   * Returns a test suite.
   *
   * @return		the test suite
   */
  public static Test suite() {
    return new TestSuite(SortTest.class);
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
