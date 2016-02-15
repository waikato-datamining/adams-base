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
 * BufferedImageMultiImageOperationTest.java
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
 * Test for BufferedImageMultiImageOperation actor.
 *
 * @author fracpete
 * @author adams.core.option.FlowJUnitTestProducer (code generator)
 * @version $Revision$
 */
public class BufferedImageMultiImageOperationTest
  extends AbstractFlowTest {

  /**
   * Initializes the test.
   *
   * @param name	the name of the test
   */
  public BufferedImageMultiImageOperationTest(String name) {
    super(name);
  }

  /**
   * Called by JUnit before each test method.
   *
   * @throws Exception 	if an error occurs.
   */
  protected void setUp() throws Exception {
    super.setUp();
    
    m_TestHelper.copyResourceToTmp("helloworld.png");
    m_TestHelper.copyResourceToTmp("helloworld_mask.png");
    m_TestHelper.deleteFileFromTmp("dumpfile.txt");
  }

  /**
   * Called by JUnit after each test method.
   *
   * @throws Exception	if tear-down fails
   */
  protected void tearDown() throws Exception {
    m_TestHelper.deleteFileFromTmp("helloworld.png");
    m_TestHelper.deleteFileFromTmp("helloworld_mask.png");
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
    return new TestSuite(BufferedImageMultiImageOperationTest.class);
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
      adams.flow.core.Actor[] actors1 = new adams.flow.core.Actor[5];

      // Flow.FileSupplier
      adams.flow.source.FileSupplier filesupplier2 = new adams.flow.source.FileSupplier();
      filesupplier2.setOutputArray(true);

      argOption = (AbstractArgumentOption) filesupplier2.getOptionManager().findByProperty("files");
      adams.core.io.PlaceholderFile[] files3 = new adams.core.io.PlaceholderFile[2];
      files3[0] = (adams.core.io.PlaceholderFile) argOption.valueOf("${TMP}/helloworld.png");
      files3[1] = (adams.core.io.PlaceholderFile) argOption.valueOf("${TMP}/helloworld_mask.png");
      filesupplier2.setFiles(files3);
      actors1[0] = filesupplier2;

      // Flow.ArrayProcess
      adams.flow.control.ArrayProcess arrayprocess4 = new adams.flow.control.ArrayProcess();
      argOption = (AbstractArgumentOption) arrayprocess4.getOptionManager().findByProperty("actors");
      adams.flow.core.Actor[] actors5 = new adams.flow.core.Actor[2];

      // Flow.ArrayProcess.ImageReader
      adams.flow.transformer.ImageReader imagereader6 = new adams.flow.transformer.ImageReader();
      argOption = (AbstractArgumentOption) imagereader6.getOptionManager().findByProperty("reader");
      adams.data.io.input.JAIImageReader jaiimagereader8 = new adams.data.io.input.JAIImageReader();
      imagereader6.setReader(jaiimagereader8);

      actors5[0] = imagereader6;

      // Flow.ArrayProcess.BufferedImageTransformer
      adams.flow.transformer.BufferedImageTransformer bufferedimagetransformer9 = new adams.flow.transformer.BufferedImageTransformer();
      argOption = (AbstractArgumentOption) bufferedimagetransformer9.getOptionManager().findByProperty("transformAlgorithm");
      adams.data.jai.transformer.Resize resize11 = new adams.data.jai.transformer.Resize();
      argOption = (AbstractArgumentOption) resize11.getOptionManager().findByProperty("width");
      resize11.setWidth((Double) argOption.valueOf("50.0"));
      argOption = (AbstractArgumentOption) resize11.getOptionManager().findByProperty("interpolationType");
      resize11.setInterpolationType((adams.data.jai.transformer.Resize.InterpolationType) argOption.valueOf("NEAREST"));
      bufferedimagetransformer9.setTransformAlgorithm(resize11);

      actors5[1] = bufferedimagetransformer9;
      arrayprocess4.setActors(actors5);

      actors1[1] = arrayprocess4;

      // Flow.BufferedImageMultiImageOperation
      adams.flow.transformer.BufferedImageMultiImageOperation bufferedimagemultiimageoperation14 = new adams.flow.transformer.BufferedImageMultiImageOperation();
      argOption = (AbstractArgumentOption) bufferedimagemultiimageoperation14.getOptionManager().findByProperty("operation");
      adams.data.image.multiimageoperation.And and16 = new adams.data.image.multiimageoperation.And();
      bufferedimagemultiimageoperation14.setOperation(and16);

      actors1[2] = bufferedimagemultiimageoperation14;

      // Flow.BufferedImageFeatureGenerator
      adams.flow.transformer.BufferedImageFeatureGenerator bufferedimagefeaturegenerator18 = new adams.flow.transformer.BufferedImageFeatureGenerator();
      argOption = (AbstractArgumentOption) bufferedimagefeaturegenerator18.getOptionManager().findByProperty("algorithm");
      adams.data.image.features.Pixels pixels20 = new adams.data.image.features.Pixels();
      argOption = (AbstractArgumentOption) pixels20.getOptionManager().findByProperty("converter");
      adams.data.featureconverter.Text text22 = new adams.data.featureconverter.Text();
      pixels20.setConverter(text22);

      bufferedimagefeaturegenerator18.setAlgorithm(pixels20);

      actors1[3] = bufferedimagefeaturegenerator18;

      // Flow.DumpFile
      adams.flow.sink.DumpFile dumpfile23 = new adams.flow.sink.DumpFile();
      argOption = (AbstractArgumentOption) dumpfile23.getOptionManager().findByProperty("outputFile");
      dumpfile23.setOutputFile((adams.core.io.PlaceholderFile) argOption.valueOf("${TMP}/dumpfile.txt"));
      actors1[4] = dumpfile23;
      flow.setActors(actors1);

      argOption = (AbstractArgumentOption) flow.getOptionManager().findByProperty("flowExecutionListener");
      adams.flow.execution.NullListener nulllistener26 = new adams.flow.execution.NullListener();
      flow.setFlowExecutionListener(nulllistener26);

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

