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
 * WekaReorderAttributesToReferenceTest.java
 * Copyright (C) 2013 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.transformer;

import junit.framework.Test;
import junit.framework.TestSuite;
import adams.core.option.AbstractArgumentOption;
import adams.core.option.OptionUtils;
import adams.env.Environment;
import adams.flow.AbstractFlowTest;
import adams.flow.control.Flow;
import adams.flow.core.Actor;
import adams.test.TmpFile;

/**
 * Test for WekaReorderAttributesToReference actor.
 *
 * @author fracpete
 * @author adams.core.option.FlowJUnitTestProducer (code generator)
 * @version $Revision$
 */
public class WekaReorderAttributesToReferenceTest
  extends AbstractFlowTest {

  /**
   * Initializes the test.
   *
   * @param name	the name of the test
   */
  public WekaReorderAttributesToReferenceTest(String name) {
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
    
    m_TestHelper.copyResourceToTmp("iris-reference.arff");
    m_TestHelper.copyResourceToTmp("iris-different_order.arff");
    m_TestHelper.copyResourceToTmp("iris-missing.arff");
    m_TestHelper.deleteFileFromTmp("dumpfile1.arff");
    m_TestHelper.deleteFileFromTmp("dumpfile2.arff");
  }

  /**
   * Called by JUnit after each test method.
   *
   * @throws Exception	if tear-down fails
   */
  @Override
  protected void tearDown() throws Exception {
    m_TestHelper.deleteFileFromTmp("iris-reference.arff");
    m_TestHelper.deleteFileFromTmp("iris-different_order.arff");
    m_TestHelper.deleteFileFromTmp("iris-missing.arff");
    m_TestHelper.deleteFileFromTmp("dumpfile1.arff");
    m_TestHelper.deleteFileFromTmp("dumpfile2.arff");
    
    super.tearDown();
  }

  /**
   * Performs a regression test, comparing against previously generated output.
   */
  public void testRegression() {
    performRegressionTest(
        new TmpFile[]{
          new TmpFile("dumpfile1.arff"),
          new TmpFile("dumpfile2.arff")
        });
  }

  /**
   * 
   * Returns a test suite.
   *
   * @return		the test suite
   */
  public static Test suite() {
    return new TestSuite(WekaReorderAttributesToReferenceTest.class);
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
      adams.flow.core.Actor[] actors1 = new adams.flow.core.Actor[3];

      // Flow.Start
      adams.flow.source.Start start2 = new adams.flow.source.Start();
      actors1[0] = start2;

      // Flow.Trigger
      adams.flow.control.Trigger trigger3 = new adams.flow.control.Trigger();
      argOption = (AbstractArgumentOption) trigger3.getOptionManager().findByProperty("actors");
      adams.flow.core.Actor[] actors4 = new adams.flow.core.Actor[4];

      // Flow.Trigger.FileSupplier
      adams.flow.source.FileSupplier filesupplier5 = new adams.flow.source.FileSupplier();
      argOption = (AbstractArgumentOption) filesupplier5.getOptionManager().findByProperty("files");
      adams.core.io.PlaceholderFile[] files6 = new adams.core.io.PlaceholderFile[1];
      files6[0] = (adams.core.io.PlaceholderFile) argOption.valueOf("${TMP}/iris-missing.arff");
      filesupplier5.setFiles(files6);
      actors4[0] = filesupplier5;

      // Flow.Trigger.WekaFileReader
      adams.flow.transformer.WekaFileReader wekafilereader7 = new adams.flow.transformer.WekaFileReader();
      argOption = (AbstractArgumentOption) wekafilereader7.getOptionManager().findByProperty("customLoader");
      weka.core.converters.ArffLoader arffloader9 = new weka.core.converters.ArffLoader();
      wekafilereader7.setCustomLoader(arffloader9);

      actors4[1] = wekafilereader7;

      // Flow.Trigger.WekaReorderAttributesToReference
      adams.flow.transformer.WekaReorderAttributesToReference wekareorderattributestoreference10 = new adams.flow.transformer.WekaReorderAttributesToReference();
      argOption = (AbstractArgumentOption) wekareorderattributestoreference10.getOptionManager().findByProperty("referenceFile");
      wekareorderattributestoreference10.setReferenceFile((adams.core.io.PlaceholderFile) argOption.valueOf("${TMP}/iris-reference.arff"));
      argOption = (AbstractArgumentOption) wekareorderattributestoreference10.getOptionManager().findByProperty("customLoader");
      weka.core.converters.ArffLoader arffloader13 = new weka.core.converters.ArffLoader();
      wekareorderattributestoreference10.setCustomLoader(arffloader13);

      wekareorderattributestoreference10.setLenient(true);

      wekareorderattributestoreference10.setKeepRelationName(true);

      actors4[2] = wekareorderattributestoreference10;

      // Flow.Trigger.WekaFileWriter
      adams.flow.sink.WekaFileWriter wekafilewriter14 = new adams.flow.sink.WekaFileWriter();
      argOption = (AbstractArgumentOption) wekafilewriter14.getOptionManager().findByProperty("outputFile");
      wekafilewriter14.setOutputFile((adams.core.io.PlaceholderFile) argOption.valueOf("${TMP}/dumpfile1.arff"));
      argOption = (AbstractArgumentOption) wekafilewriter14.getOptionManager().findByProperty("customSaver");
      weka.core.converters.ArffSaver arffsaver17 = new weka.core.converters.ArffSaver();
      arffsaver17.setOptions(OptionUtils.splitOptions("-decimal 6"));
      wekafilewriter14.setCustomSaver(arffsaver17);

      actors4[3] = wekafilewriter14;
      trigger3.setActors(actors4);

      actors1[1] = trigger3;

      // Flow.Trigger-1
      adams.flow.control.Trigger trigger18 = new adams.flow.control.Trigger();
      argOption = (AbstractArgumentOption) trigger18.getOptionManager().findByProperty("name");
      trigger18.setName((java.lang.String) argOption.valueOf("Trigger-1"));
      argOption = (AbstractArgumentOption) trigger18.getOptionManager().findByProperty("actors");
      adams.flow.core.Actor[] actors20 = new adams.flow.core.Actor[4];

      // Flow.Trigger-1.FileSupplier
      adams.flow.source.FileSupplier filesupplier21 = new adams.flow.source.FileSupplier();
      argOption = (AbstractArgumentOption) filesupplier21.getOptionManager().findByProperty("files");
      adams.core.io.PlaceholderFile[] files22 = new adams.core.io.PlaceholderFile[1];
      files22[0] = (adams.core.io.PlaceholderFile) argOption.valueOf("${TMP}/iris-different_order.arff");
      filesupplier21.setFiles(files22);
      actors20[0] = filesupplier21;

      // Flow.Trigger-1.WekaFileReader
      adams.flow.transformer.WekaFileReader wekafilereader23 = new adams.flow.transformer.WekaFileReader();
      argOption = (AbstractArgumentOption) wekafilereader23.getOptionManager().findByProperty("customLoader");
      weka.core.converters.ArffLoader arffloader25 = new weka.core.converters.ArffLoader();
      wekafilereader23.setCustomLoader(arffloader25);

      actors20[1] = wekafilereader23;

      // Flow.Trigger-1.WekaReorderAttributesToReference
      adams.flow.transformer.WekaReorderAttributesToReference wekareorderattributestoreference26 = new adams.flow.transformer.WekaReorderAttributesToReference();
      argOption = (AbstractArgumentOption) wekareorderattributestoreference26.getOptionManager().findByProperty("referenceFile");
      wekareorderattributestoreference26.setReferenceFile((adams.core.io.PlaceholderFile) argOption.valueOf("${TMP}/iris-reference.arff"));
      argOption = (AbstractArgumentOption) wekareorderattributestoreference26.getOptionManager().findByProperty("customLoader");
      weka.core.converters.ArffLoader arffloader29 = new weka.core.converters.ArffLoader();
      wekareorderattributestoreference26.setCustomLoader(arffloader29);

      wekareorderattributestoreference26.setKeepRelationName(true);

      actors20[2] = wekareorderattributestoreference26;

      // Flow.Trigger-1.WekaFileWriter
      adams.flow.sink.WekaFileWriter wekafilewriter30 = new adams.flow.sink.WekaFileWriter();
      argOption = (AbstractArgumentOption) wekafilewriter30.getOptionManager().findByProperty("outputFile");
      wekafilewriter30.setOutputFile((adams.core.io.PlaceholderFile) argOption.valueOf("${TMP}/dumpfile2.arff"));
      argOption = (AbstractArgumentOption) wekafilewriter30.getOptionManager().findByProperty("customSaver");
      weka.core.converters.ArffSaver arffsaver33 = new weka.core.converters.ArffSaver();
      arffsaver33.setOptions(OptionUtils.splitOptions("-decimal 6"));
      wekafilewriter30.setCustomSaver(arffsaver33);

      actors20[3] = wekafilewriter30;
      trigger18.setActors(actors20);

      actors1[2] = trigger18;
      flow.setActors(actors1);

      argOption = (AbstractArgumentOption) flow.getOptionManager().findByProperty("flowExecutionListener");
      adams.flow.execution.NullListener nulllistener35 = new adams.flow.execution.NullListener();
      flow.setFlowExecutionListener(nulllistener35);

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

