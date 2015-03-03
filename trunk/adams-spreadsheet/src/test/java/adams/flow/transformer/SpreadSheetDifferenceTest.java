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
 * SpreadSheetDifferenceTest.java
 * Copyright (C) 2012-2014 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.transformer;

import junit.framework.Test;
import junit.framework.TestSuite;
import adams.core.option.AbstractArgumentOption;
import adams.env.Environment;
import adams.flow.AbstractFlowTest;
import adams.flow.control.Flow;
import adams.flow.control.Trigger;
import adams.flow.core.AbstractActor;
import adams.flow.source.Start;
import adams.test.TmpFile;

/**
 * Test for SpreadSheetDifference actor.
 *
 * @author fracpete
 * @author adams.core.option.FlowJUnitTestProducer (code generator)
 * @version $Revision$
 */
public class SpreadSheetDifferenceTest
  extends AbstractFlowTest {

  /**
   * Initializes the test.
   *
   * @param name	the name of the test
   */
  public SpreadSheetDifferenceTest(String name) {
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
    m_TestHelper.copyResourceToTmp("simple1.csv");
    m_TestHelper.copyResourceToTmp("simple2.csv");
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
    m_TestHelper.deleteFileFromTmp("simple1.csv");
    m_TestHelper.deleteFileFromTmp("simple2.csv");
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
    return new TestSuite(SpreadSheetDifferenceTest.class);
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
      adams.flow.core.AbstractActor[] tmp1;
      {
	tmp1 = new adams.flow.core.AbstractActor[4];
	// Flow.FileSupplier
	adams.flow.source.FileSupplier tmp2 = new adams.flow.source.FileSupplier();
	tmp2.setOutputArray(true);

	argOption = (AbstractArgumentOption) tmp2.getOptionManager().findByProperty("files");
	adams.core.io.PlaceholderFile[] tmp3 = new adams.core.io.PlaceholderFile[2];
	tmp3[0] = (adams.core.io.PlaceholderFile) argOption.valueOf("${TMP}/bolts.csv");
	tmp3[1] = (adams.core.io.PlaceholderFile) argOption.valueOf("${TMP}/bolts.csv");
	tmp2.setFiles(tmp3);

	tmp1[0] = tmp2;
	// Flow.ArrayProcess
	adams.flow.control.ArrayProcess tmp4 = new adams.flow.control.ArrayProcess();
	argOption = (AbstractArgumentOption) tmp4.getOptionManager().findByProperty("actors");
	adams.flow.core.AbstractActor[] tmp5 = new adams.flow.core.AbstractActor[1];
	// Flow.ArrayProcess.SpreadSheetReader
	adams.flow.transformer.SpreadSheetFileReader tmp6 = new adams.flow.transformer.SpreadSheetFileReader();
	argOption = (AbstractArgumentOption) tmp6.getOptionManager().findByProperty("reader");
	adams.data.io.input.CsvSpreadSheetReader tmp8 = new adams.data.io.input.CsvSpreadSheetReader();
	tmp6.setReader(tmp8);

	tmp5[0] = tmp6;
	tmp4.setActors(tmp5);

	tmp1[1] = tmp4;
	// Flow.SpreadSheetDifference
	adams.flow.transformer.SpreadSheetDifference tmp9 = new adams.flow.transformer.SpreadSheetDifference();
	argOption = (AbstractArgumentOption) tmp9.getOptionManager().findByProperty("keyColumns");
	tmp9.setKeyColumns((adams.data.spreadsheet.SpreadSheetColumnRange) argOption.valueOf(""));

	tmp1[2] = tmp9;
	// Flow.DumpFile
	adams.flow.sink.DumpFile tmp11 = new adams.flow.sink.DumpFile();
	argOption = (AbstractArgumentOption) tmp11.getOptionManager().findByProperty("outputFile");
	tmp11.setOutputFile((adams.core.io.PlaceholderFile) argOption.valueOf("${TMP}/dumpfile.csv"));

	tmp1[3] = tmp11;
      }
      Trigger t1 = new Trigger();
      t1.setActors(tmp1);

      {
	tmp1 = new adams.flow.core.AbstractActor[4];
	// Flow.FileSupplier
	adams.flow.source.FileSupplier tmp2 = new adams.flow.source.FileSupplier();
	tmp2.setOutputArray(true);

	argOption = (AbstractArgumentOption) tmp2.getOptionManager().findByProperty("files");
	adams.core.io.PlaceholderFile[] tmp3 = new adams.core.io.PlaceholderFile[2];
	tmp3[0] = (adams.core.io.PlaceholderFile) argOption.valueOf("${TMP}/simple1.csv");
	tmp3[1] = (adams.core.io.PlaceholderFile) argOption.valueOf("${TMP}/simple2.csv");
	tmp2.setFiles(tmp3);

	tmp1[0] = tmp2;
	// Flow.ArrayProcess
	adams.flow.control.ArrayProcess tmp4 = new adams.flow.control.ArrayProcess();
	argOption = (AbstractArgumentOption) tmp4.getOptionManager().findByProperty("actors");
	adams.flow.core.AbstractActor[] tmp5 = new adams.flow.core.AbstractActor[1];
	// Flow.ArrayProcess.SpreadSheetReader
	adams.flow.transformer.SpreadSheetFileReader tmp6 = new adams.flow.transformer.SpreadSheetFileReader();
	argOption = (AbstractArgumentOption) tmp6.getOptionManager().findByProperty("reader");
	adams.data.io.input.CsvSpreadSheetReader tmp8 = new adams.data.io.input.CsvSpreadSheetReader();
	tmp6.setReader(tmp8);

	tmp5[0] = tmp6;
	tmp4.setActors(tmp5);

	tmp1[1] = tmp4;
	// Flow.SpreadSheetDifference
	adams.flow.transformer.SpreadSheetDifference tmp9 = new adams.flow.transformer.SpreadSheetDifference();
	argOption = (AbstractArgumentOption) tmp9.getOptionManager().findByProperty("keyColumns");
	tmp9.setKeyColumns((adams.data.spreadsheet.SpreadSheetColumnRange) argOption.valueOf("1"));

	tmp1[2] = tmp9;
	// Flow.DumpFile
	adams.flow.sink.DumpFile tmp11 = new adams.flow.sink.DumpFile();
	argOption = (AbstractArgumentOption) tmp11.getOptionManager().findByProperty("outputFile");
	tmp11.setAppend(true);
	tmp11.setOutputFile((adams.core.io.PlaceholderFile) argOption.valueOf("${TMP}/dumpfile.csv"));

	tmp1[3] = tmp11;
      }
      Trigger t2 = new Trigger();
      t2.setActors(tmp1);
      
      flow.setActors(new AbstractActor[]{
	  new Start(),
	  t1,
	  t2
      });

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

