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
 * SpreadSheetTransformCellsTest.java
 * Copyright (C) 2013-2014 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.transformer;

import junit.framework.Test;
import junit.framework.TestSuite;
import adams.core.option.AbstractArgumentOption;
import adams.env.Environment;
import adams.flow.AbstractFlowTest;
import adams.flow.control.Flow;
import adams.flow.core.Actor;
import adams.test.TmpFile;

/**
 * Test for SpreadSheetTransformCells actor.
 *
 * @author fracpete
 * @author adams.core.option.FlowJUnitTestProducer (code generator)
 * @version $Revision$
 */
public class SpreadSheetTransformCellsTest
  extends AbstractFlowTest {

  /**
   * Initializes the test.
   *
   * @param name	the name of the test
   */
  public SpreadSheetTransformCellsTest(String name) {
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
    return new TestSuite(SpreadSheetTransformCellsTest.class);
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
      adams.flow.core.Actor[] abstractactor1 = new adams.flow.core.Actor[5];

      // Flow.CallableActors
      adams.flow.standalone.CallableActors globalactors2 = new adams.flow.standalone.CallableActors();
      argOption = (AbstractArgumentOption) globalactors2.getOptionManager().findByProperty("actors");
      adams.flow.core.Actor[] abstractactor3 = new adams.flow.core.Actor[1];

      // Flow.CallableActors.MathExpression
      adams.flow.transformer.MathExpression mathexpression4 = new adams.flow.transformer.MathExpression();
      argOption = (AbstractArgumentOption) mathexpression4.getOptionManager().findByProperty("expression");
      mathexpression4.setExpression((adams.parser.MathematicalExpressionText) argOption.valueOf("rint(X)"));

      abstractactor3[0] = mathexpression4;
      globalactors2.setActors(abstractactor3);

      abstractactor1[0] = globalactors2;

      // Flow.FileSupplier
      adams.flow.source.FileSupplier singlefilesupplier6 = new adams.flow.source.FileSupplier();
      argOption = (AbstractArgumentOption) singlefilesupplier6.getOptionManager().findByProperty("files");
      singlefilesupplier6.setFiles(new adams.core.io.PlaceholderFile[]{(adams.core.io.PlaceholderFile) argOption.valueOf("${TMP}/bolts.csv")});

      abstractactor1[1] = singlefilesupplier6;

      // Flow.SpreadSheetFileReader
      adams.flow.transformer.SpreadSheetFileReader spreadsheetfilereader8 = new adams.flow.transformer.SpreadSheetFileReader();
      argOption = (AbstractArgumentOption) spreadsheetfilereader8.getOptionManager().findByProperty("reader");
      adams.data.io.input.CsvSpreadSheetReader csvspreadsheetreader10 = new adams.data.io.input.CsvSpreadSheetReader();
      spreadsheetfilereader8.setReader(csvspreadsheetreader10);

      abstractactor1[2] = spreadsheetfilereader8;

      // Flow.SpreadSheetTransformCells
      adams.flow.transformer.SpreadSheetTransformCells spreadsheettransformcells11 = new adams.flow.transformer.SpreadSheetTransformCells();
      argOption = (AbstractArgumentOption) spreadsheettransformcells11.getOptionManager().findByProperty("finder");
      adams.data.spreadsheet.cellfinder.CellRange cellrange13 = new adams.data.spreadsheet.cellfinder.CellRange();
      argOption = (AbstractArgumentOption) cellrange13.getOptionManager().findByProperty("columns");
      cellrange13.setColumns((adams.data.spreadsheet.SpreadSheetColumnRange) argOption.valueOf("4,7"));

      spreadsheettransformcells11.setFinder(cellrange13);

      argOption = (AbstractArgumentOption) spreadsheettransformcells11.getOptionManager().findByProperty("transformer");
      spreadsheettransformcells11.setTransformer((adams.flow.core.CallableActorReference) argOption.valueOf("MathExpression"));

      abstractactor1[3] = spreadsheettransformcells11;

      // Flow.DumpFile
      adams.flow.sink.DumpFile dumpfile16 = new adams.flow.sink.DumpFile();
      argOption = (AbstractArgumentOption) dumpfile16.getOptionManager().findByProperty("outputFile");
      dumpfile16.setOutputFile((adams.core.io.PlaceholderFile) argOption.valueOf("${TMP}/dumpfile.csv"));

      abstractactor1[4] = dumpfile16;
      flow.setActors(abstractactor1);

      argOption = (AbstractArgumentOption) flow.getOptionManager().findByProperty("flowExecutionListener");
      adams.flow.execution.NullListener nulllistener19 = new adams.flow.execution.NullListener();
      flow.setFlowExecutionListener(nulllistener19);

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

