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
 * TimeseriesAppendTest.java
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
 * Test for TimeseriesAppend actor.
 *
 * @author fracpete
 * @author adams.core.option.FlowJUnitTestProducer (code generator)
 * @version $Revision$
 */
public class TimeseriesAppendTest
  extends AbstractFlowTest {

  /**
   * Initializes the test.
   *
   * @param name	the name of the test
   */
  public TimeseriesAppendTest(String name) {
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
    
    m_TestHelper.copyResourceToTmp("wine-1980-1989.csv");
    m_TestHelper.copyResourceToTmp("wine-1990-1995.csv");
    m_TestHelper.deleteFileFromTmp("dumpfile.csv");
  }

  /**
   * Called by JUnit after each test method.
   *
   * @throws Exception	if tear-down fails
   */
  @Override
  protected void tearDown() throws Exception {
    m_TestHelper.deleteFileFromTmp("wine-1980-1989.csv");
    m_TestHelper.deleteFileFromTmp("wine-1990-1995.csv");
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
    return new TestSuite(TimeseriesAppendTest.class);
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
      adams.flow.core.AbstractActor[] actors1 = new adams.flow.core.AbstractActor[3];

      // Flow.Start
      adams.flow.source.Start start2 = new adams.flow.source.Start();
      actors1[0] = start2;

      // Flow.Trigger
      adams.flow.control.Trigger trigger3 = new adams.flow.control.Trigger();
      argOption = (AbstractArgumentOption) trigger3.getOptionManager().findByProperty("actors");
      adams.flow.core.AbstractActor[] actors4 = new adams.flow.core.AbstractActor[4];

      // Flow.Trigger.FileSupplier
      adams.flow.source.FileSupplier filesupplier5 = new adams.flow.source.FileSupplier();
      argOption = (AbstractArgumentOption) filesupplier5.getOptionManager().findByProperty("files");
      adams.core.io.PlaceholderFile[] files6 = new adams.core.io.PlaceholderFile[2];
      files6[0] = (adams.core.io.PlaceholderFile) argOption.valueOf("${TMP}/wine-1980-1989.csv");
      files6[1] = (adams.core.io.PlaceholderFile) argOption.valueOf("${TMP}/wine-1990-1995.csv");
      filesupplier5.setFiles(files6);
      actors4[0] = filesupplier5;

      // Flow.Trigger.SpreadSheetFileReader
      adams.flow.transformer.SpreadSheetFileReader spreadsheetfilereader7 = new adams.flow.transformer.SpreadSheetFileReader();
      argOption = (AbstractArgumentOption) spreadsheetfilereader7.getOptionManager().findByProperty("reader");
      adams.data.io.input.CsvSpreadSheetReader csvspreadsheetreader9 = new adams.data.io.input.CsvSpreadSheetReader();
      spreadsheetfilereader7.setReader(csvspreadsheetreader9);

      actors4[1] = spreadsheetfilereader7;

      // Flow.Trigger.Convert
      adams.flow.transformer.Convert convert10 = new adams.flow.transformer.Convert();
      argOption = (AbstractArgumentOption) convert10.getOptionManager().findByProperty("conversion");
      adams.data.conversion.SpreadSheetToTimeseries spreadsheettotimeseries12 = new adams.data.conversion.SpreadSheetToTimeseries();
      convert10.setConversion(spreadsheettotimeseries12);

      actors4[2] = convert10;

      // Flow.Trigger.TimeseriesAppend
      adams.flow.transformer.TimeseriesAppend timeseriesappend13 = new adams.flow.transformer.TimeseriesAppend();
      argOption = (AbstractArgumentOption) timeseriesappend13.getOptionManager().findByProperty("storageName");
      timeseriesappend13.setStorageName((adams.flow.control.StorageName) argOption.valueOf("combined"));
      actors4[3] = timeseriesappend13;
      trigger3.setActors(actors4);

      actors1[1] = trigger3;

      // Flow.Trigger-1
      adams.flow.control.Trigger trigger15 = new adams.flow.control.Trigger();
      argOption = (AbstractArgumentOption) trigger15.getOptionManager().findByProperty("name");
      trigger15.setName((java.lang.String) argOption.valueOf("Trigger-1"));
      argOption = (AbstractArgumentOption) trigger15.getOptionManager().findByProperty("actors");
      adams.flow.core.AbstractActor[] actors17 = new adams.flow.core.AbstractActor[3];

      // Flow.Trigger-1.StorageValue
      adams.flow.source.StorageValue storagevalue18 = new adams.flow.source.StorageValue();
      argOption = (AbstractArgumentOption) storagevalue18.getOptionManager().findByProperty("storageName");
      storagevalue18.setStorageName((adams.flow.control.StorageName) argOption.valueOf("combined"));
      actors17[0] = storagevalue18;

      // Flow.Trigger-1.Convert
      adams.flow.transformer.Convert convert20 = new adams.flow.transformer.Convert();
      argOption = (AbstractArgumentOption) convert20.getOptionManager().findByProperty("conversion");
      adams.data.conversion.TimeseriesToSpreadSheet timeseriestospreadsheet22 = new adams.data.conversion.TimeseriesToSpreadSheet();
      convert20.setConversion(timeseriestospreadsheet22);

      actors17[1] = convert20;

      // Flow.Trigger-1.SpreadSheetFileWriter
      adams.flow.sink.SpreadSheetFileWriter spreadsheetfilewriter23 = new adams.flow.sink.SpreadSheetFileWriter();
      argOption = (AbstractArgumentOption) spreadsheetfilewriter23.getOptionManager().findByProperty("outputFile");
      spreadsheetfilewriter23.setOutputFile((adams.core.io.PlaceholderFile) argOption.valueOf("${TMP}/dumpfile.csv"));
      argOption = (AbstractArgumentOption) spreadsheetfilewriter23.getOptionManager().findByProperty("writer");
      adams.data.io.output.CsvSpreadSheetWriter csvspreadsheetwriter26 = new adams.data.io.output.CsvSpreadSheetWriter();
      spreadsheetfilewriter23.setWriter(csvspreadsheetwriter26);

      actors17[2] = spreadsheetfilewriter23;
      trigger15.setActors(actors17);

      actors1[2] = trigger15;
      flow.setActors(actors1);

      argOption = (AbstractArgumentOption) flow.getOptionManager().findByProperty("flowExecutionListener");
      adams.flow.execution.NullListener nulllistener28 = new adams.flow.execution.NullListener();
      flow.setFlowExecutionListener(nulllistener28);

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

