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
 * SpreadSheetInsertColumnTest.java
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
 * Test for SpreadSheetInsertColumn actor.
 *
 * @author fracpete
 * @author adams.core.option.FlowJUnitTestProducer (code generator)
 * @version $Revision$
 */
public class SpreadSheetInsertColumnTest
  extends AbstractFlowTest {

  /**
   * Initializes the test.
   *
   * @param name	the name of the test
   */
  public SpreadSheetInsertColumnTest(String name) {
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
    return new TestSuite(SpreadSheetInsertColumnTest.class);
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
      adams.flow.core.AbstractActor[] tmp1 = new adams.flow.core.AbstractActor[6];
      // Flow.FileSupplier
      adams.flow.source.FileSupplier tmp2 = new adams.flow.source.FileSupplier();
      argOption = (AbstractArgumentOption) tmp2.getOptionManager().findByProperty("files");
      tmp2.setFiles(new adams.core.io.PlaceholderFile[]{(adams.core.io.PlaceholderFile) argOption.valueOf("${TMP}/bolts.csv")});

      tmp1[0] = tmp2;
      // Flow.SpreadSheetReader
      adams.flow.transformer.SpreadSheetFileReader tmp4 = new adams.flow.transformer.SpreadSheetFileReader();
      argOption = (AbstractArgumentOption) tmp4.getOptionManager().findByProperty("reader");
      adams.data.io.input.CsvSpreadSheetReader tmp6 = new adams.data.io.input.CsvSpreadSheetReader();
      tmp4.setReader(tmp6);

      tmp1[1] = tmp4;
      // Flow.SpreadSheetInsertColumn
      adams.flow.transformer.SpreadSheetInsertColumn tmp7 = new adams.flow.transformer.SpreadSheetInsertColumn();
      tmp7.setAfter(true);

      argOption = (AbstractArgumentOption) tmp7.getOptionManager().findByProperty("header");
      tmp7.setHeader((java.lang.String) argOption.valueOf("Last"));

      argOption = (AbstractArgumentOption) tmp7.getOptionManager().findByProperty("value");
      tmp7.setValue((java.lang.String) argOption.valueOf("last"));

      tmp1[2] = tmp7;
      // Flow.SpreadSheetInsertColumn-1
      adams.flow.transformer.SpreadSheetInsertColumn tmp10 = new adams.flow.transformer.SpreadSheetInsertColumn();
      argOption = (AbstractArgumentOption) tmp10.getOptionManager().findByProperty("name");
      tmp10.setName((java.lang.String) argOption.valueOf("SpreadSheetInsertColumn-1"));

      argOption = (AbstractArgumentOption) tmp10.getOptionManager().findByProperty("position");
      tmp10.setPosition((adams.data.spreadsheet.SpreadSheetColumnIndex) argOption.valueOf("first"));

      argOption = (AbstractArgumentOption) tmp10.getOptionManager().findByProperty("header");
      tmp10.setHeader((java.lang.String) argOption.valueOf("First"));

      argOption = (AbstractArgumentOption) tmp10.getOptionManager().findByProperty("value");
      tmp10.setValue((java.lang.String) argOption.valueOf("first"));

      tmp1[3] = tmp10;
      // Flow.SpreadSheetInsertColumn-2
      adams.flow.transformer.SpreadSheetInsertColumn tmp15 = new adams.flow.transformer.SpreadSheetInsertColumn();
      argOption = (AbstractArgumentOption) tmp15.getOptionManager().findByProperty("name");
      tmp15.setName((java.lang.String) argOption.valueOf("SpreadSheetInsertColumn-2"));

      argOption = (AbstractArgumentOption) tmp15.getOptionManager().findByProperty("position");
      tmp15.setPosition((adams.data.spreadsheet.SpreadSheetColumnIndex) argOption.valueOf("third"));

      argOption = (AbstractArgumentOption) tmp15.getOptionManager().findByProperty("header");
      tmp15.setHeader((java.lang.String) argOption.valueOf("Third"));

      tmp1[4] = tmp15;
      // Flow.SpreadSheetWriter
      adams.flow.sink.SpreadSheetFileWriter tmp19 = new adams.flow.sink.SpreadSheetFileWriter();
      argOption = (AbstractArgumentOption) tmp19.getOptionManager().findByProperty("outputFile");
      tmp19.setOutputFile((adams.core.io.PlaceholderFile) argOption.valueOf("${TMP}/dumpfile.csv"));

      argOption = (AbstractArgumentOption) tmp19.getOptionManager().findByProperty("writer");
      adams.data.io.output.CsvSpreadSheetWriter tmp22 = new adams.data.io.output.CsvSpreadSheetWriter();
      argOption = (AbstractArgumentOption) tmp22.getOptionManager().findByProperty("missingValue");
      tmp22.setMissingValue((java.lang.String) argOption.valueOf("N/A"));

      argOption = (AbstractArgumentOption) tmp22.getOptionManager().findByProperty("numberFormat");
      tmp22.setNumberFormat((java.lang.String) argOption.valueOf("0.000"));

      tmp19.setWriter(tmp22);

      tmp1[5] = tmp19;
      flow.setActors(tmp1);

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

