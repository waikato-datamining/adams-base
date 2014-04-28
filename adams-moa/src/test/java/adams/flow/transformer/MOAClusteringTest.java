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
 * MOAClusteringTest.java
 * Copyright (C) 2011 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.transformer;

import junit.framework.Test;
import junit.framework.TestSuite;
import adams.env.Environment;
import adams.flow.AbstractFlowTest;
import adams.flow.control.Flow;
import adams.flow.core.AbstractActor;

/**
 * Tests the MOAClustering actor.
 * <p/>
 * Dummy test at the moment.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class MOAClusteringTest
  extends AbstractFlowTest {

  /**
   * Initializes the test.
   *
   * @param name	the name of the test
   */
  public MOAClusteringTest(String name) {
    super(name);
  }

  /**
   * Called by JUnit before each test method. Copies from resource files into
   * the tmp directory
   *
   * @throws Exception if an error occurs
   */
  @Override
  protected void setUp() throws Exception {
    super.setUp();

    m_TestHelper.copyResourceToTmp("iris.arff");
    // TODO uncomment when clustering in MOA is stable enough
    //m_TestHelper.copyResourceToTmp("cobweb.model");
    m_TestHelper.deleteFileFromTmp("dumpfile.txt");
  }

  /**
   * Called by JUnit after each test method.
   *
   * @throws Exception	if tear-down fails
   */
  @Override
  protected void tearDown() throws Exception {
    m_TestHelper.deleteFileFromTmp("iris.arff");
    // TODO uncomment when clustering in MOA is stable enough
    //m_TestHelper.deleteFileFromTmp("cobweb.model");
    m_TestHelper.deleteFileFromTmp("dumpfile.txt");

    super.tearDown();
  }

  /**
   * Used to create an instance of a specific actor.
   *
   * @return a suitably configured <code>AbstractActor</code> value
   */
  @Override
  public AbstractActor getActor() {
    // TODO uncomment when clustering in MOA is stable enough
    /*
    SingleFileSupplier sfs = new SingleFileSupplier();
    sfs.setFile(new TmpFile("iris.arff"));

    WekaFileReader fr = new WekaFileReader();
    fr.setIncremental(true);

    Remove remove = new Remove();
    remove.setAttributeIndices("last");
    WekaFilter wf = new WekaFilter();
    wf.setFilter(remove);

    MOAClustering cls = new MOAClustering();
    cls.setModelFile(new TmpFile("cobweb.model"));

    DumpFile df = new DumpFile();
    df.setAppend(true);
    df.setOutputFile(new TmpFile("dumpfile.txt"));

    ContainerValuePicker cvp = new ContainerValuePicker();
    cvp.setValueName(WekaClusteringContainer.VALUE_CLUSTER);
    cvp.add(0, df);

    Flow flow = new Flow();
    flow.setActors(new AbstractActor[]{sfs, fr, wf, cls, cvp});

    return flow;
    */
    return new Flow();
  }

  /**
   * Performs a regression test, comparing against previously generated output.
   */
  public void testRegression() {
    // TODO uncomment when clustering in MOA is stable enough
    /*
    performRegressionTest(
	new TmpFile("dumpfile.txt"));
	*/
  }

  /**
   * Returns a test suite.
   *
   * @return		the test suite
   */
  public static Test suite() {
    return new TestSuite(MOAClusteringTest.class);
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
