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
 * SpreadSheetTransformHeaderCellsTest.java
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
 * Test for SpreadSheetTransformHeaderCells actor.
 *
 * @author fracpete
 * @author adams.core.option.FlowJUnitTestProducer (code generator)
 * @version $Revision$
 */
public class SpreadSheetTransformHeaderCellsTest
  extends AbstractFlowTest {

  /**
   * Initializes the test.
   *
   * @param name	the name of the test
   */
  public SpreadSheetTransformHeaderCellsTest(String name) {
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
    
    m_TestHelper.copyResourceToTmp("bolts.csv");
    m_TestHelper.deleteFileFromTmp("dumpfile.csv");
  }

  /**
   * Called by JUnit after each test method.
   *
   * @throws Exception	if tear-down fails
   */
  @Override
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
    return new TestSuite(SpreadSheetTransformHeaderCellsTest.class);
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
      adams.flow.core.AbstractActor[] actors1 = new adams.flow.core.AbstractActor[5];

      // Flow.CallableActors
      adams.flow.standalone.CallableActors callableactors2 = new adams.flow.standalone.CallableActors();
      argOption = (AbstractArgumentOption) callableactors2.getOptionManager().findByProperty("actors");
      adams.flow.core.AbstractActor[] actors3 = new adams.flow.core.AbstractActor[1];

      // Flow.CallableActors.to lowercase
      adams.flow.transformer.Convert convert4 = new adams.flow.transformer.Convert();
      argOption = (AbstractArgumentOption) convert4.getOptionManager().findByProperty("name");
      convert4.setName((java.lang.String) argOption.valueOf("to lowercase"));
      argOption = (AbstractArgumentOption) convert4.getOptionManager().findByProperty("conversion");
      adams.data.conversion.LowerCase lowercase7 = new adams.data.conversion.LowerCase();
      convert4.setConversion(lowercase7);

      actors3[0] = convert4;
      callableactors2.setActors(actors3);

      actors1[0] = callableactors2;

      // Flow.FileSupplier
      adams.flow.source.FileSupplier filesupplier8 = new adams.flow.source.FileSupplier();
      argOption = (AbstractArgumentOption) filesupplier8.getOptionManager().findByProperty("files");
      adams.core.io.PlaceholderFile[] files9 = new adams.core.io.PlaceholderFile[1];
      files9[0] = (adams.core.io.PlaceholderFile) argOption.valueOf("${TMP}/bolts.csv");
      filesupplier8.setFiles(files9);
      actors1[1] = filesupplier8;

      // Flow.SpreadSheetFileReader
      adams.flow.transformer.SpreadSheetFileReader spreadsheetfilereader10 = new adams.flow.transformer.SpreadSheetFileReader();
      argOption = (AbstractArgumentOption) spreadsheetfilereader10.getOptionManager().findByProperty("reader");
      adams.data.io.input.CsvSpreadSheetReader csvspreadsheetreader12 = new adams.data.io.input.CsvSpreadSheetReader();
      argOption = (AbstractArgumentOption) csvspreadsheetreader12.getOptionManager().findByProperty("spreadSheetType");
      adams.data.spreadsheet.SpreadSheet spreadsheet14 = new adams.data.spreadsheet.SpreadSheet();
      csvspreadsheetreader12.setSpreadSheetType(spreadsheet14);

      spreadsheetfilereader10.setReader(csvspreadsheetreader12);

      actors1[2] = spreadsheetfilereader10;

      // Flow.SpreadSheetTransformHeaderCells
      adams.flow.transformer.SpreadSheetTransformHeaderCells spreadsheettransformheadercells15 = new adams.flow.transformer.SpreadSheetTransformHeaderCells();
      argOption = (AbstractArgumentOption) spreadsheettransformheadercells15.getOptionManager().findByProperty("transformer");
      spreadsheettransformheadercells15.setTransformer((adams.flow.core.CallableActorReference) argOption.valueOf("to lowercase"));
      actors1[3] = spreadsheettransformheadercells15;

      // Flow.DumpFile
      adams.flow.sink.DumpFile dumpfile17 = new adams.flow.sink.DumpFile();
      argOption = (AbstractArgumentOption) dumpfile17.getOptionManager().findByProperty("outputFile");
      dumpfile17.setOutputFile((adams.core.io.PlaceholderFile) argOption.valueOf("${TMP}/dumpfile.csv"));
      actors1[4] = dumpfile17;
      flow.setActors(actors1);

      argOption = (AbstractArgumentOption) flow.getOptionManager().findByProperty("flowExecutionListener");
      adams.flow.execution.NullListener nulllistener20 = new adams.flow.execution.NullListener();
      flow.setFlowExecutionListener(nulllistener20);

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

