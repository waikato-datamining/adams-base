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
 * ArrayFoldsTest.java
 * Copyright (C) 2014 University of Waikato, Hamilton, New Zealand
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
 * Test for ArrayFolds actor.
 *
 * @author fracpete
 * @author adams.core.option.FlowJUnitTestProducer (code generator)
 * @version $Revision$
 */
public class ArrayFoldsTest
  extends AbstractFlowTest {

  /**
   * Initializes the test.
   *
   * @param name	the name of the test
   */
  public ArrayFoldsTest(String name) {
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
    
    m_TestHelper.copyResourceToTmp("numbers.txt");
    m_TestHelper.deleteFileFromTmp("dumpfile.txt");
  }

  /**
   * Called by JUnit after each test method.
   *
   * @throws Exception	if tear-down fails
   */
  @Override
  protected void tearDown() throws Exception {
    m_TestHelper.deleteFileFromTmp("numbers.txt");
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
    return new TestSuite(ArrayFoldsTest.class);
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
      adams.flow.core.Actor[] actors1 = new adams.flow.core.Actor[8];

      // Flow.FileSupplier
      adams.flow.source.FileSupplier filesupplier2 = new adams.flow.source.FileSupplier();
      argOption = (AbstractArgumentOption) filesupplier2.getOptionManager().findByProperty("files");
      adams.core.io.PlaceholderFile[] files3 = new adams.core.io.PlaceholderFile[1];
      files3[0] = (adams.core.io.PlaceholderFile) argOption.valueOf("${TMP}/numbers.txt");
      filesupplier2.setFiles(files3);
      actors1[0] = filesupplier2;

      // Flow.TextFileReader
      adams.flow.transformer.TextFileReader textfilereader4 = new adams.flow.transformer.TextFileReader();
      argOption = (AbstractArgumentOption) textfilereader4.getOptionManager().findByProperty("reader");
      adams.data.io.input.LineArrayTextReader linearraytextreader6 = new adams.data.io.input.LineArrayTextReader();
      textfilereader4.setReader(linearraytextreader6);

      actors1[1] = textfilereader4;

      // Flow.SetVariable
      adams.flow.transformer.SetVariable setvariable7 = new adams.flow.transformer.SetVariable();
      argOption = (AbstractArgumentOption) setvariable7.getOptionManager().findByProperty("variableName");
      setvariable7.setVariableName((adams.core.VariableName) argOption.valueOf("normal"));
      argOption = (AbstractArgumentOption) setvariable7.getOptionManager().findByProperty("variableValue");
      setvariable7.setVariableValue((adams.core.base.BaseText) argOption.valueOf("0"));
      actors1[2] = setvariable7;

      // Flow.ArrayFolds
      adams.flow.transformer.ArrayFolds arrayfolds10 = new adams.flow.transformer.ArrayFolds();
      argOption = (AbstractArgumentOption) arrayfolds10.getOptionManager().findByProperty("generator");
      adams.data.random.JavaRandomInt javarandomint12 = new adams.data.random.JavaRandomInt();
      arrayfolds10.setGenerator(javarandomint12);

      actors1[3] = arrayfolds10;

      // Flow.IncVariable
      adams.flow.transformer.IncVariable incvariable13 = new adams.flow.transformer.IncVariable();
      argOption = (AbstractArgumentOption) incvariable13.getOptionManager().findByProperty("variableName");
      incvariable13.setVariableName((adams.core.VariableName) argOption.valueOf("normal"));
      actors1[4] = incvariable13;

      // Flow.separator
      adams.flow.control.Trigger trigger15 = new adams.flow.control.Trigger();
      argOption = (AbstractArgumentOption) trigger15.getOptionManager().findByProperty("name");
      trigger15.setName((java.lang.String) argOption.valueOf("separator"));
      argOption = (AbstractArgumentOption) trigger15.getOptionManager().findByProperty("actors");
      adams.flow.core.Actor[] actors17 = new adams.flow.core.Actor[2];

      // Flow.separator.CombineVariables
      adams.flow.source.CombineVariables combinevariables18 = new adams.flow.source.CombineVariables();
      argOption = (AbstractArgumentOption) combinevariables18.getOptionManager().findByProperty("expression");
      combinevariables18.setExpression((adams.core.base.BaseText) argOption.valueOf("--> split @{normal}"));
      actors17[0] = combinevariables18;

      // Flow.separator.DumpFile
      adams.flow.sink.DumpFile dumpfile20 = new adams.flow.sink.DumpFile();
      argOption = (AbstractArgumentOption) dumpfile20.getOptionManager().findByProperty("outputFile");
      dumpfile20.setOutputFile((adams.core.io.PlaceholderFile) argOption.valueOf("${TMP}/dumpfile.txt"));
      dumpfile20.setAppend(true);

      actors17[1] = dumpfile20;
      trigger15.setActors(actors17);

      actors1[5] = trigger15;

      // Flow.ArrayToSequence
      adams.flow.transformer.ArrayToSequence arraytosequence22 = new adams.flow.transformer.ArrayToSequence();
      actors1[6] = arraytosequence22;

      // Flow.DumpFile
      adams.flow.sink.DumpFile dumpfile23 = new adams.flow.sink.DumpFile();
      argOption = (AbstractArgumentOption) dumpfile23.getOptionManager().findByProperty("outputFile");
      dumpfile23.setOutputFile((adams.core.io.PlaceholderFile) argOption.valueOf("${TMP}/dumpfile.txt"));
      dumpfile23.setAppend(true);

      actors1[7] = dumpfile23;
      flow.setActors(actors1);

      argOption = (AbstractArgumentOption) flow.getOptionManager().findByProperty("flowExecutionListener");
      adams.flow.execution.NullListener nulllistener26 = new adams.flow.execution.NullListener();
      flow.setFlowExecutionListener(nulllistener26);

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

