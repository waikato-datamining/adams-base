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
 * CopyTest.java
 * Copyright (C) 2010-2014 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.transformer;

import junit.framework.Test;
import junit.framework.TestSuite;
import adams.data.conversion.AnyToString;
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
 * Tests the Copy actor.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class CopyTest
  extends AbstractFlowTest {

  /**
   * Initializes the test.
   *
   * @param name	the name of the test
   */
  public CopyTest(String name) {
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

    m_TestHelper.copyResourceToTmp("vote.arff");
    m_TestHelper.deleteFileFromTmp("dumpfile.txt");
  }

  /**
   * Called by JUnit after each test method.
   *
   * @throws Exception	if tear-down fails
   */
  @Override
  protected void tearDown() throws Exception {
    m_TestHelper.deleteFileFromTmp("vote.arff");
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
    df.setName("sink");
    df.setAppend(true);
    df.setOutputFile(new TmpFile("dumpfile.txt"));

    CallableActors ga = new CallableActors();
    ga.setActors(new Actor[]{
	df
    });

    FileSupplier sfs = new FileSupplier();
    sfs.setFiles(new adams.core.io.PlaceholderFile[]{new TmpFile("vote.arff")});

    WekaFileReader fr = new WekaFileReader();

    Copy copy = new Copy();

    WekaRenameRelation ren1 = new WekaRenameRelation();
    ren1.setReplace("branch1");
    AnyToString any1 = new AnyToString();
    Convert con1 = new Convert();
    con1.setConversion(any1);
    CallableSink sink1 = new CallableSink();
    sink1.setCallableName(new CallableActorReference("sink"));
    Sequence seq1 = new Sequence();
    seq1.setActors(new Actor[]{
	ren1,
	con1,
	sink1
    });

    WekaRenameRelation ren2 = new WekaRenameRelation();
    ren2.setReplace("branch2");
    AnyToString any2 = new AnyToString();
    Convert con2 = new Convert();
    con2.setConversion(any2);
    CallableSink sink2 = new CallableSink();
    sink2.setCallableName(new CallableActorReference("sink"));
    Sequence seq2 = new Sequence();
    seq2.setActors(new Actor[]{
	ren2,
	con2,
	sink2
    });

    Branch br = new Branch();
    br.setNumThreads(1);
    br.setBranches(new Actor[]{
	seq1,
	seq2
    });

    Flow flow = new Flow();
    flow.setActors(new Actor[]{ga, sfs, fr, copy, br});

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
    return new TestSuite(CopyTest.class);
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
