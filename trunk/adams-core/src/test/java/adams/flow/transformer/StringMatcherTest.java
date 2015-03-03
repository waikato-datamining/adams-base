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
 * StringMatcherTest.java
 * Copyright (C) 2009 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.transformer;

import junit.framework.Test;
import junit.framework.TestSuite;
import adams.core.base.BaseRegExp;
import adams.core.base.BaseString;
import adams.env.Environment;
import adams.flow.AbstractFlowTest;
import adams.flow.control.Flow;
import adams.flow.core.AbstractActor;
import adams.flow.core.Token;
import adams.flow.sink.Null;
import adams.flow.source.StringConstants;

/**
 * Tests the StringMatcher actor.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class StringMatcherTest
  extends AbstractFlowTest {

  /**
   * Initializes the test.
   *
   * @param name	the name of the test
   */
  public StringMatcherTest(String name) {
    super(name);
  }

  /**
   * Used to create an instance of a specific actor.
   *
   * @return a suitably configured <code>Flow</code>
   */
  public AbstractActor getActor() {
    StringConstants con = new StringConstants();
    con.setStrings(new BaseString[]{
	new BaseString("ABC"),
	new BaseString("DEF"),
	new BaseString("BCD"),
	new BaseString("CDE")
    });

    StringMatcher actor = new StringMatcher();
    actor.setRegExp(new BaseRegExp("^.*BC.*$"));

    Null nul = new Null();

    Flow flow = new Flow();
    flow.setActors(new AbstractActor[]{con, actor, nul});

    return flow;
  }

  /**
   * Tests the default matching.
   */
  public void testActorDefaultMatching() {
    String[] strIn = new String[]{"ABC", "DEF", "BCD", "CDE"};
    String[] strOut = new String[]{"ABC", "BCD"};

    StringMatcher actor = new StringMatcher();
    actor.setRegExp(new BaseRegExp("^.*BC.*$"));
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
   * Tests the inverse matching.
   */
  public void testActorInverseMatching() {
    String[] strIn = new String[]{"ABC", "DEF", "BCD", "CDE"};
    String[] strOut = new String[]{"DEF", "CDE"};

    StringMatcher actor = new StringMatcher();
    actor.setInvert(true);
    actor.setRegExp(new BaseRegExp("^.*BC.*$"));
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
   * Returns a test suite.
   *
   * @return		the test suite
   */
  public static Test suite() {
    return new TestSuite(StringMatcherTest.class);
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
