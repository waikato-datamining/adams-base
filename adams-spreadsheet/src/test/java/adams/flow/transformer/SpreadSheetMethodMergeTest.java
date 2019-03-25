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
 * SpreadSheetMethodMergeTest.java
 * Copyright (C) 2019 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.transformer;

import adams.env.Environment;
import adams.core.option.AbstractArgumentOption;

import java.util.ArrayList;
import java.util.List;
import junit.framework.Test;
import junit.framework.TestSuite;
import adams.flow.core.AbstractActor;
import adams.flow.control.Flow;
import adams.flow.AbstractFlowTest;
import adams.test.TmpFile;
import adams.core.base.BaseRegExp;
import adams.core.base.BaseString;
import adams.core.io.PlaceholderFile;
import adams.data.io.input.CsvSpreadSheetReader;
import adams.data.io.output.CsvSpreadSheetWriter;
import adams.data.spreadsheet.DefaultSpreadSheet;
import adams.data.spreadsheet.DenseDataRow;
import adams.data.spreadsheet.columnfinder.ByName;
import adams.flow.control.ArrayProcess;
import adams.flow.control.Trigger;
import adams.flow.control.flowrestart.NullManager;
import adams.flow.core.Actor;
import adams.flow.core.CallableActorReference;
import adams.flow.execution.NullListener;
import adams.flow.sink.SpreadSheetFileWriter;
import adams.flow.source.FileSupplier;
import adams.flow.source.Start;
import adams.flow.standalone.CallableActors;
import adams.flow.transformer.spreadsheetmethodmerge.JoinOnID;
import adams.flow.transformer.spreadsheetmethodmerge.Simple;

/**
 * Test for SpreadSheetMethodMerge actor.
 *
 * @author csterlin
 * @author adams.core.option.FlowJUnitTestProducer (code generator)
 */
public class SpreadSheetMethodMergeTest
  extends AbstractFlowTest {

  /**
   * Initializes the test.
   *
   * @param name	the name of the test
   */
  public SpreadSheetMethodMergeTest(String name) {
    super(name);
  }

  /**
   * Called by JUnit before each test method.
   *
   * @throws Exception 	if an error occurs.
   */
  protected void setUp() throws Exception {
    super.setUp();

    m_TestHelper.copyResourceToTmp("test-input-1.csv");
    m_TestHelper.copyResourceToTmp("test-input-2.csv");
    m_TestHelper.deleteFileFromTmp("dumpfile.txt");
    m_TestHelper.deleteFileFromTmp("dumpfile2.txt");
  }

  /**
   * Called by JUnit after each test method.
   *
   * @throws Exception	if tear-down fails
   */
  protected void tearDown() throws Exception {
    m_TestHelper.deleteFileFromTmp("test-input-1.csv");
    m_TestHelper.deleteFileFromTmp("test-input-2.csv");
    m_TestHelper.deleteFileFromTmp("dumpfile.txt");
    m_TestHelper.deleteFileFromTmp("dumpfile2.txt");

    super.tearDown();
  }

  /**
   * Performs a regression test, comparing against previously generated output.
   */
  public void testRegression() {
    performRegressionTest(
      new TmpFile[]{
        new TmpFile("dumpfile.txt"),
        new TmpFile("dumpfile2.txt")
      });
  }

  /**
   *
   * Returns a test suite.
   *
   * @return		the test suite
   */
  public static Test suite() {
    return new TestSuite(SpreadSheetMethodMergeTest.class);
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

      // Flow.CallableActors
      CallableActors callableactors = new CallableActors();
      List<Actor> actors2 = new ArrayList<>();

      // Flow.CallableActors.ARFFFilesToSpreadsheets
      ArrayProcess arrayprocess = new ArrayProcess();
      argOption = (AbstractArgumentOption) arrayprocess.getOptionManager().findByProperty("name");
      arrayprocess.setName((String) argOption.valueOf("ARFFFilesToSpreadsheets"));
      List<Actor> actors3 = new ArrayList<>();

      // Flow.CallableActors.ARFFFilesToSpreadsheets.SpreadSheetFileReader
      SpreadSheetFileReader spreadsheetfilereader = new SpreadSheetFileReader();
      CsvSpreadSheetReader csvspreadsheetreader = new CsvSpreadSheetReader();
      DenseDataRow densedatarow = new DenseDataRow();
      csvspreadsheetreader.setDataRowType(densedatarow);

      DefaultSpreadSheet defaultspreadsheet = new DefaultSpreadSheet();
      csvspreadsheetreader.setSpreadSheetType(defaultspreadsheet);

      spreadsheetfilereader.setReader(csvspreadsheetreader);

      actors3.add(spreadsheetfilereader);
      arrayprocess.setActors(actors3.toArray(new Actor[0]));

      actors2.add(arrayprocess);
      callableactors.setActors(actors2.toArray(new Actor[0]));

      actors.add(callableactors);

      // Flow.Start
      Start start = new Start();
      actors.add(start);

      // Flow.simple-pass
      Trigger trigger = new Trigger();
      argOption = (AbstractArgumentOption) trigger.getOptionManager().findByProperty("name");
      trigger.setName((String) argOption.valueOf("simple-pass"));
      List<Actor> actors4 = new ArrayList<>();

      // Flow.simple-pass.FileSupplier
      FileSupplier filesupplier = new FileSupplier();
      filesupplier.setOutputArray(true);

      argOption = (AbstractArgumentOption) filesupplier.getOptionManager().findByProperty("files");
      List<PlaceholderFile> files = new ArrayList<>();
      files.add((PlaceholderFile) argOption.valueOf("${TMP}/test-input-1.csv"));
      files.add((PlaceholderFile) argOption.valueOf("${TMP}/test-input-2.csv"));
      filesupplier.setFiles(files.toArray(new PlaceholderFile[0]));
      actors4.add(filesupplier);

      // Flow.simple-pass.CallableTransformer
      CallableTransformer callabletransformer = new CallableTransformer();
      argOption = (AbstractArgumentOption) callabletransformer.getOptionManager().findByProperty("callableName");
      callabletransformer.setCallableName((CallableActorReference) argOption.valueOf("ARFFFilesToSpreadsheets"));
      actors4.add(callabletransformer);

      // Flow.simple-pass.SpreadSheetMethodMerge
      SpreadSheetMethodMerge spreadsheetnewmerge = new SpreadSheetMethodMerge();
      Simple simple = new Simple();
      ByName byname = new ByName();
      argOption = (AbstractArgumentOption) byname.getOptionManager().findByProperty("regExp");
      byname.setRegExp((BaseRegExp) argOption.valueOf(".*class.*"));
      simple.setClassFinder(byname);

      argOption = (AbstractArgumentOption) simple.getOptionManager().findByProperty("spreadsheetNames");
      List<BaseString> spreadsheetnames = new ArrayList<>();
      spreadsheetnames.add((BaseString) argOption.valueOf("input1"));
      spreadsheetnames.add((BaseString) argOption.valueOf("input2"));
      simple.setSpreadsheetNames(spreadsheetnames.toArray(new BaseString[0]));
      argOption = (AbstractArgumentOption) simple.getOptionManager().findByProperty("columnRenamesExp");
      List<BaseRegExp> columnrenamesexp = new ArrayList<>();
      columnrenamesexp.add((BaseRegExp) argOption.valueOf(".*"));
      columnrenamesexp.add((BaseRegExp) argOption.valueOf(".*"));
      simple.setColumnRenamesExp(columnrenamesexp.toArray(new BaseRegExp[0]));
      argOption = (AbstractArgumentOption) simple.getOptionManager().findByProperty("columnRenamesFormat");
      List<BaseString> columnrenamesformat = new ArrayList<>();
      columnrenamesformat.add((BaseString) argOption.valueOf("{SPREADSHEET}-$0"));
      columnrenamesformat.add((BaseString) argOption.valueOf("{SPREADSHEET}-$0"));
      simple.setColumnRenamesFormat(columnrenamesformat.toArray(new BaseString[0]));
      spreadsheetnewmerge.setMergeMethod(simple);

      actors4.add(spreadsheetnewmerge);

      // Flow.simple-pass.SpreadSheetFileWriter
      SpreadSheetFileWriter spreadsheetfilewriter = new SpreadSheetFileWriter();
      argOption = (AbstractArgumentOption) spreadsheetfilewriter.getOptionManager().findByProperty("outputFile");
      spreadsheetfilewriter.setOutputFile((PlaceholderFile) argOption.valueOf("${TMP}/dumpfile.txt"));
      CsvSpreadSheetWriter csvspreadsheetwriter = new CsvSpreadSheetWriter();
      spreadsheetfilewriter.setWriter(csvspreadsheetwriter);

      actors4.add(spreadsheetfilewriter);
      trigger.setActors(actors4.toArray(new Actor[0]));

      actors.add(trigger);

      // Flow.id-pass
      Trigger trigger2 = new Trigger();
      argOption = (AbstractArgumentOption) trigger2.getOptionManager().findByProperty("name");
      trigger2.setName((String) argOption.valueOf("id-pass"));
      List<Actor> actors5 = new ArrayList<>();

      // Flow.id-pass.FileSupplier
      FileSupplier filesupplier2 = new FileSupplier();
      filesupplier2.setOutputArray(true);

      argOption = (AbstractArgumentOption) filesupplier2.getOptionManager().findByProperty("files");
      List<PlaceholderFile> files2 = new ArrayList<>();
      files2.add((PlaceholderFile) argOption.valueOf("${TMP}/test-input-1.csv"));
      files2.add((PlaceholderFile) argOption.valueOf("${TMP}/test-input-2.csv"));
      filesupplier2.setFiles(files2.toArray(new PlaceholderFile[0]));
      actors5.add(filesupplier2);

      // Flow.id-pass.CallableTransformer
      CallableTransformer callabletransformer2 = new CallableTransformer();
      argOption = (AbstractArgumentOption) callabletransformer2.getOptionManager().findByProperty("callableName");
      callabletransformer2.setCallableName((CallableActorReference) argOption.valueOf("ARFFFilesToSpreadsheets"));
      actors5.add(callabletransformer2);

      // Flow.id-pass.SpreadSheetMethodMerge
      SpreadSheetMethodMerge spreadsheetnewmerge2 = new SpreadSheetMethodMerge();
      JoinOnID joinonid = new JoinOnID();
      ByName byname2 = new ByName();
      argOption = (AbstractArgumentOption) byname2.getOptionManager().findByProperty("regExp");
      byname2.setRegExp((BaseRegExp) argOption.valueOf(".*class.*"));
      joinonid.setClassFinder(byname2);

      argOption = (AbstractArgumentOption) joinonid.getOptionManager().findByProperty("spreadsheetNames");
      List<BaseString> spreadsheetnames2 = new ArrayList<>();
      spreadsheetnames2.add((BaseString) argOption.valueOf("input1"));
      spreadsheetnames2.add((BaseString) argOption.valueOf("input2"));
      joinonid.setSpreadsheetNames(spreadsheetnames2.toArray(new BaseString[0]));
      argOption = (AbstractArgumentOption) joinonid.getOptionManager().findByProperty("columnRenamesExp");
      List<BaseRegExp> columnrenamesexp2 = new ArrayList<>();
      columnrenamesexp2.add((BaseRegExp) argOption.valueOf(".*"));
      columnrenamesexp2.add((BaseRegExp) argOption.valueOf(".*"));
      joinonid.setColumnRenamesExp(columnrenamesexp2.toArray(new BaseRegExp[0]));
      argOption = (AbstractArgumentOption) joinonid.getOptionManager().findByProperty("columnRenamesFormat");
      List<BaseString> columnrenamesformat2 = new ArrayList<>();
      columnrenamesformat2.add((BaseString) argOption.valueOf("{SPREADSHEET}-$0"));
      columnrenamesformat2.add((BaseString) argOption.valueOf("{SPREADSHEET}-$0"));
      joinonid.setColumnRenamesFormat(columnrenamesformat2.toArray(new BaseString[0]));
      argOption = (AbstractArgumentOption) joinonid.getOptionManager().findByProperty("uniqueID");
      joinonid.setUniqueID((String) argOption.valueOf("id"));
      joinonid.setCompleteRowsOnly(true);

      spreadsheetnewmerge2.setMergeMethod(joinonid);

      actors5.add(spreadsheetnewmerge2);

      // Flow.id-pass.SpreadSheetFileWriter
      SpreadSheetFileWriter spreadsheetfilewriter2 = new SpreadSheetFileWriter();
      argOption = (AbstractArgumentOption) spreadsheetfilewriter2.getOptionManager().findByProperty("outputFile");
      spreadsheetfilewriter2.setOutputFile((PlaceholderFile) argOption.valueOf("${TMP}/dumpfile2.txt"));
      CsvSpreadSheetWriter csvspreadsheetwriter2 = new CsvSpreadSheetWriter();
      spreadsheetfilewriter2.setWriter(csvspreadsheetwriter2);

      actors5.add(spreadsheetfilewriter2);
      trigger2.setActors(actors5.toArray(new Actor[0]));

      actors.add(trigger2);
      flow.setActors(actors.toArray(new Actor[0]));

      NullListener nulllistener = new NullListener();
      flow.setFlowExecutionListener(nulllistener);

      NullManager nullmanager = new NullManager();
      flow.setFlowRestartManager(nullmanager);

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
