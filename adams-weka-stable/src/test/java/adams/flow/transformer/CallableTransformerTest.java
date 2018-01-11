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
 * CallableTransformerTest.java
 * Copyright (C) 2009-2014 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.transformer;

import java.io.File;

import junit.framework.Test;
import junit.framework.TestSuite;
import weka.filters.unsupervised.attribute.Remove;
import adams.env.Environment;
import adams.flow.AbstractFlowTest;
import adams.flow.control.Branch;
import adams.flow.control.Flow;
import adams.flow.control.Sequence;
import adams.flow.core.Actor;
import adams.flow.core.CallableActorReference;
import adams.flow.source.FileSupplier;
import adams.flow.standalone.CallableActors;
import adams.flow.transformer.WekaFileReader.OutputType;
import adams.test.TmpFile;

/**
 * Tests the GlobalTransformer by using one WekaFilter actor from two
 * branches of a Branch actor.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class CallableTransformerTest
  extends AbstractFlowTest {

  /**
   * Initializes the test.
   *
   * @param name	the name of the test
   */
  public CallableTransformerTest(String name) {
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
    m_TestHelper.deleteFileFromTmp("dumpfile1.arff");
    m_TestHelper.deleteFileFromTmp("dumpfile2.arff");
  }

  /**
   * Called by JUnit after each test method.
   *
   * @throws Exception	if tear-down fails
   */
  @Override
  protected void tearDown() throws Exception {
    m_TestHelper.deleteFileFromTmp("vote.arff");
    m_TestHelper.deleteFileFromTmp("dumpfile1.arff");
    m_TestHelper.deleteFileFromTmp("dumpfile2.arff");

    super.tearDown();
  }

  /**
   * Used to create an instance of a specific actor.
   *
   * @return a suitably configured <code>Actor</code> value
   */
  @Override
  public Actor getActor() {
    FileSupplier sfs = new FileSupplier();
    sfs.setFiles(new adams.core.io.PlaceholderFile[]{new TmpFile("vote.arff")});

    WekaFileReader fr = new WekaFileReader();
    fr.setOutputType(OutputType.INCREMENTAL);

    WekaFilter wf = new WekaFilter();
    wf.setInitializeOnce(false);
    wf.setFilter(new weka.filters.unsupervised.attribute.Add());
    wf.setName("global-wf");

    CallableActors gas = new CallableActors();
    gas.setActors(new Actor[]{wf});

    WekaFilter wf1 = new WekaFilter();
    Remove rem1 = new Remove();
    rem1.setAttributeIndices("1");
    wf1.setFilter(rem1);

    CallableTransformer gt1 = new CallableTransformer();
    gt1.setCallableName(new CallableActorReference("global-wf"));

    WekaRenameRelation ren1 = new WekaRenameRelation();
    ren1.setFind("[\\s\\S]+");
    ren1.setReplace("blah1");

    WekaInstanceDumper id1 = new WekaInstanceDumper();
    id1.setOutputPrefix(new TmpFile("dumpfile1"));

    Sequence sq1 = new Sequence();
    sq1.setActors(new Actor[]{wf1, gt1, ren1, id1});

    WekaFilter wf2 = new WekaFilter();
    Remove rem2 = new Remove();
    rem2.setAttributeIndices("2");
    wf2.setFilter(rem2);

    CallableTransformer gt2 = new CallableTransformer();
    gt2.setCallableName(new CallableActorReference("global-wf"));

    WekaRenameRelation ren2 = new WekaRenameRelation();
    ren2.setFind("[\\s\\S]+");
    ren2.setReplace("blah2");

    WekaInstanceDumper id2 = new WekaInstanceDumper();
    id2.setOutputPrefix(new TmpFile("dumpfile2"));

    Sequence sq2 = new Sequence();
    sq2.setActors(new Actor[]{wf2, gt2, ren2, id2});

    Branch br = new Branch();
    //br.setNumThreads(0);
    br.setBranches(new Actor[]{sq1, sq2});

    Flow flow = new Flow();
    flow.setActors(new Actor[]{gas, sfs, fr, br});

    return flow;
  }

  /**
   * Performs a regression test, comparing against previously generated output.
   */
  public void testRegression() {
    performRegressionTest(
	new File[]{
	    new TmpFile("dumpfile1.arff"),
	    new TmpFile("dumpfile2.arff")});
  }

  /**
   * Returns a test suite.
   *
   * @return		the test suite
   */
  public static Test suite() {
    return new TestSuite(CallableTransformerTest.class);
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
