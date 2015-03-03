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
 * ArraySubSampleTest.java
 * Copyright (C) 2014 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.transformer;

import junit.framework.Test;
import junit.framework.TestSuite;
import adams.core.option.AbstractArgumentOption;
import adams.env.Environment;
import adams.flow.AbstractFlowTest;
import adams.flow.control.Flow;
import adams.flow.core.AbstractActor;
import adams.test.TmpFile;

/**
 * Test for ArraySubSample actor.
 *
 * @author fracpete
 * @author adams.core.option.FlowJUnitTestProducer (code generator)
 * @version $Revision$
 */
public class ArraySubSampleTest
  extends AbstractFlowTest {

  /**
   * Initializes the test.
   *
   * @param name	the name of the test
   */
  public ArraySubSampleTest(String name) {
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
    
    m_TestHelper.copyResourceToTmp("numbers.txt");
    m_TestHelper.deleteFileFromTmp("dumpfile.txt");
  }

  /**
   * Called by JUnit after each test method.
   *
   * @throws Exception	if tear-down fails
   */
  @Override
  protected void tearDown() throws Exception {
    m_TestHelper.deleteFileFromTmp("numbers.txt");
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
    return new TestSuite(ArraySubSampleTest.class);
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
      adams.flow.core.AbstractActor[] actors1 = new adams.flow.core.AbstractActor[5];

      // Flow.FileSupplier
      adams.flow.source.FileSupplier filesupplier2 = new adams.flow.source.FileSupplier();
      argOption = (AbstractArgumentOption) filesupplier2.getOptionManager().findByProperty("files");
      adams.core.io.PlaceholderFile[] files3 = new adams.core.io.PlaceholderFile[1];
      files3[0] = (adams.core.io.PlaceholderFile) argOption.valueOf("${TMP}/numbers.txt");
      filesupplier2.setFiles(files3);
      actors1[0] = filesupplier2;

      // Flow.TextFileReader
      adams.flow.transformer.TextFileReader textfilereader4 = new adams.flow.transformer.TextFileReader();
      argOption = (AbstractArgumentOption) textfilereader4.getOptionManager().findByProperty("reader");
      adams.data.io.input.LineArrayTextReader linearraytextreader6 = new adams.data.io.input.LineArrayTextReader();
      textfilereader4.setReader(linearraytextreader6);

      actors1[1] = textfilereader4;

      // Flow.ArraySubSample
      adams.flow.transformer.ArraySubSample arraysubsample7 = new adams.flow.transformer.ArraySubSample();
      argOption = (AbstractArgumentOption) arraysubsample7.getOptionManager().findByProperty("size");
      arraysubsample7.setSize((Double) argOption.valueOf("30.0"));
      argOption = (AbstractArgumentOption) arraysubsample7.getOptionManager().findByProperty("generator");
      adams.data.random.JavaRandomInt javarandomint10 = new adams.data.random.JavaRandomInt();
      arraysubsample7.setGenerator(javarandomint10);

      actors1[2] = arraysubsample7;

      // Flow.ArrayToSequence
      adams.flow.transformer.ArrayToSequence arraytosequence11 = new adams.flow.transformer.ArrayToSequence();
      actors1[3] = arraytosequence11;

      // Flow.DumpFile
      adams.flow.sink.DumpFile dumpfile12 = new adams.flow.sink.DumpFile();
      argOption = (AbstractArgumentOption) dumpfile12.getOptionManager().findByProperty("outputFile");
      dumpfile12.setOutputFile((adams.core.io.PlaceholderFile) argOption.valueOf("${TMP}/dumpfile.txt"));
      dumpfile12.setAppend(true);

      actors1[4] = dumpfile12;
      flow.setActors(actors1);

      argOption = (AbstractArgumentOption) flow.getOptionManager().findByProperty("flowExecutionListener");
      adams.flow.execution.NullListener nulllistener15 = new adams.flow.execution.NullListener();
      flow.setFlowExecutionListener(nulllistener15);

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

