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
 * SpreadSheetSetCellTest.java
 * Copyright (C) 2012 University of Waikato, Hamilton, New Zealand
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
 * Test for SpreadSheetSetCell actor.
 *
 * @author fracpete
 * @author adams.core.option.FlowJUnitTestProducer (code generator)
 * @version $Revision$
 */
public class SpreadSheetSetCellTest
  extends AbstractFlowTest {

  /**
   * Initializes the test.
   *
   * @param name	the name of the test
   */
  public SpreadSheetSetCellTest(String name) {
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
    
    m_TestHelper.copyResourceToTmp("simple1.csv");
    m_TestHelper.deleteFileFromTmp("dumpfile.csv");
  }

  /**
   * Called by JUnit after each test method.
   *
   * @throws Exception	if tear-down fails
   */
  @Override
  protected void tearDown() throws Exception {
    m_TestHelper.deleteFileFromTmp("simple1.csv");
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
    return new TestSuite(SpreadSheetSetCellTest.class);
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
      adams.flow.core.AbstractActor[] abstractactor1 = new adams.flow.core.AbstractActor[4];

      // Flow.FileSupplier
      adams.flow.source.FileSupplier singlefilesupplier2 = new adams.flow.source.FileSupplier();
      argOption = (AbstractArgumentOption) singlefilesupplier2.getOptionManager().findByProperty("files");
      singlefilesupplier2.setFiles(new adams.core.io.PlaceholderFile[]{(adams.core.io.PlaceholderFile) argOption.valueOf("${TMP}/simple1.csv")});

      abstractactor1[0] = singlefilesupplier2;

      // Flow.SpreadSheetReader
      adams.flow.transformer.SpreadSheetFileReader spreadsheetreader4 = new adams.flow.transformer.SpreadSheetFileReader();
      argOption = (AbstractArgumentOption) spreadsheetreader4.getOptionManager().findByProperty("reader");
      adams.data.io.input.CsvSpreadSheetReader csvspreadsheetreader6 = new adams.data.io.input.CsvSpreadSheetReader();
      spreadsheetreader4.setReader(csvspreadsheetreader6);

      abstractactor1[1] = spreadsheetreader4;

      // Flow.SpreadSheetSetCell
      adams.flow.transformer.SpreadSheetSetCell spreadsheetsetcell7 = new adams.flow.transformer.SpreadSheetSetCell();
      argOption = (AbstractArgumentOption) spreadsheetsetcell7.getOptionManager().findByProperty("value");
      spreadsheetsetcell7.setValue((java.lang.String) argOption.valueOf("HELLOWORLD"));

      spreadsheetsetcell7.setNoCopy(true);

      abstractactor1[2] = spreadsheetsetcell7;

      // Flow.SpreadSheetWriter
      adams.flow.sink.SpreadSheetFileWriter spreadsheetwriter9 = new adams.flow.sink.SpreadSheetFileWriter();
      argOption = (AbstractArgumentOption) spreadsheetwriter9.getOptionManager().findByProperty("outputFile");
      spreadsheetwriter9.setOutputFile((adams.core.io.PlaceholderFile) argOption.valueOf("${TMP}/dumpfile.csv"));

      argOption = (AbstractArgumentOption) spreadsheetwriter9.getOptionManager().findByProperty("writer");
      adams.data.io.output.CsvSpreadSheetWriter csvspreadsheetwriter12 = new adams.data.io.output.CsvSpreadSheetWriter();
      spreadsheetwriter9.setWriter(csvspreadsheetwriter12);

      abstractactor1[3] = spreadsheetwriter9;
      flow.setActors(abstractactor1);

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

