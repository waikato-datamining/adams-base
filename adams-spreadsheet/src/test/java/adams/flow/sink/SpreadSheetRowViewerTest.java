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
 * SpreadSheetRowViewerTest.java
 * Copyright (C) 2016 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.sink;

import adams.core.io.PlaceholderFile;
import adams.core.option.AbstractArgumentOption;
import adams.data.io.input.CsvSpreadSheetReader;
import adams.data.spreadsheet.DefaultSpreadSheet;
import adams.data.spreadsheet.DenseDataRow;
import adams.env.Environment;
import adams.flow.AbstractFlowTest;
import adams.flow.control.Flow;
import adams.flow.core.AbstractActor;
import adams.flow.core.Actor;
import adams.flow.execution.NullListener;
import adams.flow.source.FileSupplier;
import adams.flow.transformer.SpreadSheetFileReader;
import adams.flow.transformer.SpreadSheetRowBuffer;
import adams.flow.transformer.SpreadSheetRowBuffer.Operation;
import adams.gui.print.NullWriter;
import adams.gui.visualization.core.DefaultColorProvider;
import adams.gui.visualization.spreadsheet.SpreadSheetRowLinePaintlet;
import junit.framework.Test;
import junit.framework.TestSuite;

import java.util.ArrayList;
import java.util.List;

/**
 * Test for SpreadSheetRowViewer actor.
 *
 * @author fracpete
 * @author adams.core.option.FlowJUnitTestProducer (code generator)
 * @version $Revision$
 */
public class SpreadSheetRowViewerTest
  extends AbstractFlowTest {

  /**
   * Initializes the test.
   *
   * @param name	the name of the test
   */
  public SpreadSheetRowViewerTest(String name) {
    super(name);
  }

  /**
   * Called by JUnit before each test method.
   *
   * @throws Exception 	if an error occurs.
   */
  protected void setUp() throws Exception {
    super.setUp();
    
    m_TestHelper.copyResourceToTmp("iris_with_id.csv");
  }

  /**
   * Called by JUnit after each test method.
   *
   * @throws Exception	if tear-down fails
   */
  protected void tearDown() throws Exception {
    m_TestHelper.deleteFileFromTmp("iris_with_id.csv");
    
    super.tearDown();
  }

  /**
   * 
   * Returns a test suite.
   *
   * @return		the test suite
   */
  public static Test suite() {
    return new TestSuite(SpreadSheetRowViewerTest.class);
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
      files.add((PlaceholderFile) argOption.valueOf("${TMP}/iris_with_id.csv"));
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

      // Flow.SpreadSheetRowBuffer
      SpreadSheetRowBuffer spreadsheetrowbuffer = new SpreadSheetRowBuffer();
      argOption = (AbstractArgumentOption) spreadsheetrowbuffer.getOptionManager().findByProperty("operation");
      spreadsheetrowbuffer.setOperation((Operation) argOption.valueOf("SPREADSHEET_TO_ROW"));
      actors.add(spreadsheetrowbuffer);

      // Flow.SpreadSheetRowViewer
      SpreadSheetRowViewer spreadsheetrowviewer = new SpreadSheetRowViewer();
      NullWriter nullwriter = new NullWriter();
      spreadsheetrowviewer.setWriter(nullwriter);

      DefaultColorProvider defaultcolorprovider = new DefaultColorProvider();
      spreadsheetrowviewer.setColorProvider(defaultcolorprovider);

      SpreadSheetRowLinePaintlet spreadsheetrowlinepaintlet = new SpreadSheetRowLinePaintlet();
      spreadsheetrowlinepaintlet.setAlwaysShowMarkers(false);

      spreadsheetrowviewer.setPaintlet(spreadsheetrowlinepaintlet);

      argOption = (AbstractArgumentOption) spreadsheetrowviewer.getOptionManager().findByProperty("ID");
      spreadsheetrowviewer.setID((String) argOption.valueOf("ID"));
      actors.add(spreadsheetrowviewer);
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

