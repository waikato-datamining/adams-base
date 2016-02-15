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
 * StringRangeCutTest.java
 * Copyright (C) 2011-2012 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.transformer;

import java.io.File;

import junit.framework.Test;
import junit.framework.TestSuite;
import adams.core.Range;
import adams.core.base.BaseString;
import adams.env.Environment;
import adams.flow.AbstractFlowTest;
import adams.flow.control.Flow;
import adams.flow.core.Actor;
import adams.flow.core.Token;
import adams.flow.sink.DumpFile;
import adams.flow.source.StringConstants;
import adams.flow.transformer.StringRangeCut.Type;
import adams.test.TmpFile;

/**
 * Tests the StringRangeCut actor.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class StringRangeCutTest
  extends AbstractFlowTest {

  /**
   * Initializes the test.
   *
   * @param name	the name of the test
   */
  public StringRangeCutTest(String name) {
    super(name);
  }

  /**
   * Called by JUnit before each test method.
   *
   * @throws Exception if an error occurs.
   */
  protected void setUp() throws Exception {
    super.setUp();

    m_TestHelper.deleteFileFromTmp("dumpfile.txt");
  }

  /**
   * Called by JUnit after each test method.
   *
   * @throws Exception	if tear-down fails
   */
  protected void tearDown() throws Exception {
    m_TestHelper.deleteFileFromTmp("dumpfile.txt");

    super.tearDown();
  }

  /**
   * Used to create an instance of a specific actor.
   *
   * @return a suitably configured <code>Flow</code>
   */
  public Actor getActor() {
    StringConstants con = new StringConstants();
    con.setStrings(new BaseString[]{
	new BaseString("A:BCde"),
	new BaseString("ab:cde"),
	new BaseString("ABC:DE"),
	new BaseString("abcD:E")
    });

    StringRangeCut actor = new StringRangeCut();
    actor.setType(Type.DELIMITED_FIELDS);
    actor.setDelimiter(":");
    actor.setRange(new Range("2"));

    DumpFile df = new DumpFile();
    df.setAppend(true);
    df.setOutputFile(new TmpFile("dumpfile.txt"));

    Flow flow = new Flow();
    flow.setActors(new Actor[]{con, actor, df});

    return flow;
  }

  /**
   * Performs a regression test, comparing against previously generated output.
   */
  public void testRegression() {
    performRegressionTest(
	new File[]{
	    new TmpFile("dumpfile.txt")});
  }

  /**
   * Performs a test on the actor.
   *
   * @param strIn	the input strings
   * @param strOut	the expected output strings
   * @param type	the type of cutting
   * @param range	the range
   * @param delim	the delimiter
   * @param glue	the glue
   */
  protected void performTest(String[] strIn, String[] strOut, Type type, String range, String delim, String glue) {
    StringRangeCut actor = new StringRangeCut();
    actor.setType(type);
    actor.setRange(new Range(range));
    actor.setDelimiter(delim);
    actor.setGlue(glue);
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
    performTest(strIn, strOut, Type.CHARACTER_POSITIONS, "3-5", "", "");
  }

  /**
   * Tests character positions with "glue" for the substrings.
   */
  public void testActorCharPositionsWithGlue() {
    String[] strIn = new String[]{"abcdefgh", "the ridiculous", "buggerme"};
    String[] strOut = new String[]{"cde:fgh", "e r:idi", "gge:rme"};
    performTest(strIn, strOut, Type.CHARACTER_POSITIONS, "3-5,6-8", "", ":");
  }

  /**
   * Tests delimited fields.
   */
  public void testActorDelimitedFields() {
    String[] strIn = new String[]{"ab:cde", "hell:o", "w:orld", ":bugger", "blah:"};
    String[] strOut = new String[]{"cde", "o", "orld", "bugger", ""};
    performTest(strIn, strOut, Type.DELIMITED_FIELDS, "2", ":", "");
  }

  /**
   * Tests delimited fields with "glue" for substrings.
   */
  public void testActorDelimitedFieldsWithGlue() {
    String[] strIn = new String[]{"ab:cde:fgh", "hell:o:there", "w:orld:ly", ":bugger:me", "blah:blurb:"};
    String[] strOut = new String[]{"ab\tfgh", "hell\tthere", "w\tly", "\tme", "blah\t"};
    performTest(strIn, strOut, Type.DELIMITED_FIELDS, "1,3", ":", "\t");
  }

  /**
   * Returns a test suite.
   *
   * @return		the test suite
   */
  public static Test suite() {
    return new TestSuite(StringRangeCutTest.class);
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
