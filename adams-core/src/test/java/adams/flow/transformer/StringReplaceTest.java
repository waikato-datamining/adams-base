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
 * StringReplaceTest.java
 * Copyright (C) 2009 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.transformer;

import java.util.List;

import junit.framework.Test;
import junit.framework.TestSuite;
import adams.core.Placeholders;
import adams.core.base.BaseRegExp;
import adams.core.base.BaseString;
import adams.core.io.FileUtils;
import adams.env.Environment;
import adams.flow.AbstractFlowTest;
import adams.flow.control.Flow;
import adams.flow.core.Actor;
import adams.flow.sink.DumpFile;
import adams.flow.sink.Null;
import adams.flow.source.StringConstants;
import adams.flow.transformer.StringReplace.ReplaceType;
import adams.test.TmpFile;

/**
 * Tests the StringReplace actor.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class StringReplaceTest
  extends AbstractFlowTest {

  /**
   * Initializes the test.
   *
   * @param name	the name of the test
   */
  public StringReplaceTest(String name) {
    super(name);
  }

  /**
   * Used to create an instance of a specific actor.
   *
   * @return a suitably configured <code>Flow</code>
   */
  @Override
  public Actor getActor() {
    StringConstants con = new StringConstants();
    con.setStrings(new BaseString[]{
	new BaseString("ABCDEF"),
	new BaseString("ABDEF"),
	new BaseString("BCDEF"),
	new BaseString("bcDEF ")
    });

    StringReplace actor = new StringReplace();
    actor.setFind(new BaseRegExp("BC"));
    actor.setReplace("--");

    Null nul = new Null();

    Flow flow = new Flow();
    flow.setActors(new Actor[]{con, actor, nul});

    return flow;
  }

  /**
   * Performs the test.
   *
   * @param strIn	the input strings
   * @param strOut	the output strings
   * @param find	the string to find
   * @param replace	the replacement string
   * @param type	how to replace the strings
   * @param contPl	replace contains placeholders
   * @param vars	2-dimensional string array for variables ([x][0]=name, [x][1]=value);
   * 			use null to ignore
   */
  protected void performTest(String[] strIn, String[] strOut, String find, String replace, ReplaceType type, boolean contPl, String[][] vars) {
    Flow flow = new Flow();

    if (vars != null) {
      for (int i = 0; i < vars.length; i++)
	flow.getVariables().set(vars[i][0], vars[i][1]);
    }

    StringConstants sc = new StringConstants();
    BaseString[] bs = new BaseString[strIn.length];
    for (int i = 0; i < strIn.length; i++)
      bs[i] = new BaseString(strIn[i]);
    sc.setStrings(bs);

    StringReplace actor = new StringReplace();
    actor.setFind(new BaseRegExp(find));
    actor.setReplace(replace);
    actor.setReplaceType(type);
    actor.setReplaceContainsPlaceholder(contPl);
    actor.setReplaceContainsVariable(vars != null);

    TmpFile outFile = new TmpFile("dumpfile.txt");
    DumpFile df = new DumpFile();
    df.setOutputFile(outFile);
    df.setAppend(true);

    flow.setActors(new Actor[]{
	sc,
	actor,
	df
    });

    FileUtils.delete(outFile);

    assertNull("problem with setUp()", flow.setUp());
    assertNull("problem with execute()", flow.execute());

    List<String> vecOutActor = FileUtils.loadFromFile(outFile);
    String[] strOutActor = vecOutActor.toArray(new String[vecOutActor.size()]);

    assertEquals("array length differs", strOut.length, strOutActor.length);

    for (int i = 0; i < strOut.length; i++)
      assertEquals("values differ", strOut[i], strOutActor[i]);

    actor.wrapUp();
    actor.cleanUp();
  }

  /**
   * Tests only replacing the first occurrence.
   */
  public void testActorReplaceFirst() {
    String[] strIn = new String[]{"ABCDEFBC", "ABDEF", "BCDEFBC", "bcDEFbc"};
    String[] strOut = new String[]{"A--DEFBC", "ABDEF", "--DEFBC", "bcDEFbc"};
    performTest(strIn, strOut, "BC", "--", ReplaceType.FIRST, false, null);
  }

  /**
   * Tests replacing all occurrences.
   */
  public void testActorReplaceAll() {
    String[] strIn = new String[]{"ABCDEFBC", "ABDEF", "BCDEFBC", "bcDEFbc"};
    String[] strOut = new String[]{"A--DEF--", "ABDEF", "--DEF--", "bcDEFbc"};
    performTest(strIn, strOut, "BC", "--", ReplaceType.ALL, false, null);
  }

  /**
   * Tests replacing occurrences (simple).
   */
  public void testActorReplaceSimple() {
    String[] strIn = new String[]{"ABCDEFBC", "ABDEF", "BCDEFBC", "bcDEFbc"};
    String[] strOut = new String[]{"A--DEF--", "ABDEF", "--DEF--", "bcDEFbc"};
    performTest(strIn, strOut, "BC", "--", ReplaceType.SIMPLE, false, null);
  }

  /**
   * Tests replacing placeholders.
   */
  public void testActorReplaceAllPlaceholders() {
    Placeholders.getSingleton().set("JUNITBLAH", "hello_world");
    String[] strIn = new String[]{"ABCDEFBC", "BCABDEF"};
    String[] strOut = new String[]{"Ahello_worldDEFhello_world", "hello_worldABDEF"};
    performTest(strIn, strOut, "BC", "${JUNITBLAH}", ReplaceType.ALL, true, null);
  }

  /**
   * Tests replacing variables.
   */
  public void testActorReplaceAllVariables() {
    String[] strIn = new String[]{"ABCDEFBC", "BCABDEF"};
    String[] strOut = new String[]{"Ahello_worldDEFhello_world", "hello_worldABDEF"};
    performTest(strIn, strOut, "BC", "@{blah}", ReplaceType.ALL, false, new String[][]{{"blah", "hello_world"}});
  }

  /**
   * Tests replacing variables and placeholders.
   */
  public void testActorReplaceAllPlaceholdersAndVariables() {
    Placeholders.getSingleton().set("JUNITBLAH", "hello_world");
    String[] strIn = new String[]{"ABCDEFBC", "BCABDEF"};
    String[] strOut = new String[]{"Ahello_worldDEFhello_world", "hello_worldABDEF"};
    performTest(strIn, strOut, "BC", "@{blah}", ReplaceType.ALL, true, new String[][]{{"blah", "${JUNITBLAH}"}});
  }

  /**
   * Returns a test suite.
   *
   * @return		the test suite
   */
  public static Test suite() {
    return new TestSuite(StringReplaceTest.class);
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
