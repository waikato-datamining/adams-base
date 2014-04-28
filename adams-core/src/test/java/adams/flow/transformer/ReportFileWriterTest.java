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
 * ReportFileWriterTest.java
 * Copyright (C) 2011 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.transformer;

import java.io.File;

import junit.framework.Test;
import junit.framework.TestSuite;
import adams.core.io.FixedFilenameGenerator;
import adams.core.io.PlaceholderDirectory;
import adams.data.io.input.DefaultSimpleReportReader;
import adams.data.io.output.DefaultSimpleCSVReportWriter;
import adams.env.Environment;
import adams.flow.AbstractFlowTest;
import adams.flow.control.Flow;
import adams.flow.core.AbstractActor;
import adams.flow.source.FileSupplier;
import adams.flow.standalone.DatabaseConnection;
import adams.test.TmpDirectory;
import adams.test.TmpFile;

/**
 * Tests the ReportFileReader/Writer actor.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class ReportFileWriterTest
  extends AbstractFlowTest {

  /**
   * Initializes the test.
   *
   * @param name	the name of the test
   */
  public ReportFileWriterTest(String name) {
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

    m_TestHelper.copyResourceToTmp("simple.report");
    m_TestHelper.deleteFileFromTmp("out.csv");
  }

  /**
   * Called by JUnit after each test method.
   *
   * @throws Exception	if tear-down fails
   */
  @Override
  protected void tearDown() throws Exception {
    m_TestHelper.deleteFileFromTmp("out.csv");
    m_TestHelper.deleteFileFromTmp("simple.report");

    super.tearDown();
  }

  /**
   * Used to create an instance of a specific actor.
   *
   * @return a suitably configured <code>AbstractActor</code> value
   */
  @Override
  public AbstractActor getActor() {
    DatabaseConnection dbcon = new DatabaseConnection();
    dbcon.setURL(getDatabaseURL());
    dbcon.setUser(getDatabaseUser());
    dbcon.setPassword(getDatabasePassword());

    FileSupplier sfs = new FileSupplier();
    sfs.setFiles(new adams.core.io.PlaceholderFile[]{new TmpFile("simple.report")});

    DefaultSimpleReportReader reader = new DefaultSimpleReportReader();
    ReportFileReader fr = new ReportFileReader();
    fr.setReader(reader);

    DefaultSimpleCSVReportWriter writer = new DefaultSimpleCSVReportWriter();
    FixedFilenameGenerator ffg = new FixedFilenameGenerator();
    ffg.setName("out.csv");
    ffg.setDirectory(new TmpDirectory());
    ReportFileWriter fw = new ReportFileWriter();
    fw.setWriter(writer);
    fw.setFilenameGenerator(ffg);
    fw.setOutputDir(new PlaceholderDirectory(m_TestHelper.getTmpDirectory()));

    Flow flow = new Flow();
    flow.setActors(new AbstractActor[]{dbcon, sfs, fr, fw});

    return flow;
  }

  /**
   * Performs a regression test, comparing against previously generated output.
   */
  public void testRegression() {
    performRegressionTest(
	new File[]{
	    new TmpFile("out.csv")});
  }

  /**
   * Returns a test suite.
   *
   * @return		the test suite
   */
  public static Test suite() {
    return new TestSuite(ReportFileWriterTest.class);
  }

  /**
   * Runs the test from commandline.
   *
   * @param args	ignored
   */
  public static void main(String[] args){
    Environment.setEnvironmentClass(adams.env.Environment.class);
    runTest(suite());
  }
}
