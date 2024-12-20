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
 * SpreadSheetSubsetTest.java
 * Copyright (C) 2010-2014 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.transformer;

import junit.framework.Test;
import junit.framework.TestSuite;
import adams.core.Range;
import adams.data.spreadsheet.SpreadSheetColumnRange;
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
 * Tests the SpreadSheetSubset actor.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class SpreadSheetSubsetTest
  extends AbstractFlowTest {

  /**
   * Initializes the test.
   *
   * @param name	the name of the test
   */
  public SpreadSheetSubsetTest(String name) {
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

    m_TestHelper.copyResourceToTmp("iris.csv");
    m_TestHelper.deleteFileFromTmp("dumpfile.txt");
  }

  /**
   * Called by JUnit after each test method.
   *
   * @throws Exception	if tear-down fails
   */
  @Override
  protected void tearDown() throws Exception {
    m_TestHelper.deleteFileFromTmp("iris.csv");
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
    DumpFile df = new DumpFile();
    df.setName("out");
    df.setAppend(true);
    df.setOutputFile(new TmpFile("dumpfile.txt"));

    CallableActors ga = new CallableActors();
    ga.setActors(new Actor[]{
	df
    });

    FileSupplier sfs = new FileSupplier();
    sfs.setFiles(new adams.core.io.PlaceholderFile[]{new TmpFile("iris.csv")});

    SpreadSheetFileReader ssr = new SpreadSheetFileReader();

    SpreadSheetSubset ssc1 = new SpreadSheetSubset();
    ssc1.setRows(new Range("10-20"));
    ssc1.setColumns(new SpreadSheetColumnRange("1,3,last"));
    ssc1.setCreateView(true);

    CallableSink gs1 = new CallableSink();
    gs1.setCallableName(new CallableActorReference("out"));

    Sequence seq1 = new Sequence();
    seq1.setActors(new Actor[]{
	ssc1,
	gs1
    });

    SpreadSheetSubset ssc2 = new SpreadSheetSubset();
    ssc2.setRows(new Range("20-30"));
    ssc2.setColumns(new SpreadSheetColumnRange("2,4,last"));
    ssc2.setCreateView(true);

    CallableSink gs2 = new CallableSink();
    gs2.setCallableName(new CallableActorReference("out"));

    Sequence seq2 = new Sequence();
    seq2.setActors(new Actor[]{
	ssc2,
	gs2
    });

    SpreadSheetSubset ssc3 = new SpreadSheetSubset();
    ssc3.setRows(new Range("30-40"));
    ssc3.setColumns(new SpreadSheetColumnRange("1,2,last"));
    ssc3.setCreateView(false);

    CallableSink gs3 = new CallableSink();
    gs3.setCallableName(new CallableActorReference("out"));

    Sequence seq3 = new Sequence();
    seq3.setActors(new Actor[]{
	ssc3,
	gs3
    });

    Branch br = new Branch();
    br.setNumThreads(1);
    br.setBranches(new Actor[]{
	seq1,
	seq2,
	seq3
    });

    Flow flow = new Flow();
    flow.setActors(new Actor[]{ga, sfs, ssr, br});

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
    return new TestSuite(SpreadSheetSubsetTest.class);
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
