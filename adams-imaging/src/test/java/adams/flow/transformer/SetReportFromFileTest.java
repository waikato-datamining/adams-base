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
 * SetReportFromFileTest.java
 * Copyright (C) 2012-2015 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.transformer;

import adams.data.io.input.ImageMagickImageReader;
import junit.framework.Test;
import junit.framework.TestSuite;
import adams.core.option.AbstractArgumentOption;
import adams.env.Environment;
import adams.flow.AbstractFlowTest;
import adams.flow.control.Flow;
import adams.flow.core.AbstractActor;
import adams.test.TmpFile;

/**
 * Test for SetReportFromFile actor.
 *
 * @author fracpete
 * @author adams.core.option.FlowJUnitTestProducer (code generator)
 * @version $Revision$
 */
public class SetReportFromFileTest
  extends AbstractFlowTest {

  /**
   * Initializes the test.
   *
   * @param name	the name of the test
   */
  public SetReportFromFileTest(String name) {
    super(name);
  }

  /**
   * Called by JUnit before each test method.
   *
   * @throws Exception 	if an error occurs.
   */
  @Override
  protected void setUp() throws Exception {
    super.setUp();
    
    m_TestHelper.copyResourceToTmp("adams_logo.png");
    m_TestHelper.copyResourceToTmp("test.report");
    m_TestHelper.deleteFileFromTmp("dumpfile.txt");
  }

  /**
   * Called by JUnit after each test method.
   *
   * @throws Exception	if tear-down fails
   */
  @Override
  protected void tearDown() throws Exception {
    m_TestHelper.deleteFileFromTmp("adams_logo.png");
    m_TestHelper.deleteFileFromTmp("test.report");
    m_TestHelper.deleteFileFromTmp("dumpfile.txt");
    
    super.tearDown();
  }
  
  /**
   * Returns the ignored line indices to use in the regression test.
   *
   * @return		the setups
   */
  @Override
  protected int[] getRegressionIgnoredLineIndices() {
    return new int[]{0, 1};
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
    return new TestSuite(SetReportFromFileTest.class);
  }

  /**
   * Used to create an instance of a specific actor.
   *
   * @return a suitably configured <code>AbstractActor</code> value
   */
  @Override
  public AbstractActor getActor() {
    AbstractArgumentOption    argOption;
    
    Flow flow = new Flow();
    
    try {
      argOption = (AbstractArgumentOption) flow.getOptionManager().findByProperty("actors");
      adams.flow.core.AbstractActor[] tmp1 = new adams.flow.core.AbstractActor[4];
      adams.flow.source.FileSupplier tmp2 = new adams.flow.source.FileSupplier();
      argOption = (AbstractArgumentOption) tmp2.getOptionManager().findByProperty("files");
      tmp2.setFiles(new adams.core.io.PlaceholderFile[]{(adams.core.io.PlaceholderFile) argOption.valueOf("${TMP}/adams_logo.png")});

      tmp1[0] = tmp2;
      adams.flow.transformer.ImageReader tmp4 = new adams.flow.transformer.ImageReader();
      tmp4.setReader(new ImageMagickImageReader());
      tmp1[1] = tmp4;
      adams.flow.transformer.SetReportFromFile tmp5 = new adams.flow.transformer.SetReportFromFile();
      argOption = (AbstractArgumentOption) tmp5.getOptionManager().findByProperty("reportFile");
      tmp5.setReportFile((adams.core.io.PlaceholderFile) argOption.valueOf("${TMP}/test.report"));

      argOption = (AbstractArgumentOption) tmp5.getOptionManager().findByProperty("reader");
      adams.data.io.input.DefaultSimpleReportReader tmp8 = new adams.data.io.input.DefaultSimpleReportReader();
      tmp5.setReader(tmp8);

      tmp1[2] = tmp5;
      adams.flow.transformer.ReportFileWriter tmp9 = new adams.flow.transformer.ReportFileWriter();
      argOption = (AbstractArgumentOption) tmp9.getOptionManager().findByProperty("writer");
      adams.data.io.output.DefaultSimpleReportWriter tmp11 = new adams.data.io.output.DefaultSimpleReportWriter();
      tmp9.setWriter(tmp11);

      argOption = (AbstractArgumentOption) tmp9.getOptionManager().findByProperty("outputDir");
      tmp9.setOutputDir((adams.core.io.PlaceholderDirectory) argOption.valueOf("${TMP}"));

      argOption = (AbstractArgumentOption) tmp9.getOptionManager().findByProperty("filenameGenerator");
      adams.core.io.FixedFilenameGenerator tmp14 = new adams.core.io.FixedFilenameGenerator();
      argOption = (AbstractArgumentOption) tmp14.getOptionManager().findByProperty("name");
      tmp14.setName((java.lang.String) argOption.valueOf("dumpfile.txt"));

      tmp9.setFilenameGenerator(tmp14);

      tmp1[3] = tmp9;
      flow.setActors(tmp1);

    }
    catch (Exception e) {
      fail("Failed to set up actor: " + e);
    }
    
    return flow;
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

