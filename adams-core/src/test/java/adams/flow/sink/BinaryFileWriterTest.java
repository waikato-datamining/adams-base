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
 * BinaryFileWriterTest.java
 * Copyright (C) 2014 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.sink;

import junit.framework.Test;
import junit.framework.TestSuite;
import adams.core.option.AbstractArgumentOption;
import adams.env.Environment;
import adams.flow.AbstractFlowTest;
import adams.flow.control.Flow;
import adams.flow.core.AbstractActor;
import adams.test.TmpFile;

/**
 * Test for BinaryFileWriter actor.
 *
 * @author fracpete
 * @author adams.core.option.FlowJUnitTestProducer (code generator)
 * @version $Revision$
 */
public class BinaryFileWriterTest
  extends AbstractFlowTest {

  /**
   * Initializes the test.
   *
   * @param name	the name of the test
   */
  public BinaryFileWriterTest(String name) {
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
    
    m_TestHelper.copyResourceToTmp("adams_icon.png");
    m_TestHelper.deleteFileFromTmp("dumpfile.png");
  }

  /**
   * Called by JUnit after each test method.
   *
   * @throws Exception	if tear-down fails
   */
  @Override
  protected void tearDown() throws Exception {
    m_TestHelper.deleteFileFromTmp("adams_icon.png");
    m_TestHelper.deleteFileFromTmp("dumpfile.png");
    
    super.tearDown();
  }

  /**
   * Performs a regression test, comparing against previously generated output.
   */
  public void testRegression() {
    performRegressionTest(
        new TmpFile[]{
          new TmpFile("dumpfile.png")
        });
  }

  /**
   * 
   * Returns a test suite.
   *
   * @return		the test suite
   */
  public static Test suite() {
    return new TestSuite(BinaryFileWriterTest.class);
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
      adams.flow.core.AbstractActor[] actors1 = new adams.flow.core.AbstractActor[4];

      // Flow.FileSupplier
      adams.flow.source.FileSupplier filesupplier2 = new adams.flow.source.FileSupplier();
      argOption = (AbstractArgumentOption) filesupplier2.getOptionManager().findByProperty("files");
      adams.core.io.PlaceholderFile[] files3 = new adams.core.io.PlaceholderFile[1];
      files3[0] = (adams.core.io.PlaceholderFile) argOption.valueOf("${TMP}/adams_icon.png");
      filesupplier2.setFiles(files3);
      actors1[0] = filesupplier2;

      // Flow.BinaryFileReader
      adams.flow.transformer.BinaryFileReader binaryfilereader4 = new adams.flow.transformer.BinaryFileReader();
      binaryfilereader4.setOutputArray(true);

      actors1[1] = binaryfilereader4;

      // Flow.Convert
      adams.flow.transformer.Convert convert5 = new adams.flow.transformer.Convert();
      argOption = (AbstractArgumentOption) convert5.getOptionManager().findByProperty("conversion");
      adams.data.conversion.ByteArrayToBlobContainer bytearraytoblobcontainer7 = new adams.data.conversion.ByteArrayToBlobContainer();
      argOption = (AbstractArgumentOption) bytearraytoblobcontainer7.getOptionManager().findByProperty("ID");
      argOption.setVariable("@{file}");
      convert5.setConversion(bytearraytoblobcontainer7);

      actors1[2] = convert5;

      // Flow.BinaryFileWriter
      adams.flow.sink.BinaryFileWriter binaryfilewriter8 = new adams.flow.sink.BinaryFileWriter();
      argOption = (AbstractArgumentOption) binaryfilewriter8.getOptionManager().findByProperty("outputFile");
      binaryfilewriter8.setOutputFile((adams.core.io.PlaceholderFile) argOption.valueOf("${TMP}/dumpfile.png"));
      actors1[3] = binaryfilewriter8;
      flow.setActors(actors1);

      argOption = (AbstractArgumentOption) flow.getOptionManager().findByProperty("flowExecutionListener");
      adams.flow.execution.NullListener nulllistener11 = new adams.flow.execution.NullListener();
      flow.setFlowExecutionListener(nulllistener11);

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

