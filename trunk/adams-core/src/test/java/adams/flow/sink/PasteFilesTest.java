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
 * PasteFilesTest.java
 * Copyright (C) 2012 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.sink;

import java.io.File;

import junit.framework.Test;
import junit.framework.TestSuite;
import adams.core.io.PlaceholderFile;
import adams.env.Environment;
import adams.flow.AbstractFlowTest;
import adams.flow.control.Flow;
import adams.flow.core.AbstractActor;
import adams.flow.sink.PasteFiles;
import adams.flow.source.FileSupplier;
import adams.test.TmpFile;

/**
 * Test case for the PasteFiles actor.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class PasteFilesTest
  extends AbstractFlowTest {

  /**
   * Initializes the test.
   *
   * @param name	the name of the test
   */
  public PasteFilesTest(String name) {
    super(name);
  }

  /**
   * Called by JUnit before each test method.
   *
   * @throws Exception if an error occurs
   */
  protected void setUp() throws Exception {
    super.setUp();

    m_TestHelper.deleteFileFromTmp("dumpfile.csv");
    m_TestHelper.copyResourceToTmp("a.csv");
    m_TestHelper.copyResourceToTmp("b.csv");
    m_TestHelper.copyResourceToTmp("c.csv");
    m_TestHelper.copyResourceToTmp("d.csv");
  }

  /**
   * Called by JUnit after each test method.
   *
   * @throws Exception	if tear-down fails
   */
  protected void tearDown() throws Exception {
    m_TestHelper.deleteFileFromTmp("dumpfile.csv");
    m_TestHelper.deleteFileFromTmp("a.csv");
    m_TestHelper.deleteFileFromTmp("b.csv");
    m_TestHelper.deleteFileFromTmp("c.csv");
    m_TestHelper.deleteFileFromTmp("d.csv");

    super.tearDown();
  }

  /**
   * Used to create an instance of a specific actor.
   *
   * @return a suitably configured <code>AbstractActor</code> value
   */
  public AbstractActor getActor() {
    FileSupplier mfs = new FileSupplier();
    mfs.setOutputArray(true);
    mfs.setFiles(new PlaceholderFile[]{
	new TmpFile("a.csv"),
	new TmpFile("b.csv"),
	new TmpFile("c.csv"),
	new TmpFile("d.csv")
    });
    
    PasteFiles pf = new PasteFiles();
    pf.setOutputFile(new TmpFile("dumpfile.csv"));
    
    Flow flow = new Flow();
    flow.setActors(new AbstractActor[]{
	mfs,
	pf
    });
    
    return flow;
  }

  /**
   * Performs a regression test, comparing against previously generated output.
   */
  public void testRegression() {
    performRegressionTest(
	new File[]{
	    new TmpFile("dumpfile.csv")});
  }

  /**
   * Returns a test suite.
   *
   * @return		the test suite
   */
  public static Test suite() {
    return new TestSuite(PasteFilesTest.class);
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
