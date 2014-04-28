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
 * StorageValuesArrayTest.java
 * Copyright (C) 2013 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.source;

import junit.framework.Test;
import junit.framework.TestSuite;
import adams.core.option.AbstractArgumentOption;
import adams.env.Environment;
import adams.flow.AbstractFlowTest;
import adams.flow.control.Flow;
import adams.flow.core.AbstractActor;
import adams.test.TmpFile;

/**
 * Test for StorageValuesArray actor.
 *
 * @author fracpete
 * @author adams.core.option.FlowJUnitTestProducer (code generator)
 * @version $Revision$
 */
public class StorageValuesArrayTest
  extends AbstractFlowTest {

  /**
   * Initializes the test.
   *
   * @param name	the name of the test
   */
  public StorageValuesArrayTest(String name) {
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
    
    m_TestHelper.copyResourceToTmp("diff1.txt");
    m_TestHelper.copyResourceToTmp("diff2.txt");
    m_TestHelper.deleteFileFromTmp("dumpfile.txt");
  }

  /**
   * Called by JUnit after each test method.
   *
   * @throws Exception	if tear-down fails
   */
  @Override
  protected void tearDown() throws Exception {
    m_TestHelper.deleteFileFromTmp("diff1.txt");
    m_TestHelper.deleteFileFromTmp("diff2.txt");
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
    return new TestSuite(StorageValuesArrayTest.class);
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

      // Flow.SetVariable
      adams.flow.standalone.SetVariable setvariable2 = new adams.flow.standalone.SetVariable();
      argOption = (AbstractArgumentOption) setvariable2.getOptionManager().findByProperty("variableName");
      setvariable2.setVariableName((adams.core.VariableName) argOption.valueOf("items"));

      argOption = (AbstractArgumentOption) setvariable2.getOptionManager().findByProperty("variableValue");
      setvariable2.setVariableValue((java.lang.String) argOption.valueOf(""));

      abstractactor1[0] = setvariable2;

      // Flow.Start
      adams.flow.source.Start start5 = new adams.flow.source.Start();
      abstractactor1[1] = start5;

      // Flow.load into storage
      adams.flow.control.Trigger trigger6 = new adams.flow.control.Trigger();
      argOption = (AbstractArgumentOption) trigger6.getOptionManager().findByProperty("name");
      trigger6.setName((java.lang.String) argOption.valueOf("load into storage"));

      argOption = (AbstractArgumentOption) trigger6.getOptionManager().findByProperty("actors");
      adams.flow.core.AbstractActor[] abstractactor8 = new adams.flow.core.AbstractActor[4];

      // Flow.load into storage.DirectoryLister
      adams.flow.source.DirectoryLister directorylister9 = new adams.flow.source.DirectoryLister();
      argOption = (AbstractArgumentOption) directorylister9.getOptionManager().findByProperty("watchDir");
      directorylister9.setWatchDir((adams.core.io.PlaceholderDirectory) argOption.valueOf("${EXAMPLE_FLOWS}/data"));

      directorylister9.setListFiles(true);

      argOption = (AbstractArgumentOption) directorylister9.getOptionManager().findByProperty("regExp");
      directorylister9.setRegExp((adams.core.base.BaseRegExp) argOption.valueOf("diff.*\\.txt"));

      argOption = (AbstractArgumentOption) directorylister9.getOptionManager().findByProperty("sorting");
      directorylister9.setSorting((adams.core.io.DirectoryLister.Sorting) argOption.valueOf("SORT_BY_NAME"));

      abstractactor8[0] = directorylister9;

      // Flow.load into storage.Count
      adams.flow.control.Count count13 = new adams.flow.control.Count();
      argOption = (AbstractArgumentOption) count13.getOptionManager().findByProperty("actors");
      adams.flow.core.AbstractActor[] abstractactor14 = new adams.flow.core.AbstractActor[2];

      // Flow.load into storage.Count.SetVariable
      adams.flow.transformer.SetVariable setvariable15 = new adams.flow.transformer.SetVariable();
      argOption = (AbstractArgumentOption) setvariable15.getOptionManager().findByProperty("variableName");
      setvariable15.setVariableName((adams.core.VariableName) argOption.valueOf("name"));

      abstractactor14[0] = setvariable15;

      // Flow.load into storage.Count.append list
      adams.flow.control.Trigger trigger17 = new adams.flow.control.Trigger();
      argOption = (AbstractArgumentOption) trigger17.getOptionManager().findByProperty("name");
      trigger17.setName((java.lang.String) argOption.valueOf("append list"));

      argOption = (AbstractArgumentOption) trigger17.getOptionManager().findByProperty("actors");
      adams.flow.core.AbstractActor[] abstractactor19 = new adams.flow.core.AbstractActor[4];

      // Flow.load into storage.Count.append list.Variable
      adams.flow.source.Variable variable20 = new adams.flow.source.Variable();
      argOption = (AbstractArgumentOption) variable20.getOptionManager().findByProperty("variableName");
      variable20.setVariableName((adams.core.VariableName) argOption.valueOf("items"));

      abstractactor19[0] = variable20;

      // Flow.load into storage.Count.append list.StringInsert
      adams.flow.transformer.StringInsert stringinsert22 = new adams.flow.transformer.StringInsert();
      stringinsert22.setAfter(true);

      argOption = (AbstractArgumentOption) stringinsert22.getOptionManager().findByProperty("value");
      stringinsert22.setValue((adams.core.base.BaseString) argOption.valueOf(" @{name}"));

      stringinsert22.setValueContainsVariable(true);

      abstractactor19[1] = stringinsert22;

      // Flow.load into storage.Count.append list.StringTrim
      adams.flow.transformer.StringTrim stringtrim24 = new adams.flow.transformer.StringTrim();
      abstractactor19[2] = stringtrim24;

      // Flow.load into storage.Count.append list.SetVariable
      adams.flow.transformer.SetVariable setvariable25 = new adams.flow.transformer.SetVariable();
      argOption = (AbstractArgumentOption) setvariable25.getOptionManager().findByProperty("variableName");
      setvariable25.setVariableName((adams.core.VariableName) argOption.valueOf("items"));

      abstractactor19[3] = setvariable25;
      trigger17.setActors(abstractactor19);

      abstractactor14[1] = trigger17;
      count13.setActors(abstractactor14);

      argOption = (AbstractArgumentOption) count13.getOptionManager().findByProperty("condition");
      adams.flow.condition.bool.Counting counting28 = new adams.flow.condition.bool.Counting();
      count13.setCondition(counting28);

      abstractactor8[1] = count13;

      // Flow.load into storage.BaseName
      adams.flow.transformer.BaseName basename29 = new adams.flow.transformer.BaseName();
      abstractactor8[2] = basename29;

      // Flow.load into storage.SetStorageValue
      adams.flow.transformer.SetStorageValue setstoragevalue30 = new adams.flow.transformer.SetStorageValue();
      argOption = (AbstractArgumentOption) setstoragevalue30.getOptionManager().findByProperty("storageName");
      argOption.setVariable("@{name}");

      abstractactor8[3] = setstoragevalue30;
      trigger6.setActors(abstractactor8);

      abstractactor1[2] = trigger6;

      // Flow.output array
      adams.flow.control.Trigger trigger31 = new adams.flow.control.Trigger();
      argOption = (AbstractArgumentOption) trigger31.getOptionManager().findByProperty("name");
      trigger31.setName((java.lang.String) argOption.valueOf("output array"));

      argOption = (AbstractArgumentOption) trigger31.getOptionManager().findByProperty("actors");
      adams.flow.core.AbstractActor[] abstractactor33 = new adams.flow.core.AbstractActor[4];

      // Flow.output array.StorageValuesArray
      adams.flow.source.StorageValuesArray storagevaluesarray34 = new adams.flow.source.StorageValuesArray();
      argOption = (AbstractArgumentOption) storagevaluesarray34.getOptionManager().findByProperty("storageNames");
      argOption.setVariable("@{items}");

      abstractactor33[0] = storagevaluesarray34;

      // Flow.output array.ArrayToSequence
      adams.flow.transformer.ArrayToSequence arraytosequence35 = new adams.flow.transformer.ArrayToSequence();
      abstractactor33[1] = arraytosequence35;

      // Flow.output array.Count
      adams.flow.control.Count count36 = new adams.flow.control.Count();
      argOption = (AbstractArgumentOption) count36.getOptionManager().findByProperty("actors");
      adams.flow.core.AbstractActor[] abstractactor37 = new adams.flow.core.AbstractActor[1];

      // Flow.output array.Count.SetVariable
      adams.flow.transformer.SetVariable setvariable38 = new adams.flow.transformer.SetVariable();
      argOption = (AbstractArgumentOption) setvariable38.getOptionManager().findByProperty("variableName");
      setvariable38.setVariableName((adams.core.VariableName) argOption.valueOf("index"));

      abstractactor37[0] = setvariable38;
      count36.setActors(abstractactor37);

      argOption = (AbstractArgumentOption) count36.getOptionManager().findByProperty("condition");
      adams.flow.condition.bool.Counting counting41 = new adams.flow.condition.bool.Counting();
      count36.setCondition(counting41);

      abstractactor33[2] = count36;

      // Flow.output array.DumpFile
      adams.flow.sink.DumpFile dumpfile42 = new adams.flow.sink.DumpFile();
      argOption = (AbstractArgumentOption) dumpfile42.getOptionManager().findByProperty("outputFile");
      dumpfile42.setOutputFile((adams.core.io.PlaceholderFile) argOption.valueOf("${TMP}/dumpfile.txt"));

      dumpfile42.setAppend(true);

      abstractactor33[3] = dumpfile42;
      trigger31.setActors(abstractactor33);

      abstractactor1[3] = trigger31;
      flow.setActors(abstractactor1);

      argOption = (AbstractArgumentOption) flow.getOptionManager().findByProperty("flowExecutionListener");
      adams.flow.execution.NullListener nulllistener45 = new adams.flow.execution.NullListener();
      flow.setFlowExecutionListener(nulllistener45);

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

