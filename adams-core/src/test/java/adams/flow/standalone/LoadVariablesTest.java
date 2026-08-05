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
 * LoadVariablesTest.java
 * Copyright (C) 2015 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.standalone;

import adams.core.base.BaseRegExp;
import adams.env.Environment;
import adams.flow.AbstractFlowTest;
import adams.flow.control.Flow;
import adams.flow.control.Trigger;
import adams.flow.core.Actor;
import adams.flow.sink.DumpFile;
import adams.flow.source.DumpVariables;
import adams.flow.source.Start;
import adams.flow.standalone.loadvariables.FromProperties;
import adams.test.TmpFile;
import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * Test for LoadVariables actor.
 *
 * @author fracpete
 */
public class LoadVariablesTest
  extends AbstractFlowTest {

  /**
   * Initializes the test.
   *
   * @param name	the name of the test
   */
  public LoadVariablesTest(String name) {
    super(name);
  }

  /**
   * Called by JUnit before each test method.
   *
   * @throws Exception 	if an error occurs.
   */
  protected void setUp() throws Exception {
    super.setUp();

    m_TestHelper.deleteFileFromTmp("dumpfile.txt");
    m_TestHelper.copyResourceToTmp("vars.props");
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
   * Performs a regression test, comparing against previously generated output.
   */
  public void testRegression() {
    performRegressionTest(
        new TmpFile[]{
          new TmpFile("dumpfile.txt")
        });
  }

  /**
   * 
   * Returns a test suite.
   *
   * @return		the test suite
   */
  public static Test suite() {
    return new TestSuite(LoadVariablesTest.class);
  }

  /**
   * Used to create an instance of a specific actor.
   *
   * @return a suitably configured <code>Actor</code> value
   */
  public Actor getActor() {
    Flow flow = new Flow();

    FromProperties fromProps = new FromProperties();
    fromProps.setInputFile(new TmpFile("vars.props"));
    LoadVariables loadVars = new LoadVariables();
    loadVars.setLoader(fromProps);
    flow.add(loadVars);

    flow.add(new Start());

    Trigger trig = new Trigger();
    flow.add(trig);
    {
      DumpVariables dumpVars = new DumpVariables();
      dumpVars.setRegExp(new BaseRegExp("var_.*"));
      trig.add(dumpVars);

      DumpFile dump = new DumpFile();
      dump.setOutputFile(new TmpFile("dumpfile.txt"));
      trig.add(dump);
    }

    return flow;
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

