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
 * SpreadSheetAggregateTest.java
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
 * Test for SpreadSheetAggregate actor.
 *
 * @author fracpete
 * @author adams.core.option.FlowJUnitTestProducer (code generator)
 * @version $Revision$
 */
public class SpreadSheetAggregateTest
  extends AbstractFlowTest {

  /**
   * Initializes the test.
   *
   * @param name	the name of the test
   */
  public SpreadSheetAggregateTest(String name) {
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
    
    m_TestHelper.copyResourceToTmp("iris.csv");
    m_TestHelper.deleteFileFromTmp("dumpfile.csv");
  }

  /**
   * Called by JUnit after each test method.
   *
   * @throws Exception	if tear-down fails
   */
  @Override
  protected void tearDown() throws Exception {
    m_TestHelper.deleteFileFromTmp("iris.csv");
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
    return new TestSuite(SpreadSheetAggregateTest.class);
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
      files3[0] = (adams.core.io.PlaceholderFile) argOption.valueOf("${TMP}/iris.csv");
      filesupplier2.setFiles(files3);
      actors1[0] = filesupplier2;

      // Flow.SpreadSheetFileReader
      adams.flow.transformer.SpreadSheetFileReader spreadsheetfilereader4 = new adams.flow.transformer.SpreadSheetFileReader();
      argOption = (AbstractArgumentOption) spreadsheetfilereader4.getOptionManager().findByProperty("reader");
      adams.data.io.input.CsvSpreadSheetReader csvspreadsheetreader6 = new adams.data.io.input.CsvSpreadSheetReader();
      argOption = (AbstractArgumentOption) csvspreadsheetreader6.getOptionManager().findByProperty("dataRowType");
      adams.data.spreadsheet.DenseDataRow densedatarow8 = new adams.data.spreadsheet.DenseDataRow();
      csvspreadsheetreader6.setDataRowType(densedatarow8);

      argOption = (AbstractArgumentOption) csvspreadsheetreader6.getOptionManager().findByProperty("spreadSheetType");
      adams.data.spreadsheet.SpreadSheet spreadsheet10 = new adams.data.spreadsheet.SpreadSheet();
      csvspreadsheetreader6.setSpreadSheetType(spreadsheet10);

      argOption = (AbstractArgumentOption) csvspreadsheetreader6.getOptionManager().findByProperty("missingValue");
      csvspreadsheetreader6.setMissingValue((java.lang.String) argOption.valueOf(""));
      spreadsheetfilereader4.setReader(csvspreadsheetreader6);

      actors1[1] = spreadsheetfilereader4;

      // Flow.SpreadSheetAggregate
      adams.flow.transformer.SpreadSheetAggregate spreadsheetaggregate12 = new adams.flow.transformer.SpreadSheetAggregate();
      argOption = (AbstractArgumentOption) spreadsheetaggregate12.getOptionManager().findByProperty("keyColumns");
      spreadsheetaggregate12.setKeyColumns((adams.data.spreadsheet.SpreadSheetColumnRange) argOption.valueOf("last"));
      argOption = (AbstractArgumentOption) spreadsheetaggregate12.getOptionManager().findByProperty("aggregates");
      adams.flow.transformer.SpreadSheetAggregate.Aggregate[] aggregates14 = new adams.flow.transformer.SpreadSheetAggregate.Aggregate[4];
      aggregates14[0] = (adams.flow.transformer.SpreadSheetAggregate.Aggregate) argOption.valueOf("MIN");
      aggregates14[1] = (adams.flow.transformer.SpreadSheetAggregate.Aggregate) argOption.valueOf("MAX");
      aggregates14[2] = (adams.flow.transformer.SpreadSheetAggregate.Aggregate) argOption.valueOf("AVERAGE");
      aggregates14[3] = (adams.flow.transformer.SpreadSheetAggregate.Aggregate) argOption.valueOf("STDEV");
      spreadsheetaggregate12.setAggregates(aggregates14);
      actors1[2] = spreadsheetaggregate12;

      // Flow.DumpFile
      adams.flow.sink.DumpFile dumpfile15 = new adams.flow.sink.DumpFile();
      argOption = (AbstractArgumentOption) dumpfile15.getOptionManager().findByProperty("outputFile");
      dumpfile15.setOutputFile((adams.core.io.PlaceholderFile) argOption.valueOf("${TMP}/dumpfile.csv"));
      actors1[3] = dumpfile15;
      flow.setActors(actors1);

      argOption = (AbstractArgumentOption) flow.getOptionManager().findByProperty("flowExecutionListener");
      adams.flow.execution.NullListener nulllistener18 = new adams.flow.execution.NullListener();
      flow.setFlowExecutionListener(nulllistener18);

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

