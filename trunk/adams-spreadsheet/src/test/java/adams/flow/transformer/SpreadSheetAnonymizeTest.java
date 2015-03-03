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
 * SpreadSheetAnonymizeTest.java
 * Copyright (C) 2012-2014 University of Waikato, Hamilton, New Zealand
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
 * Test for SpreadSheetAnonymize actor.
 *
 * @author fracpete
 * @author adams.core.option.FlowJUnitTestProducer (code generator)
 * @version $Revision$
 */
public class SpreadSheetAnonymizeTest
  extends AbstractFlowTest {

  /**
   * Initializes the test.
   *
   * @param name	the name of the test
   */
  public SpreadSheetAnonymizeTest(String name) {
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
    
    m_TestHelper.copyResourceToTmp("iris_with_id.csv");
    m_TestHelper.deleteFileFromTmp("dumpfile.txt");
  }

  /**
   * Called by JUnit after each test method.
   *
   * @throws Exception	if tear-down fails
   */
  @Override
  protected void tearDown() throws Exception {
    m_TestHelper.deleteFileFromTmp("iris_with_id.csv");
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
    return new TestSuite(SpreadSheetAnonymizeTest.class);
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
      adams.flow.core.AbstractActor[] tmp1 = new adams.flow.core.AbstractActor[6];
      // Flow.CallableActors
      adams.flow.standalone.CallableActors tmp2 = new adams.flow.standalone.CallableActors();
      argOption = (AbstractArgumentOption) tmp2.getOptionManager().findByProperty("actors");
      adams.flow.core.AbstractActor[] tmp3 = new adams.flow.core.AbstractActor[1];
      // Flow.CallableActors.DumpFile
      adams.flow.sink.DumpFile tmp4 = new adams.flow.sink.DumpFile();
      argOption = (AbstractArgumentOption) tmp4.getOptionManager().findByProperty("outputFile");
      tmp4.setOutputFile((adams.core.io.PlaceholderFile) argOption.valueOf("${TMP}/dumpfile.txt"));

      tmp4.setAppend(true);

      tmp3[0] = tmp4;
      tmp2.setActors(tmp3);

      tmp1[0] = tmp2;
      // Flow.FileSupplier
      adams.flow.source.FileSupplier tmp6 = new adams.flow.source.FileSupplier();
      argOption = (AbstractArgumentOption) tmp6.getOptionManager().findByProperty("files");
      tmp6.setFiles(new adams.core.io.PlaceholderFile[]{(adams.core.io.PlaceholderFile) argOption.valueOf("${TMP}/iris_with_id.csv")});

      tmp1[1] = tmp6;
      // Flow.SpreadSheetReader
      adams.flow.transformer.SpreadSheetFileReader tmp8 = new adams.flow.transformer.SpreadSheetFileReader();
      argOption = (AbstractArgumentOption) tmp8.getOptionManager().findByProperty("reader");
      adams.data.io.input.CsvSpreadSheetReader tmp10 = new adams.data.io.input.CsvSpreadSheetReader();
      tmp8.setReader(tmp10);

      tmp1[2] = tmp8;
      // Flow.Tee
      adams.flow.control.Tee tmp11 = new adams.flow.control.Tee();
      argOption = (AbstractArgumentOption) tmp11.getOptionManager().findByProperty("actors");
      adams.flow.core.AbstractActor[] tmp12 = new adams.flow.core.AbstractActor[1];
      // Flow.Tee.GlobalSink
      adams.flow.sink.CallableSink tmp13 = new adams.flow.sink.CallableSink();
      argOption = (AbstractArgumentOption) tmp13.getOptionManager().findByProperty("callableName");
      tmp13.setCallableName((adams.flow.core.CallableActorReference) argOption.valueOf("DumpFile"));

      tmp12[0] = tmp13;
      tmp11.setActors(tmp12);

      tmp1[3] = tmp11;
      // Flow.SpreadSheetAnonymize
      adams.flow.transformer.SpreadSheetAnonymize tmp15 = new adams.flow.transformer.SpreadSheetAnonymize();
      tmp1[4] = tmp15;
      // Flow.GlobalSink
      adams.flow.sink.CallableSink tmp16 = new adams.flow.sink.CallableSink();
      argOption = (AbstractArgumentOption) tmp16.getOptionManager().findByProperty("callableName");
      tmp16.setCallableName((adams.flow.core.CallableActorReference) argOption.valueOf("DumpFile"));

      tmp1[5] = tmp16;
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

