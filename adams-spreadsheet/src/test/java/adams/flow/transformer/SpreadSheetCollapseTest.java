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
 * SpreadSheetCollapseTest.java
 * Copyright (C) 2015 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.transformer;

import adams.core.io.PlaceholderFile;
import adams.core.option.AbstractArgumentOption;
import adams.data.io.input.CsvSpreadSheetReader;
import adams.data.spreadsheet.DenseDataRow;
import adams.data.spreadsheet.SpreadSheet;
import adams.env.Environment;
import adams.flow.AbstractFlowTest;
import adams.flow.control.Branch;
import adams.flow.control.Flow;
import adams.flow.control.Sequence;
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
 * Test for SpreadSheetCollapse actor.
 *
 * @author fracpete
 * @author adams.core.option.FlowJUnitTestProducer (code generator)
 * @version $Revision$
 */
public class SpreadSheetCollapseTest
  extends AbstractFlowTest {

  /**
   * Initializes the test.
   *
   * @param name	the name of the test
   */
  public SpreadSheetCollapseTest(String name) {
    super(name);
  }

  /**
   * Called by JUnit before each test method.
   *
   * @throws Exception 	if an error occurs.
   */
  protected void setUp() throws Exception {
    super.setUp();
    
    m_TestHelper.copyResourceToTmp("collapse.csv");
    m_TestHelper.deleteFileFromTmp("dumpfile1.csv");
    m_TestHelper.deleteFileFromTmp("dumpfile2.csv");
    m_TestHelper.deleteFileFromTmp("dumpfile3.csv");
  }

  /**
   * Called by JUnit after each test method.
   *
   * @throws Exception	if tear-down fails
   */
  protected void tearDown() throws Exception {
    m_TestHelper.deleteFileFromTmp("collapse.csv");
    m_TestHelper.deleteFileFromTmp("dumpfile1.csv");
    m_TestHelper.deleteFileFromTmp("dumpfile2.csv");
    m_TestHelper.deleteFileFromTmp("dumpfile3.csv");
    
    super.tearDown();
  }

  /**
   * Performs a regression test, comparing against previously generated output.
   */
  public void testRegression() {
    performRegressionTest(
        new TmpFile[]{
          new TmpFile("dumpfile1.csv"),
          new TmpFile("dumpfile2.csv"),
          new TmpFile("dumpfile3.csv")
        });
  }

  /**
   * 
   * Returns a test suite.
   *
   * @return		the test suite
   */
  public static Test suite() {
    return new TestSuite(SpreadSheetCollapseTest.class);
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
      argOption = (AbstractArgumentOption) filesupplier.getOptionManager().findByProperty("files");
      List<PlaceholderFile> files = new ArrayList<PlaceholderFile>();
      files.add((PlaceholderFile) argOption.valueOf("${TMP}/collapse.csv"));
      filesupplier.setFiles(files.toArray(new PlaceholderFile[0]));
      actors.add(filesupplier);

      // Flow.SpreadSheetFileReader
      SpreadSheetFileReader spreadsheetfilereader = new SpreadSheetFileReader();
      CsvSpreadSheetReader csvspreadsheetreader = new CsvSpreadSheetReader();
      DenseDataRow densedatarow = new DenseDataRow();
      csvspreadsheetreader.setDataRowType(densedatarow);

      SpreadSheet spreadsheet = new SpreadSheet();
      csvspreadsheetreader.setSpreadSheetType(spreadsheet);

      argOption = (AbstractArgumentOption) csvspreadsheetreader.getOptionManager().findByProperty("missingValue");
      csvspreadsheetreader.setMissingValue((String) argOption.valueOf(""));
      spreadsheetfilereader.setReader(csvspreadsheetreader);

      actors.add(spreadsheetfilereader);

      // Flow.Branch
      Branch branch = new Branch();
      List<AbstractActor> branches = new ArrayList<AbstractActor>();

      // Flow.Branch.DumpFile
      DumpFile dumpfile = new DumpFile();
      argOption = (AbstractArgumentOption) dumpfile.getOptionManager().findByProperty("outputFile");
      dumpfile.setOutputFile((PlaceholderFile) argOption.valueOf("${TMP}/dumpfile1.csv"));
      branches.add(dumpfile);

      // Flow.Branch.collapsed
      Sequence sequence = new Sequence();
      argOption = (AbstractArgumentOption) sequence.getOptionManager().findByProperty("name");
      sequence.setName((String) argOption.valueOf("collapsed"));
      List<AbstractActor> actors2 = new ArrayList<AbstractActor>();

      // Flow.Branch.collapsed.SpreadSheetCollapse
      SpreadSheetCollapse spreadsheetcollapse = new SpreadSheetCollapse();
      argOption = (AbstractArgumentOption) spreadsheetcollapse.getOptionManager().findByProperty("separator");
      spreadsheetcollapse.setSeparator((String) argOption.valueOf(" | "));
      actors2.add(spreadsheetcollapse);

      // Flow.Branch.collapsed.DumpFile
      DumpFile dumpfile2 = new DumpFile();
      argOption = (AbstractArgumentOption) dumpfile2.getOptionManager().findByProperty("outputFile");
      dumpfile2.setOutputFile((PlaceholderFile) argOption.valueOf("${TMP}/dumpfile2.csv"));
      actors2.add(dumpfile2);
      sequence.setActors(actors2.toArray(new AbstractActor[0]));

      branches.add(sequence);

      // Flow.Branch.collapsed no dup
      Sequence sequence2 = new Sequence();
      argOption = (AbstractArgumentOption) sequence2.getOptionManager().findByProperty("name");
      sequence2.setName((String) argOption.valueOf("collapsed no dup"));
      List<AbstractActor> actors3 = new ArrayList<AbstractActor>();

      // Flow.Branch.collapsed no dup.SpreadSheetCollapse
      SpreadSheetCollapse spreadsheetcollapse2 = new SpreadSheetCollapse();
      argOption = (AbstractArgumentOption) spreadsheetcollapse2.getOptionManager().findByProperty("separator");
      spreadsheetcollapse2.setSeparator((String) argOption.valueOf(" | "));
      spreadsheetcollapse2.setNoDuplicates(true);

      actors3.add(spreadsheetcollapse2);

      // Flow.Branch.collapsed no dup.DumpFile
      DumpFile dumpfile3 = new DumpFile();
      argOption = (AbstractArgumentOption) dumpfile3.getOptionManager().findByProperty("outputFile");
      dumpfile3.setOutputFile((PlaceholderFile) argOption.valueOf("${TMP}/dumpfile3.csv"));
      actors3.add(dumpfile3);
      sequence2.setActors(actors3.toArray(new AbstractActor[0]));

      branches.add(sequence2);
      branch.setBranches(branches.toArray(new AbstractActor[0]));

      argOption = (AbstractArgumentOption) branch.getOptionManager().findByProperty("numThreads");
      branch.setNumThreads((Integer) argOption.valueOf("1"));
      actors.add(branch);
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

