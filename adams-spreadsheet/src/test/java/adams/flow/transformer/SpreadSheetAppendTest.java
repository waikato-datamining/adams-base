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
 * SpreadSheetAppendTest.java
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
 * Test for SpreadSheetAppend actor.
 *
 * @author fracpete
 * @author adams.core.option.FlowJUnitTestProducer (code generator)
 * @version $Revision$
 */
public class SpreadSheetAppendTest
  extends AbstractFlowTest {

  /**
   * Initializes the test.
   *
   * @param name	the name of the test
   */
  public SpreadSheetAppendTest(String name) {
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
    m_TestHelper.copyResourceToTmp("iris.csv");
    m_TestHelper.copyResourceToTmp("iris_with_id.csv");
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
    m_TestHelper.deleteFileFromTmp("iris.csv");
    m_TestHelper.deleteFileFromTmp("iris_with_id.csv");
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
    return new TestSuite(SpreadSheetAppendTest.class);
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
      adams.flow.core.AbstractActor[] tmp1 = new adams.flow.core.AbstractActor[5];
      // Flow.Start
      adams.flow.source.Start tmp2 = new adams.flow.source.Start();
      tmp1[0] = tmp2;
      // Flow.Trigger
      adams.flow.control.Trigger tmp3 = new adams.flow.control.Trigger();
      argOption = (AbstractArgumentOption) tmp3.getOptionManager().findByProperty("actors");
      adams.flow.core.AbstractActor[] tmp4 = new adams.flow.core.AbstractActor[3];
      // Flow.Trigger.FileSupplier
      adams.flow.source.FileSupplier tmp5 = new adams.flow.source.FileSupplier();
      argOption = (AbstractArgumentOption) tmp5.getOptionManager().findByProperty("files");
      tmp5.setFiles(new adams.core.io.PlaceholderFile[]{(adams.core.io.PlaceholderFile) argOption.valueOf("${TMP}/bolts.csv")});

      tmp4[0] = tmp5;
      // Flow.Trigger.SpreadSheetReader
      adams.flow.transformer.SpreadSheetFileReader tmp7 = new adams.flow.transformer.SpreadSheetFileReader();
      argOption = (AbstractArgumentOption) tmp7.getOptionManager().findByProperty("reader");
      adams.data.io.input.CsvSpreadSheetReader tmp9 = new adams.data.io.input.CsvSpreadSheetReader();
      tmp7.setReader(tmp9);

      tmp4[1] = tmp7;
      // Flow.Trigger.SpreadSheetAppend
      adams.flow.transformer.SpreadSheetAppend tmp10 = new adams.flow.transformer.SpreadSheetAppend();
      argOption = (AbstractArgumentOption) tmp10.getOptionManager().findByProperty("storageName");
      tmp10.setStorageName((adams.flow.control.StorageName) argOption.valueOf("combined"));

      tmp4[2] = tmp10;
      tmp3.setActors(tmp4);

      tmp1[1] = tmp3;
      // Flow.Trigger-1
      adams.flow.control.Trigger tmp12 = new adams.flow.control.Trigger();
      argOption = (AbstractArgumentOption) tmp12.getOptionManager().findByProperty("name");
      tmp12.setName((java.lang.String) argOption.valueOf("Trigger-1"));

      argOption = (AbstractArgumentOption) tmp12.getOptionManager().findByProperty("actors");
      adams.flow.core.AbstractActor[] tmp14 = new adams.flow.core.AbstractActor[3];
      // Flow.Trigger-1.FileSupplier
      adams.flow.source.FileSupplier tmp15 = new adams.flow.source.FileSupplier();
      argOption = (AbstractArgumentOption) tmp15.getOptionManager().findByProperty("files");
      tmp15.setFiles(new adams.core.io.PlaceholderFile[]{(adams.core.io.PlaceholderFile) argOption.valueOf("${TMP}/iris.csv")});

      tmp14[0] = tmp15;
      // Flow.Trigger-1.SpreadSheetReader
      adams.flow.transformer.SpreadSheetFileReader tmp17 = new adams.flow.transformer.SpreadSheetFileReader();
      argOption = (AbstractArgumentOption) tmp17.getOptionManager().findByProperty("reader");
      adams.data.io.input.CsvSpreadSheetReader tmp19 = new adams.data.io.input.CsvSpreadSheetReader();
      tmp17.setReader(tmp19);

      tmp14[1] = tmp17;
      // Flow.Trigger-1.SpreadSheetAppend
      adams.flow.transformer.SpreadSheetAppend tmp20 = new adams.flow.transformer.SpreadSheetAppend();
      argOption = (AbstractArgumentOption) tmp20.getOptionManager().findByProperty("storageName");
      tmp20.setStorageName((adams.flow.control.StorageName) argOption.valueOf("combined"));

      tmp14[2] = tmp20;
      tmp12.setActors(tmp14);

      tmp1[2] = tmp12;
      // Flow.Trigger-3
      adams.flow.control.Trigger tmp22 = new adams.flow.control.Trigger();
      argOption = (AbstractArgumentOption) tmp22.getOptionManager().findByProperty("name");
      tmp22.setName((java.lang.String) argOption.valueOf("Trigger-3"));

      argOption = (AbstractArgumentOption) tmp22.getOptionManager().findByProperty("actors");
      adams.flow.core.AbstractActor[] tmp24 = new adams.flow.core.AbstractActor[3];
      // Flow.Trigger-3.FileSupplier
      adams.flow.source.FileSupplier tmp25 = new adams.flow.source.FileSupplier();
      argOption = (AbstractArgumentOption) tmp25.getOptionManager().findByProperty("files");
      tmp25.setFiles(new adams.core.io.PlaceholderFile[]{(adams.core.io.PlaceholderFile) argOption.valueOf("${TMP}/iris_with_id.csv")});

      tmp24[0] = tmp25;
      // Flow.Trigger-3.SpreadSheetReader
      adams.flow.transformer.SpreadSheetFileReader tmp27 = new adams.flow.transformer.SpreadSheetFileReader();
      argOption = (AbstractArgumentOption) tmp27.getOptionManager().findByProperty("reader");
      adams.data.io.input.CsvSpreadSheetReader tmp29 = new adams.data.io.input.CsvSpreadSheetReader();
      tmp27.setReader(tmp29);

      tmp24[1] = tmp27;
      // Flow.Trigger-3.SpreadSheetAppend
      adams.flow.transformer.SpreadSheetAppend tmp30 = new adams.flow.transformer.SpreadSheetAppend();
      argOption = (AbstractArgumentOption) tmp30.getOptionManager().findByProperty("storageName");
      tmp30.setStorageName((adams.flow.control.StorageName) argOption.valueOf("combined"));

      tmp24[2] = tmp30;
      tmp22.setActors(tmp24);

      tmp1[3] = tmp22;
      // Flow.Trigger-2
      adams.flow.control.Trigger tmp32 = new adams.flow.control.Trigger();
      argOption = (AbstractArgumentOption) tmp32.getOptionManager().findByProperty("name");
      tmp32.setName((java.lang.String) argOption.valueOf("Trigger-2"));

      argOption = (AbstractArgumentOption) tmp32.getOptionManager().findByProperty("actors");
      adams.flow.core.AbstractActor[] tmp34 = new adams.flow.core.AbstractActor[2];
      // Flow.Trigger-2.StorageValue
      adams.flow.source.StorageValue tmp35 = new adams.flow.source.StorageValue();
      argOption = (AbstractArgumentOption) tmp35.getOptionManager().findByProperty("storageName");
      tmp35.setStorageName((adams.flow.control.StorageName) argOption.valueOf("combined"));

      tmp34[0] = tmp35;
      // Flow.Trigger-2.SpreadSheetWriter
      adams.flow.sink.SpreadSheetFileWriter tmp37 = new adams.flow.sink.SpreadSheetFileWriter();
      argOption = (AbstractArgumentOption) tmp37.getOptionManager().findByProperty("outputFile");
      tmp37.setOutputFile((adams.core.io.PlaceholderFile) argOption.valueOf("${TMP}/dumpfile.csv"));

      argOption = (AbstractArgumentOption) tmp37.getOptionManager().findByProperty("writer");
      adams.data.io.output.CsvSpreadSheetWriter tmp40 = new adams.data.io.output.CsvSpreadSheetWriter();
      tmp37.setWriter(tmp40);

      tmp34[1] = tmp37;
      tmp32.setActors(tmp34);

      tmp1[4] = tmp32;
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

