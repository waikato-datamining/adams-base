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
 * AddNoteTest.java
 * Copyright (C) 2014 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.transformer;

import junit.framework.Test;
import junit.framework.TestSuite;
import adams.core.option.AbstractArgumentOption;
import adams.core.option.OptionUtils;
import adams.env.Environment;
import adams.flow.AbstractFlowTest;
import adams.flow.control.Flow;
import adams.flow.core.AbstractActor;
import adams.test.TmpFile;

/**
 * Test for AddNote actor.
 *
 * @author fracpete
 * @author adams.core.option.FlowJUnitTestProducer (code generator)
 * @version $Revision$
 */
public class AddNoteTest
  extends AbstractFlowTest {

  /**
   * Initializes the test.
   *
   * @param name	the name of the test
   */
  public AddNoteTest(String name) {
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
    
    m_TestHelper.copyResourceToTmp("vote.arff");
    m_TestHelper.deleteFileFromTmp("dumpfile.txt");
  }

  /**
   * Called by JUnit after each test method.
   *
   * @throws Exception	if tear-down fails
   */
  @Override
  protected void tearDown() throws Exception {
    m_TestHelper.deleteFileFromTmp("vote.arff");
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
    return new TestSuite(AddNoteTest.class);
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
      adams.flow.core.AbstractActor[] actors1 = new adams.flow.core.AbstractActor[10];

      // Flow.SetVariable
      adams.flow.standalone.SetVariable setvariable2 = new adams.flow.standalone.SetVariable();
      argOption = (AbstractArgumentOption) setvariable2.getOptionManager().findByProperty("variableName");
      setvariable2.setVariableName((adams.core.VariableName) argOption.valueOf("count"));
      argOption = (AbstractArgumentOption) setvariable2.getOptionManager().findByProperty("variableValue");
      setvariable2.setVariableValue((java.lang.String) argOption.valueOf("0"));
      actors1[0] = setvariable2;

      // Flow.FileSupplier
      adams.flow.source.FileSupplier filesupplier5 = new adams.flow.source.FileSupplier();
      argOption = (AbstractArgumentOption) filesupplier5.getOptionManager().findByProperty("files");
      adams.core.io.PlaceholderFile[] files6 = new adams.core.io.PlaceholderFile[1];
      files6[0] = (adams.core.io.PlaceholderFile) argOption.valueOf("${TMP}/vote.arff");
      filesupplier5.setFiles(files6);
      actors1[1] = filesupplier5;

      // Flow.WekaFileReader
      adams.flow.transformer.WekaFileReader wekafilereader7 = new adams.flow.transformer.WekaFileReader();
      argOption = (AbstractArgumentOption) wekafilereader7.getOptionManager().findByProperty("customLoader");
      weka.core.converters.ArffLoader arffloader9 = new weka.core.converters.ArffLoader();
      wekafilereader7.setCustomLoader(arffloader9);

      actors1[2] = wekafilereader7;

      // Flow.WekaFilter
      adams.flow.transformer.WekaFilter wekafilter10 = new adams.flow.transformer.WekaFilter();
      argOption = (AbstractArgumentOption) wekafilter10.getOptionManager().findByProperty("filter");
      weka.filters.unsupervised.instance.RemoveRange removerange12 = new weka.filters.unsupervised.instance.RemoveRange();
      removerange12.setOptions(OptionUtils.splitOptions("-R 6-last"));
      wekafilter10.setFilter(removerange12);

      actors1[3] = wekafilter10;

      // Flow.WekaInstanceBuffer
      adams.flow.transformer.WekaInstanceBuffer wekainstancebuffer13 = new adams.flow.transformer.WekaInstanceBuffer();
      argOption = (AbstractArgumentOption) wekainstancebuffer13.getOptionManager().findByProperty("operation");
      wekainstancebuffer13.setOperation((adams.flow.transformer.WekaInstanceBuffer.Operation) argOption.valueOf("INSTANCES_TO_INSTANCE"));
      actors1[4] = wekainstancebuffer13;

      // Flow.Convert
      adams.flow.transformer.Convert convert15 = new adams.flow.transformer.Convert();
      argOption = (AbstractArgumentOption) convert15.getOptionManager().findByProperty("conversion");
      adams.data.conversion.WekaInstanceToAdamsInstance wekainstancetoadamsinstance17 = new adams.data.conversion.WekaInstanceToAdamsInstance();
      convert15.setConversion(wekainstancetoadamsinstance17);

      actors1[5] = convert15;

      // Flow.IncVariable
      adams.flow.transformer.IncVariable incvariable18 = new adams.flow.transformer.IncVariable();
      argOption = (AbstractArgumentOption) incvariable18.getOptionManager().findByProperty("variableName");
      incvariable18.setVariableName((adams.core.VariableName) argOption.valueOf("count"));
      actors1[6] = incvariable18;

      // Flow.AddNote
      adams.flow.transformer.AddNote addnote20 = new adams.flow.transformer.AddNote();
      argOption = (AbstractArgumentOption) addnote20.getOptionManager().findByProperty("noteGroup");
      addnote20.setNoteGroup((java.lang.String) argOption.valueOf("general"));
      argOption = (AbstractArgumentOption) addnote20.getOptionManager().findByProperty("noteType");
      addnote20.setNoteType((java.lang.String) argOption.valueOf("MISC: "));
      argOption = (AbstractArgumentOption) addnote20.getOptionManager().findByProperty("noteValue");
      argOption.setVariable("@{count}");
      actors1[7] = addnote20;

      // Flow.GetNotes
      adams.flow.transformer.GetNotes getnotes23 = new adams.flow.transformer.GetNotes();
      actors1[8] = getnotes23;

      // Flow.DumpFile
      adams.flow.sink.DumpFile dumpfile24 = new adams.flow.sink.DumpFile();
      argOption = (AbstractArgumentOption) dumpfile24.getOptionManager().findByProperty("outputFile");
      dumpfile24.setOutputFile((adams.core.io.PlaceholderFile) argOption.valueOf("${TMP}/dumpfile.txt"));
      dumpfile24.setAppend(true);

      actors1[9] = dumpfile24;
      flow.setActors(actors1);

      argOption = (AbstractArgumentOption) flow.getOptionManager().findByProperty("flowExecutionListener");
      adams.flow.execution.NullListener nulllistener27 = new adams.flow.execution.NullListener();
      flow.setFlowExecutionListener(nulllistener27);

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

