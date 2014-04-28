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
 * StringSanitizerTest.java
 * Copyright (C) 2009 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.transformer;

import junit.framework.Test;
import junit.framework.TestSuite;
import adams.core.base.BaseString;
import adams.env.Environment;
import adams.flow.AbstractFlowTest;
import adams.flow.control.Flow;
import adams.flow.core.AbstractActor;
import adams.flow.core.Token;
import adams.flow.sink.Null;
import adams.flow.source.StringConstants;

/**
 * Tests the StringSanitizer actor.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class StringSanitizerTest
  extends AbstractFlowTest {

  /**
   * Initializes the test.
   *
   * @param name	the name of the test
   */
  public StringSanitizerTest(String name) {
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
	new BaseString("ABCDEF"),
	new BaseString("ABDEF"),
	new BaseString("BCDEF"),
	new BaseString("bcDEF ")
    });

    StringSanitizer actor = new StringSanitizer();
    actor.setAcceptableChars("BC");

    Null nul = new Null();

    Flow flow = new Flow();
    flow.setActors(new AbstractActor[]{con, actor, nul});

    return flow;
  }

  /**
   * Performs the test.
   *
   * @param strIn	the input strings
   * @param strOut	the output strings
   * @param acceptable	the 'acceptable' characters
   * @param replacement	the 'replacement' character
   * @param invert	whether to invert the matching
   */
  protected void performTest(String[] strIn, String[] strOut, String acceptable, String replacement, boolean invert) {
    StringSanitizer actor = new StringSanitizer();
    actor.setAcceptableChars(acceptable);
    actor.setReplacementChar(replacement);
    actor.setInvertMatching(invert);
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
   * Tests the replacement of characters with a replacement character.
   */
  public void testActorReplaceDefault() {
    String[] strIn = new String[]{"ABCDEFBC", "ABDEF", "BCDEFBC", "bcDEFbc"};
    String[] strOut = new String[]{"AB--EFB-", "AB-EF", "B--EFB-", "---EF--"};
    String acceptable = "ABEFGHIJKLMNOPQRSTUVWXYZ";
    String replacement = "-";
    performTest(strIn, strOut, acceptable, replacement, false);
  }

  /**
   * Tests the replacement of characters with a replacement character (inverted).
   */
  public void testActorReplaceInverted() {
    String[] strIn = new String[]{"ABCDEFBC", "ABDEF", "BCDEFBC", "bcDEFbc"};
    String[] strOut = new String[]{"--CD---C", "--D--", "-CD---C", "bcD--bc"};
    String acceptable = "ABEFGHIJKLMNOPQRSTUVWXYZ";
    String replacement = "-";
    performTest(strIn, strOut, acceptable, replacement, true);
  }

  /**
   * Tests the removal of characters.
   */
  public void testActorRemoveDefault() {
    String[] strIn = new String[]{"ABCDEFBC", "ABDEF", "BCDEFBC", "bcDEFbc"};
    String[] strOut = new String[]{"ABEFB", "ABEF", "BEFB", "EF"};
    String acceptable = "ABEFGHIJKLMNOPQRSTUVWXYZ";
    String replacement = "";
    performTest(strIn, strOut, acceptable, replacement, false);
  }

  /**
   * Tests the removal of characters (inverted).
   */
  public void testActorRemovalInverted() {
    String[] strIn = new String[]{"ABCDEFBC", "ABDEF", "BCDEFBC", "bcDEFbc"};
    String[] strOut = new String[]{"CDC", "D", "CDC", "bcDbc"};
    String acceptable = "ABEFGHIJKLMNOPQRSTUVWXYZ";
    String replacement = "";
    performTest(strIn, strOut, acceptable, replacement, true);
  }

  /**
   * Returns a test suite.
   *
   * @return		the test suite
   */
  public static Test suite() {
    return new TestSuite(StringSanitizerTest.class);
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
