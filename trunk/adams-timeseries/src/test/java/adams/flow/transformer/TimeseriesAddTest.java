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
 * TimeseriesAddTest.java
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
 * Test for TimeseriesAdd actor.
 *
 * @author fracpete
 * @author adams.core.option.FlowJUnitTestProducer (code generator)
 * @version $Revision$
 */
public class TimeseriesAddTest
  extends AbstractFlowTest {

  /**
   * Initializes the test.
   *
   * @param name	the name of the test
   */
  public TimeseriesAddTest(String name) {
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
    
    m_TestHelper.copyResourceToTmp("wine.arff");
    m_TestHelper.deleteFileFromTmp("dumpfile.csv");
  }

  /**
   * Called by JUnit after each test method.
   *
   * @throws Exception	if tear-down fails
   */
  @Override
  protected void tearDown() throws Exception {
    m_TestHelper.deleteFileFromTmp("wine.arff");
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
    return new TestSuite(TimeseriesAddTest.class);
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
      files3[0] = (adams.core.io.PlaceholderFile) argOption.valueOf("${TMP}/wine.arff");
      filesupplier2.setFiles(files3);
      actors1[0] = filesupplier2;

      // Flow.SpreadSheetFileReader
      adams.flow.transformer.SpreadSheetFileReader spreadsheetfilereader4 = new adams.flow.transformer.SpreadSheetFileReader();
      argOption = (AbstractArgumentOption) spreadsheetfilereader4.getOptionManager().findByProperty("reader");
      adams.data.io.input.ArffSpreadSheetReader arffspreadsheetreader6 = new adams.data.io.input.ArffSpreadSheetReader();
      spreadsheetfilereader4.setReader(arffspreadsheetreader6);

      actors1[1] = spreadsheetfilereader4;

      // Flow.red
      adams.flow.control.Tee tee7 = new adams.flow.control.Tee();
      argOption = (AbstractArgumentOption) tee7.getOptionManager().findByProperty("name");
      tee7.setName((java.lang.String) argOption.valueOf("red"));
      argOption = (AbstractArgumentOption) tee7.getOptionManager().findByProperty("actors");
      adams.flow.core.AbstractActor[] actors9 = new adams.flow.core.AbstractActor[4];

      // Flow.red.Convert
      adams.flow.transformer.Convert convert10 = new adams.flow.transformer.Convert();
      argOption = (AbstractArgumentOption) convert10.getOptionManager().findByProperty("conversion");
      adams.data.conversion.SpreadSheetToTimeseries spreadsheettotimeseries12 = new adams.data.conversion.SpreadSheetToTimeseries();
      argOption = (AbstractArgumentOption) spreadsheettotimeseries12.getOptionManager().findByProperty("dateColumn");
      spreadsheettotimeseries12.setDateColumn((adams.data.spreadsheet.SpreadSheetColumnIndex) argOption.valueOf("Date"));
      argOption = (AbstractArgumentOption) spreadsheettotimeseries12.getOptionManager().findByProperty("valueColumn");
      spreadsheettotimeseries12.setValueColumn((adams.data.spreadsheet.SpreadSheetColumnIndex) argOption.valueOf("Red"));
      convert10.setConversion(spreadsheettotimeseries12);

      actors9[0] = convert10;

      // Flow.red.SetID
      adams.flow.transformer.SetID setid15 = new adams.flow.transformer.SetID();
      argOption = (AbstractArgumentOption) setid15.getOptionManager().findByProperty("ID");
      setid15.setID((java.lang.String) argOption.valueOf("red+white"));
      actors9[1] = setid15;

      // Flow.red.Cast
      adams.flow.control.Cast cast17 = new adams.flow.control.Cast();
      argOption = (AbstractArgumentOption) cast17.getOptionManager().findByProperty("classname");
      cast17.setClassname((java.lang.String) argOption.valueOf("adams.data.timeseries.Timeseries"));
      actors9[2] = cast17;

      // Flow.red.TimeseriesAdd
      adams.flow.transformer.TimeseriesAdd timeseriesadd19 = new adams.flow.transformer.TimeseriesAdd();
      argOption = (AbstractArgumentOption) timeseriesadd19.getOptionManager().findByProperty("storageName");
      timeseriesadd19.setStorageName((adams.flow.control.StorageName) argOption.valueOf("series"));
      actors9[3] = timeseriesadd19;
      tee7.setActors(actors9);

      actors1[2] = tee7;

      // Flow.white
      adams.flow.control.Tee tee21 = new adams.flow.control.Tee();
      argOption = (AbstractArgumentOption) tee21.getOptionManager().findByProperty("name");
      tee21.setName((java.lang.String) argOption.valueOf("white"));
      argOption = (AbstractArgumentOption) tee21.getOptionManager().findByProperty("actors");
      adams.flow.core.AbstractActor[] actors23 = new adams.flow.core.AbstractActor[4];

      // Flow.white.Convert
      adams.flow.transformer.Convert convert24 = new adams.flow.transformer.Convert();
      argOption = (AbstractArgumentOption) convert24.getOptionManager().findByProperty("conversion");
      adams.data.conversion.SpreadSheetToTimeseries spreadsheettotimeseries26 = new adams.data.conversion.SpreadSheetToTimeseries();
      argOption = (AbstractArgumentOption) spreadsheettotimeseries26.getOptionManager().findByProperty("dateColumn");
      spreadsheettotimeseries26.setDateColumn((adams.data.spreadsheet.SpreadSheetColumnIndex) argOption.valueOf("Date"));
      argOption = (AbstractArgumentOption) spreadsheettotimeseries26.getOptionManager().findByProperty("valueColumn");
      spreadsheettotimeseries26.setValueColumn((adams.data.spreadsheet.SpreadSheetColumnIndex) argOption.valueOf("Dry-white"));
      convert24.setConversion(spreadsheettotimeseries26);

      actors23[0] = convert24;

      // Flow.white.SetID
      adams.flow.transformer.SetID setid29 = new adams.flow.transformer.SetID();
      argOption = (AbstractArgumentOption) setid29.getOptionManager().findByProperty("ID");
      setid29.setID((java.lang.String) argOption.valueOf("red+white"));
      actors23[1] = setid29;

      // Flow.white.Cast
      adams.flow.control.Cast cast31 = new adams.flow.control.Cast();
      argOption = (AbstractArgumentOption) cast31.getOptionManager().findByProperty("classname");
      cast31.setClassname((java.lang.String) argOption.valueOf("adams.data.timeseries.Timeseries"));
      actors23[2] = cast31;

      // Flow.white.TimeseriesAdd
      adams.flow.transformer.TimeseriesAdd timeseriesadd33 = new adams.flow.transformer.TimeseriesAdd();
      argOption = (AbstractArgumentOption) timeseriesadd33.getOptionManager().findByProperty("storageName");
      timeseriesadd33.setStorageName((adams.flow.control.StorageName) argOption.valueOf("series"));
      actors23[3] = timeseriesadd33;
      tee21.setActors(actors23);

      actors1[3] = tee21;

      // Flow.Trigger
      adams.flow.control.Trigger trigger35 = new adams.flow.control.Trigger();
      argOption = (AbstractArgumentOption) trigger35.getOptionManager().findByProperty("actors");
      adams.flow.core.AbstractActor[] actors36 = new adams.flow.core.AbstractActor[3];

      // Flow.Trigger.StorageValue
      adams.flow.source.StorageValue storagevalue37 = new adams.flow.source.StorageValue();
      argOption = (AbstractArgumentOption) storagevalue37.getOptionManager().findByProperty("storageName");
      storagevalue37.setStorageName((adams.flow.control.StorageName) argOption.valueOf("series"));
      actors36[0] = storagevalue37;

      // Flow.Trigger.Convert
      adams.flow.transformer.Convert convert39 = new adams.flow.transformer.Convert();
      argOption = (AbstractArgumentOption) convert39.getOptionManager().findByProperty("conversion");
      adams.data.conversion.TimeseriesToSpreadSheet timeseriestospreadsheet41 = new adams.data.conversion.TimeseriesToSpreadSheet();
      convert39.setConversion(timeseriestospreadsheet41);

      actors36[1] = convert39;

      // Flow.Trigger.DumpFile
      adams.flow.sink.DumpFile dumpfile42 = new adams.flow.sink.DumpFile();
      argOption = (AbstractArgumentOption) dumpfile42.getOptionManager().findByProperty("outputFile");
      dumpfile42.setOutputFile((adams.core.io.PlaceholderFile) argOption.valueOf("${TMP}/dumpfile.csv"));
      actors36[2] = dumpfile42;
      trigger35.setActors(actors36);

      actors1[4] = trigger35;
      flow.setActors(actors1);

      argOption = (AbstractArgumentOption) flow.getOptionManager().findByProperty("flowExecutionListener");
      adams.flow.execution.NullListener nulllistener45 = new adams.flow.execution.NullListener();
      flow.setFlowExecutionListener(nulllistener45);

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

