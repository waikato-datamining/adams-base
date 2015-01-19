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
 * DecodeBarcodeTest.java
 * Copyright (C) 2015 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.transformer;

import adams.env.Environment;
import adams.core.option.AbstractArgumentOption;
import adams.core.option.OptionUtils;
import junit.framework.Test;
import junit.framework.TestSuite;
import adams.flow.core.AbstractActor;
import adams.flow.control.Flow;
import adams.flow.AbstractFlowTest;
import adams.test.TmpFile;

/**
 * Test for DecodeBarcode actor.
 *
 * @author fracpete
 * @author adams.core.option.FlowJUnitTestProducer (code generator)
 * @version $Revision$
 */
public class DecodeBarcodeTest
  extends AbstractFlowTest {

  /**
   * Initializes the test.
   *
   * @param name	the name of the test
   */
  public DecodeBarcodeTest(String name) {
    super(name);
  }

  /**
   * Called by JUnit before each test method.
   *
   * @throws Exception 	if an error occurs.
   */
  protected void setUp() throws Exception {
    super.setUp();
    
    m_TestHelper.deleteFileFromTmp("dumpfile.txt");
  }

  /**
   * Called by JUnit after each test method.
   *
   * @throws Exception	if tear-down fails
   */
  protected void tearDown() throws Exception {
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
    return new TestSuite(DecodeBarcodeTest.class);
  }

  /**
   * Used to create an instance of a specific actor.
   *
   * @return a suitably configured <code>AbstractActor</code> value
   */
  public AbstractActor getActor() {
    AbstractArgumentOption    argOption;
    
    Flow flow = new Flow();
    
    try {
      argOption = (AbstractArgumentOption) flow.getOptionManager().findByProperty("actors");
      adams.flow.core.AbstractActor[] actors1 = new adams.flow.core.AbstractActor[6];

      // Flow.NewImage
      adams.flow.source.NewImage newimage2 = new adams.flow.source.NewImage();
      argOption = (AbstractArgumentOption) newimage2.getOptionManager().findByProperty("width");
      newimage2.setWidth((Integer) argOption.valueOf("500"));
      argOption = (AbstractArgumentOption) newimage2.getOptionManager().findByProperty("height");
      newimage2.setHeight((Integer) argOption.valueOf("500"));
      argOption = (AbstractArgumentOption) newimage2.getOptionManager().findByProperty("conversion");
      adams.data.conversion.BufferedImageToBufferedImage bufferedimagetobufferedimage6 = new adams.data.conversion.BufferedImageToBufferedImage();
      newimage2.setConversion(bufferedimagetobufferedimage6);

      actors1[0] = newimage2;

      // Flow.Draw
      adams.flow.transformer.Draw draw7 = new adams.flow.transformer.Draw();
      argOption = (AbstractArgumentOption) draw7.getOptionManager().findByProperty("operation");
      adams.flow.transformer.draw.Barcode barcode9 = new adams.flow.transformer.draw.Barcode();
      argOption = (AbstractArgumentOption) barcode9.getOptionManager().findByProperty("encoder");
      adams.data.barcode.encode.QRCode qrcode11 = new adams.data.barcode.encode.QRCode();
      argOption = (AbstractArgumentOption) qrcode11.getOptionManager().findByProperty("width");
      qrcode11.setWidth((Integer) argOption.valueOf("500"));
      argOption = (AbstractArgumentOption) qrcode11.getOptionManager().findByProperty("height");
      qrcode11.setHeight((Integer) argOption.valueOf("500"));
      argOption = (AbstractArgumentOption) qrcode11.getOptionManager().findByProperty("text");
      argOption.setVariable("@{content}");
      barcode9.setEncoder(qrcode11);

      draw7.setOperation(barcode9);

      draw7.setNoCopy(true);

      actors1[1] = draw7;

      // Flow.BufferedImageTransformer
      adams.flow.transformer.BufferedImageTransformer bufferedimagetransformer14 = new adams.flow.transformer.BufferedImageTransformer();
      argOption = (AbstractArgumentOption) bufferedimagetransformer14.getOptionManager().findByProperty("transformAlgorithm");
      adams.data.jai.transformer.Rotate rotate16 = new adams.data.jai.transformer.Rotate();
      argOption = (AbstractArgumentOption) rotate16.getOptionManager().findByProperty("angle");
      rotate16.setAngle((Double) argOption.valueOf("30.0"));
      bufferedimagetransformer14.setTransformAlgorithm(rotate16);

      actors1[2] = bufferedimagetransformer14;

      // Flow.DecodeBarcode
      adams.flow.transformer.DecodeBarcode decodebarcode18 = new adams.flow.transformer.DecodeBarcode();
      actors1[3] = decodebarcode18;

      // Flow.Convert
      adams.flow.transformer.Convert convert19 = new adams.flow.transformer.Convert();
      argOption = (AbstractArgumentOption) convert19.getOptionManager().findByProperty("conversion");
      adams.data.conversion.TextContainerToString textcontainertostring21 = new adams.data.conversion.TextContainerToString();
      convert19.setConversion(textcontainertostring21);

      actors1[4] = convert19;

      // Flow.DumpFile
      adams.flow.sink.DumpFile dumpfile22 = new adams.flow.sink.DumpFile();
      argOption = (AbstractArgumentOption) dumpfile22.getOptionManager().findByProperty("outputFile");
      dumpfile22.setOutputFile((adams.core.io.PlaceholderFile) argOption.valueOf("${TMP}/dumpfile.txt"));
      actors1[5] = dumpfile22;
      flow.setActors(actors1);

      argOption = (AbstractArgumentOption) flow.getOptionManager().findByProperty("flowExecutionListener");
      adams.flow.execution.NullListener nulllistener25 = new adams.flow.execution.NullListener();
      flow.setFlowExecutionListener(nulllistener25);

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

