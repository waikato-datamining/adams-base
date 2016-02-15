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
 * ReportFileReaderTest.java
 * Copyright (C) 2011 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.transformer;

import java.io.File;

import junit.framework.Test;
import junit.framework.TestSuite;
import adams.data.io.input.DefaultSimpleReportReader;
import adams.data.io.output.DefaultSimpleCSVReportWriter;
import adams.data.io.output.DefaultSimpleReportWriter;
import adams.flow.AbstractFlowTest;
import adams.core.base.BaseString;
import adams.core.io.FixedFilenameGenerator;
import adams.core.io.PlaceholderDirectory;
import adams.env.Environment;
import adams.flow.control.Flow;
import adams.flow.core.Actor;
import adams.flow.source.StringConstants;
import adams.flow.transformer.ReportFileWriter;
import adams.test.TmpFile;
import adams.test.TmpDirectory;

/**
 * Tests the ReportFileReader/Writer actor.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class ReportFileReaderTest
  extends AbstractFlowTest {

  /**
   * Initializes the test.
   *
   * @param name	the name of the test
   */
  public ReportFileReaderTest(String name) {
    super(name);
  }

  /**
   * Called by JUnit before each test method.
   *
   * @throws Exception if an error occurs
   */
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
  protected void tearDown() throws Exception {
    m_TestHelper.deleteFileFromTmp("out.csv");
    m_TestHelper.deleteFileFromTmp("simple.report");

    super.tearDown();
  }

  /**
   * Used to create an instance of a specific actor.
   *
   * @return a suitably configured <code>Actor</code> value
   */
  public Actor getActor() {
    StringConstants sc = new StringConstants();
    sc.setStrings(new BaseString[]{
	new BaseString("${TMP}/simple.report")
    });

    ReportFileReader fr = new ReportFileReader();
    fr.setReader(new DefaultSimpleReportReader());

    DefaultSimpleCSVReportWriter writer = new DefaultSimpleCSVReportWriter();
    FixedFilenameGenerator ffg = new FixedFilenameGenerator();
    ffg.setDirectory(new TmpDirectory());
    ffg.setName("out.csv");
    ReportFileWriter fw = new ReportFileWriter();
    fw.setFilenameGenerator(ffg);
    fw.setWriter(writer);
    fw.setOutputDir(new PlaceholderDirectory(m_TestHelper.getTmpDirectory()));

    Flow flow = new Flow();
    flow.setActors(new Actor[]{sc, fr, fw});

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
    return new TestSuite(ReportFileReaderTest.class);
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
