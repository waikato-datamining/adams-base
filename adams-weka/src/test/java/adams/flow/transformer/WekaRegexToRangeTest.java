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
 * WekaRegexToRangeTest.java
 * Copyright (C) 2010-2014 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.transformer;

import junit.framework.Test;
import junit.framework.TestSuite;
import adams.env.Environment;
import adams.flow.AbstractFlowTest;
import adams.flow.control.Branch;
import adams.flow.control.Flow;
import adams.flow.control.Sequence;
import adams.flow.core.Actor;
import adams.flow.core.CallableActorReference;
import adams.flow.sink.CallableSink;
import adams.flow.sink.DumpFile;
import adams.flow.source.FileSupplier;
import adams.flow.standalone.CallableActors;
import adams.test.TmpFile;

/**
 * Tests the WekaRegexToRange actor.
 *
 * @author  dale (dale at waikato dot ac dot nz)
 * @version $Revision$
 */
public class WekaRegexToRangeTest
  extends AbstractFlowTest {

  /**
   * Initializes the test.
   *
   * @param name	the name of the test
   */
  public WekaRegexToRangeTest(String name) {
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
    m_TestHelper.copyResourceToTmp("labor.arff");
  }

  /**
   * Called by JUnit after each test method.
   *
   * @throws Exception	if tear-down fails
   */
  @Override
  protected void tearDown() throws Exception {
    m_TestHelper.deleteFileFromTmp("dumpfile.txt");
    m_TestHelper.deleteFileFromTmp("labor.arff");

    super.tearDown();
  }

  /**
   * Used to create an instance of a specific actor.
   *
   * @return a suitably configured <code>Flow</code>
   */
  @Override
  public Actor getActor() {
    DumpFile df = new DumpFile();
    df.setName("out");
    df.setAppend(true);
    df.setOutputFile(new TmpFile("dumpfile.txt"));

    CallableActors ga = new CallableActors();
    ga.setActors(new Actor[]{
	df
    });

    FileSupplier sfs = new FileSupplier();
    sfs.setFiles(new adams.core.io.PlaceholderFile[]{new TmpFile("labor.arff")});

    WekaFileReader fr = new WekaFileReader();

    WekaRegexToRange rtr1 = new WekaRegexToRange();

    CallableSink gs1 = new CallableSink();
    gs1.setCallableName(new CallableActorReference("out"));

    Sequence s1 = new Sequence();
    s1.setActors(new Actor[]{
	rtr1,gs1
    });

    WekaRegexToRange rtr2 = new WekaRegexToRange();
    rtr2.setRegex("wage-.*");

    CallableSink gs2 = new CallableSink();
    gs2.setCallableName(new CallableActorReference("out"));

    Sequence s2 = new Sequence();
    s2.setActors(new Actor[]{
	rtr2,gs2
    });

    Branch br = new Branch();
    br.setNumThreads(0);
    br.setBranches(new Actor[]{
	s1, s2
    });

    Flow flow = new Flow();
    flow.setActors(new Actor[]{ga, sfs, fr, br});

    return flow;
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
    return new TestSuite(WekaRegexToRangeTest.class);
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
