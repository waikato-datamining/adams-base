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
 * SpreadSheetQuery3Test.java
 * Copyright (C) 2014 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.transformer;

import junit.framework.Test;
import junit.framework.TestSuite;
import adams.core.VariableName;
import adams.core.base.BaseText;
import adams.core.option.AbstractArgumentOption;
import adams.env.Environment;
import adams.flow.AbstractFlowTest;
import adams.flow.control.Flow;
import adams.flow.core.AbstractActor;
import adams.flow.core.CallableActorReference;
import adams.parser.SpreadSheetQueryText;
import adams.test.Regression;
import adams.test.TmpFile;

/**
 * Test for SpreadSheetQuery actor.
 *
 * @author fracpete
 * @author adams.core.option.FlowJUnitTestProducer (code generator)
 * @version $Revision$
 */
public class SpreadSheetQuery3Test
  extends AbstractFlowTest {

  /**
   * Initializes the test.
   *
   * @param name	the name of the test
   */
  public SpreadSheetQuery3Test(String name) {
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
    m_Regression.setReferenceFile(Regression.createReferenceFile(SpreadSheetQuery.class, "3", ".ref"));

    m_TestHelper.copyResourceToTmp("celltype.csv");
    m_TestHelper.deleteFileFromTmp("dumpfile.txt");
  }

  /**
   * Called by JUnit after each test method.
   *
   * @throws Exception	if tear-down fails
   */
  @Override
  protected void tearDown() throws Exception {
    m_TestHelper.deleteFileFromTmp("celltype.csv");
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
    return new TestSuite(SpreadSheetQuery3Test.class);
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
      flow.setAnnotations((adams.core.base.BaseAnnotation) argOption.valueOf("Shows how to select rows based on the cell type of a column/attribute."));
      argOption = (AbstractArgumentOption) flow.getOptionManager().findByProperty("actors");
      adams.flow.core.AbstractActor[] actors2 = new adams.flow.core.AbstractActor[4];

      // Flow.CallableActors
      adams.flow.standalone.CallableActors callableactors3 = new adams.flow.standalone.CallableActors();
      argOption = (AbstractArgumentOption) callableactors3.getOptionManager().findByProperty("actors");
      adams.flow.core.AbstractActor[] actors4 = new adams.flow.core.AbstractActor[1];

      // Flow.CallableActors.output
      adams.flow.control.Sequence sequence5 = new adams.flow.control.Sequence();
      argOption = (AbstractArgumentOption) sequence5.getOptionManager().findByProperty("name");
      sequence5.setName((java.lang.String) argOption.valueOf("output"));
      argOption = (AbstractArgumentOption) sequence5.getOptionManager().findByProperty("actors");
      adams.flow.core.AbstractActor[] actors7 = new adams.flow.core.AbstractActor[2];

      // Flow.CallableActors.output.Trigger
      adams.flow.control.Trigger trigger8 = new adams.flow.control.Trigger();
      argOption = (AbstractArgumentOption) trigger8.getOptionManager().findByProperty("actors");
      adams.flow.core.AbstractActor[] actors9 = new adams.flow.core.AbstractActor[2];

      // Flow.CallableActors.output.Trigger.Variable
      adams.flow.source.Variable variable10 = new adams.flow.source.Variable();
      argOption = (AbstractArgumentOption) variable10.getOptionManager().findByProperty("variableName");
      variable10.setVariableName((adams.core.VariableName) argOption.valueOf("name"));
      actors9[0] = variable10;

      // Flow.CallableActors.output.Trigger.DumpFile
      adams.flow.sink.DumpFile dumpfile12 = new adams.flow.sink.DumpFile();
      argOption = (AbstractArgumentOption) dumpfile12.getOptionManager().findByProperty("outputFile");
      dumpfile12.setOutputFile((adams.core.io.PlaceholderFile) argOption.valueOf("${TMP}/dumpfile.txt"));
      dumpfile12.setAppend(true);

      actors9[1] = dumpfile12;
      trigger8.setActors(actors9);

      actors7[0] = trigger8;

      // Flow.CallableActors.output.DumpFile
      adams.flow.sink.DumpFile dumpfile14 = new adams.flow.sink.DumpFile();
      argOption = (AbstractArgumentOption) dumpfile14.getOptionManager().findByProperty("outputFile");
      dumpfile14.setOutputFile((adams.core.io.PlaceholderFile) argOption.valueOf("${TMP}/dumpfile.txt"));
      dumpfile14.setAppend(true);

      actors7[1] = dumpfile14;
      sequence5.setActors(actors7);

      actors4[0] = sequence5;
      callableactors3.setActors(actors4);

      actors2[0] = callableactors3;

      // Flow.FileSupplier
      adams.flow.source.FileSupplier filesupplier16 = new adams.flow.source.FileSupplier();
      argOption = (AbstractArgumentOption) filesupplier16.getOptionManager().findByProperty("files");
      adams.core.io.PlaceholderFile[] files17 = new adams.core.io.PlaceholderFile[1];
      files17[0] = (adams.core.io.PlaceholderFile) argOption.valueOf("${TMP}/celltype.csv");
      filesupplier16.setFiles(files17);
      actors2[1] = filesupplier16;

      // Flow.SpreadSheetFileReader
      adams.flow.transformer.SpreadSheetFileReader spreadsheetfilereader18 = new adams.flow.transformer.SpreadSheetFileReader();
      argOption = (AbstractArgumentOption) spreadsheetfilereader18.getOptionManager().findByProperty("reader");
      adams.data.io.input.CsvSpreadSheetReader csvspreadsheetreader20 = new adams.data.io.input.CsvSpreadSheetReader();
      argOption = (AbstractArgumentOption) csvspreadsheetreader20.getOptionManager().findByProperty("dataRowType");
      adams.data.spreadsheet.DenseDataRow densedatarow22 = new adams.data.spreadsheet.DenseDataRow();
      csvspreadsheetreader20.setDataRowType(densedatarow22);

      argOption = (AbstractArgumentOption) csvspreadsheetreader20.getOptionManager().findByProperty("spreadSheetType");
      adams.data.spreadsheet.SpreadSheet spreadsheet24 = new adams.data.spreadsheet.SpreadSheet();
      csvspreadsheetreader20.setSpreadSheetType(spreadsheet24);

      spreadsheetfilereader18.setReader(csvspreadsheetreader20);

      actors2[2] = spreadsheetfilereader18;

      // Flow.Branch
      adams.flow.control.Branch branch25 = new adams.flow.control.Branch();
      argOption = (AbstractArgumentOption) branch25.getOptionManager().findByProperty("branches");
      adams.flow.core.AbstractActor[] branches26 = new adams.flow.core.AbstractActor[14];

      // Flow.Branch.original
      adams.flow.control.Sequence sequence27 = new adams.flow.control.Sequence();
      argOption = (AbstractArgumentOption) sequence27.getOptionManager().findByProperty("name");
      sequence27.setName((java.lang.String) argOption.valueOf("original"));
      argOption = (AbstractArgumentOption) sequence27.getOptionManager().findByProperty("actors");
      adams.flow.core.AbstractActor[] actors29 = new adams.flow.core.AbstractActor[2];

      // Flow.Branch.original.SetVariable
      adams.flow.transformer.SetVariable setvariable30 = new adams.flow.transformer.SetVariable();
      argOption = (AbstractArgumentOption) setvariable30.getOptionManager().findByProperty("variableName");
      setvariable30.setVariableName((adams.core.VariableName) argOption.valueOf("name"));
      argOption = (AbstractArgumentOption) setvariable30.getOptionManager().findByProperty("variableValue");
      setvariable30.setVariableValue((adams.core.base.BaseText) argOption.valueOf("original"));
      actors29[0] = setvariable30;

      // Flow.Branch.original.CallableSink
      adams.flow.sink.CallableSink callablesink33 = new adams.flow.sink.CallableSink();
      argOption = (AbstractArgumentOption) callablesink33.getOptionManager().findByProperty("callableName");
      callablesink33.setCallableName((adams.flow.core.CallableActorReference) argOption.valueOf("output"));
      actors29[1] = callablesink33;
      sequence27.setActors(actors29);

      branches26[0] = sequence27;

      // Flow.Branch.string
      adams.flow.control.Sequence sequence35 = new adams.flow.control.Sequence();
      argOption = (AbstractArgumentOption) sequence35.getOptionManager().findByProperty("name");
      sequence35.setName((java.lang.String) argOption.valueOf("string"));
      argOption = (AbstractArgumentOption) sequence35.getOptionManager().findByProperty("actors");
      adams.flow.core.AbstractActor[] actors37 = new adams.flow.core.AbstractActor[3];

      // Flow.Branch.string.SetVariable
      adams.flow.transformer.SetVariable setvariable38 = new adams.flow.transformer.SetVariable();
      argOption = (AbstractArgumentOption) setvariable38.getOptionManager().findByProperty("variableName");
      setvariable38.setVariableName((adams.core.VariableName) argOption.valueOf("name"));
      argOption = (AbstractArgumentOption) setvariable38.getOptionManager().findByProperty("variableValue");
      setvariable38.setVariableValue((adams.core.base.BaseText) argOption.valueOf("string"));
      actors37[0] = setvariable38;

      // Flow.Branch.string.SpreadSheetQuery
      adams.flow.transformer.SpreadSheetQuery spreadsheetquery41 = new adams.flow.transformer.SpreadSheetQuery();
      argOption = (AbstractArgumentOption) spreadsheetquery41.getOptionManager().findByProperty("query");
      spreadsheetquery41.setQuery((adams.parser.SpreadSheetQueryText) argOption.valueOf("SELECT * WHERE CELLTYPE(anyvalue) = \"string\""));
      actors37[1] = spreadsheetquery41;

      // Flow.Branch.string.CallableSink
      adams.flow.sink.CallableSink callablesink43 = new adams.flow.sink.CallableSink();
      argOption = (AbstractArgumentOption) callablesink43.getOptionManager().findByProperty("callableName");
      callablesink43.setCallableName((adams.flow.core.CallableActorReference) argOption.valueOf("output"));
      actors37[2] = callablesink43;
      sequence35.setActors(actors37);

      branches26[1] = sequence35;

      // Flow.Branch.boolean
      adams.flow.control.Sequence sequence45 = new adams.flow.control.Sequence();
      argOption = (AbstractArgumentOption) sequence45.getOptionManager().findByProperty("name");
      sequence45.setName((java.lang.String) argOption.valueOf("boolean"));
      argOption = (AbstractArgumentOption) sequence45.getOptionManager().findByProperty("actors");
      adams.flow.core.AbstractActor[] actors47 = new adams.flow.core.AbstractActor[3];

      // Flow.Branch.boolean.SetVariable
      adams.flow.transformer.SetVariable setvariable48 = new adams.flow.transformer.SetVariable();
      argOption = (AbstractArgumentOption) setvariable48.getOptionManager().findByProperty("variableName");
      setvariable48.setVariableName((adams.core.VariableName) argOption.valueOf("name"));
      argOption = (AbstractArgumentOption) setvariable48.getOptionManager().findByProperty("variableValue");
      setvariable48.setVariableValue((adams.core.base.BaseText) argOption.valueOf("boolean"));
      actors47[0] = setvariable48;

      // Flow.Branch.boolean.SpreadSheetQuery
      adams.flow.transformer.SpreadSheetQuery spreadsheetquery51 = new adams.flow.transformer.SpreadSheetQuery();
      argOption = (AbstractArgumentOption) spreadsheetquery51.getOptionManager().findByProperty("query");
      spreadsheetquery51.setQuery((adams.parser.SpreadSheetQueryText) argOption.valueOf("SELECT * WHERE CELLTYPE(anyvalue) = \"boolean\""));
      actors47[1] = spreadsheetquery51;

      // Flow.Branch.boolean.CallableSink
      adams.flow.sink.CallableSink callablesink53 = new adams.flow.sink.CallableSink();
      argOption = (AbstractArgumentOption) callablesink53.getOptionManager().findByProperty("callableName");
      callablesink53.setCallableName((adams.flow.core.CallableActorReference) argOption.valueOf("output"));
      actors47[2] = callablesink53;
      sequence45.setActors(actors47);

      branches26[2] = sequence45;

      // Flow.Branch.numeric
      adams.flow.control.Sequence sequence55 = new adams.flow.control.Sequence();
      argOption = (AbstractArgumentOption) sequence55.getOptionManager().findByProperty("name");
      sequence55.setName((java.lang.String) argOption.valueOf("numeric"));
      argOption = (AbstractArgumentOption) sequence55.getOptionManager().findByProperty("actors");
      adams.flow.core.AbstractActor[] actors57 = new adams.flow.core.AbstractActor[3];

      // Flow.Branch.numeric.SetVariable
      adams.flow.transformer.SetVariable setvariable58 = new adams.flow.transformer.SetVariable();
      argOption = (AbstractArgumentOption) setvariable58.getOptionManager().findByProperty("variableName");
      setvariable58.setVariableName((adams.core.VariableName) argOption.valueOf("name"));
      argOption = (AbstractArgumentOption) setvariable58.getOptionManager().findByProperty("variableValue");
      setvariable58.setVariableValue((adams.core.base.BaseText) argOption.valueOf("numeric"));
      actors57[0] = setvariable58;

      // Flow.Branch.numeric.SpreadSheetQuery
      adams.flow.transformer.SpreadSheetQuery spreadsheetquery61 = new adams.flow.transformer.SpreadSheetQuery();
      argOption = (AbstractArgumentOption) spreadsheetquery61.getOptionManager().findByProperty("query");
      spreadsheetquery61.setQuery((adams.parser.SpreadSheetQueryText) argOption.valueOf("SELECT * WHERE CELLTYPE(anyvalue) = \"numeric\""));
      actors57[1] = spreadsheetquery61;

      // Flow.Branch.numeric.CallableSink
      adams.flow.sink.CallableSink callablesink63 = new adams.flow.sink.CallableSink();
      argOption = (AbstractArgumentOption) callablesink63.getOptionManager().findByProperty("callableName");
      callablesink63.setCallableName((adams.flow.core.CallableActorReference) argOption.valueOf("output"));
      actors57[2] = callablesink63;
      sequence55.setActors(actors57);

      branches26[3] = sequence55;

      // Flow.Branch.long
      adams.flow.control.Sequence sequence65 = new adams.flow.control.Sequence();
      argOption = (AbstractArgumentOption) sequence65.getOptionManager().findByProperty("name");
      sequence65.setName((java.lang.String) argOption.valueOf("long"));
      argOption = (AbstractArgumentOption) sequence65.getOptionManager().findByProperty("actors");
      adams.flow.core.AbstractActor[] actors67 = new adams.flow.core.AbstractActor[3];

      // Flow.Branch.long.SetVariable
      adams.flow.transformer.SetVariable setvariable68 = new adams.flow.transformer.SetVariable();
      argOption = (AbstractArgumentOption) setvariable68.getOptionManager().findByProperty("variableName");
      setvariable68.setVariableName((adams.core.VariableName) argOption.valueOf("name"));
      argOption = (AbstractArgumentOption) setvariable68.getOptionManager().findByProperty("variableValue");
      setvariable68.setVariableValue((adams.core.base.BaseText) argOption.valueOf("long"));
      actors67[0] = setvariable68;

      // Flow.Branch.long.SpreadSheetQuery
      adams.flow.transformer.SpreadSheetQuery spreadsheetquery71 = new adams.flow.transformer.SpreadSheetQuery();
      argOption = (AbstractArgumentOption) spreadsheetquery71.getOptionManager().findByProperty("query");
      spreadsheetquery71.setQuery((adams.parser.SpreadSheetQueryText) argOption.valueOf("SELECT * WHERE CELLTYPE(anyvalue) = \"long\""));
      actors67[1] = spreadsheetquery71;

      // Flow.Branch.long.CallableSink
      adams.flow.sink.CallableSink callablesink73 = new adams.flow.sink.CallableSink();
      argOption = (AbstractArgumentOption) callablesink73.getOptionManager().findByProperty("callableName");
      callablesink73.setCallableName((adams.flow.core.CallableActorReference) argOption.valueOf("output"));
      actors67[2] = callablesink73;
      sequence65.setActors(actors67);

      branches26[4] = sequence65;

      // Flow.Branch.double
      adams.flow.control.Sequence sequence75 = new adams.flow.control.Sequence();
      argOption = (AbstractArgumentOption) sequence75.getOptionManager().findByProperty("name");
      sequence75.setName((java.lang.String) argOption.valueOf("double"));
      argOption = (AbstractArgumentOption) sequence75.getOptionManager().findByProperty("actors");
      adams.flow.core.AbstractActor[] actors77 = new adams.flow.core.AbstractActor[3];

      // Flow.Branch.double.SetVariable
      adams.flow.transformer.SetVariable setvariable78 = new adams.flow.transformer.SetVariable();
      argOption = (AbstractArgumentOption) setvariable78.getOptionManager().findByProperty("variableName");
      setvariable78.setVariableName((adams.core.VariableName) argOption.valueOf("name"));
      argOption = (AbstractArgumentOption) setvariable78.getOptionManager().findByProperty("variableValue");
      setvariable78.setVariableValue((adams.core.base.BaseText) argOption.valueOf("double"));
      actors77[0] = setvariable78;

      // Flow.Branch.double.SpreadSheetQuery
      adams.flow.transformer.SpreadSheetQuery spreadsheetquery81 = new adams.flow.transformer.SpreadSheetQuery();
      argOption = (AbstractArgumentOption) spreadsheetquery81.getOptionManager().findByProperty("query");
      spreadsheetquery81.setQuery((adams.parser.SpreadSheetQueryText) argOption.valueOf("SELECT * WHERE CELLTYPE(anyvalue) = \"double\""));
      actors77[1] = spreadsheetquery81;

      // Flow.Branch.double.CallableSink
      adams.flow.sink.CallableSink callablesink83 = new adams.flow.sink.CallableSink();
      argOption = (AbstractArgumentOption) callablesink83.getOptionManager().findByProperty("callableName");
      callablesink83.setCallableName((adams.flow.core.CallableActorReference) argOption.valueOf("output"));
      actors77[2] = callablesink83;
      sequence75.setActors(actors77);

      branches26[5] = sequence75;

      // Flow.Branch.time
      adams.flow.control.Sequence sequence85 = new adams.flow.control.Sequence();
      argOption = (AbstractArgumentOption) sequence85.getOptionManager().findByProperty("name");
      sequence85.setName((java.lang.String) argOption.valueOf("time"));
      argOption = (AbstractArgumentOption) sequence85.getOptionManager().findByProperty("actors");
      adams.flow.core.AbstractActor[] actors87 = new adams.flow.core.AbstractActor[3];

      // Flow.Branch.time.SetVariable
      adams.flow.transformer.SetVariable setvariable88 = new adams.flow.transformer.SetVariable();
      argOption = (AbstractArgumentOption) setvariable88.getOptionManager().findByProperty("variableName");
      setvariable88.setVariableName((adams.core.VariableName) argOption.valueOf("name"));
      argOption = (AbstractArgumentOption) setvariable88.getOptionManager().findByProperty("variableValue");
      setvariable88.setVariableValue((adams.core.base.BaseText) argOption.valueOf("time"));
      actors87[0] = setvariable88;

      // Flow.Branch.time.SpreadSheetQuery
      adams.flow.transformer.SpreadSheetQuery spreadsheetquery91 = new adams.flow.transformer.SpreadSheetQuery();
      argOption = (AbstractArgumentOption) spreadsheetquery91.getOptionManager().findByProperty("query");
      spreadsheetquery91.setQuery((adams.parser.SpreadSheetQueryText) argOption.valueOf("SELECT * WHERE CELLTYPE(anyvalue) = \"time\""));
      actors87[1] = spreadsheetquery91;

      // Flow.Branch.time.CallableSink
      adams.flow.sink.CallableSink callablesink93 = new adams.flow.sink.CallableSink();
      argOption = (AbstractArgumentOption) callablesink93.getOptionManager().findByProperty("callableName");
      callablesink93.setCallableName((adams.flow.core.CallableActorReference) argOption.valueOf("output"));
      actors87[2] = callablesink93;
      sequence85.setActors(actors87);

      branches26[6] = sequence85;

      // Flow.Branch.date
      adams.flow.control.Sequence sequence95 = new adams.flow.control.Sequence();
      argOption = (AbstractArgumentOption) sequence95.getOptionManager().findByProperty("name");
      sequence95.setName((java.lang.String) argOption.valueOf("date"));
      argOption = (AbstractArgumentOption) sequence95.getOptionManager().findByProperty("actors");
      adams.flow.core.AbstractActor[] actors97 = new adams.flow.core.AbstractActor[3];

      // Flow.Branch.date.SetVariable
      adams.flow.transformer.SetVariable setvariable98 = new adams.flow.transformer.SetVariable();
      argOption = (AbstractArgumentOption) setvariable98.getOptionManager().findByProperty("variableName");
      setvariable98.setVariableName((adams.core.VariableName) argOption.valueOf("name"));
      argOption = (AbstractArgumentOption) setvariable98.getOptionManager().findByProperty("variableValue");
      setvariable98.setVariableValue((adams.core.base.BaseText) argOption.valueOf("date"));
      actors97[0] = setvariable98;

      // Flow.Branch.date.SpreadSheetQuery
      adams.flow.transformer.SpreadSheetQuery spreadsheetquery101 = new adams.flow.transformer.SpreadSheetQuery();
      argOption = (AbstractArgumentOption) spreadsheetquery101.getOptionManager().findByProperty("query");
      spreadsheetquery101.setQuery((adams.parser.SpreadSheetQueryText) argOption.valueOf("SELECT * WHERE CELLTYPE(anyvalue) = \"date\""));
      actors97[1] = spreadsheetquery101;

      // Flow.Branch.date.CallableSink
      adams.flow.sink.CallableSink callablesink103 = new adams.flow.sink.CallableSink();
      argOption = (AbstractArgumentOption) callablesink103.getOptionManager().findByProperty("callableName");
      callablesink103.setCallableName((adams.flow.core.CallableActorReference) argOption.valueOf("output"));
      actors97[2] = callablesink103;
      sequence95.setActors(actors97);

      branches26[7] = sequence95;

      // Flow.Branch.timestamp
      adams.flow.control.Sequence sequence105 = new adams.flow.control.Sequence();
      argOption = (AbstractArgumentOption) sequence105.getOptionManager().findByProperty("name");
      sequence105.setName((java.lang.String) argOption.valueOf("timestamp"));
      argOption = (AbstractArgumentOption) sequence105.getOptionManager().findByProperty("actors");
      adams.flow.core.AbstractActor[] actors107 = new adams.flow.core.AbstractActor[3];

      // Flow.Branch.timestamp.SetVariable
      adams.flow.transformer.SetVariable setvariable108 = new adams.flow.transformer.SetVariable();
      argOption = (AbstractArgumentOption) setvariable108.getOptionManager().findByProperty("variableName");
      setvariable108.setVariableName((adams.core.VariableName) argOption.valueOf("name"));
      argOption = (AbstractArgumentOption) setvariable108.getOptionManager().findByProperty("variableValue");
      setvariable108.setVariableValue((adams.core.base.BaseText) argOption.valueOf("timestamp"));
      actors107[0] = setvariable108;

      // Flow.Branch.timestamp.SpreadSheetQuery
      adams.flow.transformer.SpreadSheetQuery spreadsheetquery111 = new adams.flow.transformer.SpreadSheetQuery();
      argOption = (AbstractArgumentOption) spreadsheetquery111.getOptionManager().findByProperty("query");
      spreadsheetquery111.setQuery((adams.parser.SpreadSheetQueryText) argOption.valueOf("SELECT * WHERE CELLTYPE(anyvalue) = \"timestamp\""));
      actors107[1] = spreadsheetquery111;

      // Flow.Branch.timestamp.CallableSink
      adams.flow.sink.CallableSink callablesink113 = new adams.flow.sink.CallableSink();
      argOption = (AbstractArgumentOption) callablesink113.getOptionManager().findByProperty("callableName");
      callablesink113.setCallableName((adams.flow.core.CallableActorReference) argOption.valueOf("output"));
      actors107[2] = callablesink113;
      sequence105.setActors(actors107);

      branches26[8] = sequence105;

      // Flow.Branch.anydate
      adams.flow.control.Sequence sequence115 = new adams.flow.control.Sequence();
      argOption = (AbstractArgumentOption) sequence115.getOptionManager().findByProperty("name");
      sequence115.setName((java.lang.String) argOption.valueOf("anydate"));
      argOption = (AbstractArgumentOption) sequence115.getOptionManager().findByProperty("actors");
      adams.flow.core.AbstractActor[] actors117 = new adams.flow.core.AbstractActor[3];

      // Flow.Branch.anydate.SetVariable
      adams.flow.transformer.SetVariable setvariable118 = new adams.flow.transformer.SetVariable();
      argOption = (AbstractArgumentOption) setvariable118.getOptionManager().findByProperty("variableName");
      setvariable118.setVariableName((adams.core.VariableName) argOption.valueOf("name"));
      argOption = (AbstractArgumentOption) setvariable118.getOptionManager().findByProperty("variableValue");
      setvariable118.setVariableValue((adams.core.base.BaseText) argOption.valueOf("anydate"));
      actors117[0] = setvariable118;

      // Flow.Branch.anydate.SpreadSheetQuery
      adams.flow.transformer.SpreadSheetQuery spreadsheetquery121 = new adams.flow.transformer.SpreadSheetQuery();
      argOption = (AbstractArgumentOption) spreadsheetquery121.getOptionManager().findByProperty("query");
      spreadsheetquery121.setQuery((adams.parser.SpreadSheetQueryText) argOption.valueOf("SELECT * WHERE CELLTYPE(anyvalue) = \"anydate\""));
      actors117[1] = spreadsheetquery121;

      // Flow.Branch.anydate.CallableSink
      adams.flow.sink.CallableSink callablesink123 = new adams.flow.sink.CallableSink();
      argOption = (AbstractArgumentOption) callablesink123.getOptionManager().findByProperty("callableName");
      callablesink123.setCallableName((adams.flow.core.CallableActorReference) argOption.valueOf("output"));
      actors117[2] = callablesink123;
      sequence115.setActors(actors117);

      branches26[9] = sequence115;

      // Flow.Branch.!object
      adams.flow.control.Sequence sequence125 = new adams.flow.control.Sequence();
      argOption = (AbstractArgumentOption) sequence125.getOptionManager().findByProperty("name");
      sequence125.setName((java.lang.String) argOption.valueOf("!object"));
      argOption = (AbstractArgumentOption) sequence125.getOptionManager().findByProperty("actors");
      adams.flow.core.AbstractActor[] actors127 = new adams.flow.core.AbstractActor[3];

      // Flow.Branch.!object.SetVariable
      adams.flow.transformer.SetVariable setvariable128 = new adams.flow.transformer.SetVariable();
      argOption = (AbstractArgumentOption) setvariable128.getOptionManager().findByProperty("variableName");
      setvariable128.setVariableName((adams.core.VariableName) argOption.valueOf("name"));
      argOption = (AbstractArgumentOption) setvariable128.getOptionManager().findByProperty("variableValue");
      setvariable128.setVariableValue((adams.core.base.BaseText) argOption.valueOf("!object"));
      actors127[0] = setvariable128;

      // Flow.Branch.!object.SpreadSheetQuery
      adams.flow.transformer.SpreadSheetQuery spreadsheetquery131 = new adams.flow.transformer.SpreadSheetQuery();
      argOption = (AbstractArgumentOption) spreadsheetquery131.getOptionManager().findByProperty("query");
      spreadsheetquery131.setQuery((adams.parser.SpreadSheetQueryText) argOption.valueOf("SELECT * WHERE NOT CELLTYPE(anyvalue) = \"object\""));
      actors127[1] = spreadsheetquery131;

      // Flow.Branch.!object.CallableSink
      adams.flow.sink.CallableSink callablesink133 = new adams.flow.sink.CallableSink();
      argOption = (AbstractArgumentOption) callablesink133.getOptionManager().findByProperty("callableName");
      callablesink133.setCallableName((adams.flow.core.CallableActorReference) argOption.valueOf("output"));
      actors127[2] = callablesink133;
      sequence125.setActors(actors127);

      branches26[10] = sequence125;

      // Flow.Branch.missing
      adams.flow.control.Sequence sequence135 = new adams.flow.control.Sequence();
      argOption = (AbstractArgumentOption) sequence135.getOptionManager().findByProperty("name");
      sequence135.setName((java.lang.String) argOption.valueOf("missing"));
      argOption = (AbstractArgumentOption) sequence135.getOptionManager().findByProperty("actors");
      adams.flow.core.AbstractActor[] actors137 = new adams.flow.core.AbstractActor[3];

      // Flow.Branch.missing.SetVariable
      adams.flow.transformer.SetVariable setvariable138 = new adams.flow.transformer.SetVariable();
      argOption = (AbstractArgumentOption) setvariable138.getOptionManager().findByProperty("variableName");
      setvariable138.setVariableName((adams.core.VariableName) argOption.valueOf("name"));
      argOption = (AbstractArgumentOption) setvariable138.getOptionManager().findByProperty("variableValue");
      setvariable138.setVariableValue((adams.core.base.BaseText) argOption.valueOf("missing"));
      actors137[0] = setvariable138;

      // Flow.Branch.missing.SpreadSheetQuery
      adams.flow.transformer.SpreadSheetQuery spreadsheetquery141 = new adams.flow.transformer.SpreadSheetQuery();
      argOption = (AbstractArgumentOption) spreadsheetquery141.getOptionManager().findByProperty("query");
      spreadsheetquery141.setQuery((adams.parser.SpreadSheetQueryText) argOption.valueOf("SELECT * WHERE CELLTYPE(anyvalue) = \"missing\""));
      actors137[1] = spreadsheetquery141;

      // Flow.Branch.missing.CallableSink
      adams.flow.sink.CallableSink callablesink143 = new adams.flow.sink.CallableSink();
      argOption = (AbstractArgumentOption) callablesink143.getOptionManager().findByProperty("callableName");
      callablesink143.setCallableName((adams.flow.core.CallableActorReference) argOption.valueOf("output"));
      actors137[2] = callablesink143;
      sequence135.setActors(actors137);
      
      branches26[11] = sequence135;

      // sample (percent)
      adams.flow.control.Sequence ssp = new adams.flow.control.Sequence();
      ssp.setName("sample (percent)");
      adams.flow.transformer.SetVariable svsp = new adams.flow.transformer.SetVariable();
      svsp.setVariableName(new VariableName("name"));
      svsp.setVariableValue(new BaseText("sample (percent)"));
      ssp.add(svsp);
      adams.flow.transformer.SpreadSheetQuery sqsp = new adams.flow.transformer.SpreadSheetQuery();
      sqsp.setQuery(new SpreadSheetQueryText("SELECT 0.5"));
      ssp.add(sqsp);
      adams.flow.sink.CallableSink cssp = new adams.flow.sink.CallableSink();
      cssp.setCallableName(new CallableActorReference("output"));
      ssp.add(cssp);
      branches26[12] = ssp;

      // sample (absolute)
      adams.flow.control.Sequence ssa = new adams.flow.control.Sequence();
      ssa.setName("sample (absolute)");
      adams.flow.transformer.SetVariable svsa = new adams.flow.transformer.SetVariable();
      svsa.setVariableName(new VariableName("name"));
      svsa.setVariableValue(new BaseText("sample (absolute)"));
      ssa.add(svsa);
      adams.flow.transformer.SpreadSheetQuery sqsa = new adams.flow.transformer.SpreadSheetQuery();
      sqsa.setQuery(new SpreadSheetQueryText("SELECT 3"));
      ssa.add(sqsa);
      adams.flow.sink.CallableSink cssa = new adams.flow.sink.CallableSink();
      cssa.setCallableName(new CallableActorReference("output"));
      ssa.add(cssa);
      branches26[13] = ssa;
      
      branch25.setBranches(branches26);

      argOption = (AbstractArgumentOption) branch25.getOptionManager().findByProperty("numThreads");
      branch25.setNumThreads((Integer) argOption.valueOf("1"));
      actors2[3] = branch25;
      flow.setActors(actors2);

      argOption = (AbstractArgumentOption) flow.getOptionManager().findByProperty("flowExecutionListener");
      adams.flow.execution.NullListener nulllistener147 = new adams.flow.execution.NullListener();
      flow.setFlowExecutionListener(nulllistener147);

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

