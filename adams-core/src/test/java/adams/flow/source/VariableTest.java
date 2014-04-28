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
 * VariableTest.java
 * Copyright (C) 2010 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.source;

import java.io.File;

import junit.framework.Test;
import junit.framework.TestSuite;
import adams.core.base.BaseRegExp;
import adams.core.VariableName;
import adams.core.Variables;
import adams.env.Environment;
import adams.flow.AbstractFlowTest;
import adams.flow.condition.test.FileExists;
import adams.flow.control.Flow;
import adams.flow.core.AbstractActor;
import adams.flow.standalone.ConditionalStandalone;
import adams.flow.standalone.DeleteFile;
import adams.flow.standalone.SetVariable;
import adams.flow.sink.DumpFile;
import adams.test.TmpDirectory;
import adams.test.TmpFile;

/**
 * Tests the Variable source.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class VariableTest
  extends AbstractFlowTest {

  /**
   * Initializes the test.
   *
   * @param name	the name of the test
   */
  public VariableTest(String name) {
    super(name);
  }

  /**
   * Called by JUnit before each test method.
   *
   * @throws Exception if an error occurs
   */
  protected void setUp() throws Exception {
    super.setUp();

    m_TestHelper.copyResourceToTmp("bolts.csv");
    m_TestHelper.deleteFileFromTmp("dumpfile.txt");
  }

  /**
   * Called by JUnit after each test method.
   *
   * @throws Exception	if tear-down fails
   */
  protected void tearDown() throws Exception {
    m_TestHelper.deleteFileFromTmp("bolts.csv");
    m_TestHelper.deleteFileFromTmp("dumpfile.txt");

    super.tearDown();
  }

  /**
   * Used to create an instance of a specific actor.
   *
   * @return a suitably configured <code>AbstractActor</code> value
   */
  public AbstractActor getActor() {
    FileExists fe = new FileExists();
    fe.setFile(new TmpFile("bolts.csv"));

    SetVariable sv = new SetVariable();
    sv.setVariableName(new VariableName("file_exists"));
    sv.setVariableValue("yes");

    ConditionalStandalone cond = new ConditionalStandalone();
    cond.setCondition(fe);
    cond.setActor(sv);

    DeleteFile df = new DeleteFile();
    df.setDirectory(new TmpDirectory());
    df.setRegExp(new BaseRegExp("bolts.csv"));

    SetVariable sv2 = new SetVariable();
    sv2.setVariableName(new VariableName("file_exists"));
    sv2.setVariableValue("no");

    Variable var = new Variable();
    var.setVariableName(new VariableName("file_exists"));

    DumpFile dump = new DumpFile();
    dump.setAppend(true);
    dump.setOutputFile(new TmpFile("dumpfile.txt"));

    Flow flow = new Flow();
    flow.setActors(new AbstractActor[]{cond, df, sv2, var, dump});

    flow.getVariables().set("file_exists", "no");

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
   * Returns a test suite.
   *
   * @return		the test suite
   */
  public static Test suite() {
    return new TestSuite(VariableTest.class);
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
