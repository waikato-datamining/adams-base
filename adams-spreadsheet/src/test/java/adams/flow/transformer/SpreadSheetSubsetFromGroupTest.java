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
 * SpreadSheetSubsetFromGroupTest.java
 * Copyright (C) 2018 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.transformer;

import adams.core.Range;
import adams.core.io.PlaceholderFile;
import adams.core.option.AbstractArgumentOption;
import adams.data.io.input.CsvSpreadSheetReader;
import adams.data.spreadsheet.DefaultSpreadSheet;
import adams.data.spreadsheet.DenseDataRow;
import adams.env.Environment;
import adams.flow.AbstractFlowTest;
import adams.flow.control.Flow;
import adams.flow.control.Tee;
import adams.flow.core.AbstractActor;
import adams.flow.core.Actor;
import adams.flow.execution.NullListener;
import adams.flow.sink.DumpFile;
import adams.flow.source.FileSupplier;
import adams.test.TmpFile;
import junit.framework.Test;
import junit.framework.TestSuite;

import java.util.ArrayList;
import java.util.List;

/**
 * Test for SpreadSheetSubsetFromGroup actor.
 *
 * @author fracpete
 * @author adams.core.option.FlowJUnitTestProducer (code generator)
 */
public class SpreadSheetSubsetFromGroupTest
  extends AbstractFlowTest {

  /**
   * Initializes the test.
   *
   * @param name	the name of the test
   */
  public SpreadSheetSubsetFromGroupTest(String name) {
    super(name);
  }

  /**
   * Called by JUnit before each test method.
   *
   * @throws Exception 	if an error occurs.
   */
  protected void setUp() throws Exception {
    super.setUp();
    
    m_TestHelper.copyResourceToTmp("grouped.csv");
    m_TestHelper.deleteFileFromTmp("dumpfile.txt");
  }

  /**
   * Called by JUnit after each test method.
   *
   * @throws Exception	if tear-down fails
   */
  protected void tearDown() throws Exception {
    m_TestHelper.deleteFileFromTmp("grouped.csv");
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
    return new TestSuite(SpreadSheetSubsetFromGroupTest.class);
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
      files.add((PlaceholderFile) argOption.valueOf("${TMP}/grouped.csv"));
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

      // Flow.full
      Tee tee = new Tee();
      argOption = (AbstractArgumentOption) tee.getOptionManager().findByProperty("name");
      tee.setName((String) argOption.valueOf("full"));
      List<Actor> actors2 = new ArrayList<>();

      // Flow.full.SpreadSheetSubsetFromGroup
      SpreadSheetSubsetFromGroup spreadsheetsubsetfromgroup = new SpreadSheetSubsetFromGroup();
      actors2.add(spreadsheetsubsetfromgroup);

      // Flow.full.DumpFile
      DumpFile dumpfile = new DumpFile();
      argOption = (AbstractArgumentOption) dumpfile.getOptionManager().findByProperty("outputFile");
      dumpfile.setOutputFile((PlaceholderFile) argOption.valueOf("${TMP}/dumpfile.txt"));
      actors2.add(dumpfile);
      tee.setActors(actors2.toArray(new Actor[0]));

      actors.add(tee);

      // Flow.first
      Tee tee2 = new Tee();
      argOption = (AbstractArgumentOption) tee2.getOptionManager().findByProperty("name");
      tee2.setName((String) argOption.valueOf("first"));
      List<Actor> actors3 = new ArrayList<>();

      // Flow.first.SpreadSheetSubsetFromGroup
      SpreadSheetSubsetFromGroup spreadsheetsubsetfromgroup2 = new SpreadSheetSubsetFromGroup();
      argOption = (AbstractArgumentOption) spreadsheetsubsetfromgroup2.getOptionManager().findByProperty("rows");
      spreadsheetsubsetfromgroup2.setRows((Range) argOption.valueOf("first"));
      actors3.add(spreadsheetsubsetfromgroup2);

      // Flow.first.DumpFile
      DumpFile dumpfile2 = new DumpFile();
      argOption = (AbstractArgumentOption) dumpfile2.getOptionManager().findByProperty("outputFile");
      dumpfile2.setOutputFile((PlaceholderFile) argOption.valueOf("${TMP}/dumpfile.txt"));
      dumpfile2.setAppend(true);
      actors3.add(dumpfile2);
      tee2.setActors(actors3.toArray(new Actor[0]));

      actors.add(tee2);

      // Flow.last
      Tee tee3 = new Tee();
      argOption = (AbstractArgumentOption) tee3.getOptionManager().findByProperty("name");
      tee3.setName((String) argOption.valueOf("last"));
      List<Actor> actors4 = new ArrayList<>();

      // Flow.last.SpreadSheetSubsetFromGroup
      SpreadSheetSubsetFromGroup spreadsheetsubsetfromgroup3 = new SpreadSheetSubsetFromGroup();
      argOption = (AbstractArgumentOption) spreadsheetsubsetfromgroup3.getOptionManager().findByProperty("rows");
      spreadsheetsubsetfromgroup3.setRows((Range) argOption.valueOf("last"));
      actors4.add(spreadsheetsubsetfromgroup3);

      // Flow.last.DumpFile
      DumpFile dumpfile3 = new DumpFile();
      argOption = (AbstractArgumentOption) dumpfile3.getOptionManager().findByProperty("outputFile");
      dumpfile3.setOutputFile((PlaceholderFile) argOption.valueOf("${TMP}/dumpfile.txt"));
      dumpfile3.setAppend(true);
      actors4.add(dumpfile3);
      tee3.setActors(actors4.toArray(new Actor[0]));

      actors.add(tee3);

      // Flow.second
      Tee tee4 = new Tee();
      argOption = (AbstractArgumentOption) tee4.getOptionManager().findByProperty("name");
      tee4.setName((String) argOption.valueOf("second"));
      List<Actor> actors5 = new ArrayList<>();

      // Flow.second.SpreadSheetSubsetFromGroup
      SpreadSheetSubsetFromGroup spreadsheetsubsetfromgroup4 = new SpreadSheetSubsetFromGroup();
      argOption = (AbstractArgumentOption) spreadsheetsubsetfromgroup4.getOptionManager().findByProperty("rows");
      spreadsheetsubsetfromgroup4.setRows((Range) argOption.valueOf("2"));
      actors5.add(spreadsheetsubsetfromgroup4);

      // Flow.second.DumpFile
      DumpFile dumpfile4 = new DumpFile();
      argOption = (AbstractArgumentOption) dumpfile4.getOptionManager().findByProperty("outputFile");
      dumpfile4.setOutputFile((PlaceholderFile) argOption.valueOf("${TMP}/dumpfile.txt"));
      dumpfile4.setAppend(true);
      actors5.add(dumpfile4);
      tee4.setActors(actors5.toArray(new Actor[0]));

      actors.add(tee4);
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

