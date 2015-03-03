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
 * DeserializeTest.java
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
 * Test for Deserialize actor.
 *
 * @author fracpete
 * @author adams.core.option.FlowJUnitTestProducer (code generator)
 * @version $Revision$
 */
public class DeserializeTest
  extends AbstractFlowTest {

  /**
   * Initializes the test.
   *
   * @param name	the name of the test
   */
  public DeserializeTest(String name) {
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
    
    m_TestHelper.deleteFileFromTmp("file.ser");
    m_TestHelper.deleteFileFromTmp("dumpfile.txt");
  }

  /**
   * Called by JUnit after each test method.
   *
   * @throws Exception	if tear-down fails
   */
  @Override
  protected void tearDown() throws Exception {
    m_TestHelper.deleteFileFromTmp("file.ser");
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
    return new TestSuite(DeserializeTest.class);
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
      adams.flow.core.AbstractActor[] abstractactor1 = new adams.flow.core.AbstractActor[3];

      // Flow.Start
      adams.flow.source.Start start2 = new adams.flow.source.Start();
      abstractactor1[0] = start2;

      // Flow.serialize
      adams.flow.control.Trigger trigger3 = new adams.flow.control.Trigger();
      argOption = (AbstractArgumentOption) trigger3.getOptionManager().findByProperty("name");
      trigger3.setName((java.lang.String) argOption.valueOf("serialize"));

      argOption = (AbstractArgumentOption) trigger3.getOptionManager().findByProperty("actors");
      adams.flow.core.AbstractActor[] abstractactor5 = new adams.flow.core.AbstractActor[2];

      // Flow.serialize.StringConstants
      adams.flow.source.StringConstants stringconstants6 = new adams.flow.source.StringConstants();
      stringconstants6.setOutputArray(true);

      argOption = (AbstractArgumentOption) stringconstants6.getOptionManager().findByProperty("strings");
      adams.core.base.BaseString[] basestring7 = new adams.core.base.BaseString[8];
      basestring7[0] = (adams.core.base.BaseString) argOption.valueOf("3");
      basestring7[1] = (adams.core.base.BaseString) argOption.valueOf("1");
      basestring7[2] = (adams.core.base.BaseString) argOption.valueOf("4");
      basestring7[3] = (adams.core.base.BaseString) argOption.valueOf("1");
      basestring7[4] = (adams.core.base.BaseString) argOption.valueOf("6");
      basestring7[5] = (adams.core.base.BaseString) argOption.valueOf("9");
      basestring7[6] = (adams.core.base.BaseString) argOption.valueOf("2");
      basestring7[7] = (adams.core.base.BaseString) argOption.valueOf("6");
      stringconstants6.setStrings(basestring7);

      abstractactor5[0] = stringconstants6;

      // Flow.serialize.Serialize
      adams.flow.sink.Serialize serialize8 = new adams.flow.sink.Serialize();
      argOption = (AbstractArgumentOption) serialize8.getOptionManager().findByProperty("outputFile");
      serialize8.setOutputFile((adams.core.io.PlaceholderFile) argOption.valueOf("${TMP}/file.ser"));

      abstractactor5[1] = serialize8;
      trigger3.setActors(abstractactor5);

      abstractactor1[1] = trigger3;

      // Flow.deserialize and display
      adams.flow.control.Trigger trigger10 = new adams.flow.control.Trigger();
      argOption = (AbstractArgumentOption) trigger10.getOptionManager().findByProperty("name");
      trigger10.setName((java.lang.String) argOption.valueOf("deserialize and display"));

      argOption = (AbstractArgumentOption) trigger10.getOptionManager().findByProperty("actors");
      adams.flow.core.AbstractActor[] abstractactor12 = new adams.flow.core.AbstractActor[4];

      // Flow.deserialize and display.FileSupplier
      adams.flow.source.FileSupplier filesupplier13 = new adams.flow.source.FileSupplier();
      argOption = (AbstractArgumentOption) filesupplier13.getOptionManager().findByProperty("files");
      adams.core.io.PlaceholderFile[] placeholderfile14 = new adams.core.io.PlaceholderFile[1];
      placeholderfile14[0] = (adams.core.io.PlaceholderFile) argOption.valueOf("${TMP}/file.ser");
      filesupplier13.setFiles(placeholderfile14);

      abstractactor12[0] = filesupplier13;

      // Flow.deserialize and display.Deserialize
      adams.flow.transformer.Deserialize deserialize15 = new adams.flow.transformer.Deserialize();
      abstractactor12[1] = deserialize15;

      // Flow.deserialize and display.Convert
      adams.flow.transformer.Convert convert16 = new adams.flow.transformer.Convert();
      argOption = (AbstractArgumentOption) convert16.getOptionManager().findByProperty("conversion");
      adams.data.conversion.AnyToString anytostring18 = new adams.data.conversion.AnyToString();
      convert16.setConversion(anytostring18);

      abstractactor12[2] = convert16;

      // Flow.deserialize and display.DumpFile
      adams.flow.sink.DumpFile dumpfile19 = new adams.flow.sink.DumpFile();
      argOption = (AbstractArgumentOption) dumpfile19.getOptionManager().findByProperty("outputFile");
      dumpfile19.setOutputFile((adams.core.io.PlaceholderFile) argOption.valueOf("${TMP}/dumpfile.txt"));

      abstractactor12[3] = dumpfile19;
      trigger10.setActors(abstractactor12);

      abstractactor1[2] = trigger10;
      flow.setActors(abstractactor1);

      argOption = (AbstractArgumentOption) flow.getOptionManager().findByProperty("flowExecutionListener");
      adams.flow.execution.NullListener nulllistener22 = new adams.flow.execution.NullListener();
      flow.setFlowExecutionListener(nulllistener22);

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

