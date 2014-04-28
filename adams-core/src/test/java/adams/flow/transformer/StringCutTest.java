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
 * StringCutTest.java
 * Copyright (C) 2009-2012 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.transformer;

import junit.framework.Test;
import junit.framework.TestSuite;
import adams.core.Index;
import adams.core.base.BaseString;
import adams.env.Environment;
import adams.flow.AbstractFlowTest;
import adams.flow.control.Flow;
import adams.flow.core.AbstractActor;
import adams.flow.core.Token;
import adams.flow.sink.Null;
import adams.flow.source.StringConstants;

/**
 * Tests the StringCut actor.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class StringCutTest
  extends AbstractFlowTest {

  /**
   * Initializes the test.
   *
   * @param name	the name of the test
   */
  public StringCutTest(String name) {
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
	new BaseString("A:BCde"),
	new BaseString("ab:cde"),
	new BaseString("ABC:DE"),
	new BaseString("abcD:E")
    });

    StringCut actor = new StringCut();
    actor.setFieldDelimiter(":");
    actor.setFieldIndex(new Index("2"));

    Null nul = new Null();

    Flow flow = new Flow();
    flow.setActors(new AbstractActor[]{con, actor, nul});

    return flow;
  }

  /**
   * Performs a test on the actor.
   *
   * @param strIn	the input strings
   * @param strOut	the expected output strings
   * @param start	the starting position (if using character positions)
   * @param end		the end position (if using character positions)
   * @param delim	the delimiter (if using non-character positions)
   * @param field	the field index (1-based; if using non-character positions)
   */
  protected void performTest(String[] strIn, String[] strOut, int start, int end, String delim, int field) {
    StringCut actor = new StringCut();
    if ((start > -1) && (end > -1)) {
      actor.setCharacterStartPos(start);
      actor.setCharacterEndPos(end);
      actor.setUseCharacterPos(true);
    }
    else {
      actor.setFieldDelimiter(delim);
      actor.setFieldIndex(new Index("" + field));
      actor.setUseCharacterPos(false);
    }
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
   * Tests character positions.
   */
  public void testActorCharPositions() {
    String[] strIn = new String[]{"abcde", "hello", "world", "bugger"};
    String[] strOut = new String[]{"cde", "llo", "rld", "gge"};
    performTest(strIn, strOut, 3, 5, "", -1);
  }

  /**
   * Tests fields.
   */
  public void testActorFields() {
    String[] strIn = new String[]{"ab:cde", "hell:o", "w:orld", ":bugger", "blah:"};
    String[] strOut = new String[]{"cde", "o", "orld", "bugger", ""};
    performTest(strIn, strOut, -1, -1, ":", 2);
  }

  /**
   * Returns a test suite.
   *
   * @return		the test suite
   */
  public static Test suite() {
    return new TestSuite(StringCutTest.class);
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
