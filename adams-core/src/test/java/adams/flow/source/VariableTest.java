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
 * Copyright (C) 2010-2022 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.source;

import adams.core.VariableName;
import adams.core.io.PlaceholderFile;
import adams.env.Environment;
import adams.flow.AbstractFlowTest;
import adams.flow.condition.bool.FileExists;
import adams.flow.control.Flow;
import adams.flow.control.IfThenElse;
import adams.flow.control.Tee;
import adams.flow.control.Trigger;
import adams.flow.core.Actor;
import adams.flow.sink.DumpFile;
import adams.flow.standalone.SetVariable;
import adams.flow.transformer.DeleteFile;
import adams.test.TmpFile;
import junit.framework.Test;
import junit.framework.TestSuite;

import java.io.File;

/**
 * Tests the Variable source.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
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
  @Override
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
  @Override
  protected void tearDown() throws Exception {
    m_TestHelper.deleteFileFromTmp("bolts.csv");
    m_TestHelper.deleteFileFromTmp("dumpfile.txt");

    super.tearDown();
  }

  /**
   * Used to create an instance of a specific actor.
   *
   * @return a suitably configured <code>Actor</code> value
   */
  @Override
  public Actor getActor() {
    FileSupplier fs = new FileSupplier();
    fs.setFiles(new PlaceholderFile[]{new TmpFile("bolts.csv")});

    DeleteFile df = new DeleteFile();

    Tee tee = new Tee();
    {
      FileExists fe = new FileExists();
      fe.setFile(new TmpFile("bolts.csv"));
      IfThenElse ift = new IfThenElse();
      ift.setCondition(fe);
      tee.add(ift);

      SetVariable svy = new SetVariable();
      svy.setVariableName(new VariableName("file_exists"));
      svy.setVariableValue("yes");
      ift.setThenActor(svy);

      SetVariable svn = new SetVariable();
      svn.setVariableName(new VariableName("file_exists"));
      svn.setVariableValue("no");
      ift.setElseActor(svn);
    }

    Trigger trigger = new Trigger();
    {
      Variable var = new Variable();
      var.setVariableName(new VariableName("file_exists"));
      trigger.add(var);

      DumpFile dump = new DumpFile();
      dump.setAppend(true);
      dump.setOutputFile(new TmpFile("dumpfile.txt"));
      trigger.add(dump);
    }

    Flow flow = new Flow();
    flow.setActors(new Actor[]{fs, df, tee, trigger});

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
