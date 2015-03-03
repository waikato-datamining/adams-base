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
 * JsonFileWriterTest.java
 * Copyright (C) 2013 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.sink;

import junit.framework.Test;
import junit.framework.TestSuite;
import adams.core.option.AbstractArgumentOption;
import adams.env.Environment;
import adams.flow.AbstractFlowTest;
import adams.flow.control.Flow;
import adams.flow.core.AbstractActor;
import adams.test.TmpFile;

/**
 * Test for JsonFileWriter actor.
 *
 * @author fracpete
 * @author adams.core.option.FlowJUnitTestProducer (code generator)
 * @version $Revision$
 */
public class JsonFileWriterTest
  extends AbstractFlowTest {

  /**
   * Initializes the test.
   *
   * @param name	the name of the test
   */
  public JsonFileWriterTest(String name) {
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
    
    m_TestHelper.deleteFileFromTmp("dumpfile.txt");
  }

  /**
   * Called by JUnit after each test method.
   *
   * @throws Exception	if tear-down fails
   */
  @Override
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
    return new TestSuite(JsonFileWriterTest.class);
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

      // Flow.StringConstants
      adams.flow.source.StringConstants stringconstants2 = new adams.flow.source.StringConstants();
      argOption = (AbstractArgumentOption) stringconstants2.getOptionManager().findByProperty("strings");
      adams.core.base.BaseString[] basestring3 = new adams.core.base.BaseString[1];
      basestring3[0] = (adams.core.base.BaseString) argOption.valueOf("{\"glossary\":{\"title\":\"example glossary\",\"GlossDiv\":{\"GlossList\":{\"GlossEntry\":{\"SortAs\":\"SGML\",\"GlossDef\":{\"GlossSeeAlso\":[\"GML\",\"XML\"],\"para\":\"A meta-markup language, used to create markup languages such as DocBook.\"},\"GlossSee\":\"markup\",\"GlossTerm\":\"Standard Generalized Markup Language\",\"ID\":\"SGML\",\"Acronym\":\"SGML\",\"Abbrev\":\"ISO 8879:1986\"}},\"title\":\"S\"}}}");
      stringconstants2.setStrings(basestring3);

      abstractactor1[0] = stringconstants2;

      // Flow.Convert
      adams.flow.transformer.Convert convert4 = new adams.flow.transformer.Convert();
      argOption = (AbstractArgumentOption) convert4.getOptionManager().findByProperty("conversion");
      adams.data.conversion.StringToJson stringtojson6 = new adams.data.conversion.StringToJson();
      convert4.setConversion(stringtojson6);

      abstractactor1[1] = convert4;

      // Flow.JsonFileWriter
      adams.flow.sink.JsonFileWriter jsonfilewriter7 = new adams.flow.sink.JsonFileWriter();
      argOption = (AbstractArgumentOption) jsonfilewriter7.getOptionManager().findByProperty("outputFile");
      jsonfilewriter7.setOutputFile((adams.core.io.PlaceholderFile) argOption.valueOf("${TMP}/dumpfile.txt"));

      abstractactor1[2] = jsonfilewriter7;
      flow.setActors(abstractactor1);

      argOption = (AbstractArgumentOption) flow.getOptionManager().findByProperty("flowExecutionListener");
      adams.flow.execution.NullListener nulllistener10 = new adams.flow.execution.NullListener();
      flow.setFlowExecutionListener(nulllistener10);

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

