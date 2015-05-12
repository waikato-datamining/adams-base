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
 * TimeseriesFilterTest.java
 * Copyright (C) 2013 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.transformer;

import adams.data.filter.TimeseriesWindow;
import junit.framework.Test;
import junit.framework.TestSuite;
import adams.core.option.AbstractArgumentOption;
import adams.env.Environment;
import adams.flow.AbstractFlowTest;
import adams.flow.control.Flow;
import adams.flow.core.AbstractActor;
import adams.test.TmpFile;

/**
 * Test for TimeseriesFilter actor.
 *
 * @author fracpete
 * @author adams.core.option.FlowJUnitTestProducer (code generator)
 * @version $Revision$
 */
public class TimeseriesFilterTest
  extends AbstractFlowTest {

  /**
   * Initializes the test.
   *
   * @param name	the name of the test
   */
  public TimeseriesFilterTest(String name) {
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
    return new TestSuite(TimeseriesFilterTest.class);
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
      adams.flow.core.AbstractActor[] actors1 = new adams.flow.core.AbstractActor[8];

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

      // Flow.TimeseriesFilter
      adams.flow.transformer.TimeseriesFilter timeseriesfilter9 = new adams.flow.transformer.TimeseriesFilter();
      argOption = (AbstractArgumentOption) timeseriesfilter9.getOptionManager().findByProperty("filter");
      TimeseriesWindow window11 = new TimeseriesWindow();
      argOption = (AbstractArgumentOption) window11.getOptionManager().findByProperty("start");
      window11.setStart((adams.core.base.BaseDateTime) argOption.valueOf("1990-01-01 00:00:00"));
      argOption = (AbstractArgumentOption) window11.getOptionManager().findByProperty("end");
      window11.setEnd((adams.core.base.BaseDateTime) argOption.valueOf("1993-12-31 23:59:59"));
      timeseriesfilter9.setFilter(window11);

      actors1[2] = timeseriesfilter9;

      // Flow.Tee
      adams.flow.control.Tee tee14 = new adams.flow.control.Tee();
      argOption = (AbstractArgumentOption) tee14.getOptionManager().findByProperty("actors");
      adams.flow.core.AbstractActor[] actors15 = new adams.flow.core.AbstractActor[2];

      // Flow.Tee.TimeseriesInfo
      adams.flow.transformer.TimeseriesInfo timeseriesinfo16 = new adams.flow.transformer.TimeseriesInfo();
      argOption = (AbstractArgumentOption) timeseriesinfo16.getOptionManager().findByProperty("type");
      timeseriesinfo16.setType((adams.flow.transformer.TimeseriesInfo.InfoType) argOption.valueOf("NUM_POINTS"));
      actors15[0] = timeseriesinfo16;

      // Flow.Tee.DumpFile
      adams.flow.sink.DumpFile dumpfile18 = new adams.flow.sink.DumpFile();
      argOption = (AbstractArgumentOption) dumpfile18.getOptionManager().findByProperty("outputFile");
      dumpfile18.setOutputFile((adams.core.io.PlaceholderFile) argOption.valueOf("${TMP}/dumpfile.txt"));
      dumpfile18.setAppend(true);

      actors15[1] = dumpfile18;
      tee14.setActors(actors15);

      actors1[3] = tee14;

      // Flow.Tee-1
      adams.flow.control.Tee tee20 = new adams.flow.control.Tee();
      argOption = (AbstractArgumentOption) tee20.getOptionManager().findByProperty("name");
      tee20.setName((java.lang.String) argOption.valueOf("Tee-1"));
      argOption = (AbstractArgumentOption) tee20.getOptionManager().findByProperty("actors");
      adams.flow.core.AbstractActor[] actors22 = new adams.flow.core.AbstractActor[2];

      // Flow.Tee-1.TimeseriesInfo
      adams.flow.transformer.TimeseriesInfo timeseriesinfo23 = new adams.flow.transformer.TimeseriesInfo();
      argOption = (AbstractArgumentOption) timeseriesinfo23.getOptionManager().findByProperty("type");
      timeseriesinfo23.setType((adams.flow.transformer.TimeseriesInfo.InfoType) argOption.valueOf("MIN_TIMESTAMP"));
      actors22[0] = timeseriesinfo23;

      // Flow.Tee-1.DumpFile
      adams.flow.sink.DumpFile dumpfile25 = new adams.flow.sink.DumpFile();
      argOption = (AbstractArgumentOption) dumpfile25.getOptionManager().findByProperty("outputFile");
      dumpfile25.setOutputFile((adams.core.io.PlaceholderFile) argOption.valueOf("${TMP}/dumpfile.txt"));
      dumpfile25.setAppend(true);

      actors22[1] = dumpfile25;
      tee20.setActors(actors22);

      actors1[4] = tee20;

      // Flow.Tee-2
      adams.flow.control.Tee tee27 = new adams.flow.control.Tee();
      argOption = (AbstractArgumentOption) tee27.getOptionManager().findByProperty("name");
      tee27.setName((java.lang.String) argOption.valueOf("Tee-2"));
      argOption = (AbstractArgumentOption) tee27.getOptionManager().findByProperty("actors");
      adams.flow.core.AbstractActor[] actors29 = new adams.flow.core.AbstractActor[2];

      // Flow.Tee-2.TimeseriesInfo
      adams.flow.transformer.TimeseriesInfo timeseriesinfo30 = new adams.flow.transformer.TimeseriesInfo();
      argOption = (AbstractArgumentOption) timeseriesinfo30.getOptionManager().findByProperty("type");
      timeseriesinfo30.setType((adams.flow.transformer.TimeseriesInfo.InfoType) argOption.valueOf("MAX_TIMESTAMP"));
      actors29[0] = timeseriesinfo30;

      // Flow.Tee-2.DumpFile
      adams.flow.sink.DumpFile dumpfile32 = new adams.flow.sink.DumpFile();
      argOption = (AbstractArgumentOption) dumpfile32.getOptionManager().findByProperty("outputFile");
      dumpfile32.setOutputFile((adams.core.io.PlaceholderFile) argOption.valueOf("${TMP}/dumpfile.txt"));
      dumpfile32.setAppend(true);

      actors29[1] = dumpfile32;
      tee27.setActors(actors29);

      actors1[5] = tee27;

      // Flow.Tee-3
      adams.flow.control.Tee tee34 = new adams.flow.control.Tee();
      argOption = (AbstractArgumentOption) tee34.getOptionManager().findByProperty("name");
      tee34.setName((java.lang.String) argOption.valueOf("Tee-3"));
      argOption = (AbstractArgumentOption) tee34.getOptionManager().findByProperty("actors");
      adams.flow.core.AbstractActor[] actors36 = new adams.flow.core.AbstractActor[2];

      // Flow.Tee-3.TimeseriesInfo
      adams.flow.transformer.TimeseriesInfo timeseriesinfo37 = new adams.flow.transformer.TimeseriesInfo();
      argOption = (AbstractArgumentOption) timeseriesinfo37.getOptionManager().findByProperty("type");
      timeseriesinfo37.setType((adams.flow.transformer.TimeseriesInfo.InfoType) argOption.valueOf("MIN_VALUE"));
      actors36[0] = timeseriesinfo37;

      // Flow.Tee-3.DumpFile
      adams.flow.sink.DumpFile dumpfile39 = new adams.flow.sink.DumpFile();
      argOption = (AbstractArgumentOption) dumpfile39.getOptionManager().findByProperty("outputFile");
      dumpfile39.setOutputFile((adams.core.io.PlaceholderFile) argOption.valueOf("${TMP}/dumpfile.txt"));
      dumpfile39.setAppend(true);

      actors36[1] = dumpfile39;
      tee34.setActors(actors36);

      actors1[6] = tee34;

      // Flow.Tee-4
      adams.flow.control.Tee tee41 = new adams.flow.control.Tee();
      argOption = (AbstractArgumentOption) tee41.getOptionManager().findByProperty("name");
      tee41.setName((java.lang.String) argOption.valueOf("Tee-4"));
      argOption = (AbstractArgumentOption) tee41.getOptionManager().findByProperty("actors");
      adams.flow.core.AbstractActor[] actors43 = new adams.flow.core.AbstractActor[2];

      // Flow.Tee-4.TimeseriesInfo
      adams.flow.transformer.TimeseriesInfo timeseriesinfo44 = new adams.flow.transformer.TimeseriesInfo();
      argOption = (AbstractArgumentOption) timeseriesinfo44.getOptionManager().findByProperty("type");
      timeseriesinfo44.setType((adams.flow.transformer.TimeseriesInfo.InfoType) argOption.valueOf("MAX_VALUE"));
      actors43[0] = timeseriesinfo44;

      // Flow.Tee-4.DumpFile
      adams.flow.sink.DumpFile dumpfile46 = new adams.flow.sink.DumpFile();
      argOption = (AbstractArgumentOption) dumpfile46.getOptionManager().findByProperty("outputFile");
      dumpfile46.setOutputFile((adams.core.io.PlaceholderFile) argOption.valueOf("${TMP}/dumpfile.txt"));
      dumpfile46.setAppend(true);

      actors43[1] = dumpfile46;
      tee41.setActors(actors43);

      actors1[7] = tee41;
      flow.setActors(actors1);

      argOption = (AbstractArgumentOption) flow.getOptionManager().findByProperty("flowExecutionListener");
      adams.flow.execution.NullListener nulllistener49 = new adams.flow.execution.NullListener();
      flow.setFlowExecutionListener(nulllistener49);

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

