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
 * SpreadSheetMatrixStatisticTest.java
 * Copyright (C) 2016 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.transformer;

import adams.core.io.PlaceholderFile;
import adams.core.option.AbstractArgumentOption;
import adams.data.io.input.CsvSpreadSheetReader;
import adams.data.io.output.CsvSpreadSheetWriter;
import adams.data.spreadsheet.DefaultSpreadSheet;
import adams.data.spreadsheet.DenseDataRow;
import adams.data.spreadsheet.matrixstatistic.AbstractMatrixStatistic;
import adams.data.spreadsheet.matrixstatistic.Max;
import adams.data.spreadsheet.matrixstatistic.Mean;
import adams.data.spreadsheet.matrixstatistic.Median;
import adams.data.spreadsheet.matrixstatistic.Min;
import adams.data.spreadsheet.matrixstatistic.Missing;
import adams.data.spreadsheet.matrixstatistic.MultiMatrixStatistic;
import adams.data.spreadsheet.matrixstatistic.StandardDeviation;
import adams.env.Environment;
import adams.flow.AbstractFlowTest;
import adams.flow.control.Flow;
import adams.flow.core.AbstractActor;
import adams.flow.core.Actor;
import adams.flow.execution.NullListener;
import adams.flow.sink.SpreadSheetFileWriter;
import adams.flow.source.FileSupplier;
import adams.test.TmpFile;
import junit.framework.Test;
import junit.framework.TestSuite;

import java.util.ArrayList;
import java.util.List;

/**
 * Test for SpreadSheetMatrixStatistic actor.
 *
 * @author fracpete
 * @author adams.core.option.FlowJUnitTestProducer (code generator)
 * @version $Revision$
 */
public class SpreadSheetMatrixStatisticTest
  extends AbstractFlowTest {

  /**
   * Initializes the test.
   *
   * @param name	the name of the test
   */
  public SpreadSheetMatrixStatisticTest(String name) {
    super(name);
  }

  /**
   * Called by JUnit before each test method.
   *
   * @throws Exception 	if an error occurs.
   */
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
    return new TestSuite(SpreadSheetMatrixStatisticTest.class);
  }

  /**
   * Used to create an instance of a specific actor.
   *
   * @return a suitably configured <code>AbstractActor</code> value
   */
  public AbstractActor getActor() {
    AbstractArgumentOption    argOption;
    
    Flow flow = new Flow();
    
    try {
      List<Actor> actors = new ArrayList<>();

      // Flow.FileSupplier
      FileSupplier filesupplier = new FileSupplier();
      argOption = (AbstractArgumentOption) filesupplier.getOptionManager().findByProperty("files");
      List<PlaceholderFile> files = new ArrayList<>();
      files.add((PlaceholderFile) argOption.valueOf("${TMP}/bolts.csv"));
      filesupplier.setFiles(files.toArray(new PlaceholderFile[0]));
      actors.add(filesupplier);

      // Flow.SpreadSheetFileReader
      SpreadSheetFileReader spreadsheetfilereader = new SpreadSheetFileReader();
      CsvSpreadSheetReader csvspreadsheetreader = new CsvSpreadSheetReader();
      DenseDataRow densedatarow = new DenseDataRow();
      csvspreadsheetreader.setDataRowType(densedatarow);

      DefaultSpreadSheet defaultspreadsheet = new DefaultSpreadSheet();
      csvspreadsheetreader.setSpreadSheetType(defaultspreadsheet);

      spreadsheetfilereader.setReader(csvspreadsheetreader);

      actors.add(spreadsheetfilereader);

      // Flow.SpreadSheetMatrixStatistic
      SpreadSheetMatrixStatistic spreadsheetmatrixstatistic = new SpreadSheetMatrixStatistic();
      MultiMatrixStatistic multimatrixstatistic = new MultiMatrixStatistic();
      List<AbstractMatrixStatistic> statistics = new ArrayList<>();
      Max max = new Max();
      statistics.add(max);
      Mean mean = new Mean();
      statistics.add(mean);
      Median median = new Median();
      statistics.add(median);
      Min min = new Min();
      statistics.add(min);
      Missing missing = new Missing();
      statistics.add(missing);
      StandardDeviation standarddeviation = new StandardDeviation();
      statistics.add(standarddeviation);
      multimatrixstatistic.setStatistics(statistics.toArray(new AbstractMatrixStatistic[0]));

      spreadsheetmatrixstatistic.setStatistic(multimatrixstatistic);

      actors.add(spreadsheetmatrixstatistic);

      // Flow.SpreadSheetFileWriter
      SpreadSheetFileWriter spreadsheetfilewriter = new SpreadSheetFileWriter();
      argOption = (AbstractArgumentOption) spreadsheetfilewriter.getOptionManager().findByProperty("outputFile");
      spreadsheetfilewriter.setOutputFile((PlaceholderFile) argOption.valueOf("${TMP}/dumpfile.csv"));
      CsvSpreadSheetWriter csvspreadsheetwriter = new CsvSpreadSheetWriter();
      csvspreadsheetwriter.setNumberFormat("#.######");
      spreadsheetfilewriter.setWriter(csvspreadsheetwriter);

      actors.add(spreadsheetfilewriter);
      flow.setActors(actors.toArray(new Actor[0]));

      NullListener nulllistener = new NullListener();
      flow.setFlowExecutionListener(nulllistener);

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

