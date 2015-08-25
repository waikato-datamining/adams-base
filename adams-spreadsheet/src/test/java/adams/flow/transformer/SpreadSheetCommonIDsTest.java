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
 * SpreadSheetCommonIDsTest.java
 * Copyright (C) 2015 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.transformer;

import adams.core.Range;
import adams.core.io.PlaceholderFile;
import adams.core.option.AbstractArgumentOption;
import adams.data.io.input.CsvSpreadSheetReader;
import adams.data.spreadsheet.DenseDataRow;
import adams.data.spreadsheet.SpreadSheet;
import adams.env.Environment;
import adams.flow.AbstractFlowTest;
import adams.flow.control.ArrayProcess;
import adams.flow.control.Flow;
import adams.flow.control.Tee;
import adams.flow.core.AbstractActor;
import adams.flow.execution.NullListener;
import adams.flow.sink.DumpFile;
import adams.flow.source.FileSupplier;
import adams.test.TmpFile;
import junit.framework.Test;
import junit.framework.TestSuite;

import java.util.ArrayList;
import java.util.List;

/**
 * Test for SpreadSheetCommonIDs actor.
 *
 * @author fracpete
 * @author adams.core.option.FlowJUnitTestProducer (code generator)
 * @version $Revision$
 */
public class SpreadSheetCommonIDsTest
  extends AbstractFlowTest {

  /**
   * Initializes the test.
   *
   * @param name	the name of the test
   */
  public SpreadSheetCommonIDsTest(String name) {
    super(name);
  }

  /**
   * Called by JUnit before each test method.
   *
   * @throws Exception 	if an error occurs.
   */
  protected void setUp() throws Exception {
    super.setUp();
    
    m_TestHelper.copyResourceToTmp("iris_with_id-subset1.csv");
    m_TestHelper.copyResourceToTmp("iris_with_id-subset2.csv");
    m_TestHelper.deleteFileFromTmp("dumpfile1.csv");
    m_TestHelper.deleteFileFromTmp("dumpfile2.csv");
  }

  /**
   * Called by JUnit after each test method.
   *
   * @throws Exception	if tear-down fails
   */
  protected void tearDown() throws Exception {
    m_TestHelper.deleteFileFromTmp("iris_with_id-subset1.csv");
    m_TestHelper.deleteFileFromTmp("iris_with_id-subset2.csv");
    m_TestHelper.deleteFileFromTmp("dumpfile1.csv");
    m_TestHelper.deleteFileFromTmp("dumpfile2.csv");
    
    super.tearDown();
  }

  /**
   * Performs a regression test, comparing against previously generated output.
   */
  public void testRegression() {
    performRegressionTest(
        new TmpFile[]{
          new TmpFile("dumpfile1.csv"),
          new TmpFile("dumpfile2.csv")
        });
  }

  /**
   * 
   * Returns a test suite.
   *
   * @return		the test suite
   */
  public static Test suite() {
    return new TestSuite(SpreadSheetCommonIDsTest.class);
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
      List<AbstractActor> actors = new ArrayList<AbstractActor>();

      // Flow.FileSupplier
      FileSupplier filesupplier = new FileSupplier();
      filesupplier.setOutputArray(true);

      argOption = (AbstractArgumentOption) filesupplier.getOptionManager().findByProperty("files");
      List<PlaceholderFile> files = new ArrayList<PlaceholderFile>();
      files.add((PlaceholderFile) argOption.valueOf("${TMP}/iris_with_id-subset1.csv"));
      files.add((PlaceholderFile) argOption.valueOf("${TMP}/iris_with_id-subset2.csv"));
      filesupplier.setFiles(files.toArray(new PlaceholderFile[0]));
      actors.add(filesupplier);

      // Flow.ArrayProcess
      ArrayProcess arrayprocess = new ArrayProcess();
      List<AbstractActor> actors2 = new ArrayList<AbstractActor>();

      // Flow.ArrayProcess.SpreadSheetFileReader
      SpreadSheetFileReader spreadsheetfilereader = new SpreadSheetFileReader();
      CsvSpreadSheetReader csvspreadsheetreader = new CsvSpreadSheetReader();
      DenseDataRow densedatarow = new DenseDataRow();
      csvspreadsheetreader.setDataRowType(densedatarow);

      SpreadSheet spreadsheet = new SpreadSheet();
      csvspreadsheetreader.setSpreadSheetType(spreadsheet);

      argOption = (AbstractArgumentOption) csvspreadsheetreader.getOptionManager().findByProperty("missingValue");
      csvspreadsheetreader.setMissingValue((String) argOption.valueOf(""));
      argOption = (AbstractArgumentOption) csvspreadsheetreader.getOptionManager().findByProperty("textColumns");
      csvspreadsheetreader.setTextColumns((Range) argOption.valueOf("1,last"));
      spreadsheetfilereader.setReader(csvspreadsheetreader);

      actors2.add(spreadsheetfilereader);
      arrayprocess.setActors(actors2.toArray(new AbstractActor[0]));

      actors.add(arrayprocess);

      // Flow.common
      Tee tee = new Tee();
      argOption = (AbstractArgumentOption) tee.getOptionManager().findByProperty("name");
      tee.setName((String) argOption.valueOf("common"));
      List<AbstractActor> actors3 = new ArrayList<AbstractActor>();

      // Flow.common.SpreadSheetCommonIDs
      SpreadSheetCommonIDs spreadsheetcommonids = new SpreadSheetCommonIDs();
      actors3.add(spreadsheetcommonids);

      // Flow.common.DumpFile
      DumpFile dumpfile = new DumpFile();
      argOption = (AbstractArgumentOption) dumpfile.getOptionManager().findByProperty("outputFile");
      dumpfile.setOutputFile((PlaceholderFile) argOption.valueOf("${TMP}/dumpfile1.csv"));
      actors3.add(dumpfile);
      tee.setActors(actors3.toArray(new AbstractActor[0]));

      actors.add(tee);

      // Flow.not in common
      Tee tee2 = new Tee();
      argOption = (AbstractArgumentOption) tee2.getOptionManager().findByProperty("name");
      tee2.setName((String) argOption.valueOf("not in common"));
      List<AbstractActor> actors4 = new ArrayList<AbstractActor>();

      // Flow.not in common.SpreadSheetCommonIDs
      SpreadSheetCommonIDs spreadsheetcommonids2 = new SpreadSheetCommonIDs();
      spreadsheetcommonids2.setInvert(true);

      actors4.add(spreadsheetcommonids2);

      // Flow.not in common.DumpFile
      DumpFile dumpfile2 = new DumpFile();
      argOption = (AbstractArgumentOption) dumpfile2.getOptionManager().findByProperty("outputFile");
      dumpfile2.setOutputFile((PlaceholderFile) argOption.valueOf("${TMP}/dumpfile2.csv"));
      actors4.add(dumpfile2);
      tee2.setActors(actors4.toArray(new AbstractActor[0]));

      actors.add(tee2);
      flow.setActors(actors.toArray(new AbstractActor[0]));

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

