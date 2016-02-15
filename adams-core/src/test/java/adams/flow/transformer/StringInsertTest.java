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
 * StringInsertTest.java
 * Copyright (C) 2012 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.transformer;

import java.util.List;

import junit.framework.Test;
import junit.framework.TestSuite;
import adams.core.Index;
import adams.core.base.BaseString;
import adams.core.io.FileUtils;
import adams.core.option.AbstractArgumentOption;
import adams.env.Environment;
import adams.flow.AbstractFlowTest;
import adams.flow.control.Flow;
import adams.flow.core.Actor;
import adams.flow.sink.DumpFile;
import adams.flow.source.StringConstants;
import adams.test.TmpFile;

/**
 * Test for StringInsert actor.
 *
 * @author fracpete
 * @author adams.core.option.FlowJUnitTestProducer (code generator)
 * @version $Revision$
 */
public class StringInsertTest
  extends AbstractFlowTest {

  /**
   * Initializes the test.
   *
   * @param name	the name of the test
   */
  public StringInsertTest(String name) {
    super(name);
  }

  /**
   *
   * Returns a test suite.
   *
   * @return		the test suite
   */
  public static Test suite() {
    return new TestSuite(StringInsertTest.class);
  }

  /**
   * Used to create an instance of a specific actor.
   *
   * @return a suitably configured <code>Actor</code> value
   */
  @Override
  public Actor getActor() {
    AbstractArgumentOption    argOption;

    Flow flow = new Flow();

    try {
      argOption = (AbstractArgumentOption) flow.getOptionManager().findByProperty("actors");
      adams.flow.core.Actor[] tmp1 = new adams.flow.core.Actor[5];
      adams.flow.source.DirectoryLister tmp2 = new adams.flow.source.DirectoryLister();
      argOption = (AbstractArgumentOption) tmp2.getOptionManager().findByProperty("watchDir");
      tmp2.setWatchDir((adams.core.io.PlaceholderDirectory) argOption.valueOf("${TMP}"));

      tmp2.setListDirs(true);

      tmp1[0] = tmp2;
      adams.flow.transformer.StringReplace tmp4 = new adams.flow.transformer.StringReplace();
      argOption = (AbstractArgumentOption) tmp4.getOptionManager().findByProperty("find");
      tmp4.setFind((adams.core.base.BaseRegExp) argOption.valueOf(".*\\/"));

      argOption = (AbstractArgumentOption) tmp4.getOptionManager().findByProperty("replaceType");
      tmp4.setReplaceType((adams.flow.transformer.StringReplace.ReplaceType) argOption.valueOf("ALL"));

      tmp1[1] = tmp4;
      adams.flow.transformer.StringInsert tmp7 = new adams.flow.transformer.StringInsert();
      argOption = (AbstractArgumentOption) tmp7.getOptionManager().findByProperty("position");
      tmp7.setPosition(new Index("first"));

      argOption = (AbstractArgumentOption) tmp7.getOptionManager().findByProperty("value");
      tmp7.setValue((BaseString) argOption.valueOf("/home/fracpete/"));

      tmp1[2] = tmp7;
      adams.flow.transformer.StringInsert tmp10 = new adams.flow.transformer.StringInsert();
      argOption = (AbstractArgumentOption) tmp10.getOptionManager().findByProperty("name");
      tmp10.setName((java.lang.String) argOption.valueOf("StringInsert-1"));

      tmp10.setAfter(true);

      argOption = (AbstractArgumentOption) tmp10.getOptionManager().findByProperty("value");
      tmp10.setValue((BaseString) argOption.valueOf("/"));

      tmp1[3] = tmp10;
      adams.flow.sink.Display tmp13 = new adams.flow.sink.Display();
      tmp1[4] = tmp13;
      flow.setActors(tmp1);

    }
    catch (Exception e) {
      fail("Failed to set up actor: " + e);
    }

    return flow;
  }

  /**
   * Performs the test.
   *
   * @param strIn	the input strings
   * @param strOut	the output strings
   * @param position	the position where to insert
   * @param value	the value to insert
   * @param after	whether to insert after
   */
  protected void performTest(String[] strIn, String[] strOut, String position, String value, boolean after) {
    Flow flow = new Flow();

    StringConstants sc = new StringConstants();
    BaseString[] bs = new BaseString[strIn.length];
    for (int i = 0; i < strIn.length; i++)
      bs[i] = new BaseString(strIn[i]);
    sc.setStrings(bs);

    StringInsert actor = new StringInsert();
    actor.setPosition(new Index(position));
    actor.setAfter(after);
    actor.setValue(new BaseString(value));

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
   * Tests inserting at the start (at pos).
   */
  public void testActorInsertFirstAt() {
    String[] strIn = new String[]{"A", "ABC", "123"};
    String[] strOut = new String[]{"--A", "--ABC", "--123"};
    performTest(strIn, strOut, "first", "--", false);
  }

  /**
   * Tests inserting at the start (after pos).
   */
  public void testActorInsertFirstAfter() {
    String[] strIn = new String[]{"A", "ABC", "123"};
    String[] strOut = new String[]{"A--", "A--BC", "1--23"};
    performTest(strIn, strOut, "first", "--", true);
  }

  /**
   * Tests inserting at the end (at pos).
   */
  public void testActorInsertLastAt() {
    String[] strIn = new String[]{"A", "ABC", "123"};
    String[] strOut = new String[]{"--A", "AB--C", "12--3"};
    performTest(strIn, strOut, "last", "--", false);
  }

  /**
   * Tests inserting at the end (after pos).
   */
  public void testActorInsertLastAfter() {
    String[] strIn = new String[]{"A", "ABC", "123"};
    String[] strOut = new String[]{"A--", "ABC--", "123--"};
    performTest(strIn, strOut, "last", "--", true);
  }

  /**
   * Runs the test from commandline.
   *
   * @param args	ignored
   */
  public static void main(String[] args) {
    Environment.setEnvironmentClass(adams.env.Environment.class);
    runTest(suite());
  }
}
