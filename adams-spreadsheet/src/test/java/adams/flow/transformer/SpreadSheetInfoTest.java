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
 * SpreadSheetInfoTest.java
 * Copyright (C) 2011-2014 University of Waikato, Hamilton, New Zealand
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
 * Test for SpreadSheetInfo actor.
 *
 * @author fracpete
 * @author adams.core.option.FlowJUnitTestProducer (code generator)
 * @version $Revision$
 */
public class SpreadSheetInfoTest
  extends AbstractFlowTest {

  /**
   * Initializes the test.
   *
   * @param name	the name of the test
   */
  public SpreadSheetInfoTest(String name) {
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

    m_TestHelper.copyResourceToTmp("iris.csv");
    m_TestHelper.deleteFileFromTmp("dumpfile.txt");
  }

  /**
   * Called by JUnit after each test method.
   *
   * @throws Exception	if tear-down fails
   */
  @Override
  protected void tearDown() throws Exception {
    m_TestHelper.deleteFileFromTmp("iris.csv");
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
    return new TestSuite(SpreadSheetInfoTest.class);
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
      adams.flow.core.Actor[] tmp1 = new adams.flow.core.Actor[5];
      adams.flow.standalone.CallableActors tmp2 = new adams.flow.standalone.CallableActors();
      argOption = (AbstractArgumentOption) tmp2.getOptionManager().findByProperty("actors");
      adams.flow.core.Actor[] tmp3 = new adams.flow.core.Actor[1];
      adams.flow.sink.DumpFile tmp4 = new adams.flow.sink.DumpFile();
      argOption = (AbstractArgumentOption) tmp4.getOptionManager().findByProperty("outputFile");
      tmp4.setOutputFile((adams.core.io.PlaceholderFile) argOption.valueOf("${TMP}/dumpfile.txt"));
      tmp4.setAppend(true);

      tmp3[0] = tmp4;
      tmp2.setActors(tmp3);

      tmp1[0] = tmp2;
      adams.flow.source.FileSupplier tmp6 = new adams.flow.source.FileSupplier();
      argOption = (AbstractArgumentOption) tmp6.getOptionManager().findByProperty("files");
      tmp6.setFiles(new adams.core.io.PlaceholderFile[]{(adams.core.io.PlaceholderFile) argOption.valueOf("${TMP}/iris.csv")});

      tmp1[1] = tmp6;
      adams.flow.transformer.SpreadSheetFileReader tmp8 = new adams.flow.transformer.SpreadSheetFileReader();
      argOption = (AbstractArgumentOption) tmp8.getOptionManager().findByProperty("reader");
      adams.data.io.input.CsvSpreadSheetReader tmp10 = new adams.data.io.input.CsvSpreadSheetReader();
      tmp8.setReader(tmp10);

      tmp1[2] = tmp8;
      adams.flow.transformer.SetStorageValue tmp11 = new adams.flow.transformer.SetStorageValue();
      argOption = (AbstractArgumentOption) tmp11.getOptionManager().findByProperty("storageName");
      tmp11.setStorageName((adams.flow.control.StorageName) argOption.valueOf("sheet"));

      tmp1[3] = tmp11;
      adams.flow.control.Branch tmp13 = new adams.flow.control.Branch();
      argOption = (AbstractArgumentOption) tmp13.getOptionManager().findByProperty("branches");
      adams.flow.core.Actor[] tmp14 = new adams.flow.core.Actor[3];
      adams.flow.control.Sequence tmp15 = new adams.flow.control.Sequence();
      argOption = (AbstractArgumentOption) tmp15.getOptionManager().findByProperty("name");
      tmp15.setName((java.lang.String) argOption.valueOf("rows"));

      argOption = (AbstractArgumentOption) tmp15.getOptionManager().findByProperty("actors");
      adams.flow.core.Actor[] tmp17 = new adams.flow.core.Actor[2];
      adams.flow.transformer.SpreadSheetInfo tmp18 = new adams.flow.transformer.SpreadSheetInfo();
      tmp17[0] = tmp18;
      adams.flow.sink.CallableSink tmp19 = new adams.flow.sink.CallableSink();
      argOption = (AbstractArgumentOption) tmp19.getOptionManager().findByProperty("callableName");
      tmp19.setCallableName((adams.flow.core.CallableActorReference) argOption.valueOf("DumpFile"));

      tmp17[1] = tmp19;
      tmp15.setActors(tmp17);

      tmp14[0] = tmp15;
      adams.flow.control.Sequence tmp21 = new adams.flow.control.Sequence();
      argOption = (AbstractArgumentOption) tmp21.getOptionManager().findByProperty("name");
      tmp21.setName((java.lang.String) argOption.valueOf("cols"));

      argOption = (AbstractArgumentOption) tmp21.getOptionManager().findByProperty("actors");
      adams.flow.core.Actor[] tmp23 = new adams.flow.core.Actor[3];
      adams.flow.transformer.SpreadSheetInfo tmp24 = new adams.flow.transformer.SpreadSheetInfo();
      argOption = (AbstractArgumentOption) tmp24.getOptionManager().findByProperty("type");
      tmp24.setType((adams.flow.transformer.SpreadSheetInfo.InfoType) argOption.valueOf("NUM_COLUMNS"));

      tmp23[0] = tmp24;
      adams.flow.transformer.SetVariable tmp26 = new adams.flow.transformer.SetVariable();
      argOption = (AbstractArgumentOption) tmp26.getOptionManager().findByProperty("variableName");
      tmp26.setVariableName((adams.core.VariableName) argOption.valueOf("num_values"));

      tmp23[1] = tmp26;
      adams.flow.sink.CallableSink tmp28 = new adams.flow.sink.CallableSink();
      argOption = (AbstractArgumentOption) tmp28.getOptionManager().findByProperty("callableName");
      tmp28.setCallableName((adams.flow.core.CallableActorReference) argOption.valueOf("DumpFile"));

      tmp23[2] = tmp28;
      tmp21.setActors(tmp23);

      tmp14[1] = tmp21;
      adams.flow.control.Trigger tmp30 = new adams.flow.control.Trigger();
      argOption = (AbstractArgumentOption) tmp30.getOptionManager().findByProperty("name");
      tmp30.setName((java.lang.String) argOption.valueOf("col names"));

      argOption = (AbstractArgumentOption) tmp30.getOptionManager().findByProperty("actors");
      adams.flow.core.Actor[] tmp32 = new adams.flow.core.Actor[3];
      adams.flow.source.ForLoop tmp33 = new adams.flow.source.ForLoop();
      argOption = (AbstractArgumentOption) tmp33.getOptionManager().findByProperty("loopUpper");
      argOption.setVariable("@{num_cols}");

      tmp32[0] = tmp33;
      adams.flow.transformer.SetVariable tmp34 = new adams.flow.transformer.SetVariable();
      argOption = (AbstractArgumentOption) tmp34.getOptionManager().findByProperty("variableName");
      tmp34.setVariableName((adams.core.VariableName) argOption.valueOf("col_index"));

      tmp32[1] = tmp34;
      adams.flow.control.Trigger tmp36 = new adams.flow.control.Trigger();
      argOption = (AbstractArgumentOption) tmp36.getOptionManager().findByProperty("actors");
      adams.flow.core.Actor[] tmp37 = new adams.flow.core.Actor[3];
      adams.flow.source.StorageValue tmp38 = new adams.flow.source.StorageValue();
      argOption = (AbstractArgumentOption) tmp38.getOptionManager().findByProperty("storageName");
      tmp38.setStorageName((adams.flow.control.StorageName) argOption.valueOf("sheet"));

      tmp37[0] = tmp38;
      adams.flow.transformer.SpreadSheetInfo tmp40 = new adams.flow.transformer.SpreadSheetInfo();
      argOption = (AbstractArgumentOption) tmp40.getOptionManager().findByProperty("type");
      tmp40.setType((adams.flow.transformer.SpreadSheetInfo.InfoType) argOption.valueOf("COLUMN_NAME"));

      argOption = (AbstractArgumentOption) tmp40.getOptionManager().findByProperty("columnIndex");
      argOption.setVariable("@{col_index}");

      tmp37[1] = tmp40;
      adams.flow.sink.CallableSink tmp42 = new adams.flow.sink.CallableSink();
      argOption = (AbstractArgumentOption) tmp42.getOptionManager().findByProperty("callableName");
      tmp42.setCallableName((adams.flow.core.CallableActorReference) argOption.valueOf("DumpFile"));

      tmp37[2] = tmp42;
      tmp36.setActors(tmp37);

      tmp32[2] = tmp36;
      tmp30.setActors(tmp32);

      tmp14[2] = tmp30;
      tmp13.setBranches(tmp14);

      argOption = (AbstractArgumentOption) tmp13.getOptionManager().findByProperty("numThreads");
      tmp13.setNumThreads((Integer) argOption.valueOf("0"));

      tmp1[4] = tmp13;
      flow.setActors(tmp1);

    }
    catch (Exception e) {
      fail("Failed to set up actor: " + e);
    }

    // TODO
    dumpActor(flow);
    
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

