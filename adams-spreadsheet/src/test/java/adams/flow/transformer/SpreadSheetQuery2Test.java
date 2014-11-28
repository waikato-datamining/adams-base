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
 * SpreadSheetQueryTest.java
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
import adams.test.Regression;
import adams.test.TmpFile;

/**
 * Test for SpreadSheetQuery actor.
 *
 * @author fracpete
 * @author adams.core.option.FlowJUnitTestProducer (code generator)
 * @version $Revision$
 */
public class SpreadSheetQuery2Test
  extends AbstractFlowTest {

  /**
   * Initializes the test.
   *
   * @param name	the name of the test
   */
  public SpreadSheetQuery2Test(String name) {
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
    
    m_Regression = new Regression(SpreadSheetQuery.class);
    m_Regression.setReferenceFile(Regression.createReferenceFile(SpreadSheetQuery.class, "2", ".ref"));
    
    m_TestHelper.copyResourceToTmp("dates.csv");
    m_TestHelper.deleteFileFromTmp("dumpfile.txt");
  }

  /**
   * Called by JUnit after each test method.
   *
   * @throws Exception	if tear-down fails
   */
  @Override
  protected void tearDown() throws Exception {
    m_TestHelper.deleteFileFromTmp("dates.csv");
    m_TestHelper.deleteFileFromTmp("dumpfile.txt");
    
    super.tearDown();
  }

  /**
   * Unnecessary.
   */
  @Override
  public void testImage() {
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
    return new TestSuite(SpreadSheetQuery2Test.class);
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

      // Flow.CallableActors
      adams.flow.standalone.CallableActors callableactors2 = new adams.flow.standalone.CallableActors();
      argOption = (AbstractArgumentOption) callableactors2.getOptionManager().findByProperty("actors");
      adams.flow.core.AbstractActor[] actors3 = new adams.flow.core.AbstractActor[1];

      // Flow.CallableActors.output
      adams.flow.control.Sequence sequence4 = new adams.flow.control.Sequence();
      argOption = (AbstractArgumentOption) sequence4.getOptionManager().findByProperty("name");
      sequence4.setName((java.lang.String) argOption.valueOf("output"));
      argOption = (AbstractArgumentOption) sequence4.getOptionManager().findByProperty("actors");
      adams.flow.core.AbstractActor[] actors6 = new adams.flow.core.AbstractActor[2];

      // Flow.CallableActors.output.Trigger
      adams.flow.control.Trigger trigger7 = new adams.flow.control.Trigger();
      argOption = (AbstractArgumentOption) trigger7.getOptionManager().findByProperty("actors");
      adams.flow.core.AbstractActor[] actors8 = new adams.flow.core.AbstractActor[2];

      // Flow.CallableActors.output.Trigger.Variable
      adams.flow.source.Variable variable9 = new adams.flow.source.Variable();
      argOption = (AbstractArgumentOption) variable9.getOptionManager().findByProperty("variableName");
      variable9.setVariableName((adams.core.VariableName) argOption.valueOf("name"));
      actors8[0] = variable9;

      // Flow.CallableActors.output.Trigger.DumpFile
      adams.flow.sink.DumpFile dumpfile11 = new adams.flow.sink.DumpFile();
      argOption = (AbstractArgumentOption) dumpfile11.getOptionManager().findByProperty("outputFile");
      dumpfile11.setOutputFile((adams.core.io.PlaceholderFile) argOption.valueOf("${TMP}/dumpfile.txt"));
      dumpfile11.setAppend(true);

      actors8[1] = dumpfile11;
      trigger7.setActors(actors8);

      actors6[0] = trigger7;

      // Flow.CallableActors.output.DumpFile
      adams.flow.sink.DumpFile dumpfile13 = new adams.flow.sink.DumpFile();
      argOption = (AbstractArgumentOption) dumpfile13.getOptionManager().findByProperty("outputFile");
      dumpfile13.setOutputFile((adams.core.io.PlaceholderFile) argOption.valueOf("${TMP}/dumpfile.txt"));
      dumpfile13.setAppend(true);

      actors6[1] = dumpfile13;
      sequence4.setActors(actors6);

      actors3[0] = sequence4;
      callableactors2.setActors(actors3);

      actors1[0] = callableactors2;

      // Flow.FileSupplier
      adams.flow.source.FileSupplier filesupplier15 = new adams.flow.source.FileSupplier();
      argOption = (AbstractArgumentOption) filesupplier15.getOptionManager().findByProperty("files");
      adams.core.io.PlaceholderFile[] files16 = new adams.core.io.PlaceholderFile[1];
      files16[0] = (adams.core.io.PlaceholderFile) argOption.valueOf("${TMP}/dates.csv");
      filesupplier15.setFiles(files16);
      actors1[1] = filesupplier15;

      // Flow.SpreadSheetFileReader
      adams.flow.transformer.SpreadSheetFileReader spreadsheetfilereader17 = new adams.flow.transformer.SpreadSheetFileReader();
      argOption = (AbstractArgumentOption) spreadsheetfilereader17.getOptionManager().findByProperty("reader");
      adams.data.io.input.CsvSpreadSheetReader csvspreadsheetreader19 = new adams.data.io.input.CsvSpreadSheetReader();
      argOption = (AbstractArgumentOption) csvspreadsheetreader19.getOptionManager().findByProperty("dataRowType");
      adams.data.spreadsheet.DenseDataRow densedatarow21 = new adams.data.spreadsheet.DenseDataRow();
      csvspreadsheetreader19.setDataRowType(densedatarow21);

      argOption = (AbstractArgumentOption) csvspreadsheetreader19.getOptionManager().findByProperty("spreadSheetType");
      adams.data.spreadsheet.SpreadSheet spreadsheet23 = new adams.data.spreadsheet.SpreadSheet();
      csvspreadsheetreader19.setSpreadSheetType(spreadsheet23);

      argOption = (AbstractArgumentOption) csvspreadsheetreader19.getOptionManager().findByProperty("dateColumns");
      csvspreadsheetreader19.setDateColumns((adams.core.Range) argOption.valueOf("1"));
      spreadsheetfilereader17.setReader(csvspreadsheetreader19);

      actors1[2] = spreadsheetfilereader17;

      // Flow.Branch
      adams.flow.control.Branch branch25 = new adams.flow.control.Branch();
      argOption = (AbstractArgumentOption) branch25.getOptionManager().findByProperty("branches");
      adams.flow.core.AbstractActor[] branches26 = new adams.flow.core.AbstractActor[8];

      // Flow.Branch.full
      adams.flow.control.Sequence sequence27 = new adams.flow.control.Sequence();
      argOption = (AbstractArgumentOption) sequence27.getOptionManager().findByProperty("name");
      sequence27.setName((java.lang.String) argOption.valueOf("full"));
      argOption = (AbstractArgumentOption) sequence27.getOptionManager().findByProperty("actors");
      adams.flow.core.AbstractActor[] actors29 = new adams.flow.core.AbstractActor[2];

      // Flow.Branch.full.SetVariable
      adams.flow.transformer.SetVariable setvariable30 = new adams.flow.transformer.SetVariable();
      argOption = (AbstractArgumentOption) setvariable30.getOptionManager().findByProperty("variableName");
      setvariable30.setVariableName((adams.core.VariableName) argOption.valueOf("name"));
      argOption = (AbstractArgumentOption) setvariable30.getOptionManager().findByProperty("variableValue");
      setvariable30.setVariableValue((adams.core.base.BaseText) argOption.valueOf("full"));
      actors29[0] = setvariable30;

      // Flow.Branch.full.CallableSink
      adams.flow.sink.CallableSink callablesink33 = new adams.flow.sink.CallableSink();
      argOption = (AbstractArgumentOption) callablesink33.getOptionManager().findByProperty("callableName");
      callablesink33.setCallableName((adams.flow.core.CallableActorReference) argOption.valueOf("output"));
      actors29[1] = callablesink33;
      sequence27.setActors(actors29);

      branches26[0] = sequence27;

      // Flow.Branch.lt
      adams.flow.control.Sequence sequence35 = new adams.flow.control.Sequence();
      argOption = (AbstractArgumentOption) sequence35.getOptionManager().findByProperty("name");
      sequence35.setName((java.lang.String) argOption.valueOf("lt"));
      argOption = (AbstractArgumentOption) sequence35.getOptionManager().findByProperty("actors");
      adams.flow.core.AbstractActor[] actors37 = new adams.flow.core.AbstractActor[3];

      // Flow.Branch.lt.SetVariable
      adams.flow.transformer.SetVariable setvariable38 = new adams.flow.transformer.SetVariable();
      argOption = (AbstractArgumentOption) setvariable38.getOptionManager().findByProperty("variableName");
      setvariable38.setVariableName((adams.core.VariableName) argOption.valueOf("name"));
      argOption = (AbstractArgumentOption) setvariable38.getOptionManager().findByProperty("variableValue");
      setvariable38.setVariableValue((adams.core.base.BaseText) argOption.valueOf("<"));
      actors37[0] = setvariable38;

      // Flow.Branch.lt.SpreadSheetQuery
      adams.flow.transformer.SpreadSheetQuery spreadsheetquery41 = new adams.flow.transformer.SpreadSheetQuery();
      argOption = (AbstractArgumentOption) spreadsheetquery41.getOptionManager().findByProperty("query");
      spreadsheetquery41.setQuery((adams.parser.SpreadSheetQueryText) argOption.valueOf("SELECT * WHERE date @{name} parse(\"date\", \"2013-11-03\")"));
      actors37[1] = spreadsheetquery41;

      // Flow.Branch.lt.CallableSink
      adams.flow.sink.CallableSink callablesink43 = new adams.flow.sink.CallableSink();
      argOption = (AbstractArgumentOption) callablesink43.getOptionManager().findByProperty("callableName");
      callablesink43.setCallableName((adams.flow.core.CallableActorReference) argOption.valueOf("output"));
      actors37[2] = callablesink43;
      sequence35.setActors(actors37);

      branches26[1] = sequence35;

      // Flow.Branch.le
      adams.flow.control.Sequence sequence45 = new adams.flow.control.Sequence();
      argOption = (AbstractArgumentOption) sequence45.getOptionManager().findByProperty("name");
      sequence45.setName((java.lang.String) argOption.valueOf("le"));
      argOption = (AbstractArgumentOption) sequence45.getOptionManager().findByProperty("actors");
      adams.flow.core.AbstractActor[] actors47 = new adams.flow.core.AbstractActor[3];

      // Flow.Branch.le.SetVariable
      adams.flow.transformer.SetVariable setvariable48 = new adams.flow.transformer.SetVariable();
      argOption = (AbstractArgumentOption) setvariable48.getOptionManager().findByProperty("variableName");
      setvariable48.setVariableName((adams.core.VariableName) argOption.valueOf("name"));
      argOption = (AbstractArgumentOption) setvariable48.getOptionManager().findByProperty("variableValue");
      setvariable48.setVariableValue((adams.core.base.BaseText) argOption.valueOf("<="));
      actors47[0] = setvariable48;

      // Flow.Branch.le.SpreadSheetQuery
      adams.flow.transformer.SpreadSheetQuery spreadsheetquery51 = new adams.flow.transformer.SpreadSheetQuery();
      argOption = (AbstractArgumentOption) spreadsheetquery51.getOptionManager().findByProperty("query");
      spreadsheetquery51.setQuery((adams.parser.SpreadSheetQueryText) argOption.valueOf("SELECT * WHERE date @{name} parse(\"date\", \"2013-11-03\")"));
      actors47[1] = spreadsheetquery51;

      // Flow.Branch.le.CallableSink
      adams.flow.sink.CallableSink callablesink53 = new adams.flow.sink.CallableSink();
      argOption = (AbstractArgumentOption) callablesink53.getOptionManager().findByProperty("callableName");
      callablesink53.setCallableName((adams.flow.core.CallableActorReference) argOption.valueOf("output"));
      actors47[2] = callablesink53;
      sequence45.setActors(actors47);

      branches26[2] = sequence45;

      // Flow.Branch.eq
      adams.flow.control.Sequence sequence55 = new adams.flow.control.Sequence();
      argOption = (AbstractArgumentOption) sequence55.getOptionManager().findByProperty("name");
      sequence55.setName((java.lang.String) argOption.valueOf("eq"));
      argOption = (AbstractArgumentOption) sequence55.getOptionManager().findByProperty("actors");
      adams.flow.core.AbstractActor[] actors57 = new adams.flow.core.AbstractActor[3];

      // Flow.Branch.eq.SetVariable
      adams.flow.transformer.SetVariable setvariable58 = new adams.flow.transformer.SetVariable();
      argOption = (AbstractArgumentOption) setvariable58.getOptionManager().findByProperty("variableName");
      setvariable58.setVariableName((adams.core.VariableName) argOption.valueOf("name"));
      argOption = (AbstractArgumentOption) setvariable58.getOptionManager().findByProperty("variableValue");
      setvariable58.setVariableValue((adams.core.base.BaseText) argOption.valueOf("="));
      actors57[0] = setvariable58;

      // Flow.Branch.eq.SpreadSheetQuery
      adams.flow.transformer.SpreadSheetQuery spreadsheetquery61 = new adams.flow.transformer.SpreadSheetQuery();
      argOption = (AbstractArgumentOption) spreadsheetquery61.getOptionManager().findByProperty("query");
      spreadsheetquery61.setQuery((adams.parser.SpreadSheetQueryText) argOption.valueOf("SELECT * WHERE date @{name} parse(\"date\", \"2013-11-03\")"));
      actors57[1] = spreadsheetquery61;

      // Flow.Branch.eq.CallableSink
      adams.flow.sink.CallableSink callablesink63 = new adams.flow.sink.CallableSink();
      argOption = (AbstractArgumentOption) callablesink63.getOptionManager().findByProperty("callableName");
      callablesink63.setCallableName((adams.flow.core.CallableActorReference) argOption.valueOf("output"));
      actors57[2] = callablesink63;
      sequence55.setActors(actors57);

      branches26[3] = sequence55;

      // Flow.Branch.!eq
      adams.flow.control.Sequence sequence65 = new adams.flow.control.Sequence();
      argOption = (AbstractArgumentOption) sequence65.getOptionManager().findByProperty("name");
      sequence65.setName((java.lang.String) argOption.valueOf("!eq"));
      argOption = (AbstractArgumentOption) sequence65.getOptionManager().findByProperty("actors");
      adams.flow.core.AbstractActor[] actors67 = new adams.flow.core.AbstractActor[3];

      // Flow.Branch.!eq.SetVariable
      adams.flow.transformer.SetVariable setvariable68 = new adams.flow.transformer.SetVariable();
      argOption = (AbstractArgumentOption) setvariable68.getOptionManager().findByProperty("variableName");
      setvariable68.setVariableName((adams.core.VariableName) argOption.valueOf("name"));
      argOption = (AbstractArgumentOption) setvariable68.getOptionManager().findByProperty("variableValue");
      setvariable68.setVariableValue((adams.core.base.BaseText) argOption.valueOf("<>"));
      actors67[0] = setvariable68;

      // Flow.Branch.!eq.SpreadSheetQuery
      adams.flow.transformer.SpreadSheetQuery spreadsheetquery71 = new adams.flow.transformer.SpreadSheetQuery();
      argOption = (AbstractArgumentOption) spreadsheetquery71.getOptionManager().findByProperty("query");
      spreadsheetquery71.setQuery((adams.parser.SpreadSheetQueryText) argOption.valueOf("SELECT * WHERE date @{name} parse(\"date\", \"2013-11-03\")"));
      actors67[1] = spreadsheetquery71;

      // Flow.Branch.!eq.CallableSink
      adams.flow.sink.CallableSink callablesink73 = new adams.flow.sink.CallableSink();
      argOption = (AbstractArgumentOption) callablesink73.getOptionManager().findByProperty("callableName");
      callablesink73.setCallableName((adams.flow.core.CallableActorReference) argOption.valueOf("output"));
      actors67[2] = callablesink73;
      sequence65.setActors(actors67);

      branches26[4] = sequence65;

      // Flow.Branch.ge
      adams.flow.control.Sequence sequence75 = new adams.flow.control.Sequence();
      argOption = (AbstractArgumentOption) sequence75.getOptionManager().findByProperty("name");
      sequence75.setName((java.lang.String) argOption.valueOf("ge"));
      argOption = (AbstractArgumentOption) sequence75.getOptionManager().findByProperty("actors");
      adams.flow.core.AbstractActor[] actors77 = new adams.flow.core.AbstractActor[3];

      // Flow.Branch.ge.SetVariable
      adams.flow.transformer.SetVariable setvariable78 = new adams.flow.transformer.SetVariable();
      argOption = (AbstractArgumentOption) setvariable78.getOptionManager().findByProperty("variableName");
      setvariable78.setVariableName((adams.core.VariableName) argOption.valueOf("name"));
      argOption = (AbstractArgumentOption) setvariable78.getOptionManager().findByProperty("variableValue");
      setvariable78.setVariableValue((adams.core.base.BaseText) argOption.valueOf(">="));
      actors77[0] = setvariable78;

      // Flow.Branch.ge.SpreadSheetQuery
      adams.flow.transformer.SpreadSheetQuery spreadsheetquery81 = new adams.flow.transformer.SpreadSheetQuery();
      argOption = (AbstractArgumentOption) spreadsheetquery81.getOptionManager().findByProperty("query");
      spreadsheetquery81.setQuery((adams.parser.SpreadSheetQueryText) argOption.valueOf("SELECT * WHERE date @{name} parse(\"date\", \"2013-11-03\")"));
      actors77[1] = spreadsheetquery81;

      // Flow.Branch.ge.CallableSink
      adams.flow.sink.CallableSink callablesink83 = new adams.flow.sink.CallableSink();
      argOption = (AbstractArgumentOption) callablesink83.getOptionManager().findByProperty("callableName");
      callablesink83.setCallableName((adams.flow.core.CallableActorReference) argOption.valueOf("output"));
      actors77[2] = callablesink83;
      sequence75.setActors(actors77);

      branches26[5] = sequence75;

      // Flow.Branch.gt
      adams.flow.control.Sequence sequence85 = new adams.flow.control.Sequence();
      argOption = (AbstractArgumentOption) sequence85.getOptionManager().findByProperty("name");
      sequence85.setName((java.lang.String) argOption.valueOf("gt"));
      argOption = (AbstractArgumentOption) sequence85.getOptionManager().findByProperty("actors");
      adams.flow.core.AbstractActor[] actors87 = new adams.flow.core.AbstractActor[3];

      // Flow.Branch.gt.SetVariable
      adams.flow.transformer.SetVariable setvariable88 = new adams.flow.transformer.SetVariable();
      argOption = (AbstractArgumentOption) setvariable88.getOptionManager().findByProperty("variableName");
      setvariable88.setVariableName((adams.core.VariableName) argOption.valueOf("name"));
      argOption = (AbstractArgumentOption) setvariable88.getOptionManager().findByProperty("variableValue");
      setvariable88.setVariableValue((adams.core.base.BaseText) argOption.valueOf(">"));
      actors87[0] = setvariable88;

      // Flow.Branch.gt.SpreadSheetQuery
      adams.flow.transformer.SpreadSheetQuery spreadsheetquery91 = new adams.flow.transformer.SpreadSheetQuery();
      argOption = (AbstractArgumentOption) spreadsheetquery91.getOptionManager().findByProperty("query");
      spreadsheetquery91.setQuery((adams.parser.SpreadSheetQueryText) argOption.valueOf("SELECT * WHERE date @{name} parse(\"date\", \"2013-11-03\")"));
      actors87[1] = spreadsheetquery91;

      // Flow.Branch.gt.CallableSink
      adams.flow.sink.CallableSink callablesink93 = new adams.flow.sink.CallableSink();
      argOption = (AbstractArgumentOption) callablesink93.getOptionManager().findByProperty("callableName");
      callablesink93.setCallableName((adams.flow.core.CallableActorReference) argOption.valueOf("output"));
      actors87[2] = callablesink93;
      sequence85.setActors(actors87);

      branches26[6] = sequence85;

      // Flow.Branch.update date
      adams.flow.control.Sequence sequence95 = new adams.flow.control.Sequence();
      argOption = (AbstractArgumentOption) sequence95.getOptionManager().findByProperty("name");
      sequence95.setName((java.lang.String) argOption.valueOf("update date"));
      argOption = (AbstractArgumentOption) sequence95.getOptionManager().findByProperty("actors");
      adams.flow.core.AbstractActor[] actors97 = new adams.flow.core.AbstractActor[3];

      // Flow.Branch.update date.SetVariable
      adams.flow.transformer.SetVariable setvariable98 = new adams.flow.transformer.SetVariable();
      argOption = (AbstractArgumentOption) setvariable98.getOptionManager().findByProperty("variableName");
      setvariable98.setVariableName((adams.core.VariableName) argOption.valueOf("name"));
      argOption = (AbstractArgumentOption) setvariable98.getOptionManager().findByProperty("variableValue");
      setvariable98.setVariableValue((adams.core.base.BaseText) argOption.valueOf("update"));
      actors97[0] = setvariable98;

      // Flow.Branch.update date.SpreadSheetQuery
      adams.flow.transformer.SpreadSheetQuery spreadsheetquery101 = new adams.flow.transformer.SpreadSheetQuery();
      argOption = (AbstractArgumentOption) spreadsheetquery101.getOptionManager().findByProperty("query");
      spreadsheetquery101.setQuery((adams.parser.SpreadSheetQueryText) argOption.valueOf("UPDATE  \nSET date = parse(\"date\", \"2014-11-03\")\nWHERE date < parse(\"date\", \"2013-11-03\")"));
      actors97[1] = spreadsheetquery101;

      // Flow.Branch.update date.CallableSink
      adams.flow.sink.CallableSink callablesink103 = new adams.flow.sink.CallableSink();
      argOption = (AbstractArgumentOption) callablesink103.getOptionManager().findByProperty("callableName");
      callablesink103.setCallableName((adams.flow.core.CallableActorReference) argOption.valueOf("output"));
      actors97[2] = callablesink103;
      sequence95.setActors(actors97);

      branches26[7] = sequence95;
      branch25.setBranches(branches26);

      argOption = (AbstractArgumentOption) branch25.getOptionManager().findByProperty("numThreads");
      branch25.setNumThreads((Integer) argOption.valueOf("1"));
      actors1[3] = branch25;
      flow.setActors(actors1);

      argOption = (AbstractArgumentOption) flow.getOptionManager().findByProperty("flowExecutionListener");
      adams.flow.execution.NullListener nulllistener107 = new adams.flow.execution.NullListener();
      flow.setFlowExecutionListener(nulllistener107);

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

