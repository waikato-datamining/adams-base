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
 * SpreadSheetRowStatisticTest.java
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
 * Test for SpreadSheetRowStatistic actor.
 *
 * @author fracpete
 * @author adams.core.option.FlowJUnitTestProducer (code generator)
 * @version $Revision$
 */
public class SpreadSheetRowStatisticTest
  extends AbstractFlowTest {

  /**
   * Initializes the test.
   *
   * @param name	the name of the test
   */
  public SpreadSheetRowStatisticTest(String name) {
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
    return new TestSuite(SpreadSheetRowStatisticTest.class);
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
      argOption = (AbstractArgumentOption) flow.getOptionManager().findByProperty("annotations");
      flow.setAnnotations((adams.core.base.BaseAnnotation) argOption.valueOf("Generates various statistic for a row in a spreadsheet."));
      argOption = (AbstractArgumentOption) flow.getOptionManager().findByProperty("actors");
      adams.flow.core.AbstractActor[] actors2 = new adams.flow.core.AbstractActor[5];

      // Flow.FileSupplier
      adams.flow.source.FileSupplier filesupplier3 = new adams.flow.source.FileSupplier();
      argOption = (AbstractArgumentOption) filesupplier3.getOptionManager().findByProperty("files");
      adams.core.io.PlaceholderFile[] files4 = new adams.core.io.PlaceholderFile[1];
      files4[0] = (adams.core.io.PlaceholderFile) argOption.valueOf("${TMP}/bolts.csv");
      filesupplier3.setFiles(files4);
      actors2[0] = filesupplier3;

      // Flow.SpreadSheetFileReader
      adams.flow.transformer.SpreadSheetFileReader spreadsheetfilereader5 = new adams.flow.transformer.SpreadSheetFileReader();
      argOption = (AbstractArgumentOption) spreadsheetfilereader5.getOptionManager().findByProperty("reader");
      adams.data.io.input.CsvSpreadSheetReader csvspreadsheetreader7 = new adams.data.io.input.CsvSpreadSheetReader();
      argOption = (AbstractArgumentOption) csvspreadsheetreader7.getOptionManager().findByProperty("dataRowType");
      adams.data.spreadsheet.DenseDataRow densedatarow9 = new adams.data.spreadsheet.DenseDataRow();
      csvspreadsheetreader7.setDataRowType(densedatarow9);

      argOption = (AbstractArgumentOption) csvspreadsheetreader7.getOptionManager().findByProperty("spreadSheetType");
      adams.data.spreadsheet.SpreadSheet spreadsheet11 = new adams.data.spreadsheet.SpreadSheet();
      csvspreadsheetreader7.setSpreadSheetType(spreadsheet11);

      spreadsheetfilereader5.setReader(csvspreadsheetreader7);

      actors2[1] = spreadsheetfilereader5;

      // Flow.Convert
      adams.flow.transformer.Convert convert12 = new adams.flow.transformer.Convert();
      argOption = (AbstractArgumentOption) convert12.getOptionManager().findByProperty("conversion");
      adams.data.conversion.TransposeSpreadSheet transposespreadsheet14 = new adams.data.conversion.TransposeSpreadSheet();
      convert12.setConversion(transposespreadsheet14);

      actors2[2] = convert12;

      // Flow.SpreadSheetRowStatistic
      adams.flow.transformer.SpreadSheetRowStatistic spreadsheetrowstatistic15 = new adams.flow.transformer.SpreadSheetRowStatistic();
      argOption = (AbstractArgumentOption) spreadsheetrowstatistic15.getOptionManager().findByProperty("statistic");
      adams.data.spreadsheet.rowstatistic.MultiRowStatistic multirowstatistic17 = new adams.data.spreadsheet.rowstatistic.MultiRowStatistic();
      argOption = (AbstractArgumentOption) multirowstatistic17.getOptionManager().findByProperty("statistics");
      adams.data.spreadsheet.rowstatistic.AbstractRowStatistic[] statistics18 = new adams.data.spreadsheet.rowstatistic.AbstractRowStatistic[9];
      adams.data.spreadsheet.rowstatistic.Min min19 = new adams.data.spreadsheet.rowstatistic.Min();
      statistics18[0] = min19;
      adams.data.spreadsheet.rowstatistic.Max max20 = new adams.data.spreadsheet.rowstatistic.Max();
      statistics18[1] = max20;
      adams.data.spreadsheet.rowstatistic.Mean mean21 = new adams.data.spreadsheet.rowstatistic.Mean();
      statistics18[2] = mean21;
      adams.data.spreadsheet.rowstatistic.StandardDeviation standarddeviation22 = new adams.data.spreadsheet.rowstatistic.StandardDeviation();
      statistics18[3] = standarddeviation22;
      adams.data.spreadsheet.rowstatistic.Median median23 = new adams.data.spreadsheet.rowstatistic.Median();
      statistics18[4] = median23;
      adams.data.spreadsheet.rowstatistic.IQR iqr24 = new adams.data.spreadsheet.rowstatistic.IQR();
      statistics18[5] = iqr24;
      adams.data.spreadsheet.rowstatistic.Distinct distinct25 = new adams.data.spreadsheet.rowstatistic.Distinct();
      statistics18[6] = distinct25;
      adams.data.spreadsheet.rowstatistic.Unique unique26 = new adams.data.spreadsheet.rowstatistic.Unique();
      statistics18[7] = unique26;
      adams.data.spreadsheet.rowstatistic.Missing missing27 = new adams.data.spreadsheet.rowstatistic.Missing();
      statistics18[8] = missing27;
      multirowstatistic17.setStatistics(statistics18);

      spreadsheetrowstatistic15.setStatistic(multirowstatistic17);

      actors2[3] = spreadsheetrowstatistic15;

      // Flow.DumpFile
      adams.flow.sink.DumpFile dumpfile28 = new adams.flow.sink.DumpFile();
      argOption = (AbstractArgumentOption) dumpfile28.getOptionManager().findByProperty("outputFile");
      dumpfile28.setOutputFile((adams.core.io.PlaceholderFile) argOption.valueOf("${TMP}/dumpfile.csv"));
      actors2[4] = dumpfile28;
      flow.setActors(actors2);

      argOption = (AbstractArgumentOption) flow.getOptionManager().findByProperty("flowExecutionListener");
      adams.flow.execution.NullListener nulllistener31 = new adams.flow.execution.NullListener();
      flow.setFlowExecutionListener(nulllistener31);

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

