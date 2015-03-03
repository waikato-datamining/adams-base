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
 * InjectorTest.java
 * Copyright (C) 2010 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.control;

import java.util.Vector;

import junit.framework.Test;
import junit.framework.TestSuite;
import adams.core.base.BaseString;
import adams.env.Environment;
import adams.flow.AbstractFlowTest;
import adams.flow.control.Injector.Location;
import adams.flow.core.AbstractActor;
import adams.flow.core.Token;
import adams.flow.sink.DumpFile;
import adams.flow.source.StringConstants;
import adams.test.TmpFile;

/**
 * Tests the Injector actor.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class InjectorTest
  extends AbstractFlowTest {

  /**
   * Initializes the test.
   *
   * @param name	the name of the test
   */
  public InjectorTest(String name) {
    super(name);
  }

  /**
   * Called by JUnit before each test method.
   *
   * @throws Exception if an error occur
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
   * @return a suitably configured <code>AbstractActor</code> value
   */
  public AbstractActor getActor() {
    StringConstants ids = new StringConstants();
    ids.setStrings(new BaseString[]{
	new BaseString("1"),
	new BaseString("2"),
	new BaseString("3"),
	new BaseString("4"),
	new BaseString("5"),
	new BaseString("6"),
	new BaseString("7"),
	new BaseString("8"),
	new BaseString("9"),
	new BaseString("10")
    });

    Injector in1 = new Injector();
    in1.setEveryNth(1);
    in1.setLocation(Location.AFTER);
    in1.setInjection("Inj_1");

    Injector in2 = new Injector();
    in2.setEveryNth(2);
    in2.setLocation(Location.AFTER);
    in2.setInjection("Inj_2");

    Injector in3 = new Injector();
    in3.setEveryNth(3);
    in3.setLocation(Location.BEFORE);
    in3.setInjection("Inj_3");

    DumpFile df = new DumpFile();
    df.setAppend(true);
    df.setOutputFile(new TmpFile("dumpfile.txt"));

    Flow flow = new Flow();
    flow.setActors(new AbstractActor[]{ids, in1, in2, in3, df});

    return flow;
  }

  /**
   * performs a test.
   *
   * @param actor	the actor to use
   * @param strIn	the input string sequence
   * @param strOut	the expected string sequence
   */
  protected void performTest(Injector actor, String[] strIn, String[] strOut) {
    assertNull("problem with setUp()", actor.setUp());

    Vector<String> generated = new Vector<String>();
    for (int i = 0; i < strIn.length; i++) {
      actor.input(new Token(strIn[i]));
      assertNull("problem with execute()", actor.execute());

      while (actor.hasPendingOutput()) {
	Token out = actor.output();
	assertNotNull("problem with output()", out);

	String strOutActor = (String) out.getPayload();
	generated.add(strOutActor);
      }
    }

    assertEquals("lengths differ", strOut.length, generated.size());
    for (int i = 0; i < strOut.length; i++)
      assertEquals("values differ at #" + (i+1), strOut[i], generated.get(i));

    actor.wrapUp();
    actor.cleanUp();
  }

  /**
   * Tests the actor by itself. After every token, the injection string will
   * get inserted.
   */
  public void testActorTypical() {
    Injector actor = new Injector();
    String[] strIn = new String[]{"1", "2", "3"};
    String[] strOut = new String[]{"1", "inject_me", "2", "inject_me", "3", "inject_me"};
    performTest(actor, strIn, strOut);
  }

  /**
   * Tests injection after every token with a different injection string.
   */
  public void testDifferentInjectionString() {
    Injector actor = new Injector();
    actor.setInjection("hello");
    String[] strIn = new String[]{"1", "2", "3"};
    String[] strOut = new String[]{"1", "hello", "2", "hello", "3", "hello"};
    performTest(actor, strIn, strOut);
  }

  /**
   * Tests injection before the current one.
   */
  public void testActorLocationBefore() {
    Injector actor = new Injector();
    actor.setLocation(Location.BEFORE);
    String[] strIn = new String[]{"1", "2", "3"};
    String[] strOut = new String[]{"inject_me", "1", "inject_me", "2", "inject_me", "3"};
    performTest(actor, strIn, strOut);
  }

  /**
   * Tests injection after every 2nd token.
   */
  public void testActorAfterEvery2nd() {
    Injector actor = new Injector();
    actor.setLocation(Location.AFTER);
    actor.setEveryNth(2);
    String[] strIn = new String[]{"1", "2", "3", "4"};
    String[] strOut = new String[]{"1", "2", "inject_me", "3", "4", "inject_me"};
    performTest(actor, strIn, strOut);
  }

  /**
   * Performs a regression test, comparing against previously generated output.
   */
  public void testRegression() {
    performRegressionTest(
	new TmpFile("dumpfile.txt"));
  }

  /**
   * Returns a test suite.
   *
   * @return		the test suite
   */
  public static Test suite() {
    return new TestSuite(InjectorTest.class);
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
