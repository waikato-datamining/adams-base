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
 * SpreadSheetInsertRowTest.java
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
 * Test for SpreadSheetInsertRow actor.
 *
 * @author fracpete
 * @author adams.core.option.FlowJUnitTestProducer (code generator)
 * @version $Revision$
 */
public class SpreadSheetInsertRowTest
  extends AbstractFlowTest {

  /**
   * Initializes the test.
   *
   * @param name	the name of the test
   */
  public SpreadSheetInsertRowTest(String name) {
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
    return new TestSuite(SpreadSheetInsertRowTest.class);
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
      // Flow.SpreadSheetInsertRow
      adams.flow.transformer.SpreadSheetInsertRow tmp7 = new adams.flow.transformer.SpreadSheetInsertRow();
      argOption = (AbstractArgumentOption) tmp7.getOptionManager().findByProperty("position");
      tmp7.setPosition((adams.core.Index) argOption.valueOf("first"));

      argOption = (AbstractArgumentOption) tmp7.getOptionManager().findByProperty("value");
      tmp7.setValue((java.lang.String) argOption.valueOf("first"));

      tmp1[2] = tmp7;
      // Flow.SpreadSheetInsertRow-1
      adams.flow.transformer.SpreadSheetInsertRow tmp10 = new adams.flow.transformer.SpreadSheetInsertRow();
      argOption = (AbstractArgumentOption) tmp10.getOptionManager().findByProperty("name");
      tmp10.setName((java.lang.String) argOption.valueOf("SpreadSheetInsertRow-1"));

      argOption = (AbstractArgumentOption) tmp10.getOptionManager().findByProperty("value");
      tmp10.setValue((java.lang.String) argOption.valueOf("second2last"));

      tmp1[3] = tmp10;
      // Flow.SpreadSheetInsertRow-2
      adams.flow.transformer.SpreadSheetInsertRow tmp13 = new adams.flow.transformer.SpreadSheetInsertRow();
      argOption = (AbstractArgumentOption) tmp13.getOptionManager().findByProperty("name");
      tmp13.setName((java.lang.String) argOption.valueOf("SpreadSheetInsertRow-2"));

      tmp13.setAfter(true);

      argOption = (AbstractArgumentOption) tmp13.getOptionManager().findByProperty("value");
      tmp13.setValue((java.lang.String) argOption.valueOf("last"));

      tmp1[4] = tmp13;
      // Flow.DumpFile
      adams.flow.sink.DumpFile tmp16 = new adams.flow.sink.DumpFile();
      argOption = (AbstractArgumentOption) tmp16.getOptionManager().findByProperty("outputFile");
      tmp16.setOutputFile((adams.core.io.PlaceholderFile) argOption.valueOf("${TMP}/dumpfile.csv"));

      tmp1[5] = tmp16;
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

