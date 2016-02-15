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
 * PDFStampTest.java
 * Copyright (C) 2015 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.transformer;

import adams.core.option.AbstractArgumentOption;
import adams.env.Environment;
import adams.flow.AbstractFlowTest;
import adams.flow.control.Flow;
import adams.flow.core.Actor;
import adams.test.TmpFile;
import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * Test for PDFStamp actor.
 *
 * @author fracpete
 * @author adams.core.option.FlowJUnitTestProducer (code generator)
 * @version $Revision$
 */
public class PDFStampTest
  extends AbstractFlowTest {

  /**
   * Initializes the test.
   *
   * @param name	the name of the test
   */
  public PDFStampTest(String name) {
    super(name);
  }

  /**
   * Called by JUnit before each test method.
   *
   * @throws Exception 	if an error occurs.
   */
  protected void setUp() throws Exception {
    super.setUp();
    
    m_TestHelper.copyResourceToTmp("multiple_images.pdf");
    m_TestHelper.deleteFileFromTmp("dumpfile.txt");
  }

  /**
   * Called by JUnit after each test method.
   *
   * @throws Exception	if tear-down fails
   */
  protected void tearDown() throws Exception {
    m_TestHelper.deleteFileFromTmp("multiple_images.pdf");
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
    return new TestSuite(PDFStampTest.class);
  }

  /**
   * Used to create an instance of a specific actor.
   *
   * @return a suitably configured <code>Actor</code> value
   */
  public Actor getActor() {
    AbstractArgumentOption    argOption;
    
    Flow flow = new Flow();
    
    try {
      argOption = (AbstractArgumentOption) flow.getOptionManager().findByProperty("actors");
      adams.flow.core.Actor[] actors1 = new adams.flow.core.Actor[4];

      // Flow.FileSupplier
      adams.flow.source.FileSupplier filesupplier2 = new adams.flow.source.FileSupplier();
      argOption = (AbstractArgumentOption) filesupplier2.getOptionManager().findByProperty("files");
      adams.core.io.PlaceholderFile[] files3 = new adams.core.io.PlaceholderFile[1];
      files3[0] = (adams.core.io.PlaceholderFile) argOption.valueOf("${TMP}/multiple_images.pdf");
      filesupplier2.setFiles(files3);
      actors1[0] = filesupplier2;

      // Flow.PDFStamp
      adams.flow.transformer.PDFStamp pdfstamp4 = new adams.flow.transformer.PDFStamp();
      argOption = (AbstractArgumentOption) pdfstamp4.getOptionManager().findByProperty("stamper");
      adams.flow.transformer.pdfstamp.Text text6 = new adams.flow.transformer.pdfstamp.Text();
      argOption = (AbstractArgumentOption) text6.getOptionManager().findByProperty("template");
      text6.setTemplate((java.lang.String) argOption.valueOf("- @ -"));
      argOption = (AbstractArgumentOption) text6.getOptionManager().findByProperty("font");
      text6.setFont((adams.core.io.PdfFont) argOption.valueOf("Helvetica-Bold-12"));
      argOption = (AbstractArgumentOption) text6.getOptionManager().findByProperty("X");
      text6.setX((Float) argOption.valueOf("300.0"));
      argOption = (AbstractArgumentOption) text6.getOptionManager().findByProperty("Y");
      text6.setY((Float) argOption.valueOf("800.0"));
      pdfstamp4.setStamper(text6);

      argOption = (AbstractArgumentOption) pdfstamp4.getOptionManager().findByProperty("output");
      pdfstamp4.setOutput((adams.core.io.PlaceholderFile) argOption.valueOf("${TMP}/overlay.pdf"));
      actors1[1] = pdfstamp4;

      // Flow.PDFExtractText
      adams.flow.transformer.PDFExtractText pdfextracttext12 = new adams.flow.transformer.PDFExtractText();
      actors1[2] = pdfextracttext12;

      // Flow.DumpFile
      adams.flow.sink.DumpFile dumpfile13 = new adams.flow.sink.DumpFile();
      argOption = (AbstractArgumentOption) dumpfile13.getOptionManager().findByProperty("outputFile");
      dumpfile13.setOutputFile((adams.core.io.PlaceholderFile) argOption.valueOf("${TMP}/dumpfile.txt"));
      actors1[3] = dumpfile13;
      flow.setActors(actors1);

      argOption = (AbstractArgumentOption) flow.getOptionManager().findByProperty("flowExecutionListener");
      adams.flow.execution.NullListener nulllistener16 = new adams.flow.execution.NullListener();
      flow.setFlowExecutionListener(nulllistener16);

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

