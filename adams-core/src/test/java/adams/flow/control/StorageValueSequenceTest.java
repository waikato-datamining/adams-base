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
 * StorageValueSequenceTest.java
 * Copyright (C) 2012 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.control;

import junit.framework.Test;
import junit.framework.TestSuite;
import adams.core.Index;
import adams.core.option.AbstractArgumentOption;
import adams.env.Environment;
import adams.flow.AbstractFlowTest;
import adams.flow.core.AbstractActor;
import adams.test.TmpFile;

/**
 * Test for StorageValueSequence actor.
 *
 * @author fracpete
 * @author adams.core.option.FlowJUnitTestProducer (code generator)
 * @version $Revision$
 */
public class StorageValueSequenceTest
  extends AbstractFlowTest {

  /**
   * Initializes the test.
   *
   * @param name	the name of the test
   */
  public StorageValueSequenceTest(String name) {
    super(name);
  }

  /**
   * Called by JUnit before each test method.
   *
   * @throws Exception 	if an error occurs.
   */
  protected void setUp() throws Exception {
    super.setUp();
    
    m_TestHelper.deleteFileFromTmp("dumpfile.txt");
  }

  /**
   * Called by JUnit after each test method.
   *
   * @throws Exception	if tear-down fails
   */
  protected void tearDown() throws Exception {
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
    return new TestSuite(StorageValueSequenceTest.class);
  }

  /**
   * Used to create an instance of a specific actor.
   *
   * @return a suitably configured <code>AbstractActor</code> value
   */
  public AbstractActor getActor() {
    AbstractArgumentOption    argOption;
    
    Flow flow = new Flow();
    
    try {
      argOption = (AbstractArgumentOption) flow.getOptionManager().findByProperty("actors");
      adams.flow.core.AbstractActor[] tmp1 = new adams.flow.core.AbstractActor[6];
      adams.flow.standalone.SetVariable tmp2 = new adams.flow.standalone.SetVariable();
      argOption = (AbstractArgumentOption) tmp2.getOptionManager().findByProperty("variableName");
      tmp2.setVariableName((adams.core.VariableName) argOption.valueOf("count"));

      argOption = (AbstractArgumentOption) tmp2.getOptionManager().findByProperty("variableValue");
      tmp2.setVariableValue((java.lang.String) argOption.valueOf("0"));

      tmp1[0] = tmp2;
      adams.flow.source.StringConstants tmp5 = new adams.flow.source.StringConstants();
      argOption = (AbstractArgumentOption) tmp5.getOptionManager().findByProperty("strings");
      adams.core.base.BaseString[] tmp6 = new adams.core.base.BaseString[4];
      tmp6[0] = (adams.core.base.BaseString) argOption.valueOf("blah");
      tmp6[1] = (adams.core.base.BaseString) argOption.valueOf("bloerk");
      tmp6[2] = (adams.core.base.BaseString) argOption.valueOf("some");
      tmp6[3] = (adams.core.base.BaseString) argOption.valueOf("thing");
      tmp5.setStrings(tmp6);

      tmp1[1] = tmp5;
      adams.flow.transformer.IncVariable tmp7 = new adams.flow.transformer.IncVariable();
      argOption = (AbstractArgumentOption) tmp7.getOptionManager().findByProperty("variableName");
      tmp7.setVariableName((adams.core.VariableName) argOption.valueOf("count"));

      tmp1[2] = tmp7;
      adams.flow.transformer.SetStorageValue tmp9 = new adams.flow.transformer.SetStorageValue();
      argOption = (AbstractArgumentOption) tmp9.getOptionManager().findByProperty("storageName");
      tmp9.setStorageName((adams.flow.control.StorageName) argOption.valueOf("content"));

      tmp1[3] = tmp9;
      adams.flow.control.StorageValueSequence tmp11 = new adams.flow.control.StorageValueSequence();
      argOption = (AbstractArgumentOption) tmp11.getOptionManager().findByProperty("actors");
      adams.flow.core.AbstractActor[] tmp12 = new adams.flow.core.AbstractActor[2];
      adams.flow.control.Trigger tmp13 = new adams.flow.control.Trigger();
      argOption = (AbstractArgumentOption) tmp13.getOptionManager().findByProperty("name");
      tmp13.setName((java.lang.String) argOption.valueOf("first processing step"));

      argOption = (AbstractArgumentOption) tmp13.getOptionManager().findByProperty("actors");
      adams.flow.core.AbstractActor[] tmp15 = new adams.flow.core.AbstractActor[3];
      adams.flow.source.StorageValue tmp16 = new adams.flow.source.StorageValue();
      argOption = (AbstractArgumentOption) tmp16.getOptionManager().findByProperty("storageName");
      tmp16.setStorageName((adams.flow.control.StorageName) argOption.valueOf("content"));

      tmp15[0] = tmp16;
      adams.flow.transformer.StringInsert tmp18 = new adams.flow.transformer.StringInsert();
      tmp18.setAfter(true);

      argOption = (AbstractArgumentOption) tmp18.getOptionManager().findByProperty("value");
      argOption.setVariable("@{count}");

      tmp15[1] = tmp18;
      adams.flow.transformer.SetStorageValue tmp19 = new adams.flow.transformer.SetStorageValue();
      argOption = (AbstractArgumentOption) tmp19.getOptionManager().findByProperty("storageName");
      tmp19.setStorageName((adams.flow.control.StorageName) argOption.valueOf("content"));

      tmp15[2] = tmp19;
      tmp13.setActors(tmp15);

      tmp12[0] = tmp13;
      adams.flow.control.Trigger tmp21 = new adams.flow.control.Trigger();
      argOption = (AbstractArgumentOption) tmp21.getOptionManager().findByProperty("name");
      tmp21.setName((java.lang.String) argOption.valueOf("second processing step"));

      argOption = (AbstractArgumentOption) tmp21.getOptionManager().findByProperty("actors");
      adams.flow.core.AbstractActor[] tmp23 = new adams.flow.core.AbstractActor[3];
      adams.flow.source.StorageValue tmp24 = new adams.flow.source.StorageValue();
      argOption = (AbstractArgumentOption) tmp24.getOptionManager().findByProperty("storageName");
      tmp24.setStorageName((adams.flow.control.StorageName) argOption.valueOf("content"));

      tmp23[0] = tmp24;
      adams.flow.transformer.StringInsert tmp26 = new adams.flow.transformer.StringInsert();
      argOption = (AbstractArgumentOption) tmp26.getOptionManager().findByProperty("position");
      tmp26.setPosition(new Index("first"));

      argOption = (AbstractArgumentOption) tmp26.getOptionManager().findByProperty("value");
      argOption.setVariable("@{count}");

      tmp23[1] = tmp26;
      adams.flow.transformer.SetStorageValue tmp28 = new adams.flow.transformer.SetStorageValue();
      argOption = (AbstractArgumentOption) tmp28.getOptionManager().findByProperty("storageName");
      tmp28.setStorageName((adams.flow.control.StorageName) argOption.valueOf("content"));

      tmp23[2] = tmp28;
      tmp21.setActors(tmp23);

      tmp12[1] = tmp21;
      tmp11.setActors(tmp12);

      argOption = (AbstractArgumentOption) tmp11.getOptionManager().findByProperty("storageName");
      tmp11.setStorageName((adams.flow.control.StorageName) argOption.valueOf("content"));

      tmp1[4] = tmp11;
      adams.flow.sink.DumpFile tmp31 = new adams.flow.sink.DumpFile();
      argOption = (AbstractArgumentOption) tmp31.getOptionManager().findByProperty("outputFile");
      tmp31.setOutputFile((adams.core.io.PlaceholderFile) argOption.valueOf("${TMP}/dumpfile.txt"));

      tmp31.setAppend(true);

      tmp1[5] = tmp31;
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

