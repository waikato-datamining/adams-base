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
 * SpreadSheetRowToTimeseriesTest.java
 * Copyright (C) 2014 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.transformer;

import junit.framework.Test;
import junit.framework.TestSuite;
import adams.core.base.BaseRegExp;
import adams.core.option.AbstractArgumentOption;
import adams.env.Environment;
import adams.flow.AbstractFlowTest;
import adams.flow.control.Flow;
import adams.flow.core.Actor;

/**
 * Test for SpreadSheetRowToTimeseries actor.
 *
 * @author fracpete
 * @author adams.core.option.FlowJUnitTestProducer (code generator)
 * @version $Revision: 7277 $
 */
public class SpreadSheetRowToTimeseriesTest
  extends AbstractFlowTest {

  /**
   * Initializes the test.
   *
   * @param name	the name of the test
   */
  public SpreadSheetRowToTimeseriesTest(String name) {
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
    
    m_TestHelper.copyResourceToTmp("wine_transposed.csv");
  }

  /**
   * Called by JUnit after each test method.
   *
   * @throws Exception	if tear-down fails
   */
  @Override
  protected void tearDown() throws Exception {
    m_TestHelper.deleteFileFromTmp("wine_transposed.csv");
    
    super.tearDown();
  }

  /**
   * 
   * Returns a test suite.
   *
   * @return		the test suite
   */
  public static Test suite() {
    return new TestSuite(SpreadSheetRowToTimeseriesTest.class);
  }

  /**
   * Used to create an instance of a specific actor.
   *
   * @return a suitably configured <code>Actor</code> value
   */
  @Override
  public Actor getActor() {
    AbstractArgumentOption    argOption;
    
    Flow flow = new Flow();
    
    try {
      argOption = (AbstractArgumentOption) flow.getOptionManager().findByProperty("actors");
      adams.flow.core.Actor[] abstractactor2 = new adams.flow.core.Actor[4];

      // Flow.FileSupplier
      adams.flow.source.FileSupplier filesupplier3 = new adams.flow.source.FileSupplier();
      argOption = (AbstractArgumentOption) filesupplier3.getOptionManager().findByProperty("files");
      adams.core.io.PlaceholderFile[] placeholderfile4 = new adams.core.io.PlaceholderFile[1];
      placeholderfile4[0] = (adams.core.io.PlaceholderFile) argOption.valueOf("${TMP}/wine_transposed.csv");
      filesupplier3.setFiles(placeholderfile4);

      abstractactor2[0] = filesupplier3;

      // Flow.SpreadSheetFileReader
      adams.flow.transformer.SpreadSheetFileReader spreadsheetfilereader5 = new adams.flow.transformer.SpreadSheetFileReader();
      argOption = (AbstractArgumentOption) spreadsheetfilereader5.getOptionManager().findByProperty("reader");
      adams.data.io.input.CsvSpreadSheetReader csvspreadsheetreader7 = new adams.data.io.input.CsvSpreadSheetReader();
      spreadsheetfilereader5.setReader(csvspreadsheetreader7);

      abstractactor2[1] = spreadsheetfilereader5;

      // Flow.SpreadSheetRowToTimeseries
      adams.flow.transformer.SpreadSheetRowToTimeseries spreadsheettotimeseries8 = new adams.flow.transformer.SpreadSheetRowToTimeseries();
      argOption = (AbstractArgumentOption) spreadsheettotimeseries8.getOptionManager().findByProperty("columnID");
      spreadsheettotimeseries8.setColumnID((adams.data.spreadsheet.SpreadSheetColumnIndex) argOption.valueOf("PARENT_ID"));

      argOption = (AbstractArgumentOption) spreadsheettotimeseries8.getOptionManager().findByProperty("columnTimestamp");
      spreadsheettotimeseries8.setRegExpValue(new BaseRegExp("col-.*"));

      abstractactor2[2] = spreadsheettotimeseries8;

      // Flow.TimeseriesDisplay
      adams.flow.sink.TimeseriesDisplay timeseriesdisplay12 = new adams.flow.sink.TimeseriesDisplay();
      argOption = (AbstractArgumentOption) timeseriesdisplay12.getOptionManager().findByProperty("writer");
      adams.gui.print.NullWriter nullwriter14 = new adams.gui.print.NullWriter();
      timeseriesdisplay12.setWriter(nullwriter14);

      abstractactor2[3] = timeseriesdisplay12;
      flow.setActors(abstractactor2);

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

