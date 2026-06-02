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
 * DeleteManyVariablesTest.java
 * Copyright (C) 2026 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.standalone;

import adams.core.VariableName;
import adams.core.VariableNameValuePair;
import adams.env.Environment;
import adams.flow.AbstractFlowTest;
import adams.flow.control.Flow;
import adams.flow.control.Trigger;
import adams.flow.core.Actor;
import adams.flow.sink.DumpFile;
import adams.flow.source.CombineVariables;
import adams.flow.source.DumpVariables;
import adams.flow.source.DumpVariables.OutputType;
import adams.flow.source.Start;
import adams.test.TmpFile;
import junit.framework.Test;
import junit.framework.TestSuite;

import java.io.File;

/**
 * Tests the DeleteManyVariables actor.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public class DeleteManyVariablesTest
  extends AbstractFlowTest {

  /**
   * Initializes the test.
   *
   * @param name	the name of the test
   */
  public DeleteManyVariablesTest(String name) {
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

    m_TestHelper.deleteFileFromTmp("dumpfile.txt");
  }

  /**
   * Called by JUnit after each test method.
   *
   * @throws Exception	if tear-down fails
   */
  @Override
  protected void tearDown() throws Exception {
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
    Flow flow = new Flow();

    flow.add(new Start());

    Trigger tr = new Trigger();
    flow.add(tr);
    {
      SetManyVariables set = new SetManyVariables();
      set.addVariablePair(new VariableNameValuePair("a=123"));
      set.addVariablePair(new VariableNameValuePair("b=hello world"));
      tr.add(set);

      DumpVariables dv = new DumpVariables();
      dv.setOutputType(OutputType.SPREADSHEET);
      tr.add(dv);

      DumpFile df = new DumpFile();
      df.setOutputFile(new TmpFile("dumpfile.txt"));
      df.setAppend(false);
      tr.add(df);
    }

    Trigger trSep = new Trigger();
    flow.add(trSep);
    {
      trSep.add(new CombineVariables("---"));

      DumpFile df = new DumpFile();
      df.setOutputFile(new TmpFile("dumpfile.txt"));
      df.setAppend(true);
      trSep.add(df);
    }

    Trigger tr2 = new Trigger();
    flow.add(tr2);
    {
      DeleteManyVariables set = new DeleteManyVariables();
      set.addVariableName(new VariableName("a"));
      set.addVariableName(new VariableName("b"));
      tr2.add(set);

      DumpVariables dv = new DumpVariables();
      dv.setOutputType(OutputType.SPREADSHEET);
      tr2.add(dv);

      DumpFile df = new DumpFile();
      df.setOutputFile(new TmpFile("dumpfile.txt"));
      df.setAppend(true);
      tr2.add(df);
    }

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
    return new TestSuite(DeleteManyVariablesTest.class);
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
