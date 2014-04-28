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
 * SpreadSheetMergeTest.java
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
 * Test for SpreadSheetMerge actor.
 *
 * @author fracpete
 * @author adams.core.option.FlowJUnitTestProducer (code generator)
 * @version $Revision$
 */
public class SpreadSheetMergeTest
  extends AbstractFlowTest {

  /**
   * Initializes the test.
   *
   * @param name	the name of the test
   */
  public SpreadSheetMergeTest(String name) {
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
    
    m_TestHelper.copyResourceToTmp("bolts.csv");
    m_TestHelper.deleteFileFromTmp("dumpfile.csv");
  }

  /**
   * Called by JUnit after each test method.
   *
   * @throws Exception	if tear-down fails
   */
  @Override
  protected void tearDown() throws Exception {
    m_TestHelper.deleteFileFromTmp("bolts.csv");
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
    return new TestSuite(SpreadSheetMergeTest.class);
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
      adams.flow.core.AbstractActor[] abstractactor1 = new adams.flow.core.AbstractActor[4];

      // Flow.FileSupplier
      adams.flow.source.FileSupplier filesupplier2 = new adams.flow.source.FileSupplier();
      argOption = (AbstractArgumentOption) filesupplier2.getOptionManager().findByProperty("files");
      adams.core.io.PlaceholderFile[] placeholderfile3 = new adams.core.io.PlaceholderFile[1];
      placeholderfile3[0] = (adams.core.io.PlaceholderFile) argOption.valueOf("${TMP}/bolts.csv");
      filesupplier2.setFiles(placeholderfile3);

      abstractactor1[0] = filesupplier2;

      // Flow.Tee
      adams.flow.control.Tee tee4 = new adams.flow.control.Tee();
      argOption = (AbstractArgumentOption) tee4.getOptionManager().findByProperty("actors");
      adams.flow.core.AbstractActor[] abstractactor5 = new adams.flow.core.AbstractActor[3];

      // Flow.Tee.SpreadSheetFileReader
      adams.flow.transformer.SpreadSheetFileReader spreadsheetfilereader6 = new adams.flow.transformer.SpreadSheetFileReader();
      argOption = (AbstractArgumentOption) spreadsheetfilereader6.getOptionManager().findByProperty("reader");
      adams.data.io.input.CsvSpreadSheetReader csvspreadsheetreader8 = new adams.data.io.input.CsvSpreadSheetReader();
      spreadsheetfilereader6.setReader(csvspreadsheetreader8);

      abstractactor5[0] = spreadsheetfilereader6;

      // Flow.Tee.SpreadSheetRemoveColumn
      adams.flow.transformer.SpreadSheetRemoveColumn spreadsheetremovecolumn9 = new adams.flow.transformer.SpreadSheetRemoveColumn();
      argOption = (AbstractArgumentOption) spreadsheetremovecolumn9.getOptionManager().findByProperty("position");
      spreadsheetremovecolumn9.setPosition((adams.data.spreadsheet.SpreadSheetColumnRange) argOption.valueOf("1-3"));

      abstractactor5[1] = spreadsheetremovecolumn9;

      // Flow.Tee.SetStorageValue
      adams.flow.transformer.SetStorageValue setstoragevalue11 = new adams.flow.transformer.SetStorageValue();
      argOption = (AbstractArgumentOption) setstoragevalue11.getOptionManager().findByProperty("storageName");
      setstoragevalue11.setStorageName((adams.flow.control.StorageName) argOption.valueOf("first"));

      abstractactor5[2] = setstoragevalue11;
      tee4.setActors(abstractactor5);

      abstractactor1[1] = tee4;

      // Flow.Tee-1
      adams.flow.control.Tee tee13 = new adams.flow.control.Tee();
      argOption = (AbstractArgumentOption) tee13.getOptionManager().findByProperty("name");
      tee13.setName((java.lang.String) argOption.valueOf("Tee-1"));

      argOption = (AbstractArgumentOption) tee13.getOptionManager().findByProperty("actors");
      adams.flow.core.AbstractActor[] abstractactor15 = new adams.flow.core.AbstractActor[3];

      // Flow.Tee-1.SpreadSheetFileReader
      adams.flow.transformer.SpreadSheetFileReader spreadsheetfilereader16 = new adams.flow.transformer.SpreadSheetFileReader();
      argOption = (AbstractArgumentOption) spreadsheetfilereader16.getOptionManager().findByProperty("reader");
      adams.data.io.input.CsvSpreadSheetReader csvspreadsheetreader18 = new adams.data.io.input.CsvSpreadSheetReader();
      spreadsheetfilereader16.setReader(csvspreadsheetreader18);

      abstractactor15[0] = spreadsheetfilereader16;

      // Flow.Tee-1.SpreadSheetRemoveColumn
      adams.flow.transformer.SpreadSheetRemoveColumn spreadsheetremovecolumn19 = new adams.flow.transformer.SpreadSheetRemoveColumn();
      argOption = (AbstractArgumentOption) spreadsheetremovecolumn19.getOptionManager().findByProperty("position");
      spreadsheetremovecolumn19.setPosition((adams.data.spreadsheet.SpreadSheetColumnRange) argOption.valueOf("4-last"));

      abstractactor15[1] = spreadsheetremovecolumn19;

      // Flow.Tee-1.SetStorageValue
      adams.flow.transformer.SetStorageValue setstoragevalue21 = new adams.flow.transformer.SetStorageValue();
      argOption = (AbstractArgumentOption) setstoragevalue21.getOptionManager().findByProperty("storageName");
      setstoragevalue21.setStorageName((adams.flow.control.StorageName) argOption.valueOf("second"));

      abstractactor15[2] = setstoragevalue21;
      tee13.setActors(abstractactor15);

      abstractactor1[2] = tee13;

      // Flow.Trigger
      adams.flow.control.Trigger trigger23 = new adams.flow.control.Trigger();
      argOption = (AbstractArgumentOption) trigger23.getOptionManager().findByProperty("actors");
      adams.flow.core.AbstractActor[] abstractactor24 = new adams.flow.core.AbstractActor[3];

      // Flow.Trigger.StorageValuesArray
      adams.flow.source.StorageValuesArray storagevaluesarray25 = new adams.flow.source.StorageValuesArray();
      argOption = (AbstractArgumentOption) storagevaluesarray25.getOptionManager().findByProperty("storageNames");
      adams.flow.control.StorageName[] storagename26 = new adams.flow.control.StorageName[2];
      storagename26[0] = (adams.flow.control.StorageName) argOption.valueOf("second");
      storagename26[1] = (adams.flow.control.StorageName) argOption.valueOf("first");
      storagevaluesarray25.setStorageNames(storagename26);

      abstractactor24[0] = storagevaluesarray25;

      // Flow.Trigger.SpreadSheetMerge
      adams.flow.transformer.SpreadSheetMerge spreadsheetmerge27 = new adams.flow.transformer.SpreadSheetMerge();
      abstractactor24[1] = spreadsheetmerge27;

      // Flow.Trigger.SpreadSheetFileWriter
      adams.flow.sink.SpreadSheetFileWriter spreadsheetfilewriter28 = new adams.flow.sink.SpreadSheetFileWriter();
      argOption = (AbstractArgumentOption) spreadsheetfilewriter28.getOptionManager().findByProperty("outputFile");
      spreadsheetfilewriter28.setOutputFile((adams.core.io.PlaceholderFile) argOption.valueOf("${TMP}/dumpfile.csv"));

      argOption = (AbstractArgumentOption) spreadsheetfilewriter28.getOptionManager().findByProperty("writer");
      adams.data.io.output.CsvSpreadSheetWriter csvspreadsheetwriter31 = new adams.data.io.output.CsvSpreadSheetWriter();
      spreadsheetfilewriter28.setWriter(csvspreadsheetwriter31);

      abstractactor24[2] = spreadsheetfilewriter28;
      trigger23.setActors(abstractactor24);

      abstractactor1[3] = trigger23;
      flow.setActors(abstractactor1);

      argOption = (AbstractArgumentOption) flow.getOptionManager().findByProperty("flowExecutionListener");
      adams.flow.execution.NullListener nulllistener33 = new adams.flow.execution.NullListener();
      flow.setFlowExecutionListener(nulllistener33);

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

