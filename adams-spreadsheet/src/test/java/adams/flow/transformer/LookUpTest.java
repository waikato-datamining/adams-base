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
 * LookUpTest.java
 * Copyright (C) 2013 University of Waikato, Hamilton, New Zealand
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
 * Test for LookUp actor.
 *
 * @author fracpete
 * @author adams.core.option.FlowJUnitTestProducer (code generator)
 * @version $Revision$
 */
public class LookUpTest
  extends AbstractFlowTest {

  /**
   * Initializes the test.
   *
   * @param name	the name of the test
   */
  public LookUpTest(String name) {
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
    
    m_TestHelper.copyResourceToTmp("lookup.csv");
    m_TestHelper.deleteFileFromTmp("dumpfile.txt");
  }

  /**
   * Called by JUnit after each test method.
   *
   * @throws Exception	if tear-down fails
   */
  @Override
  protected void tearDown() throws Exception {
    m_TestHelper.deleteFileFromTmp("lookup.csv");
    m_TestHelper.deleteFileFromTmp("dumpfile.txt");
    
    super.tearDown();
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
    return new TestSuite(LookUpTest.class);
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
      singlefilesupplier2.setFiles(new adams.core.io.PlaceholderFile[]{(adams.core.io.PlaceholderFile) argOption.valueOf("${TMP}/lookup.csv")});

      abstractactor1[0] = singlefilesupplier2;

      // Flow.SpreadSheetFileReader
      adams.flow.transformer.SpreadSheetFileReader spreadsheetfilereader4 = new adams.flow.transformer.SpreadSheetFileReader();
      argOption = (AbstractArgumentOption) spreadsheetfilereader4.getOptionManager().findByProperty("reader");
      adams.data.io.input.CsvSpreadSheetReader csvspreadsheetreader6 = new adams.data.io.input.CsvSpreadSheetReader();
      spreadsheetfilereader4.setReader(csvspreadsheetreader6);

      abstractactor1[1] = spreadsheetfilereader4;

      // Flow.LookUpInit
      adams.flow.transformer.LookUpInit lookupinit7 = new adams.flow.transformer.LookUpInit();
      abstractactor1[2] = lookupinit7;

      // Flow.Trigger
      adams.flow.control.Trigger trigger8 = new adams.flow.control.Trigger();
      argOption = (AbstractArgumentOption) trigger8.getOptionManager().findByProperty("actors");
      adams.flow.core.AbstractActor[] abstractactor9 = new adams.flow.core.AbstractActor[3];

      // Flow.Trigger.StringConstants
      adams.flow.source.StringConstants stringconstants10 = new adams.flow.source.StringConstants();
      argOption = (AbstractArgumentOption) stringconstants10.getOptionManager().findByProperty("strings");
      adams.core.base.BaseString[] basestring11 = new adams.core.base.BaseString[2];
      basestring11[0] = (adams.core.base.BaseString) argOption.valueOf("NZ");
      basestring11[1] = (adams.core.base.BaseString) argOption.valueOf("UK");
      stringconstants10.setStrings(basestring11);

      abstractactor9[0] = stringconstants10;

      // Flow.Trigger.LookUp
      adams.flow.transformer.LookUp lookup12 = new adams.flow.transformer.LookUp();
      abstractactor9[1] = lookup12;

      // Flow.Trigger.DumpFile
      adams.flow.sink.DumpFile dumpfile13 = new adams.flow.sink.DumpFile();
      argOption = (AbstractArgumentOption) dumpfile13.getOptionManager().findByProperty("outputFile");
      dumpfile13.setOutputFile((adams.core.io.PlaceholderFile) argOption.valueOf("${TMP}/dumpfile.txt"));

      dumpfile13.setAppend(true);

      abstractactor9[2] = dumpfile13;
      trigger8.setActors(abstractactor9);

      abstractactor1[3] = trigger8;
      flow.setActors(abstractactor1);

      argOption = (AbstractArgumentOption) flow.getOptionManager().findByProperty("flowExecutionListener");
      adams.flow.execution.NullListener nulllistener16 = new adams.flow.execution.NullListener();
      flow.setFlowExecutionListener(nulllistener16);

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

