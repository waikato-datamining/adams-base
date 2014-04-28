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
 * TimeseriesInfoTest.java
 * Copyright (C) 2013 University of Waikato, Hamilton, New Zealand
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
 * Test for TimeseriesInfo actor.
 *
 * @author fracpete
 * @author adams.core.option.FlowJUnitTestProducer (code generator)
 * @version $Revision$
 */
public class TimeseriesInfoTest
  extends AbstractFlowTest {

  /**
   * Initializes the test.
   *
   * @param name	the name of the test
   */
  public TimeseriesInfoTest(String name) {
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
    
    m_TestHelper.copyResourceToTmp("wine.csv");
    m_TestHelper.deleteFileFromTmp("dumpfile.txt");
  }

  /**
   * Called by JUnit after each test method.
   *
   * @throws Exception	if tear-down fails
   */
  @Override
  protected void tearDown() throws Exception {
    m_TestHelper.deleteFileFromTmp("wine.csv");
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
    return new TestSuite(TimeseriesInfoTest.class);
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
      adams.flow.core.AbstractActor[] actors1 = new adams.flow.core.AbstractActor[7];

      // Flow.FileSupplier
      adams.flow.source.FileSupplier filesupplier2 = new adams.flow.source.FileSupplier();
      argOption = (AbstractArgumentOption) filesupplier2.getOptionManager().findByProperty("files");
      adams.core.io.PlaceholderFile[] files3 = new adams.core.io.PlaceholderFile[1];
      files3[0] = (adams.core.io.PlaceholderFile) argOption.valueOf("${TMP}/wine.csv");
      filesupplier2.setFiles(files3);
      actors1[0] = filesupplier2;

      // Flow.TimeseriesFileReader
      adams.flow.transformer.TimeseriesFileReader timeseriesfilereader4 = new adams.flow.transformer.TimeseriesFileReader();
      argOption = (AbstractArgumentOption) timeseriesfilereader4.getOptionManager().findByProperty("reader");
      adams.data.io.input.SpreadSheetTimeseriesReader spreadsheettimeseriesreader6 = new adams.data.io.input.SpreadSheetTimeseriesReader();
      argOption = (AbstractArgumentOption) spreadsheettimeseriesreader6.getOptionManager().findByProperty("reader");
      adams.data.io.input.CsvSpreadSheetReader csvspreadsheetreader8 = new adams.data.io.input.CsvSpreadSheetReader();
      spreadsheettimeseriesreader6.setReader(csvspreadsheetreader8);

      timeseriesfilereader4.setReader(spreadsheettimeseriesreader6);

      actors1[1] = timeseriesfilereader4;

      // Flow.Tee
      adams.flow.control.Tee tee9 = new adams.flow.control.Tee();
      argOption = (AbstractArgumentOption) tee9.getOptionManager().findByProperty("actors");
      adams.flow.core.AbstractActor[] actors10 = new adams.flow.core.AbstractActor[2];

      // Flow.Tee.TimeseriesInfo
      adams.flow.transformer.TimeseriesInfo timeseriesinfo11 = new adams.flow.transformer.TimeseriesInfo();
      argOption = (AbstractArgumentOption) timeseriesinfo11.getOptionManager().findByProperty("type");
      timeseriesinfo11.setType((adams.flow.transformer.TimeseriesInfo.InfoType) argOption.valueOf("NUM_POINTS"));
      actors10[0] = timeseriesinfo11;

      // Flow.Tee.DumpFile
      adams.flow.sink.DumpFile dumpfile13 = new adams.flow.sink.DumpFile();
      argOption = (AbstractArgumentOption) dumpfile13.getOptionManager().findByProperty("outputFile");
      dumpfile13.setOutputFile((adams.core.io.PlaceholderFile) argOption.valueOf("${TMP}/dumpfile.txt"));
      dumpfile13.setAppend(true);

      actors10[1] = dumpfile13;
      tee9.setActors(actors10);

      actors1[2] = tee9;

      // Flow.Tee-1
      adams.flow.control.Tee tee15 = new adams.flow.control.Tee();
      argOption = (AbstractArgumentOption) tee15.getOptionManager().findByProperty("name");
      tee15.setName((java.lang.String) argOption.valueOf("Tee-1"));
      argOption = (AbstractArgumentOption) tee15.getOptionManager().findByProperty("actors");
      adams.flow.core.AbstractActor[] actors17 = new adams.flow.core.AbstractActor[2];

      // Flow.Tee-1.TimeseriesInfo
      adams.flow.transformer.TimeseriesInfo timeseriesinfo18 = new adams.flow.transformer.TimeseriesInfo();
      argOption = (AbstractArgumentOption) timeseriesinfo18.getOptionManager().findByProperty("type");
      timeseriesinfo18.setType((adams.flow.transformer.TimeseriesInfo.InfoType) argOption.valueOf("MIN_TIMESTAMP"));
      actors17[0] = timeseriesinfo18;

      // Flow.Tee-1.DumpFile
      adams.flow.sink.DumpFile dumpfile20 = new adams.flow.sink.DumpFile();
      argOption = (AbstractArgumentOption) dumpfile20.getOptionManager().findByProperty("outputFile");
      dumpfile20.setOutputFile((adams.core.io.PlaceholderFile) argOption.valueOf("${TMP}/dumpfile.txt"));
      dumpfile20.setAppend(true);

      actors17[1] = dumpfile20;
      tee15.setActors(actors17);

      actors1[3] = tee15;

      // Flow.Tee-2
      adams.flow.control.Tee tee22 = new adams.flow.control.Tee();
      argOption = (AbstractArgumentOption) tee22.getOptionManager().findByProperty("name");
      tee22.setName((java.lang.String) argOption.valueOf("Tee-2"));
      argOption = (AbstractArgumentOption) tee22.getOptionManager().findByProperty("actors");
      adams.flow.core.AbstractActor[] actors24 = new adams.flow.core.AbstractActor[2];

      // Flow.Tee-2.TimeseriesInfo
      adams.flow.transformer.TimeseriesInfo timeseriesinfo25 = new adams.flow.transformer.TimeseriesInfo();
      argOption = (AbstractArgumentOption) timeseriesinfo25.getOptionManager().findByProperty("type");
      timeseriesinfo25.setType((adams.flow.transformer.TimeseriesInfo.InfoType) argOption.valueOf("MAX_TIMESTAMP"));
      actors24[0] = timeseriesinfo25;

      // Flow.Tee-2.DumpFile
      adams.flow.sink.DumpFile dumpfile27 = new adams.flow.sink.DumpFile();
      argOption = (AbstractArgumentOption) dumpfile27.getOptionManager().findByProperty("outputFile");
      dumpfile27.setOutputFile((adams.core.io.PlaceholderFile) argOption.valueOf("${TMP}/dumpfile.txt"));
      dumpfile27.setAppend(true);

      actors24[1] = dumpfile27;
      tee22.setActors(actors24);

      actors1[4] = tee22;

      // Flow.Tee-3
      adams.flow.control.Tee tee29 = new adams.flow.control.Tee();
      argOption = (AbstractArgumentOption) tee29.getOptionManager().findByProperty("name");
      tee29.setName((java.lang.String) argOption.valueOf("Tee-3"));
      argOption = (AbstractArgumentOption) tee29.getOptionManager().findByProperty("actors");
      adams.flow.core.AbstractActor[] actors31 = new adams.flow.core.AbstractActor[2];

      // Flow.Tee-3.TimeseriesInfo
      adams.flow.transformer.TimeseriesInfo timeseriesinfo32 = new adams.flow.transformer.TimeseriesInfo();
      argOption = (AbstractArgumentOption) timeseriesinfo32.getOptionManager().findByProperty("type");
      timeseriesinfo32.setType((adams.flow.transformer.TimeseriesInfo.InfoType) argOption.valueOf("MIN_VALUE"));
      actors31[0] = timeseriesinfo32;

      // Flow.Tee-3.DumpFile
      adams.flow.sink.DumpFile dumpfile34 = new adams.flow.sink.DumpFile();
      argOption = (AbstractArgumentOption) dumpfile34.getOptionManager().findByProperty("outputFile");
      dumpfile34.setOutputFile((adams.core.io.PlaceholderFile) argOption.valueOf("${TMP}/dumpfile.txt"));
      dumpfile34.setAppend(true);

      actors31[1] = dumpfile34;
      tee29.setActors(actors31);

      actors1[5] = tee29;

      // Flow.Tee-4
      adams.flow.control.Tee tee36 = new adams.flow.control.Tee();
      argOption = (AbstractArgumentOption) tee36.getOptionManager().findByProperty("name");
      tee36.setName((java.lang.String) argOption.valueOf("Tee-4"));
      argOption = (AbstractArgumentOption) tee36.getOptionManager().findByProperty("actors");
      adams.flow.core.AbstractActor[] actors38 = new adams.flow.core.AbstractActor[2];

      // Flow.Tee-4.TimeseriesInfo
      adams.flow.transformer.TimeseriesInfo timeseriesinfo39 = new adams.flow.transformer.TimeseriesInfo();
      argOption = (AbstractArgumentOption) timeseriesinfo39.getOptionManager().findByProperty("type");
      timeseriesinfo39.setType((adams.flow.transformer.TimeseriesInfo.InfoType) argOption.valueOf("MAX_VALUE"));
      actors38[0] = timeseriesinfo39;

      // Flow.Tee-4.DumpFile
      adams.flow.sink.DumpFile dumpfile41 = new adams.flow.sink.DumpFile();
      argOption = (AbstractArgumentOption) dumpfile41.getOptionManager().findByProperty("outputFile");
      dumpfile41.setOutputFile((adams.core.io.PlaceholderFile) argOption.valueOf("${TMP}/dumpfile.txt"));
      dumpfile41.setAppend(true);

      actors38[1] = dumpfile41;
      tee36.setActors(actors38);

      actors1[6] = tee36;
      flow.setActors(actors1);

      argOption = (AbstractArgumentOption) flow.getOptionManager().findByProperty("flowExecutionListener");
      adams.flow.execution.NullListener nulllistener44 = new adams.flow.execution.NullListener();
      flow.setFlowExecutionListener(nulllistener44);

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

