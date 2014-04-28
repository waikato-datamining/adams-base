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
 * SplitFileTest.java
 * Copyright (C) 2014 University of Waikato, Hamilton, New Zealand
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
 * Test for SplitFile actor.
 *
 * @author fracpete
 * @author adams.core.option.FlowJUnitTestProducer (code generator)
 * @version $Revision$
 */
public class SplitFileTest
  extends AbstractFlowTest {

  /**
   * Initializes the test.
   *
   * @param name	the name of the test
   */
  public SplitFileTest(String name) {
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
    m_TestHelper.deleteFileFromTmp("dumpfile.txt");
  }

  /**
   * Called by JUnit after each test method.
   *
   * @throws Exception	if tear-down fails
   */
  @Override
  protected void tearDown() throws Exception {
    m_TestHelper.deleteFileFromTmp("bolts.csv");
    m_TestHelper.deleteFileFromTmp("split001.bin");
    m_TestHelper.deleteFileFromTmp("split002.bin");
    m_TestHelper.deleteFileFromTmp("split003.bin");
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
    return new TestSuite(SplitFileTest.class);
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
      adams.flow.core.AbstractActor[] actors1 = new adams.flow.core.AbstractActor[7];

      // Flow.FileSupplier
      adams.flow.source.FileSupplier filesupplier2 = new adams.flow.source.FileSupplier();
      argOption = (AbstractArgumentOption) filesupplier2.getOptionManager().findByProperty("files");
      adams.core.io.PlaceholderFile[] files3 = new adams.core.io.PlaceholderFile[1];
      files3[0] = (adams.core.io.PlaceholderFile) argOption.valueOf("${TMP}/bolts.csv");
      filesupplier2.setFiles(files3);
      actors1[0] = filesupplier2;

      // Flow.SplitFile
      adams.flow.transformer.SplitFile splitfile4 = new adams.flow.transformer.SplitFile();
      argOption = (AbstractArgumentOption) splitfile4.getOptionManager().findByProperty("splitter");
      adams.flow.transformer.splitfile.SplitByNumber splitbynumber6 = new adams.flow.transformer.splitfile.SplitByNumber();
      argOption = (AbstractArgumentOption) splitbynumber6.getOptionManager().findByProperty("prefix");
      splitbynumber6.setPrefix((adams.core.io.PlaceholderFile) argOption.valueOf("${TMP}/split"));
      argOption = (AbstractArgumentOption) splitbynumber6.getOptionManager().findByProperty("numFiles");
      splitbynumber6.setNumFiles((Integer) argOption.valueOf("3"));
      splitfile4.setSplitter(splitbynumber6);

      actors1[1] = splitfile4;

      // Flow.ArrayToSequence
      adams.flow.transformer.ArrayToSequence arraytosequence9 = new adams.flow.transformer.ArrayToSequence();
      actors1[2] = arraytosequence9;

      // Flow.SetVariable
      adams.flow.transformer.SetVariable setvariable10 = new adams.flow.transformer.SetVariable();
      argOption = (AbstractArgumentOption) setvariable10.getOptionManager().findByProperty("variableName");
      setvariable10.setVariableName((adams.core.VariableName) argOption.valueOf("file"));
      actors1[3] = setvariable10;

      // Flow.FileInfo
      adams.flow.transformer.FileInfo fileinfo12 = new adams.flow.transformer.FileInfo();
      actors1[4] = fileinfo12;

      // Flow.SetVariable-1
      adams.flow.transformer.SetVariable setvariable13 = new adams.flow.transformer.SetVariable();
      argOption = (AbstractArgumentOption) setvariable13.getOptionManager().findByProperty("name");
      setvariable13.setName((java.lang.String) argOption.valueOf("SetVariable-1"));
      argOption = (AbstractArgumentOption) setvariable13.getOptionManager().findByProperty("variableName");
      setvariable13.setVariableName((adams.core.VariableName) argOption.valueOf("size"));
      actors1[5] = setvariable13;

      // Flow.Trigger
      adams.flow.control.Trigger trigger16 = new adams.flow.control.Trigger();
      argOption = (AbstractArgumentOption) trigger16.getOptionManager().findByProperty("actors");
      adams.flow.core.AbstractActor[] actors17 = new adams.flow.core.AbstractActor[2];

      // Flow.Trigger.CombineVariables
      adams.flow.source.CombineVariables combinevariables18 = new adams.flow.source.CombineVariables();
      argOption = (AbstractArgumentOption) combinevariables18.getOptionManager().findByProperty("expression");
      combinevariables18.setExpression((adams.core.base.BaseString) argOption.valueOf("@{file}: @{size}"));
      actors17[0] = combinevariables18;

      // Flow.Trigger.DumpFile
      adams.flow.sink.DumpFile dumpfile20 = new adams.flow.sink.DumpFile();
      argOption = (AbstractArgumentOption) dumpfile20.getOptionManager().findByProperty("outputFile");
      dumpfile20.setOutputFile((adams.core.io.PlaceholderFile) argOption.valueOf("${TMP}/dumpfile.txt"));
      dumpfile20.setAppend(true);

      actors17[1] = dumpfile20;
      trigger16.setActors(actors17);

      actors1[6] = trigger16;
      flow.setActors(actors1);

      argOption = (AbstractArgumentOption) flow.getOptionManager().findByProperty("flowExecutionListener");
      adams.flow.execution.NullListener nulllistener23 = new adams.flow.execution.NullListener();
      flow.setFlowExecutionListener(nulllistener23);

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

