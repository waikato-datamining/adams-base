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
 * BoofCVDetectLinesTest.java
 * Copyright (C) 2013-2015 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.transformer;

import adams.core.option.AbstractArgumentOption;
import adams.env.Environment;
import adams.flow.AbstractFlowTest;
import adams.flow.control.Flow;
import adams.flow.core.Actor;
import adams.test.AdamsTestCase;
import adams.test.TmpFile;
import junit.framework.Assert;
import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * Test for BoofCVDetectLines actor.
 *
 * @author fracpete
 * @author adams.core.option.FlowJUnitTestProducer (code generator)
 * @version $Revision$
 */
public class BoofCVDetectLinesTest
  extends AbstractFlowTest {

  /**
   * Initializes the test.
   *
   * @param name	the name of the test
   */
  public BoofCVDetectLinesTest(String name) {
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
    
    m_TestHelper.copyResourceToTmp("lines.jpg");
    m_TestHelper.deleteFileFromTmp("dumpfile.csv");
  }

  /**
   * Called by JUnit after each test method.
   *
   * @throws Exception	if tear-down fails
   */
  @Override
  protected void tearDown() throws Exception {
    m_TestHelper.deleteFileFromTmp("lines.jpg");
    m_TestHelper.deleteFileFromTmp("dumpfile.csv");
    
    super.tearDown();
  }

  /**
   * Performs a regression test, comparing against previously generated output.
   */
  public void testRegression() {
    performRegressionTest(
        new TmpFile[]{
          new TmpFile("dumpfile.csv")
        });
  }

  /**
   * 
   * Returns a test suite.
   *
   * @return		the test suite
   */
  public static Test suite() {
    return new TestSuite(BoofCVDetectLinesTest.class);
  }

  /**
   * Used to create an instance of a specific actor.
   *
   * @return a suitably configured <code>Actor</code> value
   */
  @Override
  public Actor getActor() {
    AbstractArgumentOption    argOption;
    
    Flow flow = new Flow();
    
    try {
      argOption = (AbstractArgumentOption) flow.getOptionManager().findByProperty("actors");
      adams.flow.core.Actor[] actors1 = new adams.flow.core.Actor[5];

      // Flow.FileSupplier
      adams.flow.source.FileSupplier filesupplier2 = new adams.flow.source.FileSupplier();
      argOption = (AbstractArgumentOption) filesupplier2.getOptionManager().findByProperty("files");
      adams.core.io.PlaceholderFile[] files3 = new adams.core.io.PlaceholderFile[1];
      files3[0] = (adams.core.io.PlaceholderFile) argOption.valueOf("${TMP}/lines.jpg");
      filesupplier2.setFiles(files3);
      actors1[0] = filesupplier2;

      // Flow.JAIReader
      adams.flow.transformer.ImageReader jaireader4 = new adams.flow.transformer.ImageReader();
      actors1[1] = jaireader4;

      // Flow.Convert
      adams.flow.transformer.Convert convert5 = new adams.flow.transformer.Convert();
      argOption = (AbstractArgumentOption) convert5.getOptionManager().findByProperty("conversion");
      adams.data.conversion.BufferedImageToBoofCV bufferedimagetoboofcv7 = new adams.data.conversion.BufferedImageToBoofCV();
      argOption = (AbstractArgumentOption) bufferedimagetoboofcv7.getOptionManager().findByProperty("imageType");
      bufferedimagetoboofcv7.setImageType((adams.data.boofcv.BoofCVImageType) argOption.valueOf("FLOAT_32"));
      convert5.setConversion(bufferedimagetoboofcv7);

      actors1[2] = convert5;

      // Flow.BoofCVDetectLines
      adams.flow.transformer.BoofCVDetectLines boofcvdetectlines9 = new adams.flow.transformer.BoofCVDetectLines();
      actors1[3] = boofcvdetectlines9;

      // Flow.DumpFile
      adams.flow.sink.DumpFile dumpfile10 = new adams.flow.sink.DumpFile();
      argOption = (AbstractArgumentOption) dumpfile10.getOptionManager().findByProperty("outputFile");
      dumpfile10.setOutputFile((adams.core.io.PlaceholderFile) argOption.valueOf("${TMP}/dumpfile.csv"));
      actors1[4] = dumpfile10;
      flow.setActors(actors1);

      argOption = (AbstractArgumentOption) flow.getOptionManager().findByProperty("flowExecutionListener");
      adams.flow.execution.NullListener nulllistener13 = new adams.flow.execution.NullListener();
      flow.setFlowExecutionListener(nulllistener13);

    }
    catch (Exception e) {
      Assert.fail("Failed to set up actor: " + e);
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
    AdamsTestCase.runTest(suite());
  }
}

