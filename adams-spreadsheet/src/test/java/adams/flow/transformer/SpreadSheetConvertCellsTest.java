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
 * SpreadSheetConvertCellsTest.java
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
 * Test for SpreadSheetConvertCells actor.
 *
 * @author fracpete
 * @author adams.core.option.FlowJUnitTestProducer (code generator)
 * @version $Revision$
 */
public class SpreadSheetConvertCellsTest
  extends AbstractFlowTest {

  /**
   * Initializes the test.
   *
   * @param name	the name of the test
   */
  public SpreadSheetConvertCellsTest(String name) {
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
    return new TestSuite(SpreadSheetConvertCellsTest.class);
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
      adams.flow.source.FileSupplier singlefilesupplier2 = new adams.flow.source.FileSupplier();
      argOption = (AbstractArgumentOption) singlefilesupplier2.getOptionManager().findByProperty("files");
      singlefilesupplier2.setFiles(new adams.core.io.PlaceholderFile[]{(adams.core.io.PlaceholderFile) argOption.valueOf("${TMP}/bolts.csv")});

      abstractactor1[0] = singlefilesupplier2;

      // Flow.SpreadSheetFileReader
      adams.flow.transformer.SpreadSheetFileReader spreadsheetfilereader4 = new adams.flow.transformer.SpreadSheetFileReader();
      argOption = (AbstractArgumentOption) spreadsheetfilereader4.getOptionManager().findByProperty("reader");
      adams.data.io.input.CsvSpreadSheetReader csvspreadsheetreader6 = new adams.data.io.input.CsvSpreadSheetReader();
      spreadsheetfilereader4.setReader(csvspreadsheetreader6);

      abstractactor1[1] = spreadsheetfilereader4;

      // Flow.SpreadSheetConvertCells
      adams.flow.transformer.SpreadSheetConvertCells spreadsheetconvertcells7 = new adams.flow.transformer.SpreadSheetConvertCells();
      argOption = (AbstractArgumentOption) spreadsheetconvertcells7.getOptionManager().findByProperty("finder");
      adams.data.spreadsheet.cellfinder.CellRange cellrange9 = new adams.data.spreadsheet.cellfinder.CellRange();
      argOption = (AbstractArgumentOption) cellrange9.getOptionManager().findByProperty("columns");
      cellrange9.setColumns((adams.data.spreadsheet.SpreadSheetColumnRange) argOption.valueOf("4,7"));

      spreadsheetconvertcells7.setFinder(cellrange9);

      argOption = (AbstractArgumentOption) spreadsheetconvertcells7.getOptionManager().findByProperty("conversion");
      adams.data.conversion.Round round12 = new adams.data.conversion.Round();
      spreadsheetconvertcells7.setConversion(round12);

      abstractactor1[2] = spreadsheetconvertcells7;

      // Flow.DumpFile
      adams.flow.sink.DumpFile dumpfile13 = new adams.flow.sink.DumpFile();
      argOption = (AbstractArgumentOption) dumpfile13.getOptionManager().findByProperty("outputFile");
      dumpfile13.setOutputFile((adams.core.io.PlaceholderFile) argOption.valueOf("${TMP}/dumpfile.csv"));

      abstractactor1[3] = dumpfile13;
      flow.setActors(abstractactor1);

      argOption = (AbstractArgumentOption) flow.getOptionManager().findByProperty("flowExecutionListener");
      adams.flow.execution.NullListener nulllistener16 = new adams.flow.execution.NullListener();
      flow.setFlowExecutionListener(nulllistener16);

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

