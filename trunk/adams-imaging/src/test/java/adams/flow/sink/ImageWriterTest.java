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
 * ImageWriterTest.java
 * Copyright (C) 2014 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.sink;

import junit.framework.Test;
import junit.framework.TestSuite;
import adams.core.option.AbstractArgumentOption;
import adams.data.io.input.JAIImageReader;
import adams.data.io.output.JAIImageWriter;
import adams.env.Environment;
import adams.flow.AbstractFlowTest;
import adams.flow.control.Flow;
import adams.flow.core.AbstractActor;

/**
 * Test for ImageWriter actor.
 *
 * @author fracpete
 * @author adams.core.option.FlowJUnitTestProducer (code generator)
 * @version $Revision: 6839 $
 */
public class ImageWriterTest
  extends AbstractFlowTest {

  /**
   * Initializes the test.
   *
   * @param name	the name of the test
   */
  public ImageWriterTest(String name) {
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
    m_TestHelper.deleteFileFromTmp("dumpfile.png");
  }

  /**
   * Called by JUnit after each test method.
   *
   * @throws Exception	if tear-down fails
   */
  @Override
  protected void tearDown() throws Exception {
    m_TestHelper.deleteFileFromTmp("adams_logo.png");
    m_TestHelper.deleteFileFromTmp("dumpfile.png");

    super.tearDown();
  }

  /**
   *
   * Returns a test suite.
   *
   * @return		the test suite
   */
  public static Test suite() {
    return new TestSuite(ImageWriterTest.class);
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
      adams.flow.core.AbstractActor[] tmp1 = new adams.flow.core.AbstractActor[3];
      adams.flow.source.FileSupplier tmp2 = new adams.flow.source.FileSupplier();
      argOption = (AbstractArgumentOption) tmp2.getOptionManager().findByProperty("files");
      tmp2.setFiles(new adams.core.io.PlaceholderFile[]{(adams.core.io.PlaceholderFile) argOption.valueOf("${TMP}/adams_logo.png")});

      tmp1[0] = tmp2;
      adams.flow.transformer.ImageReader tmp4 = new adams.flow.transformer.ImageReader();
      tmp4.setReader(new JAIImageReader());
      tmp1[1] = tmp4;
      adams.flow.sink.ImageWriter tmp5 = new adams.flow.sink.ImageWriter();
      tmp5.setWriter(new JAIImageWriter());
      argOption = (AbstractArgumentOption) tmp5.getOptionManager().findByProperty("outputFile");
      tmp5.setOutputFile((adams.core.io.PlaceholderFile) argOption.valueOf("${TMP}/dumpfile.png"));

      tmp1[2] = tmp5;
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

